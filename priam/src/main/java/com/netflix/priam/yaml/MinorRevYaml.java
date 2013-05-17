package com.netflix.priam.yaml;

import java.util.Map;

public abstract class MinorRevYaml implements Comparable<MinorRevYaml>
{
    abstract String getVersion();

    abstract void writeValues(Map<String, String> yaml);

    public int compareTo(MinorRevYaml other)
    {
        //sample logic!!!
        return this.getVersion().compareTo(other.getVersion());
    }
}
