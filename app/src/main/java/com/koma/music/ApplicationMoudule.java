package com.koma.music;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by koma on 6/16/17.
 */

/**
 * This is a Dagger module. We use this to pass in the Context dependency to the
 * {@link
 * com.example.android.architecture.blueprints.todoapp.data.source.TasksRepositoryComponent}.
 */
@Module
public final class ApplicationMoudule {
    private final Context mContext;

    public ApplicationMoudule(Context context) {
        mContext = context;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }
}
