package com.smona.app.editorimage;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.AssetManager;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

@SuppressLint("ClickableViewAccessibility")
public class ImageEditorLayer extends FrameLayout implements OnClickListener {
    private static final String TAG = "ImageEditorLayer";
    private OnLongClickListener mOnLongListener;

    private Vibrator mVibrator;
    private boolean mIsDragging = false;
    private DragView mDragView;
    private View mFontView;
    private AssetManager mAssetManager;

    private LastOpInfo mLastInfo = new LastOpInfo();

    private DetailActivity mActivity;

    public ImageEditorLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAssetManager = context.getAssets();
        mActivity = (DetailActivity) context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mVibrator = (Vibrator) getContext().getSystemService(
                Context.VIBRATOR_SERVICE);
    }

    private float[] mDownLoc = new float[2];

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            processDown(ev);
            break;
        case MotionEvent.ACTION_MOVE:
            processMove(ev);
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            processUp(ev);
            if (isMove(ev)) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean isMove(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        float deltaX = mDownLoc[0] - x;
        float deltaY = mDownLoc[1] - y;
        if (Math.abs(deltaX) > 20 || Math.abs(deltaY) > 20) {
            return true;
        } else {
            return false;
        }
    }

    private void processDown(MotionEvent ev) {
        mDownLoc[0] = ev.getX();
        mDownLoc[1] = ev.getY();
    }

    private void processMove(MotionEvent ev) {
        if (!mIsDragging && isMove(ev)) {
            View v = getTouchChildView(ev);
            if (v != null) {
                startDrag(v);
            }
        }

        if (!mIsDragging) {
            return;
        }

        float x = ev.getX();
        float y = ev.getY();
        mDragView.move(x, y);
    }

    private Rect mTmpRect = new Rect();

    private View getTouchChildView(MotionEvent ev) {
        int size = getChildCount();
        View view = null;

        float x = ev.getX();
        float y = ev.getY();

        for (int index = 0; index < size; index++) {
            view = getChildAt(index);
            if (view instanceof FontEditorLayer) {
                Rect rect = mTmpRect;
                view.getHitRect(rect);
                if (rect.contains((int) x, (int) y)) {
                    break;
                }
            }
        }
        return view;
    }

    private void processUp(MotionEvent ev) {
        stopDrag();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            processDown(ev);
            break;
        case MotionEvent.ACTION_MOVE:
            processMove(ev);
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            processUp(ev);
            if (isMove(ev)) {
                return true;
            }
            break;
        }
        return super.onTouchEvent(ev);
    }

    @SuppressLint("InflateParams")
    public void addFontTextView(FontInfo info) {
        FontEditorLayer view = (FontEditorLayer) LayoutInflater.from(
                getContext()).inflate(R.layout.font_editor_layer, null);
        view.setOnLongClickListener(mOnLongListener);
        view.setOnClickListener(this);
        WallpaperLog.d(TAG, "addFontTextView info: " + info);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        String[] coors = info.coord.split(",");
        info.content = info.content.replace(DetailActivity.HUANGHANG_SIGN,
                DetailActivity.HUANGHANG);
        params.leftMargin = (int) (Float.valueOf(coors[0]) * EditorUtil
                .getSceenInfo().mScreenScale);
        params.topMargin = (int) (Float.valueOf(coors[1]) * EditorUtil
                .getSceenInfo().mScreenScale);
        view.init(mAssetManager, info);
        view.setTag(info);
        addView(view, params);
        view.setText(info.content);
        view.setGravity(info.align);
    }

    public void startDrag(View v) {
        if (mIsDragging) {
            return;
        }

        processFontLayer(false);
        mIsDragging = true;
        mActivity.setMoveView(v);
        beginVibrator();
        beginDrag(v);
        processFontLayer(true);
    }

    public void processFontLayer(boolean isLong) {
        if (mFontView == null) {
            return;
        }
        ((FontEditorLayer) mFontView).onLongClick(isLong);
        if (!isLong) {
            mFontView = null;
        }
    }

    private void beginVibrator() {
        if (mVibrator != null) {
            mVibrator.vibrate(WallpaperUtil.VIBRATE_DURATION);
        }
    }

    public void onCreate(OnLongClickListener onLongListener) {
        mOnLongListener = onLongListener;
    }

    public void onResume() {
        animFontLayer();
    }

    private void animFontLayer() {
        int size = getChildCount();
        View view = null;
        AnimatorSet animSet = WallpaperAnimUtils.createAnimatorSet();
        animSet.setDuration(1000);
        for (int i = 0; i < size; i++) {
            view = getChildAt(i);
            if (view instanceof FontEditorLayer) {
                Animator anim = createAnimator((FontEditorLayer) view);
                animSet.play(anim);
            }
        }
        animSet.start();
    }

    private Animator createAnimator(final FontEditorLayer view) {
        return view.createAnim();

    }

    public void onPause() {
        stopDrag();
    }

    private void stopDrag() {
        if (!mIsDragging) {
            return;
        }
        dragComplete();
        mIsDragging = false;
        removeView(mDragView);
        mDragView = null;
    }

    private void dragComplete() {
        LayoutParams params = (LayoutParams) mFontView.getLayoutParams();
        params.leftMargin += mDragView.getTranslationX();
        params.topMargin += mDragView.getTranslationY();
        removeView(mFontView);
        addView(mFontView, params);
        mFontView.setVisibility(VISIBLE);
        requestLayout();
    }

    private void beginDrag(View v) {
        Context context = getContext();
        float registionX = mDownLoc[0];
        float registionY = mDownLoc[1];
        mDragView = new DragView(context, registionX, registionY);
        LayoutParams dragParams = mDragView.createParams(v);

        mLastInfo.saveLastPos(dragParams.leftMargin, dragParams.topMargin,
                (FontEditorLayer) v);

        addView(mDragView, dragParams);
        mFontView = v;
        v.setVisibility(INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        WallpaperLog.d(TAG, "v: " + v);
        if (v instanceof FontEditorLayer) {
            if (mActivity.isNormalState()) {
                showModifyDialog((FontEditorLayer) v);
            } else {
                FontInfo info = (FontInfo) v.getTag();
                if (info.isSelected) {
                    info.isSelected = false;
                    ((FontEditorLayer) v)
                            .setTextBgColor(R.drawable.bg_border_stroke);
                } else {
                    info.isSelected = true;
                    ((FontEditorLayer) v)
                            .setTextBgColor(R.drawable.bg_border_stroke_batch);
                }
            }
        }
    }

    private AlertDialog mDialog;

    @SuppressLint("InflateParams")
    private void showModifyDialog(final FontEditorLayer text) {
        cancelDialog();

        text.onClick(true);
        final FontInfo info = (FontInfo) text.getTag();

        Context context = getContext();
        mDialog = new AlertDialog.Builder(context, R.style.DialogStyle)
                .create();
        mDialog.show();

        InputTextLayer layout = (InputTextLayer) LayoutInflater.from(context)
                .inflate(R.layout.input_layer, null);
        layout.inits(info.name, text);

        final EditText edit = (EditText) layout
                .findViewById(R.id.input_content);
        final FontFamilySelector fontList = (FontFamilySelector) layout
                .findViewById(R.id.horizonized);

        final TextView fontSizeView = (TextView) layout
                .findViewById(R.id.fontSizeText);
        fontSizeView.setText(info.fontSize + "");

        final EditText fontColor = (EditText) layout
                .findViewById(R.id.fontColor);
        fontColor.setText(info.color);

        final SeekBar seekBar = (SeekBar) layout.findViewById(R.id.fontSize);
        seekBar.setProgress(info.fontSize);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                text.setFontSize(progress);
                info.fontSize = progress;
                fontSizeView.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                WallpaperLog.d("onStartTrackingTouch",
                        "progress: " + seekBar.getProgress());
                mLastInfo.saveLastFontSize(seekBar.getProgress(), text);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                WallpaperLog.d("onStopTrackingTouch", "progress: ");
            }

        });

        WallpaperLog.d(TAG, "showModifyDialog info: " + info);

        View finish = (View) layout.findViewById(R.id.input_finish);
        finish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String color = fontColor.getText().toString().replace(" ", "");
                if (!isColorValue(color)) {
                    Toast.makeText(getContext(), "颜色值不符合要求", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                text.setTextColor(color);

                String fontFamily = fontList.getSelectedFont();
                text.setTypeface(Typeface.createFromAsset(mAssetManager,
                        fontFamily));

                String content = edit.getText().toString();
                text.setText(content);
                info.name = fontFamily;
                info.content = content;
                info.color = color;
                mDialog.dismiss();
            }
        });

        mDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                text.onClick(false);
                String color = fontColor.getText().toString().replace(" ", "");
                if (!isColorValue(color)) {
                    Toast.makeText(getContext(), "颜色值不符合要求", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
            }

        });

        edit.setText(text.getText());
        // edit.setOnEditorActionListener(new OnEditorActionListener() {
        //
        // @Override
        // public boolean onEditorAction(TextView v, int actionId,
        // KeyEvent event) {
        // boolean isEnter = (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
        // if(isEnter) {
        // String content = v.getText() + "\n";
        // v.setText(content);
        // }
        // return isEnter;
        // }
        //
        // });
        edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String fontFamily = fontList.getSelectedFont();
                text.setTypeface(Typeface.createFromAsset(mAssetManager,
                        fontFamily));

                String content = edit.getText().toString();
                WallpaperLog.d(TAG, "afterTextChanged content = " + content);

                info.line = StringUtils.countStringNumbers(content, "\n") + 1;
                WallpaperLog.d(TAG, "afterTextChanged info.line = " + info.line
                        + ", content: " + content);
                info.name = fontFamily;
                info.content = content;

                text.setText(content);
                text.setLines(info.line);
            }
        });

        Window window = mDialog.getWindow();
        window.setContentView(layout);
        android.view.WindowManager.LayoutParams windowParams = window
                .getAttributes();
        window.setGravity(Gravity.BOTTOM);
        windowParams.width = EditorUtil.getSceenInfo().mScreenWidth;
        windowParams.x = 0;
        window.setAttributes(windowParams);
        mDialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        // mDialog.getWindow().setSoftInputMode(
        // WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // InputMethodManager inManager = (InputMethodManager) edit.getContext()
        // .getSystemService(Context.INPUT_METHOD_SERVICE);
        // inManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void cancelDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public ArrayList<FontInfo> acquisitionInfos() {
        ArrayList<FontInfo> infos = new ArrayList<FontInfo>();
        int size = getChildCount();
        View view = null;
        for (int index = 0; index < size; index++) {
            view = getChildAt(index);
            if (view instanceof FontEditorLayer) {
                FontInfo info = (FontInfo) view.getTag();
                if (info.content == null || "".endsWith(info.content.trim())) {
                    continue;
                }
                LayoutParams param = (LayoutParams) view.getLayoutParams();
                WallpaperLog.d(
                        TAG,
                        "line: "
                                + (StringUtils.countStringNumbers(info.content,
                                        "\n") + 1));
                info.coord = param.leftMargin
                        / EditorUtil.getSceenInfo().mScreenScale + ","
                        + param.topMargin
                        / EditorUtil.getSceenInfo().mScreenScale;
                info.line = StringUtils.countStringNumbers(info.content, "\n") + 1;
                infos.add(info);
            }
        }
        return infos;
    }

    public static boolean isColorValue(String mobiles) {
        Pattern p = Pattern.compile("^#[0-9a-fA-F]{6}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public void resetLastOp() {
        mLastInfo.resetFeature();
    }

    public void loadViews(ArrayList<FontInfo> infos) {
        mLastInfo.resetAll();
        int size = infos.size();
        for (int i = 0; i < size; i++) {
            addFontTextView(infos.get(i));
        }
    }

    public void alignLeft() {
        int size = getChildCount();
        View view = null;
        int leftMargin = Integer.MAX_VALUE;
        for (int index = 0; index < size; index++) {
            view = getChildAt(index);
            if (view instanceof FontEditorLayer) {
                FontInfo info = (FontInfo) view.getTag();
                if (info.isSelected) {
                    LayoutParams param = (LayoutParams) view.getLayoutParams();
                    if (leftMargin > param.leftMargin) {
                        leftMargin = param.leftMargin;
                    }
                }
            }
        }

        for (int index = 0; index < size; index++) {
            view = getChildAt(index);
            if (view instanceof FontEditorLayer) {
                FontInfo info = (FontInfo) view.getTag();
                if (info.isSelected) {
                    LayoutParams param = (LayoutParams) view.getLayoutParams();
                    param.leftMargin = leftMargin;
                }
            }
        }
        this.requestLayout();
    }

    public void alignRight() {
        int size = getChildCount();
        View view = null;
        int rightMargin = Integer.MIN_VALUE;
        for (int index = 0; index < size; index++) {
            view = getChildAt(index);
            if (view instanceof FontEditorLayer) {
                FontInfo info = (FontInfo) view.getTag();
                if (info.isSelected) {
                    LayoutParams param = (LayoutParams) view.getLayoutParams();
                    if (rightMargin < (param.leftMargin + param.width)) {
                        rightMargin = (param.leftMargin + param.width);
                    }
                }
            }
        }

        for (int index = 0; index < size; index++) {
            view = getChildAt(index);
            if (view instanceof FontEditorLayer) {
                FontInfo info = (FontInfo) view.getTag();
                if (info.isSelected) {
                    LayoutParams param = (LayoutParams) view.getLayoutParams();
                    param.leftMargin = rightMargin - param.width;
                }
            }
        }
        this.requestLayout();
    }

    public void alignCenter() {
        int size = getChildCount();
        View view = null;
        int scrennCenter = EditorUtil.getSceenInfo().mScreenWidth / 2;

        for (int index = 0; index < size; index++) {
            view = getChildAt(index);
            if (view instanceof FontEditorLayer) {
                FontInfo info = (FontInfo) view.getTag();
                if (info.isSelected) {
                    LayoutParams param = (LayoutParams) view.getLayoutParams();
                    param.leftMargin = scrennCenter - param.width / 2;
                }
            }
        }
        this.requestLayout();
    }

    public void resetNormal() {
        int size = getChildCount();
        View view = null;
        for (int index = 0; index < size; index++) {
            view = getChildAt(index);
            if (view instanceof FontEditorLayer) {
                FontInfo info = (FontInfo) view.getTag();
                info.isSelected = false;
                ((FontEditorLayer) view)
                        .setTextBgColor(R.drawable.bg_border_stroke);
            }
        }
    }
}
