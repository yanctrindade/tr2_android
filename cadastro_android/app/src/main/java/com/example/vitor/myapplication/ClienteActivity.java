package com.example.vitor.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by vitor on 09/11/16.
 */

public class ClienteActivity extends Activity {

    private EditText edNome;
    private EditText edEmail;
    private EditText edSenha;
    private EditText edConfirmaSenha;

    public ClienteActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadcliente);

        edNome = (EditText)findViewById(R.id.edNome);
        edEmail = (EditText)findViewById(R.id.edEmail);
        edSenha = (EditText)findViewById(R.id.edSenha);
        edConfirmaSenha= (EditText)findViewById(R.id.edConfirmSenha);

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

        edConfirmaSenha.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View view, boolean b) {

                if(edSenha.getText() != edConfirmaSenha.getText()){
                    edConfirmaSenha.setError("Confirmação inválida");
                }
            }
        });

        edEmail.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View view, boolean b) {

                String emailInput = edEmail.getText().toString().trim();
                String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

                if(!emailInput.matches(emailPattern)) {
                    edEmail.setError("Email inválido");
                }
            }
        });


    }

    public void clickConfirmar(View v){

        Toast.makeText(ClienteActivity.this, "Dados:" + edNome.getText() + edEmail.getText() + edSenha.getText() + edConfirmaSenha.getText(), Toast.LENGTH_SHORT).show();


    }
}
