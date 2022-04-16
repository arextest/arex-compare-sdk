package io.arex.diff.model;

import java.util.*;

public class CompareOptions {

    /**
     * the node path chosen to compare, such as "Result\TransportList\FlightList\ID"
     */
    private Set<String> inclusions;

    /**
     * the node path which is ignore，such as "Result\TransportList\FlightList\ID"
     */
    private Set<String> exclusions;

    /**
     * the config to decompress
     * key：The bean name of the decompression method which is implement the DecompressService interface, you can use an alias
     * value：the node path need to decompress : ["Result\TransportList",]
     */
    private Map<String, List<String>> decompressConfig;

    /**
     * reference config
     * key：the node path which is foreign key path, such as "Result\OrigDestList\LegList\TransportRefList\%value%"
     * For the list node which is alike [a,b,c],[1,2,3]。a is described as "%value%"
     * value: the node path which is primary key. such as Result\TransportList\FlightList\ID
     */
    private Map<String, String> referenceConfig;

    /**
     * ordering rules for list nodes
     * key：the list node path, such as "Result\ProductItemList\FlightProductItemList"
     * value：The primary key formed by the child nodes under the list, such as "SaleType,PolicyRefList\RefPolicyID"
     */
    private Map<String, String> listSortConfig;


    public CompareOptions() {
    }

    public static CompareOptions options() {
        return new CompareOptions();
    }

    public CompareOptions putInclusions(String path) {
        if (path == null || path.isEmpty()) {
            return this;
        }
        if (this.inclusions == null) {
            this.inclusions = new HashSet<>();
        }
        this.inclusions.add(path);
        return this;
    }

    public CompareOptions putInclusions(Collection<String> paths) {
        if (paths == null || paths.isEmpty()) {
            return this;
        }
        if (this.inclusions == null) {
            this.inclusions = new HashSet<>();
        }
        this.inclusions.addAll(paths);
        return this;
    }

    public CompareOptions putExclusions(String path) {
        if (path == null || path.isEmpty()) {
            return this;
        }
        if (this.exclusions == null) {
            this.exclusions = new HashSet<>();
        }
        this.exclusions.add(path);
        return this;
    }

    public CompareOptions putExclusions(Collection<String> paths) {
        if (paths == null || paths.isEmpty()) {
            return this;
        }
        if (this.exclusions == null) {
            this.exclusions = new HashSet<>();
        }
        this.exclusions.addAll(paths);
        return this;
    }

    public CompareOptions putDecompressConfig(String beanName, String path) {
        if (this.decompressConfig == null) {
            this.decompressConfig = new HashMap<>();
        }
        List<String> orDefault = this.decompressConfig.getOrDefault(beanName, new ArrayList<>());
        orDefault.add(path);
        this.decompressConfig.put(beanName, orDefault);
        return this;
    }

    public CompareOptions putDecompressConfig(String beanName, List<String> paths) {
        if (this.decompressConfig == null) {
            this.decompressConfig = new HashMap<>();
        }
        this.decompressConfig.getOrDefault(beanName, new ArrayList<>()).addAll(paths);
        return this;
    }

    public CompareOptions putDecompressConfig(Map<String, List<String>> decompressConfig) {
        if (decompressConfig == null || decompressConfig.isEmpty()) {
            return this;
        }
        if (this.decompressConfig == null) {
            this.decompressConfig = new HashMap<>();
        }
        this.decompressConfig.putAll(decompressConfig);
        return this;
    }

    public CompareOptions putReferenceConfig(String fkNodePath, String pkNodePath) {
        if (this.referenceConfig == null) {
            this.referenceConfig = new HashMap<>();
        }
        this.referenceConfig.put(fkNodePath, pkNodePath);
        return this;
    }

    public CompareOptions putReferenceConfig(Map<String, String> referenceConfig) {
        if (referenceConfig == null || referenceConfig.isEmpty()) {
            return this;
        }
        if (this.referenceConfig == null) {
            this.referenceConfig = new HashMap<>();
        }
        this.referenceConfig.putAll(referenceConfig);
        return this;
    }

    public CompareOptions putListSortConfig(String listNodePath, String key) {
        if (this.listSortConfig == null) {
            this.listSortConfig = new HashMap<>();
        }
        this.referenceConfig.put(listNodePath, key);
        return this;
    }

    public CompareOptions putListSortConfig(Map<String, String> listKeyConfig) {
        if (listKeyConfig == null || listKeyConfig.isEmpty()) {
            return this;
        }
        if (this.listSortConfig == null) {
            this.listSortConfig = new HashMap<>();
        }
        this.listSortConfig.putAll(listKeyConfig);
        return this;
    }

    public Set<String> getInclusions() {
        return inclusions;
    }

    public Set<String> getExclusions() {
        return exclusions;
    }

    public Map<String, List<String>> getDecompressConfig() {
        return decompressConfig;
    }

    public Map<String, String> getReferenceConfig() {
        return referenceConfig;
    }

    public Map<String, String> getListSortConfig() {
        return listSortConfig;
    }


    public static void main(String[] args) {

    }

}
