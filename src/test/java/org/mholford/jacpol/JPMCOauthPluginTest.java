package org.mholford.jacpol;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.neo4j.driver.*;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.internal.EnterpriseInProcessServerBuilder;
import org.neo4j.test.rule.TestDirectory;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class JPMCOauthPluginTest {
  private ServerControls server;

  @Rule
  public final TestDirectory testDirectory = TestDirectory.testDirectory();

  @Before
  public void setup() {
    // Create directories and write out test config file
    File directory = testDirectory.directory();
    File configDir = new File(directory, "test/databases/conf");
    configDir.mkdirs();

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(configDir, "jpmc-oauth.conf")));
         BufferedReader br = new BufferedReader(new FileReader(new File("src/main/resources/conf/jpmc-oauth.conf")))) {
      String s;
      while ((s = br.readLine()) != null){
        bw.write(s + "\n");
        bw.flush();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    server = new EnterpriseInProcessServerBuilder(directory, "test")
            .withConfig("dbms.security.auth_enabled", "true")
            .withConfig("dbms.security.auth_provider", "plugin-org.mholford.jpmc.JPMCOauthPlugin")
            .newServer();
  }

  @After
  public void tearDown() {
    server.close();
  }

  @Test
  public void shouldAuthenticate() {
    Driver driver = GraphDatabase.driver(server.boltURI(), AuthTokens.basic("matt", "1234"));
    Session session = driver.session();
    Value single = session.run("RETURN 1").single().get(0);
    assertEquals(single.asLong(), 1L);
  }
}
