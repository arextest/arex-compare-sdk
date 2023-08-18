package com.arextest.diff.utils;

import com.arextest.diff.handler.decompress.DecompressServiceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
public class ClassLoaderUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassLoaderUtils.class);
    public static final String JDK_INTER_APP_CLASSLOADER = "jdk.internal.loader.ClassLoaders$AppClassLoader";

    public static void loadJar(String jarPath) {
        try {
            int javaVersion = getJavaVersion();
            ClassLoader classLoader = ClassLoaderUtils.class.getClassLoader();

            URL resource;
            if (jarPath.startsWith("http")) {
                resource = new URL(jarPath);
            } else {
                resource = ClassLoaderUtils.class.getClassLoader().getResource(jarPath);
            }

            if (resource == null) {
                File jarFile = new File(jarPath);
                if (!jarFile.exists()) {
                    LOGGER.error("JarFile doesn't exist! path:{}", jarPath);
                    return;
                }
                resource = new File(jarPath).toURI().toURL();
            }

            Method addURL = Class.forName("java.net.URLClassLoader").getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);

            if (javaVersion <= 8) {
                if (classLoader instanceof URLClassLoader) {
                    addURL.invoke(classLoader, resource.toURI().toURL());
                }
            } else if (javaVersion < 11) {
                /*
                 * Due to Java 8 vs java 9+ incompatibility issues
                 * See https://stackoverflow.com/questions/46694600/java-9-compatability-issue-with-classloader-getsystemclassloader/51584718
                 */
                ClassLoader urlClassLoader = ClassLoader.getSystemClassLoader();
                if (!(urlClassLoader instanceof URLClassLoader)) {
                    urlClassLoader = new URLClassLoader(new URL[] {resource.toURI().toURL()}, urlClassLoader);
                }
                addURL.invoke(urlClassLoader, resource.toURI().toURL());
            } else if (JDK_INTER_APP_CLASSLOADER.equalsIgnoreCase(classLoader.getClass().getName())) {
                /**
                 * append jar jdk.internal.loader.ClassLoaders.AppClassLoader
                 * if java >= 11 need add jvm option:--add-opens=java.base/jdk.internal.loader=ALL-UNNAMED
                 */
                Method classPathMethod = classLoader.getClass().getDeclaredMethod("appendToClassPathForInstrumentation", String.class);
                classPathMethod.setAccessible(true);
                classPathMethod.invoke(classLoader, resource.getPath());

            }
        } catch (Exception e) {
            LOGGER.error("loadJar failed, jarPath:{}, message:{}", jarPath, e.getMessage());
        }
    }

    public static <T> List<T> loadService(Class<T> clazz) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
        List<T> res = new ArrayList<>();
        Iterator<T> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            res.add(iterator.next());
        }
        return res;
    }

    private static int getJavaVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if (dot != -1) {
                version = version.substring(0, dot);
            }
        }
        return Integer.parseInt(version);
    }
}
