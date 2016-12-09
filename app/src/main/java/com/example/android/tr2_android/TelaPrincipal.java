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
    private File fileFoto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        final Button tirar_foto = (Button) findViewById(R.id.tirar_foto);
        final Button mostrar_imagem = (Button) findViewById(R.id.mostrar_imagem);
        final ImageView foto = (ImageView) findViewById(R.id.foto);

        moduloCamera = new ModuloCamera(this);

        tirar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File fileFoto = moduloCamera.tirarFoto();
                if (fileFoto == null) {
                    Log.e("Erro", "Deu null;");
                }
            }
        });

        mostrar_imagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileFoto = moduloCamera.ultimoArquivo;
                Bitmap myBitmap = BitmapFactory.decodeFile(fileFoto.getAbsolutePath());
                foto.setImageBitmap(myBitmap);
                Log.i("Certo", "É nois!");
            }
        });
    }
}
