package com.koma.music.data;

import com.koma.music.ApplicationMoudule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by koma on 6/16/17.
 */
@Singleton
@Component(modules = {ApplicationMoudule.class})
public interface MusicRepositoryComponent {
}
