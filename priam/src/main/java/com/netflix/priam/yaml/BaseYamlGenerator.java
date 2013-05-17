package com.netflix.priam.yaml;

import java.util.Map;

public abstract class BaseYamlGenerator
{
    void writeValues(Map<String, String> yaml)
    {
        yaml.put("cluster_name", "TestCluster");

        //other yaml settings common to _all_ versions

        writeMajorVersionValues(yaml);
    }

    abstract void writeMajorVersionValues(Map<String, String> yaml);
}