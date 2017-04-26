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
package com.koma.music.listener;

import com.koma.music.service.MusicServiceConstants;

/**
 * Created by koma on 4/10/17.
 */

public interface MusicStateListener {
    /**
     * Called when {@link MusicServiceConstants#REFRESH} is invoked
     */
    public void refreshData();

    /**
     * Called when {@link MusicServiceConstants#PLAYLIST_CHANGED} is invoked
     */
    public void onPlaylistChanged();

    /**
     * Called when {@link MusicServiceConstants#META_CHANGED} is invoked
     */
    public void onMetaChanged();

    /**
     * Called when {@link MusicServiceConstants#PLAYSTATE_CHANGED} is invoked
     */
    public void onPlayStateChanged();
}
