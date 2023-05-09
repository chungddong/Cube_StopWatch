package com.sophra.cube_stopwatch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


public class MainActivity extends AppCompatActivity {

    LinearLayout mainbackground; // 배경색 변경 위한 메인 리니어 레이아웃

    Boolean isrec;  // 기록시작했는지 확인하는 거
    Boolean isfinish; // 기록 끝났는지 확인하는 거

    TextView sec, ms; // 초, 밀리초

    Button btn_reset;  // 초기화버튼
    Button btn_rec;  // 기록확인 버튼

    private InterstitialAd iniad;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainbackground = findViewById(R.id.mainbackground);
        sec = findViewById(R.id.sec);
        ms = findViewById(R.id.ms);

        btn_reset = findViewById(R.id.btn_reset);
        btn_rec = findViewById(R.id.btn_rec);



        handler = new Handler() ;


        isrec = false;
        isfinish = false;


        AdRequest adRequest = new AdRequest.Builder().build();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });


        InterstitialAd.load(this,"ca-app-pub-9067824891811161/3908889029", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        iniad = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        iniad = null;
                    }
                });


        /*iniad.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdClicked() {
            }

            @Override
            public void onAdDismissedFullScreenContent() {
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                iniad = null;
            }

            @Override
            public void onAdImpression() {
            }

            @Override
            public void onAdShowedFullScreenContent() {
            }
        });*/




        btn_reset.setOnClickListener(new View.OnClickListener() {   //초기화 버튼 눌렀을 때
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("Reset"); //제목
                dlg.setMessage("RESET THE RECORD");

                dlg.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  // ------ 취소버튼
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                dlg.setPositiveButton("ACCEPT",new DialogInterface.OnClickListener(){  //  ------- 확인버튼  /  초기화 시키키
                    public void onClick(DialogInterface dialog, int which) {
                        //토스트 메시지
                        if (iniad != null) {
                            iniad.show(getParent());
                        } else {
                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                        }

                        InterstitialAd.load(getApplicationContext(),"ca-app-pub-9067824891811161/3908889029", adRequest,
                                new InterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                        // The mInterstitialAd reference will be null until
                                        // an ad is loaded.
                                        iniad = interstitialAd;
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        // Handle the error
                                        iniad = null;
                                    }
                                });

                        mainbackground.setBackgroundColor(Color.parseColor("#BA7C30"));  //배경색 변경 - 갈색

                        sec.setText("00");
                        ms.setText("000");
                        isrec = false;
                    }
                });
                dlg.show();

            }
        });


        btn_rec.setOnClickListener(new View.OnClickListener() {  // 기록버튼 눌렀을 때
            @Override
            public void onClick(View view) {

                btn_rec.getParent().getParent().requestDisallowInterceptTouchEvent(true);

                Intent intent = new Intent(getApplicationContext(), RecordsActivity.class);
                startActivity(intent);


            }
        });


    }

    boolean isno;

    public boolean dispatchTouchEvent(MotionEvent event) {   //화면 터치 관련
        int userAction = event.getAction();

        switch (userAction) {
            case MotionEvent.ACTION_DOWN:
                //Toast.makeText(getApplicationContext(), "" + view.getId(), Toast.LENGTH_SHORT).show();
                Log.v("watch_verse", "화면 누름");

                if(isrec == true)
                {
                    mainbackground.setBackgroundColor(Color.parseColor("#BA4530"));  //배경색 변경 - 빨간색

                    handler.removeCallbacks(runnable); //정지

                }
                break;

            case MotionEvent.ACTION_UP:
                //Toast.makeText(getApplicationContext(), "화면에서 손땜", Toast.LENGTH_SHORT).show();
                Log.v("watch_verse", "화면에서 손 땜");

                if(isrec != true)
                {
                    mainbackground.setBackgroundColor(Color.parseColor("#30BA32"));  //배경색 변경 - 초록색
                    isrec = true;

                    StartTime = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);

                }
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;  // 이 아래로는 스톱워치 굴리는 거
    Handler handler;
    int Seconds, Minutes, MilliSeconds ;

    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000); //초

            //Minutes = Seconds / 60;  //분

            Seconds = Seconds % 60;  //초

            MilliSeconds = (int) (UpdateTime % 1000);

            /*timer.setText("" + Minutes + ":"
                    + String.format("%02d", Seconds) + ":"
                    + String.format("%03d", MilliSeconds));*/

            sec.setText(String.format("%02d", Seconds));
            ms.setText(String.format("%03d", MilliSeconds));

            handler.postDelayed(this, 0);
        }

    };
}