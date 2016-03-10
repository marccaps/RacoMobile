package com.example.mcabezas.racomobile.Connect;

/**
 * Created by mcabezas on 10/02/16.
 */
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;

/**
 * Aquesta classe no conté cap vista però ens farà servei per posar operacions
 * comunes o útils a diferents classes
 */
public class AndroidUtils {

    // Per carregar imatges en la primera vista
    public String URL_AVISOS_IMATGE = "http://www.fib.upc.edu/docroot/androidMobile/assignatures_foto.jpg";
    public String URL_CORREU_IMATGE = "http://www.fib.upc.edu/docroot/androidMobile/correo_foto.jpg";
    public String URL_IMATGE_DEFAULT = "http://www.fib.upc.edu/docroot/androidMobile/default.png";

    // URL'S comunes
    public String URL_LOGIN = "https://raco.fib.upc.edu/cas/login?service=https%3A%2F%2Fraco.fib.upc.edu%2Fservlet%2Fraco.webservices.InitKeys&loginDirecte=true&";
    public String URL_NOTICIES = "http://www.fib.upc.edu/fib/rss.rss";
    public String URL_CORREU = "https://webmail.fib.upc.edu/horde/imp/check_mail/resum_mail_json.php";
    public String URL_AVISOS = "https://raco.fib.upc.edu/extern/rss_avisos.jsp?KEY=";

    public String URL_AULA_A5 = "https://raco.fib.upc.edu/mapa_ocupades.php?mod=a5";
    public String URL_AULA_B5 = "https://raco.fib.upc.edu/mapa_ocupades.php?mod=b5";
    public String URL_AULA_C6 = "https://raco.fib.upc.edu/mapa_ocupades.php?mod=c6";
    public String URL_HORARI_RACO = "https://raco.fib.upc.edu/ical/horari.ics?KEY=";

    // Per saber si en tenim de pendents o no
    public String NOTIFICATION_COUNTER = "NUMERO_NOTIFICACIONS";
    // Constants
    public static final int TIPUS_NOTICIA = 0;
    public static final int TIPUS_CORREU = 1;
    public static final int TIPUS_AVISOS = 2;
    // INICIAL
    public static final int TIPUS_ASSIG = 4;
    public static final int TIPUS_AGENDA_RACO = 5; // AGENDA
    public static final int TIPUS_AULES_I_OCUPACIO_RACO = 6;
    public static final int TIPUS_CLASSES_DIA_RACO = 7; // ASSIGNATURES A LA

    public final String KEY_AVISOS = "KEY_AVISOS";
    public final String KEY_ASSIG_FIB = "KEY_ASSIG_FIB";
    public final String KEY_AGENDA_RACO_XML = "KEY_AGENDA_RACO_XML";
    public final String KEY_AGENDA_RACO_CAL = "KEY_AGENDA_RACO_CAL";
    public final String KEY_ASSIGS_RACO = "KEY_ASSIGS_RACO";
    public final String KEY_HORARI_RACO = "KEY_HORARI_RACO";
    public final String KEY_NOTIFICACIONS_REGISTRAR = "KEY_NOTIFICACIONS_REGISTRAR";
    public final String KEY_NOTIFICACIONS_DESREGISTRAR = "KEY_NOTIFICACIONS_DESREGISTRAR";

    // SharedPreferences
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    // UTILS
    public final int MAX_CORREUS = 16;
    public final int TEMPS_REFRESC = 5; // minuts

    private static AndroidUtils sInstancia = null;

    // Formatters
    public final static SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss",Locale.ENGLISH);

    // creador sincronizado para protegerse de posibles problemas multi-hilo
    // otra prueba para evitar instanciación múltiple
    private synchronized static void crearInstancia() {
        if (sInstancia == null) {
            sInstancia = new AndroidUtils();
        }
    }

    public static AndroidUtils getInstance() {
        if (sInstancia == null)
            crearInstancia();
        return sInstancia;
    }

    public URL crearURL(String url) throws MalformedURLException {
        return new URL(url);
    }

    public Date crearDataActual() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        return cal.getTime();
    }

    public VEvent crearVEventAvui() {
        java.util.Calendar startDate = Calendar.getInstance();
        startDate.setTimeZone(java.util.TimeZone.getDefault());

        DateTime start = new DateTime(startDate.getTime());
        start.setUtc(true);
        VEvent Eavui = new VEvent(start, start, "avui");
        return Eavui;
    }

    public VEvent crearVEvent(Date data) {

        java.util.Calendar startDate = Calendar.getInstance();
        startDate.set(data.getYear(), data.getMonth() + 1, data.getDate());
        DateTime start = new DateTime(startDate.getTime());
        start.setUtc(true);
        VEvent Eavui = new VEvent(start, start, "diaConcret");
        return Eavui;
    }

    public Date resetHora() {
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Madrid");
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(timeZone);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        return cal.getTime();
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (;;) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }


    public String htmlToString(String html) {
        // Remove HTML tag from java String
        String noHTMLString = html.replaceAll("\\<.*?\\>", "");

        // Remove Carriage return from java String
        noHTMLString = noHTMLString.replaceAll("\r", "<br/>");

        // Remove New line from java string and replace html break
        noHTMLString = noHTMLString.replaceAll("\n", " ");
        noHTMLString = noHTMLString.replaceAll("\'", "&#39;");
        noHTMLString = noHTMLString.replaceAll("\"", "&quot;");
        return noHTMLString;
    }

    public static SimpleDateFormat crearSimpleDateFormat(){
        return format;
    }

    public static Date dateXMLStringToDateParser(String sDate) {
        Date date = null;
        try{
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
            date = formatter.parse(sDate);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return date;
    }

    public static Date dateXMLStringToDateControlador(SimpleDateFormat formatter, String sDate) {
        Date date = null;
        try {
            if(sDate.length() > 10) {
                sDate = sDate.substring(0, sDate.length() - 10);
                date = formatter.parse(sDate);
            }
            else date = formatter.parse(sDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    public static Date dateJSONStringToDateCorreu (String sDate){
        Date date = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "\"dd/MM/yyyy HH:mm\"");
            date = formatter.parse(sDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    public static Date dateJSONStringToDateOcupacioAules (String sDate){
        Date date = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "dd/MM/yyyy HH:mm:ss");
            date = formatter.parse(sDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    public static String dateToStringVistaInici(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        String sDate = sdf.format(date);
        return sDate;
    }
}