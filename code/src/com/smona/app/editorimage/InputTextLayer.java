package com.smona.app.editorimage;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;

public class InputTextLayer extends LinearLayout {

    private AssetManager mAsset;
    private FontFamilySelector mFontFamily;
    private EditText mInputEdit;
    
    private ArrayList<String> mSort = new ArrayList<String>();

    public InputTextLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAsset = context.getAssets();
        mSort.add("Broadw.ttf");
        mSort.add("HelveticaNeueUltraLight.ttf");
        mSort.add("Imprisha.ttf");
        mSort.add("LearningCurveDashed_OT.otf");
        mSort.add("DroidSansFallback.ttf");
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mFontFamily = (FontFamilySelector) findViewById(R.id.horizonized);
        mInputEdit = (EditText) findViewById(R.id.input_content);
    }

    public void inits(String font) {
        mFontFamily.setSelectedFont(mInputEdit, font);
//        ArrayList<String> datas = new ArrayList<String>();
//        try {
//            String[] files = mAsset.list("");
//            for (String file : files) {
//                if (file.endsWith(".ttf") || file.endsWith(".otf")) {
//                    datas.add(file);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        mFontFamily.setupSimpleList(mSort);
    }
}
