package com.bravenewgames.huxley.n1wab;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class Enemy extends Entity {

    int hp;
    int damage;
    boolean shield;
    boolean shoot;
    long hitWindow;
    long hitTime;
    long despawnTime;
    Vec2 toPlayer;

    public Enemy(Bitmap bm, float dx, float dy, long ht)
    {
        super(bm,dx,dy);
        hp = 10;
        damage = 10;
        shield = true;
        hitTime = ht;
        hitWindow = 150;
        numFrames = 8;
        despawnTime = ht + hitWindow;
    }

    public boolean TakeDamage(int dmg)
    {
        hp -= dmg;
        if (hp <= 0)
        {
            return true;
        }
        return false;
    }

    public int getDamage()
    {
        return damage;
    }

    public boolean Shielded()
    {
        return shield;
    }

    public long getHitTime()
    {
        return hitTime;
    }

    public void despawn()
    {
        expired = true;
    }

    public void update(float dt, Player player)
    {
        toPlayer = player.getPosition().Subtract(pos);
        theta = toPlayer.Angle();
        move(dt);
        if (hitTime > System.currentTimeMillis() - hitWindow && hitTime < System.currentTimeMillis() + hitWindow)
        {
            toMove = false;
            shield = false;
            aiming = true;
        }
        else
        {
            shield = true;
            aiming = false;
        }
        if (despawnTime <= System.currentTimeMillis())
        {
            despawn();
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint)
    {
        if (theta > -Math.PI/2 && theta < Math.PI/2)
        {
            frameX = 2;
        }
        else
        {
            frameX = 3;
        }
        if (!shield)
        {
            frameX -= 2;
        }
        if (aiming)
        {
            frameX = 4;
            frameY = 2;
        }
        int modY = frameY / 2;
        srcRect.set(frameX * frameWidth, modY * frameHeight, (frameX * frameWidth + frameWidth) - 1, (modY * frameHeight + frameHeight) - 1);
        destRect.set(pos.x-hitboxSize,pos.y-hitboxSize,pos.x+hitboxSize,pos.y+hitboxSize);
        paint.setColor(Color.argb(255,255,255,255));
        canvas.drawBitmap(sprite, srcRect, destRect, paint);

        float xhair = hitboxSize * 2;
        paint.setColor(Color.argb(100,200,0,0));
        long timeGap = hitTime - System.currentTimeMillis();
        if (timeGap <= 2000 && timeGap > hitWindow)
        {
            float size = xhair*(float)timeGap/2000.0f;
            canvas.drawLine(pos.x,pos.y+xhair,pos.x,pos.y-xhair,paint); // y-crosshair
            canvas.drawLine(pos.x+xhair,pos.y,pos.x-xhair,pos.y,paint); // x-crosshair
            canvas.drawLine(pos.x-size,pos.y-size,pos.x-size,pos.y+size,paint); // left
            canvas.drawLine(pos.x+size,pos.y-size,pos.x+size,pos.y+size,paint); // right
            canvas.drawLine(pos.x-size,pos.y-size,pos.x+size,pos.y-size,paint); // top
            canvas.drawLine(pos.x-size,pos.y+size,pos.x+size,pos.y+size,paint); // bottom
        }
    }
}
