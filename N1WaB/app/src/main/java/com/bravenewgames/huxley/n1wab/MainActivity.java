package com.bravenewgames.huxley.n1wab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class MainActivity extends Activity {

    WebView webView;
    Canvas canvas;
    Button playButton;
    Button helpButton;
    Typeface tf;

    int screenWidth;
    int screenHeight;

    Intent game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        tf = Typeface.createFromAsset(getAssets(),"fonts/press_start_2p.ttf");

        game = new Intent(this, GameActivity.class);

        webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/www/menu_bg.html");

        playButton = findViewById(R.id.playButton);
        PlayButtonListener playListener = new PlayButtonListener();
        playButton.setOnClickListener(playListener);
    }

    private class PlayButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            startActivity(game);
        }
    }
}