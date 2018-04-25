package com.bravenewgames.huxley.n1wab;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class BeatMap {

    int index;
    long nextTimeStamp;
    long offset;
    ArrayList<Spawn> spawnList;
    Spawn current;
    Bitmap bm;

    public BeatMap(Bitmap enemyBitmap)
    {
        spawnList = new ArrayList<Spawn>();
        bm = enemyBitmap;
        index = 0;
    }

    public boolean update(ArrayList<Enemy> enemies)
    {
        if (nextTimeStamp <= System.currentTimeMillis())
        {
            Enemy e = new Enemy(bm, current.pos.x, current.pos.y, current.hitTime);
            e.setMoveTarget(current.move.x, current.move.y, true);
            enemies.add(e);
            if (index < spawnList.size()) {
                current = spawnList.get(index);
                index++;
                nextTimeStamp = current.spawnTime + offset;
                return true;
            }
            else
            {
                return false;
            }
        }
        return true;
    }
    public void start()
    {
        offset = System.currentTimeMillis();
        current = spawnList.get(index);
        index++;
        nextTimeStamp = current.spawnTime + offset;
    }
    public void addSpawn(Vec2 p, Vec2 m, long ht, long st)
    {
        Spawn s = new Spawn(p, m, ht, st);
        spawnList.add(s);
    }

    class Spawn
    {
        Vec2 pos;
        Vec2 move;
        long hitTime;
        long spawnTime;

        public Spawn(Vec2 p, Vec2 m, long ht, long st)
        {
            pos = p;
            move = m;
            hitTime = ht;
            spawnTime = st;
        }
    }
}
