package com.example.android.tr2_android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.android.tr2_android.Camera.ModuloCamera;
import com.example.android.tr2_android.Camera.Preview;

import java.io.File;
import java.net.URI;

public class TelaPrincipal extends AppCompatActivity {

    private Preview mPreview;
    private FrameLayout preview;
    private ModuloCamera moduloCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        preview = (FrameLayout) findViewById(R.id.camera_preview);
        mPreview = new Preview(this);
        preview.addView(mPreview);
        final Button tirar_foto = (Button) findViewById(R.id.tirar_foto);
        final ImageView foto = (ImageView) findViewById(R.id.foto);

        moduloCamera = new ModuloCamera(this,mPreview);

        tirar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Tira a foto
                File fileFoto = moduloCamera.tirarFoto();
                Bitmap myBitmap = BitmapFactory.decodeFile(fileFoto.getAbsolutePath());
                foto.setImageBitmap(myBitmap);
            }
        });
    }
}
