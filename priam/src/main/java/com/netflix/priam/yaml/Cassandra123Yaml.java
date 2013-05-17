package com.netflix.priam.yaml;

import java.util.Map;

public class Cassandra123Yaml extends MinorRevYaml
{

    String getVersion()
    {
        return "1.2.3";
    }

    void writeValues(Map<String, String> yaml)
    {
        //remove a setting (if it's no loner in the c* yaml as of this rev)
        yaml.remove("newSetting1");

        //add something new
        yaml.put("newSetting3", "val2");
    }
}
