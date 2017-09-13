package pas.au.pivotal.pcf.servicebroker.oracle;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pas.au.pivotal.pcf.servicebroker.oracle.service.Constants;
import pas.au.pivotal.pcf.servicebroker.oracle.service.OracleManager;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OracleServiceBrokerApplicationTests {

	private final String username = "apples";
	private final String password = "welcome1";

	@Autowired
	private OracleManager oracleManager;

	@Test
	public void aa_createUser() throws SQLException
	{
		oracleManager.createUserForService(username, password);

		List<Map<String, Object>> resultList = oracleManager.executeQuery
				(String.format(Constants.USER_EXISTS, username), null);

		Assert.assertEquals(resultList.size(), 1);
	}

	@Test
	public void ab_deleteUser() throws SQLException
	{
		oracleManager.deleteUser(username);

		List<Map<String, Object>> resultList = oracleManager.executeQuery
				(String.format(Constants.USER_EXISTS, username), null);

		Assert.assertEquals(resultList.size(), 0);
	}
}
