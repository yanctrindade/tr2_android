package com.example.android.tr2_android;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.tr2_android.Camera.ModuloCamera;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;

public class TelaPrincipal extends AppCompatActivity {

    private ModuloCamera moduloCamera;
    private Button gravar_video;
    private Button tirar_foto;

    private String UPLOAD_URL ="http://bspy.herokuapp.com/salvar";
    private String KEY_NAME = "name";  //strig
    private String KEY_FILE = "file";  //base64

    private int duracaoVideo = 3000; //milissegundos

    private Thread arduinoSocket = null;
    private ServerSocket ss = null;

    public static final int SERVERPORT = 12345;

    Handler updateConversationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        updateConversationHandler = new Handler();

        tirar_foto = (Button) findViewById(R.id.tirar_foto);
        gravar_video = (Button) findViewById(R.id.gravar_video);

        tirar_foto.bringToFront();
        gravar_video.bringToFront();

        tirar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moduloCamera.tirarFoto(new ModuloCamera.FotoCallBack() {
                    @Override
                    public void fotoCallBack(File foto) {
                        Log.i("CallBack","Foto: "+foto);
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(foto));
                            Matrix matrix = new Matrix();
                            int width = bitmap.getWidth();
                            int height = bitmap.getHeight();
                            matrix.setRotate(moduloCamera.getDegrees());
                            bitmap = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
                            uploadImage(bitmap, foto.getName());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        gravar_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moduloCamera.gravarVideo(duracaoVideo, new ModuloCamera.VideoCallBack() {
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
        moduloCamera.releaseMediaRecorder();
        moduloCamera.releaseCamera();
        arduinoSocket.interrupt();
        try {
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        moduloCamera = new ModuloCamera(this);
        arduinoSocket = new Thread(new ServerThread());
        arduinoSocket.start();
    }

    private void uploadImage(final Bitmap bitmap, final String nameFile)
    {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(TelaPrincipal.this, s , Toast.LENGTH_LONG).show();
                        // Log.d("log1", s);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(TelaPrincipal.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        // Log.d("log2", volleyError.getMessage());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                Log.d("base64", image);

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_NAME, nameFile);
                params.put(KEY_FILE, image);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    class ServerThread implements Runnable {
        CommunicationThread commThread;
        public void run() {
            Socket s = null;
            try {
                ss = new ServerSocket(SERVERPORT);
                Log.d("Socket", "Socket criado " + ss.getInetAddress() + " " + ss.getLocalPort() + " " + ss);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    s = ss.accept();
                    commThread = new CommunicationThread(s);
                    new Thread(commThread).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class CommunicationThread implements Runnable{

        private Socket clientSocket;
        private BufferedReader input;

        public CommunicationThread(Socket clientSocket){
            this.clientSocket = clientSocket;

            try {
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()){
                String read = null;
                try {
                    read = input.readLine();
                    updateConversationHandler.post(new updateUIThread(read));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        public void interrupt() {
            this.interrupt();
        }
    }

    private class updateUIThread implements Runnable{

        private String msg;

        public updateUIThread(String str){
            this.msg = str;
        }

        @Override
        public void run() {
            if(msg != null && msg.equalsIgnoreCase("foto")) {
                moduloCamera.tirarFoto(new ModuloCamera.FotoCallBack() {
                    @Override
                    public void fotoCallBack(File foto) {
                        Log.i("CallBack", "Foto: " + foto);
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(foto));
                            Matrix matrix = new Matrix();
                            int width = bitmap.getWidth();
                            int height = bitmap.getHeight();
                            matrix.setRotate(moduloCamera.getDegrees());
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                            uploadImage(bitmap, foto.getName());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
}