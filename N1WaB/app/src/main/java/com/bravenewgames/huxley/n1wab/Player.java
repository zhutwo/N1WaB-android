package com.bravenewgames.huxley.n1wab;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.ArrayList;

public class Player extends Entity {

    int hp;
    int damage;
    int meleeOffset;
    int shootFrames;
    float meleeRange;
    boolean toMelee;
    boolean toShoot;
    ArrayList<Illusion> illusionList;

    public Player(Bitmap bm, float dx, float dy)
    {
        super(bm,dx,dy);
        hp = 100;
        damage = 10;
        meleeRange = 96;
        moveSpeed = 400;
        shootFrames = 3;
        illusionList = new ArrayList<Illusion>();
    }

    @Override
    public void update(float dt)
    {
        move(dt);
        if(toMelee) {
            frameY++;
            if (frameY == numFrames) {
                frameY--;
                toMelee = false;
            }
        }
        else if (toShoot) {
            shootFrames++;
            if (frameY == 4) {
                //frameY--;
                toShoot = false;
            }
        }
        if (!illusionList.isEmpty()) {
            for (Illusion i : illusionList) {
                i.update(dt);
                if (i.isExpired())
                {
                    illusionList.remove(i);
                }
            }
        }
    }

    @Override
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
        if(toMelee)
        {
            frameX += meleeOffset*2;
        }
        else if (toShoot)
        {
            frameX += 6;
        }
        int modY = frameY / 2;
        srcRect.set(frameX * frameWidth, modY * frameHeight, (frameX * frameWidth + frameWidth) - 1, (modY * frameHeight + frameHeight) - 1);
        destRect.set(pos.x-hitboxSize,pos.y-hitboxSize,pos.x+hitboxSize,pos.y+hitboxSize);
        paint.setColor(Color.argb(255,255,255,255));
        canvas.drawBitmap(sprite, srcRect, destRect, paint);
        if (toShoot)
        {
            srcRect.set(frameX * frameWidth, shootFrames * frameHeight, (frameX * frameWidth + frameWidth) - 1, (shootFrames * frameHeight + frameHeight) - 1);
            canvas.drawBitmap(sprite, srcRect, destRect, paint);
        }
        if (!illusionList.isEmpty()) {
            for (Illusion i : illusionList) {
                i.draw(canvas, paint);
            }
        }
    }

    @Override
    public void move(float dt)
    {
        if (toMove)
        {
            Vec2 moveVec = targetPos.Subtract(pos);
            if (moveVec.Magnitude() > 2.0f) {
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

    public void attack(Vec2 target)
    {
        Vec2 dist = target.Subtract(pos);
        if (dist.Magnitude() <= meleeRange + 8.0f)
        {
            melee();
        }
    }

    public void heal(int h)
    {
        hp += h;
        if (hp > 100)
        {
            hp = 100;
        }
    }

    public void shoot()
    {
        frameY = 0;
        shootFrames = 1;
        toShoot = true;
        toMelee = false;
    }

    public void melee()
    {
        frameY = 0;
        toMelee = true;
        toShoot = false;
        meleeOffset++;
        if (meleeOffset == 3)
        {
            meleeOffset = 1;
        }
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

    public void dashAttack(Vec2 target)
    {
        meleeOffset = 0;
        melee();
        pos = pos.Subtract(target);
        float mag = pos.Magnitude();
        int num = (int)(mag/meleeRange);
        pos.Normalize();
        Vec2 nextPos = pos.Scale(meleeRange);
        pos = target.Add(nextPos);
        Vec2 iPos = pos;
        for (int i = num; i > 1; i--)
        {
            iPos = iPos.Add(nextPos);
            float timer = 0.4f * (float)i/(float)num;
            Illusion ill = new Illusion(sprite, iPos, frameX+2, timer);
            illusionList.add(ill);
        }
    }

    class Illusion
    {
        Vec2 pos;
        Bitmap sprite;
        float timer;
        float maxTimer;
        int frameX;
        Rect srcRect;
        RectF destRect;
        boolean expired;

        public Illusion(Bitmap bm, Vec2 p, int dirFrameX, float t)
        {
            sprite = bm;
            pos = p;
            timer = t;
            maxTimer = t;
            frameX = dirFrameX;
            srcRect = new Rect();
            destRect = new RectF();
        }

        public void draw(Canvas canvas, Paint paint)
        {
            srcRect.set(frameX * frameWidth, 0, (frameX * frameWidth + frameWidth) - 1, frameHeight - 1);
            destRect.set(pos.x-hitboxSize,pos.y-hitboxSize,pos.x+hitboxSize,pos.y+hitboxSize);
            int alpha = (int)(255.0f * (timer/maxTimer));
            paint.setColor(Color.argb(alpha,255,255,255));
            canvas.drawBitmap(sprite, srcRect, destRect, paint);
        }

        public void update(float dt)
        {
            timer -= dt;
            if (timer <= 0.0f)
            {
                timer = 0.0f;
                expired = true;
            }
        }
        public boolean isExpired()
        {
            return expired;
        }
    }
}
