package com.protv.mm;
import android.content.*;
import android.graphics.*;
import java.lang.reflect.*;

public class TypefaceUtils
{

    public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {
        try {

            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);

            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);

            defaultFontTypefaceField.setAccessible(true);

            defaultFontTypefaceField.set(null, customFontTypeface);

        } catch (Exception e) {

            //            Log.e("Can not set custom font " + customFontFileNameInAssets + " instead of " + defaultFontNameToOverride);

        }
    }
}

