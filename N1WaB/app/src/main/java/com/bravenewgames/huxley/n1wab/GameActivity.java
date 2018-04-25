package com.bravenewgames.huxley.n1wab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameActivity extends Activity {

    Canvas canvas;
    SnakeView snakeView;
    Typeface tf;
    Player player;
    Bitmap enemyBitmap;
    Bitmap level;
    //BeatMap map;

    CopyOnWriteArrayList<Enemy> enemyList;
    CopyOnWriteArrayList<Gib> gibList;

    float frameTime = 33.3f;

    private SoundPool soundPool;
    int sample1 = -1;
    int sample2 = -1;
    int sample3 = -1;
    int sample4 = -1;

    int screenWidth;
    int screenHeight;
    int topGap;

    long lastFrameTime;
    int fps;
    int combo;
    public static int score;

    int blockSize;
    int numBlocksWide;
    int numBlocksHigh;

    boolean spawn;
    boolean playerSelect;

    Intent gameover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameover = new Intent(this, GameOverActivity.class);
        score = 0;

        tf = Typeface.createFromAsset(getAssets(),"fonts/press_start_2p.ttf");
        loadSound();
        configureDisplay();
        snakeView = new SnakeView(this);
        setContentView(snakeView);
    }

    @Override
    protected void onStop() {
        super.onStop();
        while (true) {
            snakeView.pause();
            break;
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        snakeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        snakeView.pause();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            snakeView.pause();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        return false;
    }

    public void loadSound() {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("sample1.ogg");
            sample1 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sample2.ogg");
            sample2 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sample3.ogg");
            sample3 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sample4.ogg");
            sample4 = soundPool.load(descriptor, 0);

        } catch (IOException e) {
            Log.e("error", "failed to load sound files");
        }
    }



    public void configureDisplay() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        topGap = screenHeight/14;

        blockSize = screenWidth/40;

        numBlocksWide = 40;
        numBlocksHigh = ((screenHeight - topGap)) / blockSize;


    }

    class SnakeView extends SurfaceView implements Runnable {
        Thread ourThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playingSnake;
        Paint paint;

        public SnakeView(Context context) {
            super(context);
            ourHolder = getHolder();
            paint = new Paint();
            loadEntities();
        }

        @Override
        public  void run() {
            while (playingSnake) {
                updateGame();
                drawGame();
                controlFPS();
            }
        }

        public void loadEntities() {
            Bitmap playerBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.playersprite);

            player = new Player(playerBitmap, screenWidth/2, screenHeight/2);

            enemyBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.enemysprite);
            level = BitmapFactory.decodeResource(getResources(),R.drawable.level);
            //map = new BeatMap(enemyBitmap);
            //loadBeatmap(map);
            enemyList = new CopyOnWriteArrayList<Enemy>();
            gibList = new CopyOnWriteArrayList<Gib>();
            randomSpawn();
            //map.start();
        }

        public void loadBeatmap(BeatMap map)
        {
            Vec2 pos = new Vec2(150, 50);
            Vec2 move = new Vec2(400, 400);
            long ht = 3000;
            long st = 1000;
            map.addSpawn(pos,move,ht,st);
        }

        public void randomSpawn()
        {
            if (enemyList.isEmpty()) {
                Random rand = new Random();
                int num = rand.nextInt(3) + 2;
                for (int i = 0; i < num; i++) {
                    float x = rand.nextFloat() * (float) (screenWidth - 100) + 50.0f;
                    long ht = (long) 2000 + i*1000 + System.currentTimeMillis();
                    Enemy e = new Enemy(enemyBitmap, x, screenHeight - 1, ht);
                    x = rand.nextFloat() * (float) (screenWidth - 100) + 50.0f;
                    float y = rand.nextFloat() * (float) (screenHeight*2/3) + screenHeight/3;
                    e.setMoveTarget(x, y, true);
                    enemyList.add(e);
                }
            }
        }

        public void updateGame() {

            for (Enemy e : enemyList)
            {
                e.update(frameTime/1000, player);
                if (e.isExpired())
                {
                    if (player.TakeDamage(e.getDamage()))
                    {
                        startActivity(gameover);
                    }
                    combo = 0;
                    enemyList.remove(e);
                }
            }
            for (Gib g : gibList)
            {
                g.update(frameTime/1000);
                if (g.isExpired())
                {
                    gibList.remove(g);
                }
            }
            player.update(frameTime/1000);
            randomSpawn();
        }

        public void drawGame() {

            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
                Paint paint = new Paint();
                canvas.drawColor(Color.BLACK);
                paint.setStrokeWidth(8);

                paint.setTextSize(topGap/2);
                paint.setTypeface(tf);
                paint.setTextSize(32);
                paint.setColor(Color.argb(255,255,255,255));
                canvas.drawBitmap(level, 0, 0, paint);
                canvas.drawText("Health: " + player.hp + " Score: " + score + " Combo: " + combo, 40, topGap-10, paint);

                for (Enemy e : enemyList)
                {
                    e.draw(canvas, paint);
                }
                for (Gib g : gibList)
                {
                    g.draw(canvas,paint);
                }
                player.draw(canvas, paint);

                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void controlFPS() {
            long timeThisFrame = (System.currentTimeMillis() - lastFrameTime);
            long timeToSleep = (long) frameTime - timeThisFrame;
            if (timeThisFrame > 0) {
                fps = (int)(1000/timeThisFrame);
            }
            if (timeToSleep > 0) {
                try {
                    ourThread.sleep(timeToSleep);
                } catch (InterruptedException e) {
                }
            }
            lastFrameTime = System.currentTimeMillis();
        }

        public void pause() {
            playingSnake = false;
            try {
                ourThread.join();
            } catch (InterruptedException e) {

            }
        }

        public void resume() {
            playingSnake = true;
            ourThread = new Thread(this);
            ourThread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if (player.checkHit(motionEvent.getX(), motionEvent.getY()))
                    {
                        playerSelect = true;
                        Log.d("hit","player select");
                    }
                    else
                    {
                        for (Enemy e : enemyList)
                        {
                            if (e.checkHit(motionEvent.getX(), motionEvent.getY()))
                            {
                                if (!e.isExpired()) {
                                    player.attack(e.getPosition());
                                    if (!e.Shielded()) {
                                        if (e.TakeDamage(player.getDamage())) {
                                            Gib g = new Gib(e.sprite, e.getPosition().x, e.getPosition().y);
                                            combo++;
                                            score += 10 + 10 * (combo / 10);
                                            player.heal(5);
                                            gibList.add(g);
                                            enemyList.remove(e);
                                        }
                                        Log.d("hit", "enemy hit");
                                    } else {
                                        if (player.TakeDamage(e.getDamage())) {
                                            startActivity(gameover);
                                        }
                                        combo = 0;
                                        Log.d("hit", "shield hit");
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (playerSelect)
                    {
                        boolean hit = false;
                        for (Enemy e : enemyList)
                        {
                            hit = e.checkHit(motionEvent.getX(), motionEvent.getY());
                            if (hit && !e.isExpired())
                            {
                                playerSelect = false;
                                player.setMoveTarget(0,0,false);
                                player.dashAttack(e.getPosition());
                                if (!e.Shielded()) {
                                    if (e.TakeDamage(player.getDamage())) {
                                        Gib g = new Gib(e.sprite, e.getPosition().x, e.getPosition().y);
                                        combo++;
                                        score += 10 + 10*(combo/10);
                                        player.heal(5);
                                        gibList.add(g);
                                        enemyList.remove(e);
                                    }
                                    Log.d("hit", "enemy hit");
                                }
                                else {
                                    if (player.TakeDamage(e.getDamage()))
                                    {
                                        startActivity(gameover);
                                    }
                                    combo = 0;
                                    Log.d("hit", "shield hit");
                                }
                                break;
                            }
                        }
                        if (!hit) {
                            player.setMoveTarget(motionEvent.getX(), motionEvent.getY(), true);
                            Log.d("move", "player move");
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    playerSelect = false;
                    player.setMoveTarget(0,0,false);
                    Log.d("up","player up");
                    break;
                default:
                    break;
            }
            return true;
        }
    }
}

