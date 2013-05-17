package com.netflix.priam.yaml;

import java.util.Map;

public class Cassandra121Yaml extends MinorRevYaml
{

    String getVersion()
    {
        return "1.2.1";
    }

    void writeValues(Map<String, String> yaml)
    {
        //add a couple of settings
        yaml.put("newSetting1", "val1");
        yaml.put("newSetting2", "val2");
    }
}
