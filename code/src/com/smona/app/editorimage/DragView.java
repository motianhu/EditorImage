package com.smona.app.editorimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;

public class DragView extends View {

    private float mRegistrationX;
    private float mRegistrationY;

    public DragView(Context context, float registrationX, float registrationY) {
        super(context);
        mRegistrationX = registrationX;
        mRegistrationY = registrationY;
    }

    @SuppressWarnings("deprecation")
    public LayoutParams createParams(View source) {
        Bitmap b = WallpaperUtil.createDragOutline(source);
        setBackgroundDrawable((new BitmapDrawable(getContext().getResources(),
                b)));
        LayoutParams params = (LayoutParams) source.getLayoutParams();
        LayoutParams dragParams = new LayoutParams(params);
        dragParams.width = source.getWidth();
        dragParams.height = source.getHeight();
        dragParams.leftMargin = source.getLeft();
        dragParams.topMargin = source.getTop();
        setRotation(source.getRotation());
        return dragParams;
    }

    public void move(float touchX, float touchY) {
        setTranslationX(touchX - mRegistrationX);
        setTranslationY(touchY - mRegistrationY);
    }
}
