package com.mobilesolutionworks.android.facebookhelpers;

/**
 * Created by Yunarta on 7/23/13.
 */
public enum FacebookControllerConstants {

    ERR_UNKNOWN(-2),
    ERR_CONTROLLER_NOT_INITIALIZED(-1),

    OK_SESSION_CREATED(1),
    OK_SESSION_OPENED(2),
    OK_SESSION_CLOSED(3),
    OK_SESSION_OPENING(4);

    private int mCode;

    FacebookControllerConstants(int code) {
        mCode = code;
    }
}
