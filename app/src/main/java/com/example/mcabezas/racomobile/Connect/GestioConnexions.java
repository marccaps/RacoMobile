package com.example.mcabezas.racomobile.Connect;

/**
 * Created by mcabezas on 11/02/16.
 */
    import java.lang.ref.WeakReference;

    import android.app.Activity;
    import android.os.AsyncTask;
    import android.util.Log;

    import com.example.mcabezas.racomobile.AndroidUtils;
    import com.example.mcabezas.racomobile.LlistesItems;
    import com.example.mcabezas.racomobile.Model.BaseDadesManager;
    import com.example.mcabezas.racomobile.Model.Correu;
    import com.example.mcabezas.racomobile.Model.Noticia;


public class GestioConnexions extends AsyncTask<ParserAndUrl, Integer, LlistesItems> {

        WeakReference<Activity> mContext;
        private final String mTAG = "GestioConnexio";
        private BaseDadesManager mBd;

        public GestioConnexions(Activity activity) {
            mContext = new WeakReference<Activity>(activity);
            mBd = new BaseDadesManager(activity);
        }

        /**
         * Cridada abans de l'execució, serveix per si es vol mostrar algu a
         * l'usuari
         */
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected LlistesItems doInBackground(ParserAndUrl... urls) {

            LlistesItems lli = new LlistesItems();
            LlistesItems tmp = new LlistesItems();

            JsonParser json = JsonParser.getInstance();
            XmlParser xml = XmlParser.getInstance();
            IcalParser ics = IcalParser.getInstance();

            // Donem accés als certificats de la FIB
            GestorCertificats.allowAllSSL();

            //Així tanquem les connexions segur
            System.setProperty("http.keepAlive", "false");

            for (int i = 0; i < urls.length; i++) {

                switch (urls[i].getTipus()) {
                    case AndroidUtils.TIPUS_NOTICIA: // Noticies
                        tmp = xml.parserData(urls[i].getTipus(), urls[i].getUrl());
                        insertarItemsGenerics(lli, tmp);

                        Noticia ig;

                        mBd.open();
                        mBd.deleteTableNoticies();

                        for (int j = 0; j < tmp.getLitemsGenerics().size(); j++) {
                            ig = (Noticia) tmp.getLitemsGenerics().get(j);
                            mBd.insertItemNoticia(ig.getTitol(), ig.getDescripcio(),
                                    ig.getImatge(), ig.getDataPub().toString(),
                                    ig.getTipus(), ig.getmLink());
                        }
                        mBd.close();
                        break;

                    case AndroidUtils.TIPUS_CORREU: // Correu
                        tmp = json.parserData(urls[i].getTipus(), urls[i].getUrl(),
                                urls[i].getUsername(), urls[i].getPassword());
                        insertarItemsGenerics(lli, tmp);

                        Correu correu;

                        mBd.open();
                        mBd.deleteTableCorreus();
                        for (int j = 0; j < tmp.getLitemsGenerics().size(); j++) {
                            correu = (Correu) tmp.getLitemsGenerics().get(j);
                            mBd.insertItemCorreu(correu.getTitol(),correu.getDescripcio(),correu.getImatge(),correu.getFrom()+ " ",correu.getTipus(),correu.getLlegits(),correu.getNo_llegits());
                        }
                        mBd.close();
                        break;

//                        case AndroidUtils.TIPUS_AVISOS: // Avisos
//                            tmp = xml.parserData(urls[i].getTipus(), urls[i].getUrl());
//                            insertarItemsGenerics(lli, tmp);
//                            break;
//
//                        case AndroidUtils.TIPUS_ASSIG: // Assignatures
//                            lli = json.parserData(urls[i].getTipus(), urls[i].getUrl(),
//                                    null, null);
//                            break;
//
//                        case AndroidUtils.TIPUS_AGENDA_RACO: // Agenda
//                            //lli = xml.parserData(urls[i].getTipus(), urls[i].getUrl());
//                            lli = ics.parserData(urls[i].getTipus(), urls[i].getUrl(), null);
//                            break;
//
//                        case AndroidUtils.TIPUS_AULES_I_OCUPACIO_RACO:
//                            tmp = json.parserData(urls[i].getTipus(), urls[i].getUrl(),
//                                    null, null);
//                            insertarItemsAulaOcupacio(lli, tmp);
//                            break;
//
//                        case AndroidUtils.TIPUS_CLASSES_DIA_RACO:
//                            tmp = ics.parserData(urls[i].getTipus(), urls[i].getUrl(), lli);
//                            break;
//
//                        case AndroidUtils.TIPUS_HORARI_RACO:
//                            lli = ics.parserData(urls[i].getTipus(), urls[i].getUrl(), null);
//                            break;
                    default:
                        break;
                }
            }

                return lli;
        }

        /** Captura les dades parsejades en el doInBackground per a la vista ControladorVistaEvents i NotíciesFIB */
        private void insertarItemsGenerics(LlistesItems lli, LlistesItems tmp) {
            if (tmp != null) {
                for (int i = 0; i < tmp.getLitemsGenerics().size(); i++) {
                    lli.afegirItemGeneric(tmp.getLitemsGenerics().get(i));
                    lli.afegirImatge(tmp.getLimatges().get(i));
                }
            }
        }

        /** Captura les dades parsejades en el doInBackground per a les aules */
        private void insertarItemsAulaOcupacio(LlistesItems lli, LlistesItems tmp) {
            if (tmp != null) {
                for (int i = 0; i < tmp.getLaula().size(); i++) {
                    lli.afegirItemAula(tmp.getLaula().get(i));
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progres) {
        }

        @Override
        protected void onPostExecute(LlistesItems result) {
//            try {
//                Activity activity = mContext.get();
//
//                //Actualitzem les base de dades
//                ((GestioActualitzaLlistesActivity) activity)
//                        .actualitzarLlistaBaseDades(result);
//                if (activity != null && !activity.isFinishing()) {
//                    //mostrem la informació si la pantalla està activa
//                    ((GestioActualitzaLlistesActivity) activity).mostrarLlistes();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
}
