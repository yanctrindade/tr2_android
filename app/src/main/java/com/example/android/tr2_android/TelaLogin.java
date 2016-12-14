package com.example.android.tr2_android;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.tr2_android.Login.LoginServidor;


public class TelaLogin extends AppCompatActivity {

    private EditText edNome;
    private EditText edSenha;
    private EditText edServidor;
    private Button login;
    private Button teste;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login);


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

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginServidor objLogin = new LoginServidor(edNome,edSenha);
                final String response = objLogin.sendDataToServer();
                if(response == "true"){
                    Intent principal = new Intent(TelaLogin.this, TelaPrincipal.class);
                    startActivity(principal);
                }
            }
        });

        teste = (Button) findViewById(R.id.teste);
        teste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pula o login e vai para a tela principal. *Enquanto o login não está funcionando
                Intent principal = new Intent(TelaLogin.this, TelaPrincipal.class);
                startActivity(principal);

            }
        });
    }

    @Override
    public void OnLoginConcluido(String sitiacaoLogin) {

    }
}
