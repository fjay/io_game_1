package org.io.competition.stat.game.two;

import org.io.competition.stat.game.one.Result1;

/**
 * @author Jay Wu
 */
public class Result2 extends Result1 {
    private Integer count = 0;

    public Integer getCount() {
        return count;
    }

    public Result2 setCount(Integer count) {
        this.count = count;
        return this;
    }
}