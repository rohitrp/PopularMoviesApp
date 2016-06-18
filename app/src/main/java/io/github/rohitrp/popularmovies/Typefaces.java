package io.github.rohitrp.popularmovies;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

public class Typefaces {

    private static final HashMap<String, Typeface> cache =
            new HashMap<>();

    public static final String OPEN_SANS_CONDENSED_LIGHT_FONT =
            "fonts/OpenSans-CondLight.ttf";

    /**
     * This method is caches the font if it is asked for the first
     * time. Otherwise, it returns the cached font.
     * @param context Context for accessing assets
     * @param fontName One of the constant font names of this class
     * @return Typeface of the provided fontName
     */
    public static Typeface get(Context context, String fontName) {
        if (!cache.containsKey(fontName)) {
            Typeface typeface = Typeface.createFromAsset(
                    context.getAssets(),
                    fontName);

            cache.put(fontName, typeface);
        }

        return cache.get(fontName);
    }
}
