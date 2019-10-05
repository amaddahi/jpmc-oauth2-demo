package org.mholford.jacpol;

import org.junit.Test;
import org.keycloak.representations.AccessToken;
import org.mholford.jpmc.KeycloakJwksReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class JacpolTest {
  @Test
  public void testJacpol() {
    Jacpol jacpol = Jacpol.get();
    try {
      PolicySet policySet = jacpol.getPolicySet("src/main/resources/oauth-jacpol.json");
      Properties props = new Properties();
      try (BufferedReader reader = Files.newBufferedReader(Path.of("src/main/resources/conf/jpmc-oauth.conf"))) {
        props.load(reader);
      } catch (IOException e) {
        e.printStackTrace();
      }
      AccessToken accessToken = new KeycloakJwksReader(props).getVerifiedToken("", "");
      List<String> neoRoles = jacpol.getNeoRoles(policySet, accessToken);
      assertEquals(1, neoRoles.size());
      assertTrue(neoRoles.contains("read"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
