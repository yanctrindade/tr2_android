package com.example.android.tr2_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by eduar on 15/12/2016.
 */

public class SplashActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        startAnimations();
    }
    private void startAnimations() {
        Animation animl = AnimationUtils.loadAnimation(this, R.anim.alpha);
        animl.reset();
        LinearLayout l = (LinearLayout) findViewById(R.id.layoutanimation);
        l.clearAnimation();
        l.startAnimation(animl);

        Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.abc_fade_in);
        anim1.reset();
        ImageView iv = (ImageView) findViewById(R.id.letters);
        iv.clearAnimation();
        iv.startAnimation(anim1);

        Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        anim2.reset();
        iv.startAnimation(anim2);

        Animation anim3 = AnimationUtils.loadAnimation(this, R.anim.abc_fade_in);
        anim3.reset();
        ImageView iv1 = (ImageView) findViewById(R.id.phrase);
        iv1.clearAnimation();
        iv1.startAnimation(anim3);

        final Animation anim4 = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        iv1.startAnimation(anim4);

        Thread tAnim = new Thread(){
            @Override
            public void run() {
                try {
                    while (!anim4.hasEnded());
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    Intent intent = new Intent(SplashActivity.this, TelaLogin.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }
            }
        };
        tAnim.start();
    }
}
