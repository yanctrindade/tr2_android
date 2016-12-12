//https://code.tutsplus.com/tutorials/streaming-video-in-android-apps--cms-19888
//libraries you can use to stream media files, such as remote videos
//we will stream a video file
//displaying it using the VideoView component together with a MediaController object to let the user control playback.
//We will also briefly run through the process of presenting the video using the MediaPlayer class

package com.example.gabriel.streamingvideoinandroidapps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.net.Uri;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VideoView vidView = (VideoView)findViewById(R.id.myVideo);

        //String vidAddress = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
        String vidAddress = "http://silver472.com.br/video1.mp4";
        Uri vidUri = Uri.parse(vidAddress);
        //We parse the address string as a URI so that we can pass it to the VideoView object:
        vidView.setVideoURI(vidUri);

        //We've implemented video playback, but the user will expect and be accustomed to having
        //control over it. Again, the Android platform provides resources for handling this using familiar
        //interaction via the MediaController class.
        MediaController vidControl = new MediaController(this);

        //Next, set it to use the VideoView instance as its anchor
        vidControl.setAnchorView(vidView);

        //And finally, set it as the media controller for the VideoView object:
        vidView.setMediaController(vidControl);

        //Now you can simply start playback
        vidView.start();
    }
}
