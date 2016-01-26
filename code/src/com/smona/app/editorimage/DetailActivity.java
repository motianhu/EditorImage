package com.smona.app.editorimage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.smona.app.editorimage.config.Config;
import com.smona.app.editorimage.preview.PreviewActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;

public class DetailActivity extends Activity implements OnLongClickListener,
        OnClickListener {

    private static final boolean INTENERL = Config.INTENERL;

    private static final String TAG = "DetailActivity";
    private ImageEditorLayer mEditorLayer;

    enum State {
        NORMAL, BATCH
    }

    private State mCurrentState = State.NORMAL;

    private ArrayList<String> mImpressionFiles = new ArrayList<String>();

    private int mCurPos = 0;
    private ZipManager mZipMgr;

    private String mReadUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EditorUtil.initParams(this);
        initEnv();
        setContentView(R.layout.detail);

        initFiles();

        initViews();
    }

    private void initEnv() {
        mkdir(getSourceDir());
        mkdir(getImpressDir());
        mkdir(getZipDir());
        mkdir(getXMLDir());
    }

    private void mkdir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private void initFiles() {
        ArrayList<String> zipFiles = new ArrayList<String>();
        initFiles(getZipDir(), zipFiles, Config.ZIP);
        initFiles(getImpressDir(), mImpressionFiles);
        sortFiles(mImpressionFiles, zipFiles);

        ArrayList<String> sourceFiles = new ArrayList<String>();
        initFiles(getSourceDir(), sourceFiles);
        diff(sourceFiles, mImpressionFiles);
    }

    private void initFiles(String path, ArrayList<String> list) {
        initFiles(path, list, Config.JPG);
    }

    private void initFiles(String path, ArrayList<String> list, String suffix) {
        File root = new File(path);
        File[] files = root.listFiles();
        if (files == null) {
            return;
        }
        String rename;
        for (File filePath : files) {
            if (filePath.getName().endsWith(suffix)) {
                rename = rename(path, filePath.getName());
                list.add(rename);
            }
        }
    }

    private void sortFiles(ArrayList<String> impress, ArrayList<String> sortList) {
        int iCount = impress.size();
        int sCount = sortList.size();
        String impPath;
        String zipPath;
        for (int i = iCount - 1; i >= 0; i--) {
            impPath = impress.get(i);
            for (int j = 0; j < sCount; j++) {
                zipPath = sortList.get(j);
                boolean pipei = impPath.substring(0, impPath.length() - 4)
                        .equalsIgnoreCase(
                                zipPath.substring(0, zipPath.length() - 4));
                if (pipei) {
                    impress.add(impress.remove(i));
                    break;
                }
            }
        }
    }

    private void diff(ArrayList<String> source, ArrayList<String> impress) {
        int iCount = impress.size();
        int sCount = source.size();
        int count = 0;
        String impPath;
        String srcPath;
        int j = 0;
        WallpaperLog.d(TAG, "iCount: " + iCount + ",sCount: " + sCount);
        for (int i = iCount - 1; i >= 0; i--) {
            impPath = impress.get(i);
            for (j = sCount - 1; j >= 0; j--) {
                srcPath = source.get(j);
                WallpaperLog.d(TAG, "srcPath: " + srcPath + ",impPath: "
                        + impPath);
                if (impPath.equalsIgnoreCase(srcPath)) {
                    break;
                }
            }
            if (j < 0) {
                count++;
                impress.remove(i);
            }
        }

        WallpaperLog.d(TAG, "diff finish impress.size(): " + impress.size());
        if (count > 0) {
            showMessage("有" + count + "张效果图没有对应的原图");
        }
    }

    private String rename(String path, String oldPath) {
        File file = new File(path + oldPath);
        file.renameTo(new File(path + oldPath.replaceAll(" ", "_")));
        WallpaperLog.d(TAG, "rename old: " + (path + oldPath) + ", new: "
                + file.getName());
        return file.getName();
    }

    @SuppressLint("NewApi")
    private void initViews() {
        mEditorLayer = (ImageEditorLayer) findViewById(R.id.editor_layer);
        mEditorLayer.onCreate(this);
        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.next).setOnClickListener(this);
        findViewById(R.id.previous).setOnClickListener(this);
        findViewById(R.id.preview).setOnClickListener(this);
        findViewById(R.id.batch).setOnClickListener(this);

        // orien
        findViewById(R.id.left).setOnClickListener(this);
        findViewById(R.id.right).setOnClickListener(this);
        findViewById(R.id.top).setOnClickListener(this);
        findViewById(R.id.bottom).setOnClickListener(this);

        // batch
        findViewById(R.id.recovery).setOnClickListener(this);
        findViewById(R.id.aligin_left).setOnClickListener(this);
        findViewById(R.id.aligin_center).setOnClickListener(this);
        findViewById(R.id.aligin_right).setOnClickListener(this);
        findViewById(R.id.reset_last).setOnClickListener(this);

        // align
        findViewById(R.id.text_left_algin).setOnClickListener(this);
        findViewById(R.id.text_left_algin).setVisibility(
                Config.SUPPORT_ENTER ? View.VISIBLE : View.GONE);
        findViewById(R.id.text_center_algin).setOnClickListener(this);
        findViewById(R.id.text_center_algin).setVisibility(
                Config.SUPPORT_ENTER ? View.VISIBLE : View.GONE);
        findViewById(R.id.text_right_algin).setOnClickListener(this);
        findViewById(R.id.text_right_algin).setVisibility(
                Config.SUPPORT_ENTER ? View.VISIBLE : View.GONE);

        // read link
        findViewById(R.id.read_url).setOnClickListener(this);
        findViewById(R.id.read_url).setVisibility(
                INTENERL ? View.GONE : View.VISIBLE);

        initZipMgr();

        int count = mImpressionFiles.size();
        WallpaperLog.d(TAG, "initViews count=" + count);
        if (count > 0) {
            setEditorBackground(0);
        } else {
            showMessage("没有效果图或者原图没有对应的效果图");
        }
    }

    private View mMoveView = null;

    @Override
    public boolean onLongClick(View v) {
        mEditorLayer.startDrag(v);
        return true;
    }

    public void setMoveView(View v) {
        mMoveView = v;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        case R.id.add:
            clickAdd();
            break;
        case R.id.next:
            clickNext();
            this.alignLeft();
            break;
        case R.id.previous:
            clickPrevios();
            this.alignRight();
            break;
        case R.id.save:
            clickSave();
            break;
        case R.id.preview:
            clickPreview();
            break;
        case R.id.batch:
            clickBatch(true);
            break;
        case R.id.left:
            clickLeft();
            break;
        case R.id.right:
            clickRight();
            break;
        case R.id.top:
            clickTop();
            break;
        case R.id.bottom:
            clickBottom();
            break;
        case R.id.recovery:
            clickBatch(false);
            break;
        case R.id.aligin_left:
            alignLeft();
            break;
        case R.id.aligin_center:
            alignCenter();
            break;
        case R.id.aligin_right:
            alignRight();
            break;
        case R.id.reset_last:
            resetLastOp();
            break;
        case R.id.text_left_algin:
            clickAlign("left");
            break;
        case R.id.text_center_algin:
            clickAlign("center");
            break;
        case R.id.text_right_algin:
            clickAlign("right");
            break;
        case R.id.read_url:
            clickReadUrl();
            break;
        }
    }

    private static final int STEP = 4;

    private void clickLeft() {
        if (mMoveView == null) {
            return;
        }
        LayoutParams param = (LayoutParams) mMoveView.getLayoutParams();
        param.leftMargin -= STEP;
        mEditorLayer.removeView(mMoveView);
        mEditorLayer.addView(mMoveView, param);
    }

    private void clickRight() {
        if (mMoveView == null) {
            return;
        }
        LayoutParams param = (LayoutParams) mMoveView.getLayoutParams();
        param.leftMargin += STEP;
        mEditorLayer.removeView(mMoveView);
        mEditorLayer.addView(mMoveView, param);
    }

    private void clickTop() {
        if (mMoveView == null) {
            return;
        }
        LayoutParams param = (LayoutParams) mMoveView.getLayoutParams();
        param.topMargin -= STEP;
        mEditorLayer.removeView(mMoveView);
        mEditorLayer.addView(mMoveView, param);
    }

    private void clickBottom() {
        if (mMoveView == null) {
            return;
        }
        LayoutParams param = (LayoutParams) mMoveView.getLayoutParams();
        param.topMargin += STEP;
        mEditorLayer.removeView(mMoveView);
        mEditorLayer.addView(mMoveView, param);
    }

    private void clickPreview() {
        Intent intent = new Intent();
        intent.setClass(this, PreviewActivity.class);

        String fileName = (String) mEditorLayer.getTag();
        intent.putExtra(WallpaperUtil.CURRENT_FILE_NAME, fileName);
        startActivity(intent);
    }

    private void clickBatch(boolean enterBatch) {
        mCurrentState = enterBatch ? State.BATCH : State.NORMAL;
        if (enterBatch) {
            findViewById(R.id.batch_container).setVisibility(View.VISIBLE);
            findViewById(R.id.normal).setVisibility(View.GONE);
        } else {
            mEditorLayer.resetNormal();
            findViewById(R.id.normal).setVisibility(View.VISIBLE);
            findViewById(R.id.batch_container).setVisibility(View.GONE);
        }
    }

    @SuppressLint("NewApi")
    private void clickNext() {
        int count = mImpressionFiles.size();
        if (count == 0) {
            return;
        }
        int pos = mCurPos;
        pos += 1;
        if (pos == count) {
            Toast.makeText(this, "已经是最后一张了", Toast.LENGTH_SHORT).show();
            return;
        }

        setEditorBackground(pos);

        mCurPos += 1;
    }

    private void resetLastOp() {
        mEditorLayer.resetLastOp();
    }

    public boolean isNormalState() {
        return State.NORMAL.equals(mCurrentState);
    }

    @SuppressLint("NewApi")
    private void clickPrevios() {
        int count = mImpressionFiles.size();
        if (count == 0) {
            return;
        }
        int pos = mCurPos;
        pos -= 1;
        if (pos == -1) {
            Toast.makeText(this, "已经是第一张了", Toast.LENGTH_SHORT).show();
            return;
        }

        setEditorBackground(pos);

        mCurPos -= 1;
    }

    private void clickAlign(String align) {
        if (mMoveView == null) {
            return;
        }
        Object tag = mMoveView.getTag();
        if (tag instanceof FontInfo) {
            ((FontInfo) tag).align = align;
        }
        FontEditorLayer text = (FontEditorLayer) mMoveView;
        text.setGravity(align);
    }

    private void clickReadUrl() {
        if (INTENERL) {
            return;
        }
        final EditText edit = new EditText(this);
        edit.setText(mReadUrl);

        new AlertDialog.Builder(this)
                .setTitle("请输入阅读链接")
                .setView(edit)
                .setPositiveButton("确定",
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                String url = edit.getText().toString();
                                if (!TextUtils.isEmpty(url)) {
                                    String picName = (String) mEditorLayer
                                            .getTag();
                                    String simpleFileName = picName.replace(
                                            Config.JPG, "");
                                    writeConfig(simpleFileName, edit.getText()
                                            .toString());
                                }
                            }
                        }).setNegativeButton("取消", null).create().show();
    }

    private void setEditorBackground(int pos) {
        String filePath = mImpressionFiles.get(pos);
        String simpleFileName = filePath;
        simpleFileName = simpleFileName.replace(Config.JPG, "");
        mEditorLayer.setTag(filePath);
        mEditorLayer.setBackground(new BitmapDrawable(this.getResources(),
                BitmapFactory.decodeFile(getImpressDir() + filePath)));

        File zipFile = new File(getZipDir() + simpleFileName + Config.ZIP);
        mEditorLayer.removeAllViews();
        if (zipFile.exists()) {
            setFontText(simpleFileName + Config.ZIP);
        }
    }

    private void initZipMgr() {
        mZipMgr = new ZipManager();
    }

    private void setFontText(String zipFile) {
        int cookie = mZipMgr.addZipPath(getZipDir() + zipFile);
        String fileName = zipFile.substring(0, zipFile.length() - 4);
        ArrayList<FontInfo> infos = readFontInfo(cookie, fileName);
        mEditorLayer.loadViews(infos);

        if (INTENERL) {
            return;
        }
        mReadUrl = (String) PropertiesUtils.getProperty(fileName, "read_url");
    }

    private void clickAdd() {
        Toast.makeText(this, "Add Text", Toast.LENGTH_SHORT).show();
        FontInfo info = new FontInfo();
        info.content = " ";
        info.coord = "28,170";
        info.line = 1;
        info.color = "#ffffff";
        info.fontSize = 36;
        info.align = "center";
        info.name = "DroidSansFallback.ttf";
        mEditorLayer.addFontTextView(info);
        mEditorLayer.onResume();
    }

    private void clickSave() {
        ArrayList<FontInfo> infos = mEditorLayer.acquisitionInfos();
        if (infos.size() < 1) {
            showMessage("有文字才可保存!请新添加文字");
            return;
        }

        try {
            String picName = (String) mEditorLayer.getTag();
            WallpaperLog.d(TAG, "picName: " + picName + ",infos: " + infos);
            String simpleFileName = picName;
            simpleFileName = simpleFileName.replace(Config.JPG, "");
            writeXML(simpleFileName, infos);
            ZipFileAction action = new ZipFileAction();
            action.zip(simpleFileName, simpleFileName, simpleFileName,
                    simpleFileName, simpleFileName + Config.ZIP);

            Toast.makeText(this, "Save success", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            WallpaperLog.d(TAG, "e: " + e.toString());
            e.printStackTrace();
            Toast.makeText(this, "Save failed! IOException ",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Save failed! Exception ", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void writeXML(String fileName, ArrayList<FontInfo> infos)
            throws IOException {
        Document document = DocumentHelper.createDocument();
        document.setXMLEncoding("UTF-8");
        Element root = document.addElement("font-family");
        for (FontInfo info : infos) {
            writeFont(root, info);
        }
        FileOutputStream fos = new FileOutputStream(getXMLDir() + fileName
                + Config.XML);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        OutputFormat of = new OutputFormat();
        of.setEncoding("UTF-8");
        of.setIndent(true);
        XMLWriter writer = new XMLWriter(osw, of);
        writer.write(document);
        writer.close();
    }

    private void writeFont(Element root, FontInfo info) {
        Element font = root.addElement("font");
        WallpaperLog.d(TAG, "info.content: " + info.content);
        if (Config.SUPPORT_ENTER) {
            info.content = info.content.replace(Config.NEW_LINE,
                    Config.NEW_LINE_SIGN);
        }
        WallpaperLog.d(TAG, "info.content: " + info.content);
        font.addAttribute("content", info.content);
        font.addAttribute("size", info.fontSize + "");
        font.addAttribute("coordinate", info.coord);
        font.addAttribute("color", info.color);
        font.addAttribute("align", info.align);
        font.addAttribute("line", info.line + "");
        font.addAttribute("name", info.name);
        font.addAttribute("rotation", info.rotation + "");
    }

    private void writeConfig(String file, String url) {
        String filename = getXMLDir() + file + ".properties";
        try {
            String value = (String) PropertiesUtils.getProperty(filename,
                    "read_url");
            WallpaperLog.d(TAG, "writeConfig value: " + value);
            PropertiesUtils.save(filename, "read_url", url.trim());
        } catch (IOException e) {
            e.printStackTrace();
            WallpaperLog.d(TAG, "writeConfig e: " + e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    private void readXML(int cookie, String fileName, ArrayList<FontInfo> infos)
            throws IOException {
        InputStream is = mZipMgr.getEntryInputStream(cookie, fileName);
        if (is == null) {
            return;
        }
        parseXml(is, infos);
    }

    private void parseXml(InputStream is, ArrayList<FontInfo> infos) {
        SAXReader saxReader = new SAXReader();
        Document document;
        try {
            document = saxReader.read(new BufferedReader(new InputStreamReader(
                    is, "UTF-8")));
            Element root = document.getRootElement();
            @SuppressWarnings("unchecked")
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

    private void showMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private String getSourceDir() {
        return WallpaperUtil.RES_DIR_SOURCE;
    }

    private String getImpressDir() {
        return WallpaperUtil.RES_DIR_IMPRESSION;
    }

    private String getZipDir() {
        return WallpaperUtil.RES_DIR_ZIP;
    }

    private String getXMLDir() {
        return WallpaperUtil.RES_DIR_XML;
    }

    private void alignLeft() {
        mEditorLayer.alignLeft();
    }

    private void alignRight() {
        mEditorLayer.alignRight();
    }

    private void alignCenter() {
        mEditorLayer.alignCenter();
    }
}
