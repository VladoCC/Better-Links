package com.vladocc.blink.betterlink;

/**
 * Created by Voyager on 10.09.2017.
 */

public class BlinkData {

    private String link;
    private int type;

    public BlinkData(String link, int type) {
        this.link = link;
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public int getType() {
        return type;
    }
}
