/*
 * Copyright 2014 - present Yunarta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mobilesolutionworks.android.facebookhelpers;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;

import java.util.ArrayList;

import static com.facebook.FacebookRequestError.Category;

/**
 * Created by Yunarta on 7/23/13.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FacebookControllerFragment extends FacebookLifecycleFragment implements FacebookHelpers {

    /**
     * Facebook controller instance.
     */
    private FacebookController mController;

    /**
     * Dialog cache.
     */
    private WebDialog mDialog;

    private ArrayList<String> mPermissions;

    private SessionLoginBehavior mLoginBehavior;

    private String mAuthorization;

    private String mId;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (mController == null) {
            mController = FacebookController.getInstance(activity);
        }
    }

    public String getFbId() {
        return mId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle args = getArguments();
        mPermissions = args.getStringArrayList("permission");
        mAuthorization = args.getString("authorization");
        if (TextUtils.isEmpty(mAuthorization)) {
            mAuthorization = "read";
        }

        String behavior = args.getString("loginBehavior");
        if ("SUPPRESS_SSO".equals(behavior)) {
            mLoginBehavior = SessionLoginBehavior.SUPPRESS_SSO;
        } else if ("SSO_ONLY".equals(behavior)) {
            mLoginBehavior = SessionLoginBehavior.SSO_ONLY;
        } else {
            mLoginBehavior = SessionLoginBehavior.SSO_WITH_FALLBACK;
        }
    }

    /**
     * Get current session.
     */
    public Session getSession() {
        return mController.getSession();
    }

    /**
     * Get Facebook session state.
     */
    public FacebookControllerConstants getState() {
        if (mController == null) {
            return FacebookControllerConstants.ERR_CONTROLLER_NOT_INITIALIZED;
        }

        Session session = getSession();
        SessionState state = session.getState();
        if (state == SessionState.CREATED || state == SessionState.CREATED_TOKEN_LOADED) {
            return FacebookControllerConstants.OK_SESSION_CREATED;
        } else if (state == SessionState.OPENED || state == SessionState.OPENED_TOKEN_UPDATED) {
            return FacebookControllerConstants.OK_SESSION_OPENED;
        } else if (state == SessionState.CLOSED || state == SessionState.CLOSED_LOGIN_FAILED) {
            return FacebookControllerConstants.OK_SESSION_CLOSED;
        } else if (state == SessionState.OPENING) {
            return FacebookControllerConstants.OK_SESSION_OPENING;
        }

        return FacebookControllerConstants.ERR_UNKNOWN;
    }

    /**
     * Request to open Facebook session.
     */
    public void open(OnControllerListener listener) {
        Session session = getSession();
        if (!session.isOpened()) {
            Session.OpenRequest openRequest = new Session.OpenRequest(this);
            openRequest.setCallback(new OpenCallback(listener));
            openRequest.setLoginBehavior(mLoginBehavior);
            openRequest.setPermissions(mPermissions);

            if ("read".equals(mAuthorization)) {
                session.openForRead(openRequest);
            } else {
                session.openForPublish(openRequest);
            }
        }
    }

    /**
     * Clear out the Facebook session and de-auth.
     */
    public void clear() {
        Session session = getSession();
        session.closeAndClearTokenInformation();
    }

    /**
     * Check whether the session is valid for request.
     */
    public boolean isSessionValid() {
        Session session = getSession();
        return session.isOpened() || !TextUtils.isEmpty(session.getAccessToken());
    }

    public boolean isSessionCached() {
        Session session = getSession();
        return (session.getState() == SessionState.CREATED_TOKEN_LOADED);
    }

    public void rawRequest(String path, Bundle parameters, HttpMethod method, OnControllerListener listener) {
        Request request = new Request(null, path, parameters, method, new RequestCallbackImpl(listener));
        request.executeAsync();
    }

    public void request(String path, Bundle parameters, HttpMethod method, OnControllerListener listener) {
        Session session = getSession();

        Request request = new Request(session, path, parameters, method, new RequestCallbackImpl(listener));
        request.setSession(session);

        execute(session, request);
    }

    public void request(String path, Bundle parameters, HttpMethod method, OnControllerListener listener, String tag) {
        Session session = getSession();

        Request request = new Request(session, path, parameters, method, new RequestCallbackImpl(listener));
        request.setTag(tag);
        request.setSession(session);

        execute(session, request);
    }

    public void request(Request request, OnControllerListener listener) {
        Session session = getSession();

        request.setSession(session);
        request.setCallback(new RequestCallbackImpl(listener));

        execute(session, request);
    }

    public void request(Request request, OnControllerListener listener, String tag) {
        Session session = getSession();

        request.setSession(session);
        request.setTag(tag);
        request.setCallback(new RequestCallbackImpl(listener));

        execute(session, request);
    }

    private void execute(Session session, Request request) {
        if (!session.isOpened()) {
            Session.OpenRequest openRequest = new Session.OpenRequest(this);
            openRequest.setCallback(new OpenCallbackAndRun(request));
            openRequest.setLoginBehavior(mLoginBehavior);
            openRequest.setPermissions(mPermissions);

            if ("read".equals(mAuthorization)) {
                session.openForRead(openRequest);
            } else {
                session.openForPublish(openRequest);
            }
        } else {
            request.executeAsync();
        }
    }

    public void validate(OnControllerListener listener) {

        Session session = getSession();
        if (!session.isOpened()) {
            Session.OpenRequest openRequest = new Session.OpenRequest(this);
            openRequest.setCallback(new OpenCallback(listener));
            openRequest.setLoginBehavior(mLoginBehavior);
            openRequest.setPermissions(mPermissions);

            if ("read".equals(mAuthorization)) {
                session.openForRead(openRequest);
            } else {
                session.openForPublish(openRequest);
            }
        } else {
            Request.newMeRequest(getSession(), new RequestCallbackImpl(listener)).executeAsync();
        }
    }

    public void processError(Response response, FacebookRequestError error, OnControllerListener listener) {
        boolean retry = false;
        if (error.getErrorCode() == -1) {
            retry = true;
        }

        Category category = error.getCategory();
        retry |= category == Category.PERMISSION || category == Category.AUTHENTICATION_REOPEN_SESSION || category == Category.AUTHENTICATION_RETRY;

        if (retry) {
            Session session = getSession();
            session.closeAndClearTokenInformation();

            if (listener != null) {
                listener.onSessionStateChanged(FacebookControllerFragment.this);
                listener.onAuthenticationRetryRequired(FacebookControllerFragment.this, response.getRequest());
            }
        } else {
            if (listener != null) {
                listener.onError(FacebookControllerFragment.this, response);
            }
        }
    }

    private class RequestCallbackImpl implements Request.Callback, Request.GraphUserCallback {

        private OnControllerListener mListener;

        public RequestCallbackImpl(OnControllerListener listener) {
            mListener = listener;
        }

        @Override
        public void onCompleted(Response response) {
            try {
                FacebookRequestError error = response.getError();
                if (error != null) {
                    processError(response, error, mListener);
                } else {
                    if (mListener != null) {
                        mListener.onCompleted(FacebookControllerFragment.this, response);
                    }
                }
            } finally {
                if (mListener != null) {
                    mListener.onFinalized(FacebookControllerFragment.this);
                }
            }
        }


        @Override
        public void onCompleted(GraphUser user, Response response) {
            try {
                FacebookRequestError error = response.getError();
                if (error != null) {
                    processError(response, error, mListener);
                } else {
                    mId = user.getId();

                    if (mListener != null) {
                        mListener.onAuthTokenValidated(FacebookControllerFragment.this);
                    }
                }
            } finally {
                if (mListener != null) {
                    mListener.onFinalized(FacebookControllerFragment.this);
                }
            }
        }
    }

    private class OpenCallback implements Session.StatusCallback {

        private OnControllerListener mListener;

        public OpenCallback(OnControllerListener listener) {
            mListener = listener;
        }

        /**
         * Facebook callback method.
         */
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if (mListener != null) {
                mListener.onSessionStateChanged(FacebookControllerFragment.this);
                if (state == SessionState.OPENED || state == SessionState.OPENED_TOKEN_UPDATED) {
                    // mListener.onSessionOpened(FacebookControllerFragment.this);
                    Request.newMeRequest(getSession(), new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            mId = user.getId();

                            if (mListener != null) {
                                mListener.onSessionOpened(FacebookControllerFragment.this);
                                mListener.onAuthTokenValidated(FacebookControllerFragment.this);
                            }
                        }
                    }).executeAsync();
//                    Request request = new Request(getSession(), "me?fields=id", null, HttpMethod.GET, new Request.GraphUserCallback() {
//
//                        @Override
//                        public void onCompleted(GraphUser user, Response response) {
//
//                        }
//                    });
//                    request.setSession(getSession());
                }
            }
        }
    }

    private class OpenCallbackAndRun implements Session.StatusCallback {

        private Request mRequest;

        public OpenCallbackAndRun(Request request) {
            mRequest = request;
        }

        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if (state == SessionState.OPENED || state == SessionState.OPENED_TOKEN_UPDATED) {
                mRequest.setSession(session);
                mRequest.executeAsync();
            }
        }
    }
}
