package com.mobilesolutionworks.android.facebookhelpers;

import android.content.Context;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.SharedPreferencesTokenCachingStrategy;

/**
 * Created by Yunarta on 7/23/13.
 */
public class FacebookController {

    static FacebookController mController;

    public static FacebookController getInstance(Context context) {
        if (mController == null) {
            mController = new FacebookController(context.getApplicationContext());
        }

        return mController;
    }

    private final Context mContext;

    private final SharedPreferencesTokenCachingStrategy mTokenCachingStrategy;

    private FacebookController(Context context) {
        mContext = context;
        mTokenCachingStrategy = new SharedPreferencesTokenCachingStrategy(mContext);
    }

    /**
     * Create new session.
     */
    public Session getSession() {
        Session session = Session.getActiveSession();
        if (session == null || session.getState() == SessionState.CLOSED || session.getState() == SessionState.CLOSED_LOGIN_FAILED) {
            session = new Session.Builder(mContext).setTokenCachingStrategy(mTokenCachingStrategy).build();
            session.addCallback(new MyStatusCallback());
            Session.setActiveSession(session);
        }

        return session;
    }

    /**
     * Facebook Status callback.
     */
    private class MyStatusCallback implements Session.StatusCallback {

        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if (state == SessionState.CLOSED || state == SessionState.CLOSED_LOGIN_FAILED) {
                session.close();
                getSession();
            }
        }
    }
}
