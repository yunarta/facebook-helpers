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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.facebook.UiLifecycleHelper;

/**
 * Created by yunarta on 16/8/14.
 */
public class FacebookLifecycleFragment extends Fragment {

    private UiLifecycleHelper mLifecycleHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLifecycleHelper = new UiLifecycleHelper(getActivity(), null);
        mLifecycleHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLifecycleHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLifecycleHelper.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mLifecycleHelper.onSaveInstanceState(outState);

    }

    @Override
    public void onPause() {
        super.onPause();
        mLifecycleHelper.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mLifecycleHelper.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLifecycleHelper.onDestroy();
    }

    public UiLifecycleHelper getLifecycleHelper() {
        return mLifecycleHelper;
    }
}