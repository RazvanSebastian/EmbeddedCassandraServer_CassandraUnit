package ro.fortech.test.integration.configuration;

import org.apache.commons.logging.Log;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.CassandraEntityClassScanner;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.SimpleUserTypeResolver;

@Configuration
@PropertySource(value = { "classpath:application.properties" })
public class CassandraConfig extends AbstractCassandraConfiguration {

	@Autowired
	private Environment environment;

	@Override
	protected String getKeyspaceName() {
		return environment.getProperty("cassandra.keyspace");
	}

	@Bean
	public CassandraClusterFactoryBean cluster() {
		final CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
		cluster.setContactPoints(environment.getProperty("cassandra.contactpoints"));
		cluster.setPort(Integer.parseInt(environment.getProperty("cassandra.port")));
	
		return cluster;
	}

	@Override
	@Bean
	public CassandraMappingContext cassandraMapping() throws ClassNotFoundException {
		 CassandraMappingContext cassandraMappingContext = new CassandraMappingContext();
	        cassandraMappingContext.setUserTypeResolver(new SimpleUserTypeResolver(getRequiredCluster(), getKeyspaceName()));
	        cassandraMappingContext
	                .setInitialEntitySet(CassandraEntityClassScanner.scan("ro.fortech.test.integration.domain"));
	        return cassandraMappingContext;
	}
}