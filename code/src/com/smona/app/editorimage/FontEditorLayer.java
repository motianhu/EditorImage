package com.smona.app.editorimage;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class FontEditorLayer extends FrameLayout implements IChangeText {

    private FontTextView mText;
    private View mDelete;

    public FontEditorLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mText = (FontTextView) findViewById(R.id.font);
        mDelete = findViewById(R.id.del);
        mDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                delSelf();
            }
        });
    }

    public void setTypeface(Typeface tf) {
        mText.setTypeface(tf);
    }

    private void delSelf() {
        ((ViewGroup) this.getParent()).removeView(this);
    }

    @Override
    public void setText(String text) {
        mText.setText(text);
    }

    public void setTextColor(String color) {
        mText.setTextColor(Color.parseColor(color));
    }

    public void setFontSize(int size) {
        mText.setTextSize(size);
    }

    @Override
    public String getText() {
        return mText.getText().toString();
    }

    public void init(AssetManager asset, FontInfo info) {
        Typeface typefaceRegular = Typeface.createFromAsset(asset, info.name);
        mText.setText(info.content);
        mText.setLines(info.line);
        if (!TextUtils.isEmpty(info.color) && !"#".equals(info.color)) {
            mText.setTextColor(Color.parseColor(info.color));
        }
        mText.setTextSize(info.fontSize);
        mText.setRotation(info.rotation);
        mText.setTypeface(typefaceRegular);
    }

    public void onClick(boolean click) {
        if (click) {
            showFrame();
        } else {
            hideFrame();
            hideDel();
        }
    }

    public void onLongClick(boolean longClick) {
        if (longClick) {
            showFrame();
            showDel();
        } else {
            hideFrame();
            hideDel();
        }
    }

    private void showDel() {
        actionDel(VISIBLE);
    }

    private void hideDel() {
        actionDel(GONE);
    }

    private void actionDel(int visible) {
        mDelete.setVisibility(visible);
    }

    private void showFrame() {
        actionFrame(true);
    }

    private void hideFrame() {
        actionFrame(false);
    }

    private void actionFrame(boolean isShow) {
        int alpha = isShow ? 1 : 0;
        setTextBgAlpha(alpha);
    }

    private void setTextBgAlpha(float alpha) {
        mText.setBgAlpha(alpha);
    }

    public Animator createAnim() {
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(5000);
        anim.addUpdateListener(new AnimatorUpdateListener() {

            @SuppressLint("NewApi")
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float a = animation.getAnimatedFraction();
                setTextBgAlpha(1 - a);
            }

        });
        anim.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                hideFrame();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });
        return anim;
    }
}
