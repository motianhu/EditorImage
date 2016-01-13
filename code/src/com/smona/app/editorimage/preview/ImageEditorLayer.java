package com.smona.app.editorimage.preview;

import java.util.ArrayList;

import com.smona.app.editorimage.EditorUtil;
import com.smona.app.editorimage.FontEditorLayer;
import com.smona.app.editorimage.FontInfo;
import com.smona.app.editorimage.R;
import com.smona.app.editorimage.WallpaperLog;
import com.smona.app.editorimage.config.Config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

@SuppressLint("ClickableViewAccessibility")
public class ImageEditorLayer extends FrameLayout {
    private static final String TAG = "ImageEditorLayer";

    private AssetManager mAssetManager;
    private static final int screen_base_dp = 640;

    public ImageEditorLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAssetManager = context.getAssets();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void loadViews(ArrayList<FontInfo> infos, float scale) {
        int size = infos.size();
        for (int i = 0; i < size; i++) {
            addFontTextView(infos.get(i), scale);
        }
    }

    @SuppressLint("InflateParams")
    public void addFontTextView(FontInfo info, float scale) {
        FontEditorLayer view = (FontEditorLayer) LayoutInflater.from(
                getContext()).inflate(R.layout.font_editor_layer, null);

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        // coord is dp.
        String[] coors = info.coord.split(",");
        if (Config.SUPPORT_ENTER) {
            info.content = info.content.replace(Config.NEW_LINE_SIGN,
                    Config.NEW_LINE);
        }
        params.leftMargin = (int) (Float.valueOf(coors[0]) * scale);
        int mImageCropHeight = (int) (screen_base_dp * scale)
                - EditorUtil.getSceenInfo().mScreenHeight;
        WallpaperLog.d(TAG, "coors: " + coors + ", mImageCropHeight: "
                + mImageCropHeight + ", scale: " + scale);
        params.topMargin = (int) (Float.valueOf(coors[1]) * scale)
                - mImageCropHeight / 2;
        view.init(mAssetManager, info);
        view.setTag(info);
        addView(view, params);
        view.setText(info.content);
        view.setGravity(info.align);
    }
}
