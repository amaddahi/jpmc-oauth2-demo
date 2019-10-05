package org.mholford.jpmc;

import org.keycloak.representations.AccessToken;
import org.mholford.jacpol.Jacpol;
import org.mholford.jacpol.PolicySet;
import org.neo4j.server.security.enterprise.auth.plugin.api.AuthProviderOperations;
import org.neo4j.server.security.enterprise.auth.plugin.api.AuthToken;
import org.neo4j.server.security.enterprise.auth.plugin.api.AuthenticationException;
import org.neo4j.server.security.enterprise.auth.plugin.spi.AuthInfo;
import org.neo4j.server.security.enterprise.auth.plugin.spi.AuthPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

public class JPMCOauthPlugin extends AuthPlugin.Adapter {
  private AuthProviderOperations api;
  private Jacpol jacpol = Jacpol.get();
  private PolicySet policySet;
  private KeycloakJwksReader jwksReader;

  @Override
  public AuthInfo authenticateAndAuthorize(AuthToken authToken) throws AuthenticationException {
    String user = authToken.principal();
    char[] pw = authToken.credentials();

    AccessToken token = null;
    try {
      token = jwksReader.getVerifiedToken(user, new String(pw));
    } catch (IOException e) {
      throw new AuthenticationException("Error while getting auth token", e);
    }
    List<String> roles = jacpol.getNeoRoles(policySet, token);
    return AuthInfo.of(user, roles);
  }

  @Override
  public void initialize(AuthProviderOperations authProviderOperations) throws Throwable {
    api = authProviderOperations;

    Path configPath = api.neo4jHome().resolve("conf/jpmc-oauth.conf");

    Properties properties = new Properties();
    try (BufferedReader reader = Files.newBufferedReader(configPath)) {
      properties.load(reader);
    }

    jwksReader = new KeycloakJwksReader(properties);
    String jacpolPath = (String) properties.get("jacpol.filepath");
    policySet = jacpol.getPolicySet(jacpolPath);
  }
}
