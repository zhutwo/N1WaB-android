package com.bravenewgames.huxley.n1wab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

public class GameOverActivity extends Activity {

    Intent menu;
    TextView scoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        menu = new Intent(this, MainActivity.class);
        scoreText = findViewById(R.id.scoreText);
        scoreText.setText("Score: "+GameActivity.score);
    }

    @Override
    public boolean onTouchEvent (MotionEvent motionEvent) {
        startActivity(menu);
        return true;
    }
}
