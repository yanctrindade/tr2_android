package com.example.android.tr2_android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
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

    private ImageView foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        final Button tirar_foto = (Button) findViewById(R.id.tirar_foto);
        foto = (ImageView) findViewById(R.id.foto);

        tirar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*File fileFoto = moduloCamera.tirarFoto();
                if (fileFoto == null) {
                    Log.e("Erro", "Deu null;");
                }*/
                new TiraFoto().execute();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        moduloCamera.releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        moduloCamera = new ModuloCamera(this);
    }

    private class TiraFoto extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            File fileFoto = moduloCamera.tirarFoto();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            fileFoto = moduloCamera.ultimoArquivo;
            Bitmap myBitmap = BitmapFactory.decodeFile(fileFoto.getAbsolutePath());
            foto.setImageBitmap(myBitmap);
            Log.i("Certo", "É nois!");
        }
    }
}
