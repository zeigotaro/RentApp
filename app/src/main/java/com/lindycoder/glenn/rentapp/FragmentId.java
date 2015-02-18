package com.lindycoder.glenn.rentapp;

/**
 * Created by gh250086 on 2/13/2015.
 */
public enum FragmentId {
    HOME(1), HOTBUYS(2), MESSAGES(3), EVENTS(4), LOGOUT(5), HOTBUY_ITEM(6);
    private int value;
    private FragmentId (int value) {
        this.value = value;
    }

    public int getValue() { return this.value; }
};
