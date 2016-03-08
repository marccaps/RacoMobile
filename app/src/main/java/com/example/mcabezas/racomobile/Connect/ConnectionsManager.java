package com.example.mcabezas.racomobile.Connect;

/**
 * Created by mcabezas on 11/02/16.
 */
    import java.lang.ref.WeakReference;

    import android.app.Activity;
    import android.os.AsyncTask;

    import com.example.mcabezas.racomobile.ItemList;
    import com.example.mcabezas.racomobile.Model.Notice;
    import com.example.mcabezas.racomobile.Model.BaseDadesManager;
    import com.example.mcabezas.racomobile.Model.Mail;
    import com.example.mcabezas.racomobile.Model.News;


public class ConnectionsManager extends AsyncTask<ParserAndUrl, Integer, ItemList> {

        WeakReference<Activity> mContext;
        private final String mTAG = "GestioConnexio";
        private BaseDadesManager mBd;

        public ConnectionsManager(Activity activity) {
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
        protected ItemList doInBackground(ParserAndUrl... urls) {

            ItemList lli = new ItemList();
            ItemList tmp = new ItemList();

            JsonParser json = JsonParser.getInstance();
            XmlParser xml = XmlParser.getInstance();
            IcalParser ics = IcalParser.getInstance();

            // Donem accés als certificats de la FIB
            CertificateManager.allowAllSSL();

            //Així tanquem les connexions segur
            System.setProperty("http.keepAlive", "false");

            for (int i = 0; i < urls.length; i++) {

                switch (urls[i].getTipus()) {
                    case AndroidUtils.TIPUS_NOTICIA: // Noticies
                        tmp = xml.parserData(urls[i].getTipus(), urls[i].getUrl());
                        insertarItemsGenerics(lli, tmp);

                        News ig;

                        mBd.open();
                        mBd.deleteTableNoticies();

                        for (int j = 0; j < tmp.getLitemsGenerics().size(); j++) {
                            ig = (News) tmp.getLitemsGenerics().get(j);
                            mBd.insertItemNoticia(ig.getTitol(), ig.getDescripcio(),
                                    ig.getImatge(), ig.getDataPub().toString(),
                                    ig.getTipus(), ig.getmLink());
                        }
                        mBd.close();
                        break;

                    case AndroidUtils.TIPUS_CORREU: // Mail
                        tmp = json.parserData(urls[i].getTipus(), urls[i].getUrl(),
                                urls[i].getUsername(), urls[i].getPassword());
                        insertarItemsGenerics(lli, tmp);

                        Mail correu;

                        mBd.open();
                        mBd.deleteTableCorreus();
                        for (int j = 0; j < tmp.getLitemsGenerics().size(); j++) {
                            correu = (Mail) tmp.getLitemsGenerics().get(j);
                            mBd.insertItemCorreu(correu.getTitol(),correu.getDescripcio(),correu.getImatge(),
                                    correu.getDataPub().toString(),correu.getTipus(),correu.getLlegits(),correu.getNo_llegits());
                        }
                        mBd.close();
                        break;

                        case AndroidUtils.TIPUS_AVISOS: // Avisos
                            tmp = xml.parserData(urls[i].getTipus(), urls[i].getUrl());
                            insertarItemsGenerics(lli, tmp);

                            Notice avis;

                            mBd.open();
                            mBd.deleteTableAvisos();
                            mBd.deleteTableAssigRaco();
                            mBd.deleteTableAssigFib();
                            for (int j = 0; j < tmp.getLitemsGenerics().size(); j++) {
                                avis = (Notice) tmp.getLitemsGenerics().get(j);
                                mBd.insertItemAvis(avis.getTitol(),avis.getDescripcio(),avis.getImatge(),avis.getDataPub().toString()+ " ",avis.getTipus(),avis.getNomAssig(),avis.getUrl());
                            }
                            mBd.close();
                            break;
                    default:
                        break;
                }
            }

                return lli;
        }

        /** Captura les dades parsejades en el doInBackground per a la vista ControladorVistaEvents i NotíciesFIB */
        private void insertarItemsGenerics(ItemList lli, ItemList tmp) {
            if (tmp != null) {
                for (int i = 0; i < tmp.getLitemsGenerics().size(); i++) {
                    lli.afegirItemGeneric(tmp.getLitemsGenerics().get(i));
                    lli.afegirImatge(tmp.getLimatges().get(i));
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progres) {
        }

        @Override
        protected void onPostExecute(ItemList result) {

        }
}
