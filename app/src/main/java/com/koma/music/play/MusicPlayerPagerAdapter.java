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
package com.koma.music.play;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.koma.music.play.playartwork.NowPlayingCardFragment;
import com.koma.music.play.playqueue.PlayQueueFragment;

/**
 * Created by koma on 4/27/17.
 */

public class MusicPlayerPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE_NUMBER = 2;

    public MusicPlayerPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PlayQueueFragment();
            case 1:
                return new NowPlayingCardFragment();
            case 2:
                return new NowPlayingCardFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return PAGE_NUMBER;
    }
}
