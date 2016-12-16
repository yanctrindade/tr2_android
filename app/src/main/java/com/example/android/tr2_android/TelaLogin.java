package com.example.android.tr2_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.tr2_android.Login.LoginServidor;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.ImageViewBuilder;


public class TelaLogin extends AppCompatActivity {

    private EditText edEmail;
    private EditText edSenha;
    private EditText edServidor;
    private TextView creditos;
    private Button login;
    private Button teste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login);


        edEmail = (EditText)findViewById(R.id.edEmail);
        edSenha = (EditText)findViewById(R.id.edSenha);
        edServidor = (EditText)findViewById(R.id.edServidor);
        creditos = (TextView) findViewById(R.id.link_creditos);

        final ImageView creditosImage = new ImageView(this);
        creditosImage.setImageResource(R.drawable.creditos);

        final AlertDialog.Builder builder = new AlertDialog.Builder(TelaLogin.this);
        builder.setMessage("Créditos")
                .setView(creditosImage)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        final AlertDialog alert = builder.create();

        creditos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.show();
            }
        });

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                if(validate()) {

                    String login = edEmail.getText().toString();
                    String senha = edSenha.getText().toString();

                    JsonObject json = new JsonObject();
                    json.addProperty("email", login);
                    json.addProperty("password", senha);
                    Ion.with(getApplicationContext())
                            .load("http://bspy.herokuapp.com/autenticar")
                            .setJsonObjectBody(json)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    // do stuff with the result or error
                                    if (e == null) {
                                        boolean success = result.get("res").getAsBoolean();
                                        if (success) {
                                            Intent principal = new Intent(TelaLogin.this, TelaPrincipal.class);
                                            startActivity(principal);
                                        } else {

                                            Toast toast = Toast.makeText(getApplicationContext(), "Acesso negado", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    } else {

                                        Toast toast = Toast.makeText(getApplicationContext(), "Serviço indisponível ou usuário incorreto", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }

                                }
                            });
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

    public boolean validate() {
        boolean valid = true;

        String email = edEmail.getText().toString();
        String password = edSenha.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmail.setError("");
            valid = false;
        } else {
            edEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 ) {
            edSenha.setError("senha tem que ter mais do que 4 caracteres");
            valid = false;
        } else {
            edSenha.setError(null);
        }

        return valid;
    }
}
