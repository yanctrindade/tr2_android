package com.example.android.tr2_android.Login;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by vitor on 12/12/16.
 */

public class LoginServidor extends AsyncTask {

    private LoginListener listener;
    private static final String URL_STRING = " ";

    public LoginServidor(LoginListener listener){

        this.listener = listener;

    }

    void OnPostExecute(String result){
        listener.OnLoginConcluido(result);
    }
    @Override
    protected Object doInBackground(Object[] objects) {

        try {
            String result = ConsultaServidor();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String ConsultaServidor() throws IOException {
        InputStream is =  null;
        try{

            URL url = new URL(URL_STRING);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            is = conn.getInputStream();

        }finally {
            if(is != null) {
                is.close();
            }
        }
        return ""; //falta o retorno;
    }

    public interface LoginListener{
        void OnLoginConcluido(String sitiacaoLogin);
    }
}