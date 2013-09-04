    package com.netflix.priam.dse;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.netflix.priam.IConfiguration;
import com.netflix.priam.defaultimpl.StandardTuner;
import org.apache.cassandra.io.util.FileUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import static com.netflix.priam.dse.IDseConfiguration.NodeType;
import static org.apache.cassandra.locator.SnitchProperties.RACKDC_PROPERTY_FILENAME;

/**
 * Makes Datastax Enterprise-specific changes to the c* yaml and dse-yaml.
 *
 * @author jason brown
 */
public class DseTuner extends StandardTuner
{
    private static final Logger logger = LoggerFactory.getLogger(DseTuner.class);
    private final IDseConfiguration dseConfig;

    @Inject
    public DseTuner(IConfiguration config, IDseConfiguration dseConfig)
    {
        super(config);
        this.dseConfig = dseConfig;
    }

    public void writeAllProperties(String yamlLocation, String hostname, String seedProvider) throws IOException
    {
        super.writeAllProperties(yamlLocation, hostname, seedProvider);
        writeDseYaml();
        writeCassandraSnitchProperties();
    }

    private void writeDseYaml() throws IOException
    {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        String dseYaml = dseConfig.getDseYamlLocation();
        @SuppressWarnings("rawtypes")
        Map map = (Map) yaml.load(new FileInputStream(dseYaml));
        map.put("delegated_snitch", config.getSnitch());

        if(dseConfig.getNodeType() == NodeType.SEARCH)
        {
            List<?> searchOptions = (List) map.get("ttl_index_rebuild_options");
            Map<String, String> m = (Map<String, String>) searchOptions.get(0);
            if(dseConfig.getSearchIndexFixedRateSeconds() != null)
                m.put("fixed_rate_period", dseConfig.getSearchIndexFixedRateSeconds().toString());
            if(dseConfig.getSearchIndexInitialDelay() != null)
                m.put("initial_delay", dseConfig.getSearchIndexInitialDelay().toString());
            if(dseConfig.getSearchIndexMaxDocsPerBatch() != null)
                m.put("max_docs_per_batch", dseConfig.getSearchIndexMaxDocsPerBatch().toString());

            if(dseConfig.getSearchConcurrencyPerCore() != null)
                map.put("max_solr_concurrency_per_core", dseConfig.getSearchConcurrencyPerCore());
        }

        logger.info("Updating dse-yaml:\n" + yaml.dump(map));
        yaml.dump(map, new FileWriter(dseYaml));
    }

    private void writeCassandraSnitchProperties()
    {
        final NodeType nodeType = dseConfig.getNodeType();
        if(nodeType == NodeType.REAL_TIME_QUERY)
            return;

        Reader reader = null;
        try
        {
            String filePath = config.getCassHome() + "/conf/" + RACKDC_PROPERTY_FILENAME;
            reader = new FileReader(filePath);
            Properties properties = new Properties();
            properties.load(reader);
            String suffix = "";
            if(nodeType == NodeType.SEARCH)
                suffix = "_solr";
            if(nodeType == NodeType.ANALYTIC)
                suffix = "_hadoop";
            properties.put("dc_suffix", suffix);
            properties.store(new FileWriter(filePath), "");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to read " + RACKDC_PROPERTY_FILENAME, e);
        }
        finally
        {
            FileUtils.closeQuietly(reader);
        }

    }

    protected String getSnitch()
    {
        return dseConfig.getDseDelegatingSnitch();
    }
}
