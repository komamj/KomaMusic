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
package com.koma.music.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by koma on 4/13/17.
 */

public abstract class MusicButton extends AppCompatImageButton implements View.OnClickListener {
    protected static float ACTIVE_ALPHA = 1.0f;
    protected static float INACTIVE_ALPHA = 0.4f;

    public MusicButton(final Context context, final AttributeSet attrs) {
        super(context);

        setPadding(0, 0, 0, 0);

        setOnClickListener(this);
    }
}
