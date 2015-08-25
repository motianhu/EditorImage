package com.smona.app.editorimage;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

public class FontFamilySelector extends HorizontalListView {
    private static final String TAG = "FontFamilySelector";
    private String mSelectedFont;
    private TextView mInputContent;
    private FontFamilyAdapter mAdapter;
    private AssetManager mAsset;
    private FontEditorLayer fontEditorLayer;

    public FontFamilySelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAsset = context.getAssets();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOnItemClickListener(mItemClick);
        WallpaperLog.d(TAG, "onFinishInflate ");
    }

    public String getSelectedFont() {
        return mSelectedFont;
    }

    public void setShowFontEditorLayer(FontEditorLayer fontEditorLayer) {
        this.fontEditorLayer = fontEditorLayer;
    }

    public void setSelectedFont(TextView content, String name) {
        mInputContent = content;
        mSelectedFont = name;
        setTypeface();
    }

    public void setupSimpleList(ArrayList<String> datas) {
        mAdapter = new FontFamilyAdapter(getContext(), datas);
        mAdapter.setSelectedFont(mSelectedFont);
        setAdapter(mAdapter);
    }

    private OnItemClickListener mItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            int count = parent.getChildCount();
            mSelectedFont = (String) view.getTag();
            mAdapter.setSelectedFont(mSelectedFont);
            int viewPosition = position - getmLeftViewIndex() - 1;
            WallpaperLog.d(TAG, "mItemClick onClick  position = " + position + " viewPosition= " + viewPosition);
            for (int index = 0; index < count; index++) {
                parent.getChildAt(index).setSelected(index == viewPosition);
            }
            setTypeface();
        }
    };

    private void setTypeface() {
        WallpaperLog.d(TAG, "setTypeface mSelectedFont： " + mSelectedFont + " mInputContent:" + mInputContent + " fontEditorLayer:" + fontEditorLayer + " mSelectedFont:" + mSelectedFont);
        if (TextUtils.isEmpty(mSelectedFont)) {
            return;
        }

        if (mInputContent == null) {
            return;
        }

        if (mSelectedFont.endsWith(".ttf") || mSelectedFont.endsWith(".otf")) {
            Typeface typeface = Typeface.createFromAsset(mAsset, mSelectedFont);
            mInputContent.setTypeface(typeface);
            if (fontEditorLayer != null) {
                fontEditorLayer.setTypeface(typeface);
            }
        }
    }

}

