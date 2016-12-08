package com.example.android.tr2_android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.android.tr2_android.Camera.ModuloCamera;

import java.io.File;

public class TelaPrincipal extends AppCompatActivity {

    private ModuloCamera moduloCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        final Button tirar_foto = (Button) findViewById(R.id.tirar_foto);
        final ImageView foto = (ImageView) findViewById(R.id.foto);

        moduloCamera = new ModuloCamera(this);

        tirar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Tira a foto
                File fileFoto = moduloCamera.tirarFoto();
                while (true) {
                    try{
                        Bitmap myBitmap = BitmapFactory.decodeFile(fileFoto.getAbsolutePath());
                        foto.setImageBitmap(myBitmap);
                        break;
                    } catch(NullPointerException e) {
                        //Log.e("Erro", "Deu null;");
                    }
                }
                Log.i("Certo", "É nois!");
            }
        });
    }
}
