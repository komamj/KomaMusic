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
package com.koma.music.util;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Pair;
import android.view.View;

import com.koma.music.R;
import com.koma.music.artist.detail.ArtistDetailFragment;

/**
 * Created by koma on 5/5/17.
 */

public class NavigationUtils {
    public static void navigateToArtist(Activity context, long artistID, String name, Pair<View, String> transitionViews) {
        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment fragment;
        Transition changeImage = TransitionInflater.from(context).inflateTransition(R.transition.image_transform);
        transaction.addSharedElement(transitionViews.first, transitionViews.second);
        fragment = ArtistDetailFragment.newInstance(artistID, name, transitionViews.second);
        fragment.setSharedElementEnterTransition(changeImage);
        fragment.setSharedElementReturnTransition(changeImage);
        transaction.hide(((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container));
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null).commitAllowingStateLoss();
    }
}