package com.vipsfin.competition.stat;

public class Stopwatch {

    private long start;
    private long end;

    public static Stopwatch create() {
        return new Stopwatch();
    }

    public void start() {
        start = System.currentTimeMillis();
    }

    public void stop() {
        end = System.currentTimeMillis();
    }

    public long duration() {
        return end - start;
    }
}
