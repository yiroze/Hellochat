package com.example.hellochat.Activity.Feed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hellochat.PlayState;
import com.example.hellochat.R;
import com.example.hellochat.Interface.TextPlayer;

import java.util.Locale;

import static android.content.ContentValues.TAG;

public class Activity_TTS extends Activity implements TextPlayer {
    String content, targetLang;
    private final Bundle params = new Bundle();
    private TextToSpeech tts;
    private PlayState playState = PlayState.STOP;
    private int standbyIndex = 0;
    private int lastPlayIndex = 0;
    TextView text;
    ImageView play, stop;
    private Spannable spannable;
    private final BackgroundColorSpan colorSpan = new BackgroundColorSpan(Color.YELLOW);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tts);
        Intent intent = getIntent();
        content = intent.getStringExtra("content");
        targetLang = intent.getStringExtra("targetLang");
        Log.d(TAG, "targetLang: "+targetLang);
        InitView();
        initTTS();
        text.setText(content);
        play.setOnClickListener(v -> {
            if (!playState.isPlaying()) {
                startPlay();
                play.setImageResource(R.drawable.pause);
            } else {
                pausePlay();
                play.setImageResource(R.drawable.play);
            }
        });
        stop.setOnClickListener(v -> {
            stopPlay();
        });

    }

    void InitView() {
        text = findViewById(R.id.text);
        play = findViewById(R.id.play);
        stop = findViewById(R.id.stop);
    }

    public void mOnClose(View view) {
    }

    private void initTTS() {
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, null);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int state) {
                if (state == TextToSpeech.SUCCESS) {
                    setLanguage(targetLang);
                } else {
//                    showState("TTS 객체 초기화 중 에러가 발생했습니다.");
                }
            }
        });
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
            }

            @Override
            public void onDone(String s) {
                clearAll();
            }
            @Override
            public void onError(String s) {
//                showState("재생 중 에러가 발생했습니다.");
            }
            @Override
            public void onRangeStart(String utteranceId, int start, int end, int frame) {
                changeHighlight(standbyIndex + start, standbyIndex + end);
                lastPlayIndex = start;
            }
        });
    }


    @Override
    public void startPlay() {
        Log.d(TAG, "playState.isStopping(): "+playState.isStopping());
        Log.d(TAG, "tts.isSpeaking() "+tts.isSpeaking());

        if (playState.isStopping() || !tts.isSpeaking()) {
            Log.d(TAG, "startPlay: 1");
            setContentFromEditText(content);
            startSpeak(content);
        } else if (playState.isWaiting()) {
            Log.d(TAG, "startPlay: 2");
            standbyIndex += lastPlayIndex;
            startSpeak(content.substring(standbyIndex));
        }
        playState = PlayState.PLAY;
    }

    @Override
    public void pausePlay() {
        if (playState.isPlaying()) {
            playState = PlayState.WAIT;
            tts.stop();
        }
    }

    @Override
    public void stopPlay() {
        tts.stop();
        clearAll();
    }

    private void setContentFromEditText(String content) {
        text.setText(content, TextView.BufferType.SPANNABLE);
        spannable = (SpannableString) text.getText();
    }

    private void startSpeak(final String text) {
        Log.d(TAG, "startSpeak: ");
        tts.speak(text, TextToSpeech.QUEUE_ADD, params, text);
    }

    private void changeHighlight(final int start, final int end) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spannable.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        });
    }

    private void clearAll() {
        playState = PlayState.STOP;
        standbyIndex = 0;
        lastPlayIndex = 0;
        play.setImageResource(R.drawable.play);
        if (spannable != null) {
            changeHighlight(0, 0); // remove highlight
        }
    }

    @Override
    protected void onPause() {
        if (playState.isPlaying()) {
            pausePlay();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (playState.isWaiting()) {
            startPlay();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        tts.stop();
        tts.shutdown();
        super.onDestroy();
    }

    public void setLanguage(String targetLang){
        switch(targetLang){
            case "ko" :
                tts.setLanguage(Locale.KOREAN);
                break;
            case "jp" :
                tts.setLanguage(Locale.JAPANESE);
                break;
            case "zh-cn" :
                tts.setLanguage(Locale.CHINA);
                break;
            case "zh-tw" :
                tts.setLanguage(Locale.TAIWAN);
                break;
            case "fr" :
                tts.setLanguage(Locale.FRENCH);
                break;
            case "de" :
                tts.setLanguage(Locale.GERMAN);
                break;
            case "it" :
                tts.setLanguage(Locale.ITALIAN);
                break;
            default:
                tts.setLanguage(Locale.ENGLISH);
                break;
        }
    }

}