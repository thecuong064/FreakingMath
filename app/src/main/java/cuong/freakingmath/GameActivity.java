package cuong.freakingmath;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    static final String[] backGrColor= {"#9C27B0", "#6A1B9A", "#3949AB", "#512DA8", "#1565C0", "#FFB300", "#9E9D24",
                                        "#64DD17", "#00E676", "#FFA000", "#5D4037", "#455A64"};
    RelativeLayout layout;
    TextView formulaText, resText, topScoreText, curScoreText, cScore;
    int a, b, res, maxx, minn, keyRes, currentScore;
    ImageView trueChoice, falseChoice;
    Timer timer;
    TimerTask timerTask;
    Random random;
    ProgressBar proBar;
    CountDownTimer countDownTimer;
    MediaPlayer rightSound;
    MediaPlayer wrongSound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        random = new Random();
        //find View
        layout = findViewById(R.id.layout_rela);
        formulaText = findViewById(R.id.formulaView);
        resText = findViewById(R.id.resView);
        trueChoice = findViewById(R.id.trueBtn);
        falseChoice = findViewById(R.id.falseBtn);
        curScoreText = findViewById(R.id.cScoreView);
        cScore = findViewById(R.id.cScore);
        proBar = findViewById(R.id.progress);

        //set random background color
        int randomColor = random.nextInt(12);
        layout.setBackgroundColor(Color.parseColor(backGrColor[randomColor]));

        //set custom font
        Typeface font = Typeface.createFromAsset(getAssets(),"UVNBaiSau_N.TTF");
        formulaText.setTypeface(font);
        resText.setTypeface(font);
        Typeface t = Typeface.createFromAsset(getAssets(),"NewAthleticM54.ttf");
        curScoreText.setTypeface(t);
        cScore.setTypeface(t);       //SCORE

        cScore.setText("SCORE");    cScore.setTextColor(getResources().getColor(R.color.textColor));
        curScoreText.setText("0");  curScoreText.setTextColor(getResources().getColor(R.color.textColor));

        currentScore = 0;

        //user input
        trueChoice.setOnClickListener(this);
        falseChoice.setOnClickListener(this);

        //create sound for button click
        rightSound = MediaPlayer.create(this,R.raw.rightbell);
        wrongSound = MediaPlayer.create(this,R.raw.wrongbell);

        gameplay();
    }

    private void gameplay() {
        //for range
            minn = 1;
            maxx = 5;

        a = random.nextInt((maxx - minn) + 1) + minn;
        b = random.nextInt((maxx - minn) + 1) + minn;
        res = a + b;

        //display question
        formulaText.setText(a+" + "+b);
        formulaText.setTextColor(getResources().getColor(R.color.textColor));

        keyRes = random.nextInt(2);       //random for wrong or rightbell result
        switch (keyRes) {
            case 0:     //display wrong result
                int comp = random.nextInt(2);      //random for bigger or smaller result
                if (comp == 0) res -= random.nextInt( (2 - 1) + 1) + 1;    //if 0->smaller
                else res +=random.nextInt( (2 - 1) + 1) + 1;              //else bigger
                break;
            case 1:
                break;
            default:    //right result
                break;
        }
        if (res<0) res = 0;         //cannot have zero result
        resText.setText("= "+res); //display the result
        resText.setTextColor(getResources().getColor(R.color.textColor));


        //create timer task
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //show game over
                        gameOverDisplay();
                    }
                });
            }
        };
        startProgressbar();       //create progress bar for timer task
        timer.schedule(timerTask,1500);        //time each turn
    }

    private void startProgressbar() {
    proBar.setMax(1500);
    proBar.setProgress(1500);
        //call onTick every 1500ms
        countDownTimer = new CountDownTimer(1500,10) {
            @Override
            public void onTick(long time) {
               proBar.setProgress(((int)time));
            }

            @Override
            public void onFinish() {
               proBar.setProgress(0);
            }
        };
        countDownTimer.start();
    }

    private void gameOverDisplay() {
        wrongSound.start();
        cancelTimer();
        Intent t = new Intent(this,GameOverActivity.class);
        t.putExtra("score",currentScore);
        startActivity(t);
        finish();
    }

    private void cancelTimer() {
        timerTask.cancel();
        timer.cancel();
        countDownTimer.cancel();
    }

    private void updateCurrentScore() {
        curScoreText.setText(""+currentScore);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.trueBtn:
                if (keyRes == 1) {
                    currentScore+=1;
                    playRightSound();   //play sound
                    updateCurrentScore();
                    cancelTimer();
                    gameplay();
                } else {
                    gameOverDisplay();  }
                break;
            case R.id.falseBtn:
                if (keyRes == 0) {
                    currentScore += 1;
                    playRightSound();  //play sound
                    updateCurrentScore();
                    cancelTimer();
                    gameplay();
                } else {
                    gameOverDisplay();  }
            default:
                break;
        }
    }

    private void playRightSound() {
        if (rightSound.isPlaying()) {
            rightSound.stop();
            rightSound.release();
            rightSound = MediaPlayer.create(this,R.raw.rightbell);
        }       //passed the last sound
        rightSound.start();       //start new sound
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }
}
