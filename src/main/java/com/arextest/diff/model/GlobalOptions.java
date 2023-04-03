package com.arextest.diff.model;

import com.arextest.diff.service.DecompressService;
import com.arextest.diff.utils.DeCompressUtil;
import com.arextest.diff.utils.StringUtil;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

public class GlobalOptions {

    /**
     * the url address of the plug-in jar which is specified by the interface
     * http or absolute path
     */
    private String pluginJarUrl;

    private Map<String, DecompressService> decompressServices;

    /**
     * change the message and configuration to lowercase, for the inconsistency between the actual message and the contract case
     */
    private boolean nameToLower;

    /**
     * This option is true, The null, CollectionUtils.isEmpty and Strings.empty are equal
     * for example：the baseMsg: {"age":""} is consistent with testMsg: "{\"age\":null}"
     */
    private boolean nullEqualsEmpty;

    /**
     * This refers to ignoring the precision of specified time fields when comparing them,
     * which means that if the difference between two fields is less than or equal to a certain parameter,
     * they are considered to be no error.
     * Unit of time: mm
     */
    private long ignoredTimePrecision;

    public GlobalOptions() {
        this.nameToLower = false;
        this.nullEqualsEmpty = false;
    }

    public GlobalOptions putPluginJarUrl(String pluginJarUrl) {
        if (StringUtil.isEmpty(pluginJarUrl)) {
            return this;
        }
        this.pluginJarUrl = pluginJarUrl;
        this.decompressServices = Optional.ofNullable(decompressServices).orElse(new HashMap<>());
        this.decompressServices.putAll(getDecompressServices(pluginJarUrl));
        return this;
    }

    public GlobalOptions putNameToLower(boolean nameToLower) {
        this.nameToLower = nameToLower;
        return this;
    }

    public GlobalOptions putNullEqualsEmpty(boolean nullEqualsEmpty) {
        this.nullEqualsEmpty = nullEqualsEmpty;
        return this;
    }

    public GlobalOptions putIgnoredTimePrecision(long ignoredTimePrecision) {
        this.ignoredTimePrecision = ignoredTimePrecision;
        return this;
    }

    public String getPluginJarUrl() {
        return pluginJarUrl;
    }

    public Map<String, DecompressService> getDecompressServices() {
        return decompressServices;
    }

    public boolean isNameToLower() {
        return nameToLower;
    }

    public boolean isNullEqualsEmpty() {
        return nullEqualsEmpty;
    }

    public long getIgnoredTimePrecision() {
        return ignoredTimePrecision;
    }

    private Map<String, DecompressService> getDecompressServices(String decompressJarUrl) {
        URL resource = null;
        Map<String, DecompressService> result = new HashMap<>();
        try {
            if (decompressJarUrl.startsWith("http")) {
                resource = new URL(decompressJarUrl);
            } else {
                resource = DeCompressUtil.class.getClassLoader().getResource(decompressJarUrl);
            }
            if (resource == null) {
                resource = new File(decompressJarUrl).toURI().toURL();
            }
            URLClassLoader serviceClassLoader = new URLClassLoader(new URL[]{resource},
                    Thread.currentThread().getContextClassLoader());
            ServiceLoader<DecompressService> load = ServiceLoader.load(DecompressService.class, serviceClassLoader);
            for (DecompressService decompressService : load) {
                if (decompressService.getAliasName() != null) {
                    result.put(decompressService.getAliasName(), decompressService);
                } else {
                    result.put(decompressService.getClass().getName(), decompressService);
                }
            }

        } catch (Throwable e) {
            return Collections.emptyMap();
        }
        return result;
    }
}
