package me.ht9.rose.util.module;

import me.ht9.rose.util.Globals;

public final class Timer implements Globals
{
    private long time;

    public Timer()
    {
        this.time = System.currentTimeMillis();
    }

    public boolean hasReached(double ms)
    {
        return System.currentTimeMillis() - this.time >= ms;
    }

    public long getTime()
    {
        return System.currentTimeMillis() - this.time;
    }

    public void reset()
    {
        this.time = System.currentTimeMillis();
    }
}