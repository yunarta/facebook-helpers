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

import android.os.Bundle;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;

/**
 * Created by yunarta on 16/8/14.
 */
public interface FacebookHelpers {

    public interface OnControllerListener {

        /**
         * On auth token test validated.
         * <p/>
         * Execute retry on previous request call.
         */
        void onAuthTokenValidated(FacebookHelpers helpers);

        /**
         * On session state changed.
         */
        void onSessionStateChanged(FacebookHelpers helpers);

        /**
         * On session opened.
         * <p/>
         * Execute request in this method.
         */
        void onSessionOpened(FacebookHelpers helpers);

        /**
         * On authentication retry is required.
         * <p/>
         * Execute retry to open session here.
         */
        void onAuthenticationRetryRequired(FacebookHelpers helpers, Request request);

        void onCompleted(FacebookHelpers fragment, Response response);

        void onError(FacebookHelpers fragment, Response response);
    }

    public class OnControllerAdapter implements OnControllerListener {

        @Override
        public void onAuthTokenValidated(FacebookHelpers helpers) {

        }

        @Override
        public void onSessionStateChanged(FacebookHelpers helpers) {

        }

        @Override
        public void onSessionOpened(FacebookHelpers helpers) {

        }

        @Override
        public void onAuthenticationRetryRequired(FacebookHelpers helpers, Request request) {

        }

        @Override
        public void onCompleted(FacebookHelpers fragment, Response response) {

        }

        @Override
        public void onError(FacebookHelpers fragment, Response response) {

        }
    }

    void open(OnControllerListener listener);

    void clear();

    void validate(OnControllerListener listener);

    boolean isSessionValid();

    Request request(String path, Bundle parameters, HttpMethod method, OnControllerListener listener);

    Request request(Request request, OnControllerListener listener);

    void processError(Response response, FacebookRequestError error, OnControllerListener listener);
}
