package ro.fortech.test.integration.domain;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("test_table")
public class TestTable {

	@PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 0)
	private String id;

	@PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordinal = 1)
	private String text;

	public TestTable() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
