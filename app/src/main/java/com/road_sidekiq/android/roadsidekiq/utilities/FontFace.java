package com.road_sidekiq.android.roadsidekiq.utilities;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by rewin0087 on 4/18/15.
 */
public class FontFace {

    public static final String LUCIDA = "fonts/lucida.ttf";
    public static final String MONOSPACE = "MONOSPACE";

    /**
     * Using reflection to override default typeface
     * NOTICE: DO NOT FORGET TO SET TYPEFACE FOR APP THEME AS DEFAULT TYPEFACE WHICH WILL BE OVERRIDDEN
     * @param context to work with assets
     * @param defaultFontNameToOverride for example "monospace"
     * @param customFontFileNameInAssets file name of the font from assets
     */
    public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {
        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);
            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
            Log.d("FONT OVERRIDED", "TRUE");
        } catch (Exception e) {
            Log.e("CUSTOM FONT ERROR", e.getMessage());
            Log.e("CUSTOM FONT ERROR", "Can not set custom font " + customFontFileNameInAssets + " instead of " + defaultFontNameToOverride);
        }
    }

    public static void setAppFontToLed(Context context) {
        overrideFont(context, MONOSPACE, LUCIDA);
    }

    public static Typeface toDigitalLedFontface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), LUCIDA);
    }

}
