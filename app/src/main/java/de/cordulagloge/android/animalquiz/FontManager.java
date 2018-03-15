package de.cordulagloge.android.animalquiz;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Use custom font
 * <p>
 * based on Gianluca Segato
 * https://code.tutsplus.com/tutorials/how-to-use-fontawesome-in-an-android-app--cms-24167
 * <p>
 * Created by Cordula Gloge on 15/03/2018.
 */

public class FontManager {
    public final static String ROOT = "fonts/",
            FONTAWESOME = ROOT + "fontawesome-webfont.ttf";

    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), FONTAWESOME);
    }
}
