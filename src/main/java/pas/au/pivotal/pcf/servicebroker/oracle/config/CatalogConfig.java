package pas.au.pivotal.pcf.servicebroker.oracle.config;

import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.model.Plan;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class CatalogConfig
{
    @Bean
    public Catalog catalog() {
        return new Catalog(Collections.singletonList(
                new ServiceDefinition(
                        "oracle-service-broker",
                        "oracle",
                        "A simple Oracle 12c service broker implementation",
                        true,
                        false,
                        Collections.singletonList(
                                new Plan("oracle-plan",
                                        "default",
                                        "This is a default Oracle plan.  All services are created equally.",
                                        getPlanMetadata())),
                        Arrays.asList("oracle", "Database storage"),
                        getServiceDefinitionMetadata(),
                        null,
                        null)));
    }

    /* Used by Pivotal CF console */

    private Map<String, Object> getServiceDefinitionMetadata() {
        Map<String, Object> sdMetadata = new HashMap<>();
        sdMetadata.put("displayName", "Oracle");
        sdMetadata.put("imageUrl", "https://calipia.files.wordpress.com/2011/10/logo-oracle.jpg");
        sdMetadata.put("longDescription", "Oracle 12c Database Service");
        sdMetadata.put("providerDisplayName", "Pivotal");
        sdMetadata.put("documentationUrl", "https://www.oracle.com/");
        sdMetadata.put("supportUrl", "https://www.oracle.com/");
        return sdMetadata;
    }

    private Map<String,Object> getPlanMetadata() {
        Map<String,Object> planMetadata = new HashMap<>();
        planMetadata.put("costs", getCosts());
        planMetadata.put("bullets", getBullets());
        return planMetadata;
    }

    private List<Map<String,Object>> getCosts() {
        Map<String,Object> costsMap = new HashMap<>();

        Map<String,Object> amount = new HashMap<>();
        amount.put("usd", 0.0);

        costsMap.put("amount", amount);
        costsMap.put("unit", "MONTHLY");

        return Collections.singletonList(costsMap);
    }

    private List<String> getBullets() {
        return Arrays.asList("Single Oracle Instance", "Limited Quota Given", "Shared Tablespace");
    }
}
