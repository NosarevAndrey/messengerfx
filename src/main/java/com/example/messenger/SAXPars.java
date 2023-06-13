package com.example.messenger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SAXPars extends DefaultHandler{

    String thisElement = "";


    private Consumer<Map<String,String>> attributeMapListener;
    public void setAttributeListener(Consumer<Map<String,String>> listener) {
        this.attributeMapListener = listener;
    }
    @Override
    public void startDocument() throws SAXException {
        System.out.println("startDocument");
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        thisElement = qName;
        System.out.println("startElement");
        System.out.println(" " + qName);
        Map<String,String> attributeMap = new HashMap<>();
        for(int i=0; i<atts.getLength();i++)
        {
            String attributeName = atts.getLocalName(i);
            String attributeValue = atts.getValue(i);
            System.out.println(attributeName + " : " + attributeValue);
            attributeMap.put(attributeName, attributeValue);
        }
        if (attributeMapListener != null) {
            attributeMapListener.accept(attributeMap);
        }
        System.out.println(attributeMap.toString());

    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        thisElement = "";
        System.out.println("endElement");
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //if (thisElement.equals("id")) {
        //   doc.setId(new Integer(new String(ch, start, length)));
        //}
    }

    @Override
    public void endDocument() {
        System.out.println("endDocument");
    }
}
