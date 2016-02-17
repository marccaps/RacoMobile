package com.example.mcabezas.racomobile.Connect;

/**
 * Created by mcabezas on 17/02/16.
 */
import com.example.mcabezas.racomobile.AndroidUtils;
import com.example.mcabezas.racomobile.LlistesItems;
import com.example.mcabezas.racomobile.Model.Assignatura;
import com.example.mcabezas.racomobile.Model.Aula;
import com.example.mcabezas.racomobile.Model.Correu;
import com.example.mcabezas.racomobile.Model.Professors;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;



public class JsonParser {

    private final String mTAG = "JSONParser";
    private static JsonParser sInstancia = null;

    private synchronized static void crearInstancia() {
        if (sInstancia == null) {
            sInstancia = new JsonParser();
        }
    }

    public static JsonParser getInstance() {
        if (sInstancia == null)
            crearInstancia();
        return sInstancia;
    }

    public LlistesItems parserData(int tipus, URL url, String username,
                                   String password) {
        LlistesItems lli = new LlistesItems();
        switch (tipus) {
            case AndroidUtils.TIPUS_CORREU:// Correu
                lli = parserCorreu(url, username, password);
                break;

            case AndroidUtils.TIPUS_ASSIG:// Assig FIB
                lli = parserAssig(url);
                break;
            case AndroidUtils.TIPUS_AULES_I_OCUPACIO_RACO:
                lli = parseAulesOcupacio(url);
                break;
            default:
                break;
        }
        return lli;
    }

    protected LlistesItems parserCorreu(URL url, String username,
                                        String password) {

        LlistesItems lli = new LlistesItems();
        AndroidUtils au = AndroidUtils.getInstance();
        String subjecte, from;
        Date pubDate;

        JsonNode rootNode;
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(1000);
            con.setUseCaches(false);
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setDoInput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes("username=" + username + "&password=" + password
                    + "&max_emails="+au.MAX_CORREUS);
            wr.flush();
            wr.close();
            InputStream is = ((HttpURLConnection) con).getInputStream();
            ObjectMapper maper = new ObjectMapper();
            rootNode = maper.readValue(is, JsonNode.class);
            is.close();
            String statusNode = rootNode.path("status").textValue()
                    .toString();
            if (statusNode.equalsIgnoreCase("0")) {
                return null;
            } else {
                String numNoLlegitsNode = rootNode.path("numNoLlegits")
                        .textValue().toString();

                String numLlegitsNode = rootNode.path("numMails")
                        .textValue().toString();

                // POSAR AL SERVIDOR!!!
                String _urlImatge = au.URL_CORREU_IMATGE;

                for (JsonNode node : rootNode.path("mails")) {
                    subjecte = node.path("subject").toString(); // title
                    from = node.path("from").toString(); // descripcio
                    pubDate = AndroidUtils.dateJSONStringToDateCorreu(node.path("date").toString());
                    lli.afegirItemGeneric(new Correu(subjecte, from,
                            _urlImatge, pubDate, AndroidUtils.TIPUS_CORREU, Integer
                            .parseInt(numLlegitsNode), Integer
                            .parseInt(numNoLlegitsNode)));
                    lli.afegirImatge(_urlImatge);
                }
            }
        } catch (JsonParseException e) {
            con.disconnect();
            e.printStackTrace();
        } catch (JsonMappingException e) {
            con.disconnect();
            e.printStackTrace();
        } catch (IOException e) {
            con.disconnect();
            e.printStackTrace();
        }
        con.disconnect();
        return lli;
    }

    protected LlistesItems parserAssig(URL url) {
        LlistesItems lli = new LlistesItems();
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(1000);
            InputStream is = ((HttpURLConnection) con).getInputStream();
            ObjectMapper m = new ObjectMapper();
            JsonNode rootNode = m.readValue(is, JsonNode.class);

            String idAssig = null, nom = null;
            int codi = 0;

            for (JsonNode node : rootNode) {
                codi = Integer.valueOf(node.path("codi_upc").textValue()
                        .toString());
                nom = node.path("nom").textValue().toString();
                idAssig = node.path("idAssig").textValue().toString();
                Assignatura a = new Assignatura(codi, nom, idAssig, 0, null,
                        null);
                lli.afegirItemAssig(a);
            }
            con.disconnect();
            return lli;
        } catch (Exception e) {
            con.disconnect();
            e.printStackTrace();
            return null;
        }
    }

    public Assignatura parserInfoAssig(URL url) {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(1000);
            InputStream is = ((HttpURLConnection) con).getInputStream();
            ObjectMapper m = new ObjectMapper();
            JsonNode rootNode = m.readValue(is, JsonNode.class);
            int status = rootNode.path("status").intValue();
            if (status == 0) {
                String idAssig = rootNode.path("idAssig").textValue()
                        .toString();
                String nomA = rootNode.path("nom").textValue().toString();
                ArrayList<String> objectius = new ArrayList<String>();
                String objectiu;
                for (JsonNode nodeObj : rootNode.path("descripcio")) {
                    objectiu = nodeObj.textValue().toString();
                    objectius.add(objectiu);
                }
                int credits = Integer.valueOf(rootNode.path("credits").textValue()
                        .toString());
                String email = null;
                String nom = null;
                ArrayList<Professors> prof = new ArrayList<Professors>();
                for (JsonNode nodeProf : rootNode.path("professors")) {
                    email = nodeProf.path("email").textValue().toString();
                    nom = nodeProf.path("nom").textValue().toString();
                    Professors p = new Professors(nom, email, 0);
                    prof.add(p);
                }
                Assignatura a = new Assignatura(0, nomA, idAssig, credits,
                        prof, objectius);
                con.disconnect();
                return a;
            } else {
                return null;
            }

        } catch (Exception e) {
            con.disconnect();
            e.printStackTrace();
            return null;
        }
    }

    protected LlistesItems parseAulesOcupacio(URL url) {
        LlistesItems lli = new LlistesItems();
        AndroidUtils au = AndroidUtils.getInstance();
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(1000);
            InputStream is = ((HttpURLConnection) con).getInputStream();
            ObjectMapper m = new ObjectMapper();
            JsonNode rootNode = m.readValue(is, JsonNode.class);
            String _aula, _places, _dataUpdate;
            Aula aula = null;
            Date dataUpdate;
            Calendar dataFi = Calendar.getInstance();
            Calendar cDataUpdate = Calendar.getInstance();

            for (JsonNode node : rootNode.path("aules")) {
                _aula = node.path("nom").toString().replace('"', ' ');
                _places = node.path("places").toString().replace('"', ' ');
                _dataUpdate = rootNode.path("update").textValue().toString();
                dataUpdate = AndroidUtils.dateJSONStringToDateOcupacioAules(_dataUpdate);
                cDataUpdate.setTime(dataUpdate);
                int horaAulaFi = cDataUpdate.get(Calendar.HOUR);
                horaAulaFi++;
                dataFi.set(Calendar.HOUR, horaAulaFi);
                aula = new Aula(_aula.toUpperCase().trim(), _places, dataUpdate, dataUpdate, dataFi.getTime(), " ", "false");
                lli.afegirItemAula(aula);
            }
            con.disconnect();
            return lli;
        } catch (Exception e) {
            con.disconnect();
            e.printStackTrace();
            return null;
        }
    }
}