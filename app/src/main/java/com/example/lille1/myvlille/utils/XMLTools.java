package com.example.lille1.myvlille.utils;

import android.util.Log;
import android.location.Location;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by havet on 15/09/14.
 */
public class XMLTools {

    private String getXmlFromUrl(String url) {
        String xml = null;

        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            Log.e("Error: ", e.getMessage());
        } catch (ClientProtocolException e) {
            Log.e("Error: ", e.getMessage());
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
        }
        // return XML
        return xml;
    }

    private Document getDomElement(String xml){
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        boolean pending = true;
        int count = 0;

        while (pending) {
            try {

                DocumentBuilder db = dbf.newDocumentBuilder();

                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xml));
                doc = db.parse(is);

            } catch (ParserConfigurationException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (SAXException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (IOException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (NullPointerException npe) {
                if (++count == 5) {
                    Log.e("Error", "Unable to retrieve data -> " + npe.getMessage());
                }
            }

            pending = false;
        }

        // return DOM
        return doc;

    }

    private String getValue(Element item, String str) {
        return item.getAttribute(str);
    }

    private String getElementValue( Node elem ) {
        Node child;
        if( elem != null){
            if (elem.hasChildNodes()){
                for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
                    if( child.getNodeType() == Node.TEXT_NODE  ){
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }


    // All static variables
    static final String LIST_URL = "http://www.vlille.fr/stations/xml-stations.aspx";
    static final String STATION_URL = "http://vlille.fr/stations/xml-station.aspx?borne=";

    // XML node keys

    static final String KEY_MARKER = "marker"; // parent node
    static final String KEY_ID = "id";
    static final String KEY_LAT = "lat";
    static final String KEY_LNG = "lng";
    static final String KEY_NAME = "name";

    static final String KEY_STATION = "station";
    static final String KEY_ADRESS = "adress";
    static final String KEY_STATUS = "status";
    static final String KEY_BIKES = "bikes";
    static final String KEY_ATTACHS = "attachs";
    static final String KEY_PAIEMENT = "paiement";
    static final String KEY_LASTUPD = "lastupd";


    public Station[] getStations(Location origin) {

        String xml = this.getXmlFromUrl(LIST_URL); // getting XML
        Document doc = this.getDomElement(xml); // getting DOM element

        NodeList nl = doc.getElementsByTagName(KEY_MARKER);

        Station[] stations = new Station[nl.getLength()];

        // looping through all item nodes <item>
        Element e;
        for (int i = 0; i < nl.getLength(); i++) {
            e = (Element) nl.item(i);
            String id = this.getValue(e, KEY_ID);
            String lat = this.getValue(e, KEY_LAT);
            String lng = this.getValue(e, KEY_LNG);
            String name = this.getValue(e, KEY_NAME);
            Log.i("Info", id + "|" + lat + "|" + lng + "|" + name);
            stations[i] = new Station(Integer.parseInt(id), Double.parseDouble(lat), Double.parseDouble(lng), name, origin);
        }

        return stations;
    }

    public Station getStationWithInfos(Station station) {
        Log.i("Info", "URL " + STATION_URL + station.getId());
        String xml = this.getXmlFromUrl(STATION_URL + station.getId()); // getting XML
        Log.i("Info", "XML " + xml);
        Document doc = this.getDomElement(xml); // getting DOM element

        NodeList nl = doc.getElementsByTagName(KEY_STATION);

        Element e = (Element) nl.item(0);
        Log.i("Info", e.getTextContent());

        station.setAdress(e.getElementsByTagName(KEY_ADRESS).item(0).getTextContent());
        station.setStatus(e.getElementsByTagName(KEY_STATUS).item(0).getTextContent());
        station.setBikes(e.getElementsByTagName(KEY_BIKES).item(0).getTextContent());
        station.setAttachs(e.getElementsByTagName(KEY_ATTACHS).item(0).getTextContent());
        station.setPaiement(e.getElementsByTagName(KEY_PAIEMENT).item(0).getTextContent());
        station.setLastupd(e.getElementsByTagName(KEY_LASTUPD).item(0).getTextContent());
        Log.i("Info : ", station.getAdress() + "|" + station.getStatus() + "|" + station.getBikes() + "|" + station.getAttachs() + "|" + station.getPaiement() + "|" + nl.getLength());

        return station;
    }
}
