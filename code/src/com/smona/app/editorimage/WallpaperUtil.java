package com.smona.app.editorimage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

public class WallpaperUtil {
    public static final int VIBRATE_DURATION = 500;
    public static final String GOTO_DETAILACTIVITY_SOURE = "source_type";
    public static final String GOTO_DETAILACTIVITY_KEY = "info_intent";
    public static final String GOTO_DETAILACTIVITY_KEY_INFO = "info_bundle";

    private static String SDCARD = Environment.getExternalStorageDirectory()
            .getAbsolutePath();

    public static final String RES_DIR_SOURCE = SDCARD + "/EditorImage/source/";

    public static final String RES_DIR_IMPRESSION = SDCARD
            + "/EditorImage/impress/";

    public static final String RES_DIR_XML = SDCARD + "/EditorImage/xml/";

    public static final String RES_DIR_ZIP = SDCARD + "/EditorImage/zip/";
    
    
    public static final String CURRENT_FILE_NAME = "current_file_name";

    public static Bitmap createDragOutline(View source) {
        Canvas canvas = new Canvas();
        int imgW = source.getWidth();
        int imgH = source.getHeight();
        final Bitmap b = Bitmap.createBitmap(imgW, imgH,
                Bitmap.Config.ARGB_8888);
        canvas.setBitmap(b);
        Rect clipRect = new Rect();
        source.getDrawingRect(clipRect);
        canvas.save();
        canvas.translate(-source.getScrollX(), -source.getScrollY());
        canvas.clipRect(clipRect, Op.REPLACE);
        source.draw(canvas);
        canvas.restore();
        canvas.setBitmap(null);
        return b;
    }

    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        if (!TextUtils.isEmpty(imei)) {
            return imei;
        }
        return imei;
    }

    public static String getAppVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
    }

    public static String getConnectedType(Context context) {

        String netstyle = "";

        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();

            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                netstyle = mNetworkInfo.getTypeName();
                if (netstyle.equalsIgnoreCase("WIFI")) {
                    return "WiFi";
                } else if (netstyle.equalsIgnoreCase("MOBILE")) {
                    switch (mNetworkInfo.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                        return "2G"; // ~ 50-100 kbps
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                        return "2G"; // ~ 14-64 kbps
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        return "2G"; // ~ 50-100 kbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        return "3G"; // ~ 400-1000 kbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        return "3G"; // ~ 600-1400 kbps
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        return "2G"; // ~ 100 kbps
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        return "3G"; // ~ 2-14 Mbps
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                        return "3G"; // ~ 700-1700 kbps
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                        return "3G"; // ~ 1-23 Mbps
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                        return "3G"; // ~ 400-7000 kbps
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        return "3G"; // ~ 1-2 Mbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        return "3G"; // ~ 5 Mbps
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return "4G"; // ~ 10-20 Mbps
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return "2G"; // ~25 kbps
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return "4G"; // ~ 10+ Mbps
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                        return "UNKNOWN";
                    default:
                        return netstyle;
                    }
                }
                return netstyle;
            }
        }
        return netstyle;
    }

    public static String getCountry(Context context) {
        String imsi = getIMSI(context);
        String mcc = "0";
        if (null != imsi && imsi.trim().length() > 0) {
            mcc = imsi.substring(0, 3);
        }
        return mcc;
    }

    private static String getIMSI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = telephonyManager.getSubscriberId();
        return imsi;
    }

    private static void makeFolders() {
    }

    public static void gotoActivity(Context packageContext, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(packageContext, cls);
        packageContext.startActivity(intent);
    }

    public static void initAppEnvironment() {
        WallpaperUtil.makeFolders();
    }

    public static Bitmap resizeBitmap(Bitmap srcBitmap, int newHeight,
            int newWidth) {
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        float scaleWidth = ((float) newWidth) / srcWidth;
        float scaleHeight = ((float) newHeight) / srcHeight;
        if (isNotNeedScale(scaleWidth, scaleHeight)) {
            return srcBitmap;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap result = Bitmap.createBitmap(srcBitmap, 0, 0, srcWidth,
                srcHeight, matrix, true);
        return result;
    }

    private static boolean isNotNeedScale(float scaleWidth, float scaleHeight) {
        return scaleWidth == 1.0f && scaleHeight == 1.0f;
    }

    public static void recycleBitmap(Bitmap recycle) {
        recycleBitmap(recycle, null);
    }

    @SuppressLint("NewApi")
    private static void recycleBitmap(Bitmap recycle, Bitmap source) {
        if (recycle != null) {
            if (recycle.sameAs(source) || recycle.isRecycled()) {
                return;
            }
            recycle.recycle();
        }
    }

}
