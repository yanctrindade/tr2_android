package com.example.android.tr2_android.Camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.android.tr2_android.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Túlio on 22/11/2016.
 */

public class ModuloCamera {

    private static final String TAG = "CamTestActivity";
    private Camera camera;
    private Activity act;
    private Context ctx;

    private File ultimoArquivo = null;

    public ModuloCamera(Context context){
        ctx = context;
        act = (Activity) context;
        configurarCamera();

        FrameLayout frameLayout = (FrameLayout) act.findViewById(R.id.camera_preview);
        Preview preview = new Preview(ctx, camera);
        frameLayout.addView(preview);
        //preview.setKeepScreenOn(true);

    }

    public File tirarFoto(){
        camera.takePicture(null, null, null, jpegCallback);
        /*while (loop == true) {

        }*/
        return  ultimoArquivo;
    }

    protected void configurarCamera() {
        int numCams = Camera.getNumberOfCameras();
        if(numCams > 0){
            try{
                camera = getCameraInstance();
                Log.i("Open","camera"+camera);
            } catch (RuntimeException ex){
                Toast.makeText(ctx, "A camera nao foi encontrada", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    protected void onPause() {
        if(camera != null) {
            camera.stopPreview();
            //preview.setCamera(null);
            camera.release();
            camera = null;
        }
    }

    private void resetCam() {
        camera.startPreview();
        //preview.setCamera(camera);
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        act.sendBroadcast(mediaScanIntent);
    }

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            new SaveImageTask().execute(data);
            resetCam();
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/camtest");
                dir.mkdirs();

                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());

                refreshGallery(outFile);

                ultimoArquivo = outFile;
                Log.i("Salvar", "Passou pelo JPEG");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
