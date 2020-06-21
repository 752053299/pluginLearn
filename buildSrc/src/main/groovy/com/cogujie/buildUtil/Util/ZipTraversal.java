package com.cogujie.buildUtil.Util;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipTraversal {

    public static void traversal(ZipFile zipFile, Callback callback) {
        try {
            Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry entry = enumeration.nextElement();
                callback.oneEntry(entry, Utils.toByteArray(zipFile.getInputStream(entry)));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Utils.closeQuietly(zipFile);
        }

    }

    public static void traversal(File file, Callback callback) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(file);
            traversal(zipFile, callback);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.closeQuietly(zipFile);
        }
    }

    public interface Callback {
        void oneEntry(ZipEntry entry, byte[] bytes);
    }
}
