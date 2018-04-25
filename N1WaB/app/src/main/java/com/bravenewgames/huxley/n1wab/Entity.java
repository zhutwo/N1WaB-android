package com.bravenewgames.huxley.n1wab;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class Entity {

    float moveSpeed;
    float theta;
    int frameHeight;
    int frameWidth;
    int frameX;
    int frameY;
    int numFrames;
    float hitboxSize;
    boolean toMove;
    boolean aiming;
    boolean expired;

    Vec2 pos;
    Vec2 targetPos;
    public Bitmap sprite;
    Rect srcRect;
    RectF destRect;

    public Entity(Bitmap bm, float dx, float dy)
    {
        pos = new Vec2(dx, dy);
        sprite = bm;
        frameX = 0;
        frameY = 0;
        numFrames = 12;
        frameWidth = 96;
        frameHeight = 96;
        theta = 0;
        hitboxSize = 128;
        moveSpeed = 200;
        srcRect = new Rect();
        destRect = new RectF();
    }

    public void draw(Canvas canvas, Paint paint)
    {
        if (theta > -Math.PI/2 && theta < Math.PI/2)
        {
            frameX = 0;
        }
        else
        {
            frameX = 1;
        }
        srcRect.set(frameX * frameWidth, 0, (frameX * frameWidth + frameWidth) - 1, frameHeight - 1);
        destRect.set(pos.x-hitboxSize,pos.y-hitboxSize,pos.x+hitboxSize,pos.y+hitboxSize);
        paint.setColor(Color.argb(255,255,255,255));
        canvas.drawBitmap(sprite, srcRect, destRect, paint);
        if (aiming)
        {
            // draw gun
        }
    }

    public Vec2 getPosition()
    {
        return pos;
    }

    public float getHitboxSize()
    {
        return hitboxSize;
    }

    public boolean checkHit(float dx, float dy)
    {
        if (dx > pos.x - hitboxSize &&
                dx < pos.x + hitboxSize &&
                dy > pos.y - hitboxSize &&
                dy < pos.y + hitboxSize)
        {
            return true;
        }
        return false;
    }

    public void setMoveTarget(float dx, float dy, boolean move)
    {
        toMove = move;
        targetPos = new Vec2(dx,dy);
    }

    public void update(float dt)
    {
        move(dt);
    }

    public void move(float dt)
    {
        if (toMove)
        {
            Vec2 moveVec = targetPos.Subtract(pos);
            if (moveVec.Magnitude() > 4.0f) {
                theta = moveVec.Angle();
                moveVec.Normalize();
                moveVec = moveVec.Scale(dt * moveSpeed);
                pos = pos.Add(moveVec);
                frameY++;
                if (frameY == numFrames)
                {
                    frameY = 0;
                }
            }
            else
            {
                toMove = false;
            }
        }
    }
    public boolean isExpired()
    {
        return expired;
    }
}
