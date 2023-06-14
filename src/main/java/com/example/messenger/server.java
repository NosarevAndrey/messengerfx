package com.example.messenger;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;


class ServeICQ extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    public String UniqueID;
    public static Map<String, Socket> clientSockets;


    public ServeICQ(Socket s, Map<String, Socket> clientSockets, String ID) throws IOException {
        socket = s;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                .getOutputStream())), true);
        this.clientSockets = clientSockets;
        this.UniqueID = ID;
        //this.parser = parser;
        start();
    }
    public static void handleMessage(String receivedMessage, PrintWriter out){
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            SAXPars saxp = new SAXPars();
            saxp.setAttributeListener(map -> {
                switch (map.get("type")){
                    case "dialog_accept":
                    case "dialog_request":
                    case "send_message":
                        String targetID = map.get("receiver");
                        Socket targetSocket = clientSockets.get(targetID);

                        if (targetSocket != null) {
                            try {
                                PrintWriter targetOut = new PrintWriter(
                                        new BufferedWriter(new OutputStreamWriter(targetSocket.getOutputStream())), true);

                                // Send the message to the target client
                                targetOut.println(receivedMessage);
                            } catch (IOException e) {
                                System.err.println("Error sending message to target client: " + e.getMessage());
                            }
                        } else {
                            System.out.println("Target client not found: " + targetID);
                        }
                        break;
                    default:
                        System.out.println("Unrecognized message");
                }
            });
            InputStream is = new ByteArrayInputStream(receivedMessage.getBytes());
            parser.parse(is, saxp);
            System.out.println("Echoing: " + receivedMessage);

        }
        catch (IOException e) {
            System.err.println("IO Exception");
        }
        catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static <K, V> K getKeyFromValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
    public void run() {
        try {
            String ID_message = buildID(UniqueID);
            out.println(ID_message);
            while (true) {

                String str = in.readLine();
                if (str == null) break;
                if (str.equals("END"))
                    break;
                handleMessage(str,out);
            }
            System.out.println("closing..." + socket);
            clientSockets.remove(getKeyFromValue(clientSockets,socket));
        }
        catch (IOException e) {
            System.err.println("IO Exception");
        }
        finally {
            try {
                socket.close();
            }
            catch (IOException e) {
                System.err.println("Socket not closed");
            }
        }
    }
    public static String buildID(String uniqueID){
        LocalDateTime currentTime = LocalDateTime.now();
        String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));

        String start ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?> <message ";
        String type = String.format("type=\"%s\" ","ID_message");
        String sender = String.format("receiver=\"%s\" ",uniqueID);
        String time = String.format("time=\"%s\" ",formattedTime);
        String end = "></message>";
        String result = start + type + sender + time + end;
        System.out.println(result);
        return result;
    }
}

public class server {
    static final int PORT = 8080;
    private static Map<String, Socket> clientSockets = new HashMap<>();

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        ServerSocket s = new ServerSocket(PORT);

        System.out.println("Server Started");


        try {
            Scanner sc = new Scanner(System.in);
            Thread inputThread = new Thread(() -> {
                while (true) {
                    String input = sc.nextLine();
                    String pattern = "^close \\| ([a-fA-F0-9-]+)$";
                    if (input.equals("help")) {
                        String text = "Commands:\n" +
                                "threads\n Displays number of active threads\n" +
                                "sockets\n Displays map of active sockets and corresponding clientId\n" +
                                "remove | clientId\n Shuts down socket with id = clientId and remove it from map\n";
                        System.out.print(text);
                    }
                    if (input.equals("threads"))
                        System.out.println("Number of active threads: " + Thread.activeCount());
                    if (input.equals("sockets")) {
                        System.out.print("{  ");
                        for (String key : clientSockets.keySet())
                            System.out.print( "\n" + key + "  " + clientSockets.get(key));
                        System.out.print("  }\n");
                    }
                    if (input.matches(pattern)){
                        String uuidString = input.replaceAll(pattern, "$1");
                        UUID uuid = UUID.fromString(uuidString);
                        if (clientSockets.containsKey(uuid.toString())) {
                            try {
                                clientSockets.get(uuid.toString()).close();
                                clientSockets.remove(uuid.toString());
                            }
                            catch (IOException e) { e.printStackTrace(); }
                        }
                    }

                }
            });
            inputThread.start();


            while (true) {
                Socket clientSocket = s.accept();
                String clientId = UUID.randomUUID().toString();
                clientSockets.put(clientId, clientSocket);

                try {
                    new ServeICQ(clientSocket, clientSockets, clientId);
                }
                catch (IOException e) {
                    s.close();
                }
            }
        }
        finally {
            s.close();
        }
    }
}