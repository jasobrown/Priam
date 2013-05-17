package com.netflix.priam.yaml;

import java.util.Map;
import java.util.SortedSet;

public class Cassandra12Yaml extends BaseYamlGenerator
{
    SortedSet<MinorRevYaml> minorRevs;


    void writeMajorVersionValues(Map<String, String> yaml)
    {
        yaml.put("authorizer", "SomeValue");

        for(MinorRevYaml mry : minorRevs)
        {
            if(getCurrCassandraVersion())
            mry.writeValues(yaml);
        }
    }
}
