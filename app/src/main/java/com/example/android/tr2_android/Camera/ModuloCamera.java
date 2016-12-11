package com.example.android.tr2_android.Camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * Created by Túlio on 22/11/2016.
 */

public class ModuloCamera {

    private static final String TAG = "CamTestActivity";
    private Camera camera;
    private Activity act;
    private Context ctx;
    private MediaRecorder mediaRecorder;
    private Preview preview;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    public File ultimoArquivo = null;

    private FotoCallBack fotoCallBack;
    private VideoCallBack videoCallBack;

    private int tamanhoVideo;

    public ModuloCamera(Context context){
        ctx = context;
        act = (Activity) context;
        configurarCamera();

        FrameLayout frameLayout = (FrameLayout) act.findViewById(R.id.camera_preview);
        preview = new Preview(ctx, camera);
        frameLayout.addView(preview);
        //preview.setKeepScreenOn(true);

    }

    public void tirarFoto(FotoCallBack fotoCallBack){
        this.fotoCallBack = fotoCallBack;
        ultimoArquivo = null;
        camera.takePicture(shutterCallback, rawCallback, null, mPicture);
    }

    public void gravarVideo(int tamanhoVideo, VideoCallBack videoCallBack){
        this.videoCallBack = videoCallBack;
        this.tamanhoVideo = tamanhoVideo;
        new GravaVideo().execute();
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

    public void releaseCamera() {
        if(camera != null) {
            camera.stopPreview();
            //preview.setCamera(null);
            camera.release();
            camera = null;
        }
    }

    public void releaseMediaRecorder(){
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            camera.lock();           // lock camera for later use
        }
    }

    public void startMediaRecorder(){
        mediaRecorder.start();
    }

    public void stopMediaRecordig(){
        mediaRecorder.stop();
        getvideo();
    }

    private void getvideo(){
        videoCallBack.videoCallBack(getOutputMediaFile(MEDIA_TYPE_VIDEO));
    }

    private void resetCam() {
        camera.startPreview();
        //preview.setCamera(camera);
    }


    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            //			 Log.d(TAG, "onShutter'd");
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            //			 Log.d(TAG, "onPictureTaken - raw");
        }
    };


    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

            Log.d("JPEG", "Entrei");

            ultimoArquivo = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (ultimoArquivo == null) {
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(ultimoArquivo);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }

            fotoCallBack.fotoCallBack(ultimoArquivo);

            resetCam();
        }
    };


    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public boolean prepareVideoRecorder(){

        mediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        camera.unlock();
        mediaRecorder.setCamera(camera);

        // Step 2: Set sources
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        mediaRecorder.setPreviewDisplay(preview.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    public interface FotoCallBack {
        void fotoCallBack(File foto);
    }

    public interface VideoCallBack {
        void videoCallBack(File video);
    }

    private class GravaVideo extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(prepareVideoRecorder()){
                startMediaRecorder();
                //gravar_video.setText("Gravando...");
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                // inform user
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(tamanhoVideo);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            stopMediaRecordig();  // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object

            // inform the user that recording has stopped
            //gravar_video.setText("Gravar");
        }
    }
}
