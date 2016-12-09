package com.example.android.tr2_android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private boolean isRecording = false;
    private Button gravar_video;

    private ImageView foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        final Button tirar_foto = (Button) findViewById(R.id.tirar_foto);
        gravar_video = (Button) findViewById(R.id.gravar_video);
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
        gravar_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GravaVideo().execute();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        moduloCamera.releaseCamera();
        moduloCamera.releaseMediaRecorder();
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

    private class GravaVideo extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(moduloCamera.prepareVideoRecorder()){
                moduloCamera.startMediaRecorder();
                gravar_video.setText("Gravando...");
            } else {
                // prepare didn't work, release the camera
                moduloCamera.releaseMediaRecorder();
                // inform user
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            moduloCamera.stopMediaRecordig();  // stop the recording
            moduloCamera.releaseMediaRecorder(); // release the MediaRecorder object

            // inform the user that recording has stopped
            gravar_video.setText("Gravar");
        }
    }
}
