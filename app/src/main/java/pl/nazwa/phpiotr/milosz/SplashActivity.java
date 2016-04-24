package pl.nazwa.phpiotr.milosz;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.util.Random;

public class SplashActivity extends AppCompatActivity {

    MediaPlayer splashSound;
    Thread thread;
    long splashDuration = 0;
    int splashIndex = 0;

    private int[] mp3 = {
            R.raw.aga_milosz,
            R.raw.deszcz_milosz,
            R.raw.dwa,
            R.raw.dym_z_komina,
            R.raw.gdzie_jest_mucha,
            R.raw.gdzie_jest_niebo,
            R.raw.halo,
            R.raw.halo_milosz,
            R.raw.hau,
            R.raw.hau_milosz,
            R.raw.hau_po_deszczu_milosz,
            R.raw.jak_robi_kotek,
            R.raw.jak_robi_piesek,
            R.raw.miau,
            R.raw.niebo_milosz,
            R.raw.piesek_robi_hau,
            R.raw.raz,
            R.raw.raz_milosz,
            R.raw.tam_jest_deszcz,
            R.raw.trzy,
            R.raw.zegar_na_wiezy_koscielnej,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Random ran = new Random();
        splashIndex = ran.nextInt(mp3.length);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                splashSound = MediaPlayer.create(SplashActivity.this, mp3[splashIndex]);
                splashDuration = splashSound.getDuration();
                splashSound.start();
            }
        });

        thread.start();

        ImageView animated = (ImageView) findViewById(R.id.animatedSplash);
        animated.setBackgroundResource(R.drawable.animated);
        AnimationDrawable animationDrawable = (AnimationDrawable) animated.getBackground();
        animationDrawable.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MiloszActivity.class);
                SplashActivity.this.startActivity(intent);
                splashSound.stop();
                splashSound.reset();
                splashSound.release();
                splashSound = null;
                thread.interrupt();
                SplashActivity.this.finish();
            }
        }, splashDuration);
    }

}
