package com.vipsfin.competition.stat.util;

/**
 * @author Jay Wu
 */
public class Stopwatch {

    private long start;
    private long end;

    public static Stopwatch create() {
        return new Stopwatch();
    }

    public Stopwatch start() {
        start = System.currentTimeMillis();
        return this;
    }

    public void stop() {
        end = System.currentTimeMillis();
    }

    public long duration() {
        return end - start;
    }
}
