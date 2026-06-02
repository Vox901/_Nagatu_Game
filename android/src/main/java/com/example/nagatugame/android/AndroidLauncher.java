package com.example.nagatugame.android;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.example.nagatugame.NagatuGame;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;
        config.useGyroscope = false;
        config.useWakelock = true;
        config.r = 8;
        config.g = 8;
        config.b = 8;
        config.a = 8;
        config.depth = 0;
        config.stencil = 0;
        config.numSamples = 2;
        initialize(new NagatuGame(), config);
    }
}
