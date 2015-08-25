package com.smona.app.editorimage;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;

public class InputTextLayer extends LinearLayout {

    private FontFamilySelector mFontFamily;
    private EditText mInputEdit;
    
    private ArrayList<String> mSort = new ArrayList<String>();

    public InputTextLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
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

    public void inits(String font, FontEditorLayer fontEditorLayer) {
        mFontFamily.setSelectedFont(mInputEdit, font);
        mFontFamily.setShowFontEditorLayer(fontEditorLayer);
        mFontFamily.setupSimpleList(mSort);
    }
}
