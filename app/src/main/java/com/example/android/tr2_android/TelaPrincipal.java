package com.example.android.tr2_android;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        tirar_foto = (Button) findViewById(R.id.tirar_foto);
        gravar_video = (Button) findViewById(R.id.gravar_video);

        tirar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moduloCamera.tirarFoto(new ModuloCamera.FotoCallBack() {
                    @Override
                    public void fotoCallBack(File foto) {
                        Log.i("CallBack","Foto: "+foto);
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(foto));
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
                        int resposta = upLoad2Server(video);
                        Log.d("Resposta do video", ""+resposta);
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

    public int upLoad2Server(File sourceFile) {
        String upLoadServerUri = UPLOAD_URL;
        String fileName = sourceFile.getName();

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        int serverResponseCode = 0;

        /*if (!sourceFile.isFile()) {
            Log.e("Huzza", "Source File Does not exist");
            return 0;
        }*/
        try { // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(upLoadServerUri);
            conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);
            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
            Log.i("Huzza", "Initial .available : " + bytesAvailable);

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i("Upload file to server", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
            // close streams
            Log.i("Upload file to server", fileName + " File is written");
            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
//this block will give the response of upload link
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                Log.i("Huzza", "RES Message: " + line);
            }
            rd.close();
        } catch (IOException ioex) {
            Log.e("Huzza", "error: " + ioex.getMessage(), ioex);
        }
        return serverResponseCode;  // like 200 (Ok)

    } // end upLoad2Server
}
