package com.example.android.tr2_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.tr2_android.Login.LoginServidor;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


public class TelaLogin extends AppCompatActivity {

    private EditText edEmail;
    private EditText edSenha;
    private EditText edServidor;
    private Button login;
    private Button teste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login);


        edEmail = (EditText)findViewById(R.id.edEmail);
        edSenha = (EditText)findViewById(R.id.edSenha);
        edServidor = (EditText)findViewById(R.id.edServidor);

        edEmail.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean b) {
                if(edEmail.getText().length() == 0){
                    edEmail.setError("Nome deve ter algum valor");
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
            public void onClick(View v){

                String login = edEmail.getText().toString();
                String senha = edSenha.getText().toString();

                JsonObject json = new JsonObject();
                json.addProperty("email",login);
                json.addProperty("password", senha);
                Ion.with(getApplicationContext())
                        .load("http://bspy.herokuapp.com/autenticar")
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                // do stuff with the result or error
                                if(e == null){
                                    boolean success = result.get("res").getAsBoolean();
                                    if (success) {
                                        Intent principal = new Intent(TelaLogin.this, TelaPrincipal.class);
                                        startActivity(principal);
                                    } else {

                                        Toast toast = Toast.makeText(getApplicationContext(), "Acesso negado", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                }
                                else {

                                    Toast toast = Toast.makeText(getApplicationContext(), "Serviço indisponível ou usuário incorreto", Toast.LENGTH_SHORT);
                                    toast.show();
                                }

                            }
                        });

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
}
