package com.smona.app.editorimage;

import android.content.Context;
import android.util.DisplayMetrics;

public class EditorUtil {
    private static ScreenInfo sScreenInfo;

    public static void initParams(Context context) {
        initScreenInfo(context);
    }

    private static void initScreenInfo(Context context) {
        sScreenInfo = new ScreenInfo(context);
    }

    public static ScreenInfo getSceenInfo() {
        return sScreenInfo;
    }

    public static class ScreenInfo {
        public int mScreenWidth;
        public int mScreenHeight;
        public int mScreenDpi;
        public float mScreenScale;

        public ScreenInfo(Context context) {
            initScreenInfo(context);
        }

        private void initScreenInfo(Context context) {
            DisplayMetrics display = context.getResources().getDisplayMetrics();
            mScreenWidth = display.widthPixels;
            mScreenHeight = display.heightPixels;
            mScreenDpi = display.densityDpi;
            mScreenScale = display.scaledDensity;
            if (mScreenHeight < 960) {
                mScreenScale = mScreenHeight * 1.0f / 640;
            }
            WallpaperLog.d("", "ScreenInfo mScreenWidth: " + mScreenWidth
                    + ", mScreenHeight: " + mScreenHeight + ", mDpi: "
                    + mScreenDpi + ", mScreenScale: " + mScreenScale);
        }
    }
}
