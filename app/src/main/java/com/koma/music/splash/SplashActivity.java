/*
 * Copyright (C) 2017 Koma MJ
 *
 * Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.koma.music.splash;

import android.content.Intent;
import android.os.Bundle;

import com.koma.music.R;
import com.koma.music.base.PermissionActivity;
import com.koma.music.main.MainActivity;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by koma on 4/21/17.
 */

public class SplashActivity extends PermissionActivity {
    private static final int TIME_TO_MAINACTIVITY = 2000;

    private CompositeSubscription mSubsriptions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSubsriptions = new CompositeSubscription();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void init() {

    }

    private void launchMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        SplashActivity.this.finish();
    }

    @Override
    public void onStart() {
        super.onStart();

        mSubsriptions.add(Observable.timer(TIME_TO_MAINACTIVITY, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        launchMainActivity();
                    }
                }));
    }


    @Override
    public void onStop() {
        super.onStop();

        mSubsriptions.clear();
    }

    @Override
    public void onBackPressed() {

    }
}
