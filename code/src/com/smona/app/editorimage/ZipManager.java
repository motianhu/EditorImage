package com.smona.app.editorimage;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipManager {

    private static final String TAG = ZipManager.class.getSimpleName();

    private Vector<String> mZipPaths = new Vector<String>();
    private LinkedHashMap<String, ZipFile> mLinkedZipFile = new LinkedHashMap<String, ZipFile>();
    private boolean mClosed = false;

    public ZipManager() {

    }


    public int addZipPath(String zipPath) {
        File zipFile = new File(zipPath);
        if (!zipFile.exists()) {
            return -1;
        }


        int index = mZipPaths.indexOf(zipPath);
        if (index != -1) {
            return index;

        }
        index = mZipPaths.size();
        mZipPaths.add(zipPath);

        return index;
    }

    public synchronized InputStream getEntryInputStream(int cookie, String filePath) throws IOException {

        ZipFile zipFile = getZipFile(cookie);
        if (zipFile != null) {
            ZipEntry zipEntry = new ZipEntry(filePath);
            return zipFile.getInputStream(zipEntry);
        }

        return null;
    }

    public void close() {

        synchronized (this) {
            mClosed = true;

            Set<Map.Entry<String, ZipFile>> entrySet = mLinkedZipFile.entrySet();
            for (Map.Entry<String, ZipFile> entry : entrySet) {
                try {
                    entry.getValue().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            mZipPaths.clear();
            mZipPaths = null;
            mLinkedZipFile = null;
        }
    }

    public HashMap<String, String> list(int cookie) throws IOException {

        ZipFile zipFile = getZipFile(cookie);

        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        ZipEntry zipEntry = null;
        HashMap<String, String> allDirMap = new HashMap<String, String>();
        String dirName = null;
        while (enumeration.hasMoreElements()) {
            zipEntry = enumeration.nextElement();
            String name = zipEntry.getName();
            int dirIndex = name.indexOf('/');
            if (dirIndex != -1) {
                dirName = name.substring(0, dirIndex);
                allDirMap.put(dirName, dirName);
            }
        }
        // LogUtil.i(TAG, "dirSet " + allDirMap.keySet());
        return allDirMap;
    }


    private ZipFile getZipFile(int cookie) throws IOException {

        synchronized (this) {
            if (mClosed) {
                return null;
            }

            if (mZipPaths.size() == 0) {

                return null;
            }

            if (cookie < 0 || cookie >= mZipPaths.size()) {
                throw new IllegalArgumentException("illegal cookie!");
            }

            String zipPath = mZipPaths.get(cookie);


            if (!mLinkedZipFile.containsKey(zipPath)) {

                ZipFile zipFile = new ZipFile(zipPath);
                mLinkedZipFile.put(zipPath, zipFile);
            }

            return mLinkedZipFile.get(zipPath);
        }
    }
}
