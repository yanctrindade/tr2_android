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
    private Button gravar_video;

    private int tamanhoVideo = 3000; //milissegundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        final Button tirar_foto = (Button) findViewById(R.id.tirar_foto);
        gravar_video = (Button) findViewById(R.id.gravar_video);

        tirar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moduloCamera.tirarFoto(new ModuloCamera.FotoCallBack() {
                    @Override
                    public void fotoCallBack(File foto) {
                        Log.i("CallBack","Foto: "+foto);
                    }
                });
            }
        });

        gravar_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moduloCamera.gravarVideo(tamanhoVideo, new ModuloCamera.VideoCallBack() {
                    @Override
                    public void videoCallBack(File video) {
                        Log.i("CallBack","Video: "+video);
                    }
                });
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
}
