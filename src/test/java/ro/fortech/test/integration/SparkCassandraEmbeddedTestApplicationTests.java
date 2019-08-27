package ro.fortech.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;

import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.data.cassandra.core.cql.CqlIdentifier;
import org.springframework.test.annotation.TestAnnotationUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

import ro.fortech.test.integration.configuration.CassandraConfig;
import ro.fortech.test.integration.domain.TestTable;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CassandraConfig.class)
public class SparkCassandraEmbeddedTestApplicationTests {

	public static final String KEYSPACE_CREATION_QUERY = "CREATE KEYSPACE IF NOT EXISTS local "
			+ "WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '1' };";

	public static final String KEYSPACE_ACTIVATE_QUERY = "USE local;";

	public static final String DATA_TABLE_NAME = "test_table";

	@Autowired
	private CassandraAdminOperations adminTemplate;

	@BeforeClass
	public static void startCassandraEmbedded()
			throws ConfigurationException, TTransportException, IOException, InterruptedException {
		EmbeddedCassandraServerHelper.startEmbeddedCassandra();
		final Cluster cluster = Cluster.builder().addContactPoints("127.0.0.1").withPort(9142).build();
		final Session session = cluster.connect();
		session.execute(KEYSPACE_CREATION_QUERY);
		session.execute(KEYSPACE_ACTIVATE_QUERY);
		Thread.sleep(5000);

	}

	@Before
	public void createTable() throws InterruptedException, TTransportException, ConfigurationException, IOException {
		adminTemplate.createTable(true, CqlIdentifier.of(DATA_TABLE_NAME), TestTable.class,
				new HashMap<String, Object>());
	}

	@Test
	public void whenSpringContextIsBootstrapped_thenNoExceptions() {
		TestTable mock1 = new TestTable();
		mock1.setId("id1");
		mock1.setText("text1");

		TestTable mock2 = new TestTable();
		mock2.setId("id2");
		mock2.setText("text2");

		adminTemplate.insert(mock1);
		adminTemplate.insert(mock2);
		
		long countRows = adminTemplate.count(TestTable.class);

		assertEquals(2, countRows);
	}

	@After
	public void dropTable() {
		adminTemplate.dropTable(CqlIdentifier.of(DATA_TABLE_NAME));
	}

	@AfterClass
	public static void stopCassandraEmbedded() {
		EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
	}

}
