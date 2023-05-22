package com.arextest.diff.model;

import com.arextest.diff.handler.decompress.DecompressServiceBuilder;
import com.arextest.diff.utils.StringUtil;

public class GlobalOptions {

    /**
     * The url address of the plug-in jar which is specified by the interface http or absolute path.
     * The decompressService which is loaded from this pluginJarUrl is the level of SYSTEM.
     */
    private String pluginJarUrl;

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

    /**
     * This option is true, The "null" and the situation that the field is not existed are equal
     * for example：the baseMsg: {"age":null} is consistent with testMsg: "{}"
     */
    private boolean nullEqualsNotExist;

    public GlobalOptions() {
        this.nameToLower = false;
        this.nullEqualsEmpty = false;
    }

    public GlobalOptions putPluginJarUrl(String pluginJarUrl) {
        if (StringUtil.isEmpty(pluginJarUrl)) {
            return this;
        }
        this.pluginJarUrl = pluginJarUrl;
        DecompressServiceBuilder.loadSystemDecompressService(pluginJarUrl);
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

    public GlobalOptions putNullEqualsNotExist(boolean nullEqualsNotExist) {
        this.nullEqualsNotExist = nullEqualsNotExist;
        return this;
    }

    public String getPluginJarUrl() {
        return pluginJarUrl;
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

    public boolean isNullEqualsNotExist() {
        return nullEqualsNotExist;
    }

}
