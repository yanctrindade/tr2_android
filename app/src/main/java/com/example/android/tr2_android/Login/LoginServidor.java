package com.example.android.tr2_android.Login;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by vitor on 12/12/16.
 */

public class LoginServidor{

    private String login;
    private String senha;

    public LoginServidor(EditText edNome,EditText edSenha){

        login = edNome.getText().toString();
        senha = edSenha.getText().toString();

    }

    public String sendDataToServer() {

        final String json = formatDataAsJSON();
        final String response = null;
        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... voids) {
                response = getServerResponse(json);
                return response;
            }

            protected void OnPostExecute(String result){

                if(response == null){

                    Toast toast = Toast.makeText(getApplicationContext(),"Erro na Autenticaçao",Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

        }.execute();

        return response;

    }



    private String formatDataAsJSON(){

        final JSONObject root = new JSONObject();
        try{
            root.put("user",login);
            root.put("password",senha);

            return root.toString(1);
        }catch(JSONException e1){
            Log.d("JWP","Can't format JSON");
        }
        return null;
    }

    private String getServerResponse(String json){

        HttpPost post = new HttpPost("");
        try{
            DefaultHttpClient client = new DefaultHttpClient();

            BasicResponseHandler handler = new BasicResponseHandler();

            String response = client.execute(post, handler);

            return response;
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();

        }catch(ClientProtocolException e){
            e.printStackTrace();

        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
}