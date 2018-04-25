package com.bravenewgames.huxley.n1wab;

public class Vec2 {
    public float x;
    public float y;

    public Vec2(float dx, float dy)
    {
        x = dx;
        y = dy;
    }

    public void Normalize()
    {
        float mag = (float)Math.sqrt((double)(x*x + y*y));
        x = x/mag;
        y = y/mag;
    }

    public float Magnitude()
    {
        float mag = (float)Math.sqrt((double)(x*x + y*y));
        return mag;
    }

    public Vec2 Subtract(Vec2 other)
    {
        Vec2 temp = new Vec2(x-other.x, y-other.y);
        return temp;
    }

    public Vec2 Add(Vec2 other)
    {
        Vec2 temp = new Vec2(x+other.x, y+other.y);
        return temp;
    }

    public Vec2 Scale(float scalar)
    {
        Vec2 temp = new Vec2(x*scalar,y*scalar);
        return temp;
    }

    public float Angle()
    {
        return (float)Math.atan2((double)y,(double)x);
    }
}
