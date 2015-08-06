package com.smona.app.editorimage;

//Gionee <Moth><2015-03-14> add for CR01454311 begin
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.util.Log;

public class WallpaperLog {
    public static final boolean DEBUG = true;
    public static final boolean DEBUG_DRAW = false;
    public static final boolean DEBUG_DRAG = true;
    public static final boolean DEBUG_KEY = true;
    public static final boolean DEBUG_LAYOUT = false;
    public static final boolean DEBUG_LOADER = true;
    public static final boolean DEBUG_MOTION = false;
    public static final boolean DEBUG_PERFORMANCE = true;
    public static final boolean DEBUG_SURFACEWIDGET = true;
    public static final boolean DEBUG_UNREAD = false;
    public static final boolean DEBUG_AUTOTESTCASE = true;

    private static final String MODULE_NAME = "Editor";
    private static final WallpaperLog INSTANCE = new WallpaperLog();

    /**
     * private constructor here, It is a singleton class.
     */
    private WallpaperLog() {
    }

    /**
     * The FileManagerLog is a singleton class, this static method can be used
     * to obtain the unique instance of this class.
     * 
     * @return The global unique instance of FileManagerLog.
     */
    public static WallpaperLog getInstance() {
        return INSTANCE;
    }

    /**
     * The method prints the log, level error.
     * 
     * @param tag
     *            the tag of the class.
     * @param msg
     *            the message to print.
     */
    public static void e(String tag, String msg) {
        Log.e(MODULE_NAME, tag + ", " + msg);
    }

    /**
     * The method prints the log, level error.
     * 
     * @param tag
     *            the tag of the class.
     * @param msg
     *            the message to print.
     * @param t
     *            an exception to log.
     */
    public static void e(String tag, String msg, Throwable t) {
        Log.e(MODULE_NAME, tag + ", " + msg, t);
    }

    /**
     * The method prints the log, level warning.
     * 
     * @param tag
     *            the tag of the class.
     * @param msg
     *            the message to print.
     */
    public static void w(String tag, String msg) {
        Log.w(MODULE_NAME, tag + ", " + msg);
    }

    /**
     * The method prints the log, level warning.
     * 
     * @param tag
     *            the tag of the class.
     * @param msg
     *            the message to print.
     * @param t
     *            an exception to log.
     */
    public static void w(String tag, String msg, Throwable t) {
        Log.w(MODULE_NAME, tag + ", " + msg, t);
    }

    /**
     * The method prints the log, level debug.
     * 
     * @param tag
     *            the tag of the class.
     * @param msg
     *            the message to print.
     */
    public static void i(String tag, String msg) {
        Log.i(MODULE_NAME, tag + ", " + msg);
    }

    /**
     * The method prints the log, level debug.
     * 
     * @param tag
     *            the tag of the class.
     * @param msg
     *            the message to print.
     * @param t
     *            an exception to log.
     */
    public static void i(String tag, String msg, Throwable t) {
        Log.i(MODULE_NAME, tag + ", " + msg, t);
    }

    /**
     * The method prints the log, level debug.
     * 
     * @param tag
     *            the tag of the class.
     * @param msg
     *            the message to print.
     */
    public static void d(String tag, String msg) {
        Log.e(MODULE_NAME, tag + ", " + msg);
    }

    /**
     * The method prints the log, level debug.
     * 
     * @param tag
     *            the tag of the class.
     * @param msg
     *            the message to print.
     * @param t
     *            An exception to log.
     */
    public static void d(String tag, String msg, Throwable t) {
        Log.d(MODULE_NAME, tag + ", " + msg, t);
    }

    /**
     * The method prints the log, level debug.
     * 
     * @param tag
     *            the tag of the class.
     * @param msg
     *            the message to print.
     */
    public static void v(String tag, String msg) {
        Log.v(MODULE_NAME, tag + ", " + msg);
    }

    /**
     * The method prints the log, level debug.
     * 
     * @param tag
     *            the tag of the class.
     * @param msg
     *            the message to print.
     * @param t
     *            An exception to log.
     */
    public static void v(String tag, String msg, Throwable t) {
        Log.v(MODULE_NAME, tag + ", " + msg, t);
    }

    public static void printTrace(String tag) {
        Log.v(MODULE_NAME, tag + ", Trace start");
        Thread.dumpStack();
        Log.v(MODULE_NAME, tag + ", Trace end");
    }

    public static void saveBitmap(Bitmap bitmap, String path) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void printData(String tag, Object obj) {
        Log.v(MODULE_NAME, tag + ": PrintData-----------" + obj);
    }
}
// Gionee <Moth><2015-03-14> add for CR01454311 end