package pl.nazwa.phpiotr.milosz;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

public class MiloszActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private final int COUNT = 10;
    private final String TAG = String.format("pl.nazwa.phpiotr.milosz.%s", this.getClass().getSimpleName());
    private Thread threadInClick = null;
    private SeekBar seekbars[] = new SeekBar[COUNT];
    private MediaPlayer players[] = new MediaPlayer[COUNT];
    private Handler clickHandler = null;
    private int currentButtonIndex = -1;
    private int currentSeekBarIndex = -1;
    private int playerProgress = -1;

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

        int i = 0;
        for (int id : ids) {
            ImageButton imageButton = (ImageButton) findViewById(id);
            if (imageButton != null) {
                seekbars[i] = (SeekBar) findViewById(seekIds[i]);
                seekbars[i].setOnSeekBarChangeListener(this);
                imageButton.setOnClickListener(this);
            }
            i++;
        }

    }

    @Override
    public void onClick(View v) {

        if (currentButtonIndex != -1 && ids[currentButtonIndex] == v.getId()) {
            if (players[currentButtonIndex] != null) {
                if (players[currentButtonIndex].isPlaying()) {
                    players[currentButtonIndex].pause();
                    return;
                }
            }
        }

        if (threadInClick != null) {
            if (!threadInClick.isInterrupted()) {
                threadInClick.interrupt();
            }
            threadInClick = null;
        }

        for (int i = 0; i < ids.length; i++) {
            if (players[i] != null) {
                players[i].stop();
                players[i].reset();
                players[i].release();
                players[i] = null;
            }
            if (ids[i] == v.getId()) {
                currentButtonIndex = i;
            }
        }

        if (currentButtonIndex == -1) {
            return;
        }

        players[currentButtonIndex] = MediaPlayer.create(MiloszActivity.this, mp3toPng[currentButtonIndex]);
        players[currentButtonIndex].setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekbars[currentButtonIndex].setMax(players[currentButtonIndex].getDuration());
                int seekTo =
                        seekbars[currentButtonIndex].getProgress() != players[currentButtonIndex].getDuration()
                                && !players[currentButtonIndex].isPlaying()
                                ? seekbars[currentButtonIndex].getProgress()
                                : 0;
                mp.seekTo(seekTo);
                mp.start();
                clickHandler = new Handler();

                threadInClick = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (players[currentButtonIndex] != null && players[currentButtonIndex].isPlaying()) {
                            seekbars[currentButtonIndex].setProgress(players[currentButtonIndex].getCurrentPosition());
                            clickHandler.postDelayed(this, 50);
                        }
                    }
                });

                threadInClick.start();
            }
        });
        players[currentButtonIndex].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                seekbars[currentButtonIndex].setProgress(0);
            }
        });

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        playerProgress = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        if (threadInClick != null && !threadInClick.isInterrupted()) {
            threadInClick.interrupt();
        }

        for (int i = 0; i < seekIds.length; i++) {
            if (seekIds[i] != seekBar.getId()) {
                continue;
            }
            currentSeekBarIndex = i;
        }

        if (currentSeekBarIndex == -1) {
            return;
        }

        if (players[currentSeekBarIndex] == null) {
            players[currentSeekBarIndex] = MediaPlayer.create(MiloszActivity.this, mp3toPng[currentSeekBarIndex]);
            seekbars[currentSeekBarIndex].setMax(players[currentSeekBarIndex].getDuration());
            return;
        }

        if (players[currentSeekBarIndex].isPlaying()) {
            players[currentSeekBarIndex].pause();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        if (playerProgress == -1) {
            return;
        }

        if (players[currentSeekBarIndex] != null) {
            players[currentSeekBarIndex].seekTo(playerProgress);
            players[currentSeekBarIndex] = null;
        }

        seekbars[currentSeekBarIndex].setProgress(playerProgress);

        playerProgress = -1;
    }
}
