package org.mholford.jacpol;

import org.junit.Test;
import org.keycloak.representations.AccessToken;
import org.mholford.jpmc.KeycloakJwksReader;

import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class JacpolTest {
  @Test
  public void testJacpol() {
    Jacpol jacpol = Jacpol.get();
    try {
      PolicySet policySet = jacpol.getPolicySet("src/main/resources/oauth-jacpol.json");
      AccessToken accessToken = new KeycloakJwksReader().verifyToken();
      List<String> neoRoles = jacpol.getNeoRoles(policySet, accessToken);
      assertEquals(1, neoRoles.size());
      assertTrue(neoRoles.contains("read"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
