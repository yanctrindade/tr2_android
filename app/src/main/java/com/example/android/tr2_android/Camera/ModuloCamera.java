package com.example.android.tr2_android.Camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Túlio on 22/11/2016.
 */

public class ModuloCamera {
    private static final String TAG = "CamTestActivity";
    Preview preview;
    Button buttonClick;
    Camera camera;
    Context ctx;
    File ultimoArquivo = null;

    public ModuloCamera(Context context, Preview preview){
        this.ctx = context;
        this.preview = preview;
        //preview.setLayoutParams(new ViewGroup.LayoutParams(1, 1));

        preview.setKeepScreenOn(true);

        configurarCamera();
    }

    public File tirarFoto(){
        try {
            camera.takePicture(null, null, null, jpegCallback);
            ultimoArquivo.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  ultimoArquivo;
    }

    protected void configurarCamera() {
        int numCams = Camera.getNumberOfCameras();
        if(numCams > 0){
            try{
                camera = getCameraInstance();
                Log.i("Open","camera"+camera);
                camera.startPreview();
                Log.i("Open","startpreview"+camera);
                preview.setCamera(camera);
                Log.i("Open","preview"+preview);
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
            preview.setCamera(null);
            camera.release();
            camera = null;
        }
    }

    private void resetCam() {
        camera.startPreview();
        preview.setCamera(camera);
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        final Activity activity = (Activity) ctx;
        activity.sendBroadcast(mediaScanIntent);
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

                ultimoArquivo.notify();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }

    }

    private class TimeoutOperation extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {

            try {
                Log.i(TAG, "Going to sleep");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "This is executed after 10 seconds and runs on the main thread");
            super.onPostExecute(result);
        }
    }
}
