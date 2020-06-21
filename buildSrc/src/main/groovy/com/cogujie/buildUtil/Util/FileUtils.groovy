package com.cogujie.buildUtil.Util

import com.google.common.base.Joiner

import java.nio.file.Files
import java.security.MessageDigest

public class FileUtils {

    private static final Joiner PATH_JOINER = Joiner.on(File.separatorChar);

    public static boolean isStringEmpty(String s) {
        return (s == null || s.length() == 0)
    }

    public static String joinPath(String... paths) {
        PATH_JOINER.join(paths)
    }

    public static File joinFile(File file, String... paths) {
        return new File(file, joinPath(paths))
    }

    public static void writeFile(File file, byte[] bytes) {
        initParentDir(file)
        OutputStream os = new BufferedOutputStream(new FileOutputStream(file))
        os.write(bytes, 0, bytes.length)
        os.close()
    }

    public static String openFileToString(File file) {
        byte[] bytes = new byte[1024]
        String str = ""
        if (!file.exists()) {
            return str
        }
        file.withInputStream { input ->
            int readCount = -1
            while ((readCount = input.read(bytes)) != -1) {
                str += new String(bytes, 0, readCount)
            }
            input.close()
        }
        return str
    }


    public static void renameFile(File originFile, File targetFile) {
        if (!originFile.renameTo(targetFile)) {
            throw new RuntimeException("${originFile} rename to ${targetFile} failed ");
        }
    }


    public static byte[] toByteArray(final InputStream input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final byte[] buffer = new byte[8024];
        int n = 0;
        long count = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return output.toByteArray();
    }


    public static void initParentDir(File file) {
        if (file == null) {
            return
        }
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
    }

    public static void initDir(File file) {
        if (file == null) {
            return
        }
        file.mkdirs()
    }

    public static String md5(File file) {
        return MessageDigest.getInstance("MD5").digest(file.bytes).encodeHex().toString()
    }


    public static boolean checkFile(File file) {
        return file != null && file.exists()
    }

    public static boolean checkFile(String path) {
        File file = new File(path)
        return file != null && file.exists()
    }

    public static void clearDir(File dir) {
        if (dir == null) {
            return
        }
        if (dir.exists()) {
            dir.deleteDir()
        }
        dir.mkdirs()
    }

    public static initFile(File file) {
        if (file == null) {
            return
        }
        initParentDir(file)
        file.delete()
        return file
    }

    public static void closeQuietly(Closeable co) {
        try {
            if (co != null) {
                co.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<File> listFileRecurse(File dir) {
        List<File> files = new ArrayList<>();
        if (dir.isDirectory()) {
            dir.eachFileRecurse { file ->
                files.add(file)
            }
        }
        return files
    }

    public static boolean copyFile(File src, File dst) {
        if (dst.exists()&&!dst.delete()) {
            throw new RuntimeException("can not delete file : " + dst)
        }
        if (!dst.parentFile.exists()) {
            dst.parentFile.mkdirs()
        }
        src.withInputStream { input ->
            dst.withOutputStream { output ->
                output << input
            }
        }
        return true
    }

    public static boolean testCopy(File src, File dst) {
        src.withInputStream { input ->
            dst.withOutputStream { output ->
                output << input
            }
        }
        return true
    }

    public static void moveFile(File src, File dst) {
        copyFile(src, dst)
        Files.delete(src.toPath());
    }

    public static boolean hasArchiveSuffix(String fileName) {
        return fileName.endsWith(".zip") || fileName.endsWith(".jar") || fileName.endsWith(".apk");
    }


}