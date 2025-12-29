/*
 * Decompiled with CFR 0.152.
 */
package SwordieX.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Util {
    private static final Map<Class, Class> boxedToPrimClasses = new HashMap<Class, Class>();

    public static int getRandom(int start, int end) {
        if (end - start == 0) {
            return start;
        }
        if (start > end) {
            int temp = end;
            end = start;
            start = temp;
        }
        return start + new Random().nextInt(end - start);
    }

    public static byte[] getByteArrayByString(String s) {
        s = s.replace("|", " ");
        s = s.replace(" ", "");
        s = s.replace("\n", "");
        s = s.replace("\r", "");
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String readableByteArray(byte[] arr) {
        StringBuilder res = new StringBuilder();
        for (byte b : arr) {
            res.append(String.format("%02X ", b));
        }
        return res.toString();
    }

    public static <T> T findWithPred(Collection<T> collection, Predicate<T> pred) {
        return collection.stream().filter(pred).findAny().orElse(null);
    }

    public static <T> T findWithPred(T[] arr, Predicate<T> pred) {
        return Util.findWithPred(Arrays.asList(arr), pred);
    }

    public static String formatNumber(String number) {
        return NumberFormat.getInstance(Locale.ENGLISH).format(Long.parseLong(number));
    }

    public static Class<?> convertBoxedToPrimitiveClass(Class<?> clazz) {
        return boxedToPrimClasses.getOrDefault(clazz, clazz);
    }

    public static void findAllFilesInDirectory(Set<File> toAdd, File dir) {
        if (dir != null && dir.isDirectory()) {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (file.isDirectory()) {
                    Util.findAllFilesInDirectory(toAdd, file);
                    continue;
                }
                toAdd.add(file);
            }
        }
    }

    public static List<Class<?>> getClasses(String packageName, boolean isRecursive, Class<? extends Annotation> annotation) throws IOException, ClassNotFoundException {
        ArrayList classList = new ArrayList();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String strFile = packageName.replaceAll("\\.", "/");
        Enumeration<URL> urls = loader.getResources(strFile);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (url == null) continue;
            String protocol = url.getProtocol();
            String pkgPath = url.getPath();
            if ("file".equals(protocol)) {
                Util.findClasses(classList, packageName, pkgPath, isRecursive, annotation);
                continue;
            }
            if (!"jar".equals(protocol)) continue;
            Util.findClasses(classList, packageName, url, isRecursive, annotation);
        }
        return classList;
    }

    private static void findClasses(List<Class<?>> clazzList, String packageName, URL url, boolean isRecursive, Class<? extends Annotation> annotation) throws IOException, ClassNotFoundException {
        JarURLConnection jarURLConnection = (JarURLConnection)url.openConnection();
        JarFile jarFile = jarURLConnection.getJarFile();
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarEntryName = jarEntry.getName();
            int endIndex = jarEntryName.lastIndexOf(".");
            String clazzName = endIndex > 0 ? jarEntryName.substring(0, endIndex) : jarEntryName;
            clazzName = clazzName.replace("/", ".");
            String prefix = null;
            endIndex = clazzName.lastIndexOf(".");
            if (endIndex > 0) {
                prefix = clazzName.substring(0, endIndex);
            }
            if (prefix == null || !jarEntryName.endsWith(".class")) continue;
            if (prefix.equals(packageName)) {
                Util.addClass(clazzList, clazzName, annotation);
                continue;
            }
            if (!isRecursive || !prefix.startsWith(packageName)) continue;
            Util.addClass(clazzList, clazzName, annotation);
        }
    }

    private static void findClasses(List<Class<?>> classes, String packageName, String packagePath, boolean isRecursive, Class<? extends Annotation> annotation) throws ClassNotFoundException {
        if (classes == null) {
            return;
        }
        File[] files = Util.filterClassFiles(packagePath);
        if (files != null) {
            for (File f : files) {
                String fileName = f.getName();
                if (f.isFile()) {
                    String clazzName = Util.getClassName(packageName, fileName);
                    Util.addClass(classes, clazzName, annotation);
                    continue;
                }
                if (!isRecursive) continue;
                String subPkgName = packageName + "." + fileName;
                String subPkgPath = packagePath + "/" + fileName;
                Util.findClasses(classes, subPkgName, subPkgPath, true, annotation);
            }
        }
    }

    private static File[] filterClassFiles(String packagePath) {
        if (packagePath == null) {
            return null;
        }
        return new File(packagePath).listFiles(file -> file.isFile() && file.getName().endsWith(".class") || file.isDirectory());
    }

    private static String getClassName(String pkgName, String fileName) {
        int endIndex = fileName.lastIndexOf(".");
        String clazz = null;
        if (endIndex >= 0) {
            clazz = fileName.substring(0, endIndex);
        }
        String clazzName = null;
        if (clazz != null) {
            clazzName = pkgName + "." + clazz;
        }
        return clazzName;
    }

    private static void addClass(List<Class<?>> classes, String className, Class<? extends Annotation> annotation) throws ClassNotFoundException {
        if (classes != null && className != null) {
            Class<?> clazz = Class.forName(className);
            if (annotation == null) {
                classes.add(clazz);
            } else if (clazz.isAnnotationPresent(annotation)) {
                classes.add(clazz);
            }
        }
    }

    public static void makeDirIfAbsent(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static int getCurrentTime() {
        return (int)System.currentTimeMillis();
    }

    public static long getCurrentTimeLong() {
        return System.currentTimeMillis();
    }

    public static boolean isNumber(String string) {
        return string != null && string.matches("-?\\d+(\\.\\d+)?");
    }

    static {
        boxedToPrimClasses.put(Boolean.class, Boolean.TYPE);
        boxedToPrimClasses.put(Byte.class, Byte.TYPE);
        boxedToPrimClasses.put(Short.class, Short.TYPE);
        boxedToPrimClasses.put(Character.class, Character.TYPE);
        boxedToPrimClasses.put(Integer.class, Integer.TYPE);
        boxedToPrimClasses.put(Long.class, Long.TYPE);
        boxedToPrimClasses.put(Float.class, Float.TYPE);
        boxedToPrimClasses.put(Double.class, Double.TYPE);
    }
}

