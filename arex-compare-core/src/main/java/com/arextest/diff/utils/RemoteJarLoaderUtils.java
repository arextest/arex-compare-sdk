package com.arextest.diff.utils;

import com.arextest.diff.model.classloader.RemoteJarClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class RemoteJarLoaderUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteJarLoaderUtils.class);
    public static RemoteJarClassLoader loadJar(String jarUrl) throws MalformedURLException {
        URL resource;
        if (jarUrl.startsWith("http")) {
            resource = new URL(jarUrl);
        } else {
            resource = RemoteJarLoaderUtils.class.getClassLoader().getResource(jarUrl);
        }
        if (resource == null) {
            resource = new File(jarUrl).toURI().toURL();
        }

        try {
            URLConnection urlConnection = resource.openConnection();
            urlConnection.getInputStream().close();
        } catch (IOException e) {
            LOGGER.error("Failed to load jar: {}", jarUrl, e);
            throw new RuntimeException("Failed to load jar: " + jarUrl, e);
        }

        return new RemoteJarClassLoader(new URL[]{resource}, RemoteJarLoaderUtils.class.getClassLoader());
    }

    public static <T> List<T> loadService(Class<T> clazz, SecureClassLoader classLoader) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz, classLoader);
        List<T> res = new ArrayList<>();
        Iterator<T> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            res.add(iterator.next());
        }
        return res;
    }
}