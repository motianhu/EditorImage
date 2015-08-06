package com.smona.app.editorimage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

public class ZipFileAction {
    public void zip(String sourceFile, String effectFile, String xmlFile,
            String outputFileName) throws Exception {
        OutputStream output = new FileOutputStream(WallpaperUtil.RES_DIR_ZIP
                + outputFileName);
        CheckedOutputStream stream = new CheckedOutputStream(output,
                new CRC32());
        ZipOutputStream out = new ZipOutputStream(stream);
        compress(out, sourceFile, effectFile, xmlFile);
        out.closeEntry();
        out.close();
    }

    private void compress(ZipOutputStream out, String sourceFile,
            String effectFile, String xmlFile) {
        compressFile(WallpaperUtil.RES_DIR_SOURCE, sourceFile, ".jpg", ".jpg",
                out);
        compressFile(WallpaperUtil.RES_DIR_IMPRESSION, effectFile, ".jpg",
                "_font.jpg", out);
        compressFile(WallpaperUtil.RES_DIR_XML, xmlFile, ".xml", ".xml", out);
    }

    private void compressFile(String path, String name, String preffix,
            String targetSuffix, ZipOutputStream out) {
        File file = new File(path + name + preffix);
        if (!file.exists()) {
            return;
        }
        try {
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            ZipEntry entry = new ZipEntry(name + targetSuffix);
            out.putNextEntry(entry);
            int count;
            byte data[] = new byte[1024];
            while ((count = bis.read(data, 0, 1024)) != -1) {
                out.write(data, 0, count);
            }
            bis.close();
        } catch (Exception e) {
            WallpaperLog.d("ZipFileAction", "e: " + e.toString());
            throw new RuntimeException(e);
        }
    }
}
