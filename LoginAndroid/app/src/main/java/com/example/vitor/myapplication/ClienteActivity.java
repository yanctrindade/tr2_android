package com.example.vitor.myapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vitor.myapplication.LoginServidor.LoginListener;

/**
 * Created by vitor on 09/11/16.
 */

public class ClienteActivity extends Activity implements LoginListener{

    private EditText edNome;
    private EditText edSenha;
    private EditText edServidor;

    public ClienteActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadcliente);

        edNome = (EditText)findViewById(R.id.edNome);
        edSenha = (EditText)findViewById(R.id.edSenha);
        edServidor = (EditText)findViewById(R.id.edServidor);

        edNome.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View view, boolean b) {

                if(edNome.getText().length() == 0){
                    edNome.setError("Nome deve ter algum valor");
                }
            }
        });

        edSenha.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View view, boolean b) {

                if(edSenha.getText().length() < 8){
                    edSenha.setError("A senha deve ter no mínimo 8 caracteres");
                }
            }
        });

        edServidor.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View view, boolean b) {

                if(edServidor.getText().length() == 0){
                    edServidor.setError("Nome deve ter algum valor");
                }
            }
        });

    }

    public void clickConfirmar(View v){

        AsyncTask execute = new LoginServidor(this).execute();


    }

    @Override
    public void OnLoginConcluido(String sitiacaoLogin) {

    }
}
