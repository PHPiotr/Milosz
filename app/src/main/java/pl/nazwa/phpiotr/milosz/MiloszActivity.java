package pl.nazwa.phpiotr.milosz;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

public class MiloszActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Thread threadInClick = null;
    private MediaPlayer mediaPlayerInClick = null;
    private SeekBar seekBar = null;
    private Handler handler = null;

    private int[] mp3toPng = {
            R.raw.aga_milosz,
            R.raw.deszcz_milosz,
            R.raw.dwa,
            R.raw.jak_robi_kotek,
            R.raw.gdzie_jest_mucha,
            R.raw.jak_robi_piesek,
            R.raw.niebo_milosz,
            R.raw.raz,
            R.raw.trzy,
            R.raw.zegar_na_wiezy_koscielnej,
    };

    private int[] ids = {
            R.id.aga_milosz,
            R.id.deszcz_milosz,
            R.id.dwa,
            R.id.jak_robi_kotek,
            R.id.jak_robi_mucha,
            R.id.jak_robi_piesek,
            R.id.niebo_milosz,
            R.id.raz,
            R.id.trzy,
            R.id.zegar_na_wiezy_koscielnej,
    };

    private int[] seekIds = {
            R.id.aga_milosz_seek,
            R.id.deszcz_milosz_seek,
            R.id.dwa_seek,
            R.id.jak_robi_kotek_seek,
            R.id.jak_robi_mucha_seek,
            R.id.jak_robi_piesek_seek,
            R.id.niebo_milosz_seek,
            R.id.raz_seek,
            R.id.trzy_seek,
            R.id.zegar_na_wiezy_koscielnej_seek,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milosz);

        for (int id : ids) {
            ImageButton imageButton = (ImageButton) findViewById(id);
            if (imageButton != null) {
                imageButton.setOnClickListener(this);
            }
        }

    }

    @Override
    public void onClick(View v) {

        if (threadInClick != null) {
            threadInClick.interrupt();
            threadInClick = null;
        }

        if (mediaPlayerInClick != null && mediaPlayerInClick.isPlaying()) {
            mediaPlayerInClick.stop();
            mediaPlayerInClick.reset();
            mediaPlayerInClick.release();
            mediaPlayerInClick = null;
        }

        for (int i = 0; i < ids.length; i++) {

            if (ids[i] == v.getId()) {

                final int soundId = mp3toPng[i];
                final int seekId = seekIds[i];
                mediaPlayerInClick = MediaPlayer.create(MiloszActivity.this, soundId);
                seekBar = (SeekBar) findViewById(seekId);
                if (seekBar != null) {
                    seekBar.setMax(mediaPlayerInClick.getDuration());
                }
                mediaPlayerInClick.start();

                handler = new Handler();

                threadInClick = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayerInClick.isPlaying()) {
                            seekBar.setProgress(mediaPlayerInClick.getCurrentPosition());
                            handler.postDelayed(this, 50);
                        }
                    }
                });

                threadInClick.start();

                break;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
