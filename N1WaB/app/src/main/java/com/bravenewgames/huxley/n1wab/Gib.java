package com.bravenewgames.huxley.n1wab;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Gib extends Entity {

    public Gib(Bitmap bm,float dx, float dy)
    {
        super(bm, dx, dy);
        frameX = 5;
        numFrames = 12;
    }

    @Override
    public void update(float dt)
    {
        if (frameY != numFrames)
        {
            frameY++;
        }
        else
        {
            expired = true;
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint)
    {
        int modY = frameY / 2;
        srcRect.set(frameX * frameWidth, modY * frameHeight, (frameX * frameWidth + frameWidth) - 1, (modY * frameHeight + frameHeight) - 1);
        destRect.set(pos.x-hitboxSize,pos.y-hitboxSize,pos.x+hitboxSize,pos.y+hitboxSize);
        paint.setColor(Color.argb(255,255,255,255));
        canvas.drawBitmap(sprite, srcRect, destRect, paint);
    }
}
