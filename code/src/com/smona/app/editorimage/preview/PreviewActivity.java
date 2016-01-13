package com.smona.app.editorimage.preview;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.smona.app.editorimage.EditorUtil;
import com.smona.app.editorimage.FontInfo;
import com.smona.app.editorimage.PropertiesUtils;
import com.smona.app.editorimage.R;
import com.smona.app.editorimage.WallpaperLog;
import com.smona.app.editorimage.WallpaperUtil;
import com.smona.app.editorimage.ZipManager;
import com.smona.app.editorimage.config.Config;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PreviewActivity extends Activity implements OnClickListener {
    private static final String TAG = "PreviewActivity";

    private static final boolean INTENERL = Config.INTENERL;

    private TextView mFileName = null;

    private String mReadUrl = null;

    private ZipManager mZipMgr;
    private ArrayList<String> mZipFiles = new ArrayList<String>();

    private ImageEditorLayer mImageLayer;
    private int[][] screens = new int[][] { { 1440, 2560 }, { 1080, 1920 },
            { 720, 1280 }, { 540, 960 }, { 480, 854 }, { 480, 800 } };

    private float[] scales = new float[] { 4.0f, 3.0f, 2.0f, 1.5f, 1.334f,
            1.25f };

    private String mCurrentZip = null;
    private int mCurPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview);
        initZipMgr();
        String file = getIntent().getStringExtra(
                WallpaperUtil.CURRENT_FILE_NAME);
        readAllZip();
        if (TextUtils.isEmpty(file)) {
            showMessage("请在编辑界面保存后再预览!现在展示的是所有已编辑过的图片");
        } else {
            readZip(file);
            if (TextUtils.isEmpty(mCurrentZip)) {
                showMessage("请在编辑界面保存后再预览");
            }
        }
        initViews();
    }

    private void initViews() {
        mImageLayer = (ImageEditorLayer) findViewById(R.id.editor_layer);
        if (TextUtils.isEmpty(mCurrentZip)) {
            return;
        }

        mFileName = (TextView) findViewById(R.id.file_name);
        mFileName.setText(mCurrentZip);
        findViewById(R.id.previous).setOnClickListener(this);
        findViewById(R.id.next).setOnClickListener(this);

        findViewById(R.id.read_url).setOnClickListener(this);
        findViewById(R.id.read_url).setVisibility(
                INTENERL ? View.GONE : View.VISIBLE);

        setEditorBg();
    }

    private void setEditorBg() {
        float width = EditorUtil.getSceenInfo().mScreenWidth;
        WallpaperLog.d(
                TAG,
                "scale: "
                        + width
                        + ", month_day_text_padding: "
                        + this.getResources().getDimensionPixelSize(
                                R.dimen.month_day_text_padding));
        int index = 0;
        for (; index < screens.length; index++) {
            if (Math.abs((screens[index][0] - width)) <= 0.0009) {
                setFontAndBitmap(index);
                break;
            }
        }
    }

    private void setFontAndBitmap(int index) {
        float scale = scales[index];
        int cookie = mZipMgr.addZipPath(getZipDir() + mCurrentZip);
        String fileName = mCurrentZip.substring(0, mCurrentZip.length() - 4);
        ArrayList<FontInfo> infos = readFontInfo(cookie, fileName);
        mImageLayer.loadViews(infos, scale);
        Bitmap bitmap = readBitmap(cookie, fileName);
        WallpaperLog.d(TAG, "setFontAndBitmap fileName: " + fileName
                + "; infos: " + infos + ", bitmap: " + bitmap);
        if (bitmap != null) {
            Bitmap temp = WallpaperUtil.resizeBitmap(bitmap, screens[index][0],
                    screens[index][1]);
            WallpaperLog.d(TAG,
                    "temp: " + temp.getWidth() + "," + temp.getHeight());
            mImageLayer.setBackgroundDrawable(new BitmapDrawable(
                    getResources(), temp));
        }

        if (INTENERL) {
            return;
        }
        readReadUrl(cookie, fileName);
    }

    private void readReadUrl(int cookie, String fileName) {
        try {
            InputStream in = readInstream(cookie, fileName + Config.PROPERTIES);
            mReadUrl = (String) PropertiesUtils.getProperty(in, "read_url");
        } catch (IOException e) {
            e.printStackTrace();
        }
        WallpaperLog.d(TAG, "setFontAndBitmap mReadUrl: " + mReadUrl);
    }

    private String getZipDir() {
        return WallpaperUtil.RES_DIR_ZIP;
    }

    private void setScreenSize(int index) {
        int w = screens[index][0];
        int h = screens[index][1];
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mImageLayer
                .getLayoutParams();
        params.width = w;
        params.height = h;
        params.leftMargin = 0;
        params.topMargin = 0;
        mImageLayer.setLayoutParams(params);

        setFontAndBitmap(index);
    }

    private void initZipMgr() {
        mZipMgr = new ZipManager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void readAllZip() {
        File root = new File(getZipDir());
        String[] files = root.list();
        if (files == null) {
            return;
        }
        for (String filePath : files) {
            WallpaperLog.d(TAG, "aquireFontInfo filePath: " + filePath);
            if (filePath.endsWith(Config.ZIP)) {
                mZipFiles.add(filePath);
            }
        }

        if (mZipFiles.size() <= 0) {
            return;
        }

        mCurrentZip = mZipFiles.get(0);
        mCurPos = 0;
    }

    private void readZip(String file) {
        String zipName = file.replace(Config.JPG, Config.ZIP);
        int size = mZipFiles.size();
        for (int i = 0; i < size; i++) {
            String filePath = mZipFiles.get(i);
            WallpaperLog.d(TAG, "aquireFontInfo filePath: " + filePath);
            if (filePath.equals(zipName)) {
                mCurrentZip = filePath;
                mCurPos = i;
                break;
            }
        }
    }

    private ArrayList<FontInfo> readFontInfo(int cookie, String xmlName) {
        ArrayList<FontInfo> infos = new ArrayList<FontInfo>();
        try {
            readXML(cookie, xmlName + Config.XML, infos);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return infos;
    }

    private Bitmap readBitmap(int cookie, String fileName) {
        InputStream is = null;
        try {

            is = mZipMgr.getEntryInputStream(cookie, fileName + Config.JPG);
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void readXML(int cookie, String fileName, ArrayList<FontInfo> infos)
            throws IOException {
        InputStream is = readInstream(cookie, fileName);
        if (is == null) {
            return;
        }
        parseXml(is, infos);
    }

    private InputStream readInstream(int cookie, String fileName)
            throws IOException {
        InputStream is = mZipMgr.getEntryInputStream(cookie, fileName);
        return is;
    }

    private void parseXml(InputStream is, ArrayList<FontInfo> infos) {
        SAXReader saxReader = new SAXReader();
        Document document;
        try {
            document = saxReader.read(new BufferedReader(new InputStreamReader(
                    is, "UTF-8")));
            Element root = document.getRootElement();
            List<Element> childs = root.elements();
            FontInfo info;
            Attribute attr;
            for (Element e : childs) {
                info = new FontInfo();
                attr = e.attribute("content");
                info.content = attr.getText();
                attr = e.attribute("size");
                info.fontSize = Integer.valueOf(attr.getText());
                attr = e.attribute("coordinate");
                info.coord = attr.getText();
                attr = e.attribute("color");
                info.color = attr.getText();
                attr = e.attribute("line");
                info.line = Integer.valueOf(attr.getText());
                attr = e.attribute("name");
                info.name = attr.getText();
                attr = e.attribute("align");
                info.align = attr != null ? attr.getText() : "center";
                attr = e.attribute("rotation");
                info.rotation = Float.valueOf(attr.getText());
                infos.add(info);
            }
        } catch (DocumentException e) {
            WallpaperLog.d(TAG, "parseXml DocumentException: " + e.toString());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            WallpaperLog.d(TAG,
                    "parseXml UnsupportedEncodingException: " + e.toString());
            e.printStackTrace();
        } catch (@SuppressWarnings("hiding") IOException e) {
            WallpaperLog.d(TAG, "parseXml IOException: " + e.toString());
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Menu.FIRST + 1, 1, "1440").setIcon(
                R.drawable.ic_launcher);
        menu.add(Menu.NONE, Menu.FIRST + 2, 2, "1080").setIcon(
                R.drawable.ic_launcher);
        menu.add(Menu.NONE, Menu.FIRST + 3, 3, "720").setIcon(
                R.drawable.ic_launcher);
        menu.add(Menu.NONE, Menu.FIRST + 4, 4, "540").setIcon(
                R.drawable.ic_launcher);
        menu.add(Menu.NONE, Menu.FIRST + 5, 5, "480(854)").setIcon(
                R.drawable.ic_launcher);
        menu.add(Menu.NONE, Menu.FIRST + 6, 6, "480(800)").setIcon(
                R.drawable.ic_launcher);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case Menu.FIRST + 1:
            setScreenSize(0);
            break;
        case Menu.FIRST + 2:
            setScreenSize(1);
            break;
        case Menu.FIRST + 3:
            setScreenSize(2);
            break;
        case Menu.FIRST + 4:
            setScreenSize(3);
            break;
        case Menu.FIRST + 5:
            setScreenSize(4);
            break;
        }
        return false;
    }

    private void showMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        case R.id.previous:
            clickPrevios();
            break;
        case R.id.next:
            clickNext();
            break;
        case R.id.read_url:
            gotoBrowser();
            break;
        }
    }

    private void clickNext() {
        int count = mZipFiles.size();
        if (count == 0) {
            return;
        }
        int pos = mCurPos;
        pos += 1;
        if (pos == count) {
            Toast.makeText(this, "已经是最后一张了", Toast.LENGTH_SHORT).show();
            return;
        }
        mCurrentZip = mZipFiles.get(pos);
        mFileName.setText(mCurrentZip);
        setEditorBg();
        mCurPos += 1;
    }

    @SuppressLint("NewApi")
    private void clickPrevios() {
        int count = mZipFiles.size();
        if (count == 0) {
            return;
        }
        int pos = mCurPos;
        pos -= 1;
        if (pos == -1) {
            Toast.makeText(this, "已经是第一张了", Toast.LENGTH_SHORT).show();
            return;
        }

        mCurrentZip = mZipFiles.get(pos);
        mFileName.setText(mCurrentZip);
        setEditorBg();
        mCurPos -= 1;
    }

    private void gotoBrowser() {
        if (TextUtils.isEmpty(mReadUrl)) {
            Toast.makeText(this, "URL不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = Uri.parse(mReadUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}
