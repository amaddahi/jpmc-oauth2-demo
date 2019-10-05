package org.mholford.jpmc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Properties;

public class KeycloakJwksReader {
  String realmUrl;
  String jwksUrl;
  String tokenUrl;
  String clientId;
  String clientSecret;

  private static String fmt(String orig, String... subs) {
    return String.format(orig, (Object[]) subs);
  }

  public KeycloakJwksReader(Properties props) {
    realmUrl = (String) props.get("oauth.realmUrl");
    jwksUrl = (String) props.get("oauth.jwksUrl");
    tokenUrl = (String) props.get("oauth.tokenUrl");
    clientId = (String) props.get("oauth.clientId");
    clientSecret = (String) props.get("oauth.clientSecret");
  }

  public AccessToken getVerifiedToken(String user, String pass) throws IOException {
    String rawToken = getRawToken(user, pass);
    String kid = getKeyId(rawToken);
    PublicKey publicKey = getPublicKey(kid);

    try {
      TokenVerifier<AccessToken> rsTV = TokenVerifier.create(rawToken, AccessToken.class);
      System.out.println("JWS Algorithm : " + rsTV.getHeader().getAlgorithm());
      System.out.println("JWS Type : " + rsTV.getHeader().getType());
      System.out.println("JWS Key Id : " + rsTV.getHeader().getKeyId());

      AccessToken parsedToken = TokenVerifier.create(rawToken, AccessToken.class)
              .publicKey(publicKey)
              .withChecks(new TokenVerifier.RealmUrlCheck(realmUrl),
                      TokenVerifier.IS_ACTIVE,
                      new TokenVerifier.TokenTypeCheck("Bearer"))
              .verify().getToken();

      System.out.println("Issued for : " + parsedToken.issuedFor);
      System.out.println("Issuer : " + parsedToken.getIssuer());
      System.out.println("Type : " + parsedToken.getType());
      return parsedToken;
    } catch (VerificationException e) {
      e.printStackTrace();
    }
    return null;
  }

  public String getRawToken(String user, String pass) throws IOException {
    CloseableHttpClient client = HttpClients.createDefault();

    String postBody = fmt("grant_type=client_credentials&client_id=%s&client_secret=%s",
            clientId, clientSecret);
    HttpPost post = new HttpPost(tokenUrl);
    post.addHeader("Content-Type", "application/x-www-form-urlencoded");
    post.setEntity(new StringEntity(postBody));

    CloseableHttpResponse response = client.execute(post);
    InputStream accessTokenStream = response.getEntity().getContent();

    ObjectMapper om = new ObjectMapper();
    JsonNode rootNode = om.readTree(accessTokenStream);
    String accessToken = rootNode.get("access_token").asText();

    response.close();
    client.close();

    return accessToken;
  }

  public String getKeyId(String accessToken) {

    //get the JWT header
    String tokenHeader = accessToken.split("\\.")[0];
    //Base64 decode it
    tokenHeader = new String(Base64.getDecoder().decode(tokenHeader.getBytes()));
    String kid = null;
    try {
      ObjectMapper om = new ObjectMapper();
      JsonNode rootNode = om.readTree(tokenHeader);

      // extract the kid value from the header
      kid = rootNode.get("kid").asText();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return kid;
  }

  public PublicKey getPublicKey(String kid) throws IOException {

    PublicKey publicKey = null;
    CloseableHttpClient client = HttpClients.createDefault();

    HttpGet get = new HttpGet(jwksUrl);
    CloseableHttpResponse response = client.execute(get);

    ObjectMapper om = new ObjectMapper();
    JsonNode rootNode = om.readTree(response.getEntity().getContent());
    ArrayNode keys = (ArrayNode) rootNode.get("keys");
    String modulusString = null, exponentString = null;
    for (JsonNode key : keys) {
      String id = key.get("kid").asText();
      if (kid.equals(id)) {
        modulusString = key.get("n").asText();
        exponentString = key.get("e").asText();
        break;
      }
    }

    BigInteger modulus = new BigInteger(1, base64Decode(modulusString));
    BigInteger publicExponent = new BigInteger(1, base64Decode(exponentString));

    try {
      KeyFactory kf = KeyFactory.getInstance("RSA");
      publicKey = kf.generatePublic(new RSAPublicKeySpec(modulus, publicExponent));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return publicKey;
  }

  // The Base64 strings that come from a JWKS need some manipilation before they can be decoded.
  // we do that here
  public byte[] base64Decode(String base64) throws IOException {
    base64 = base64.replaceAll("-", "+");
    base64 = base64.replaceAll("_", "/");
    switch (base64.length() % 4) // Pad with trailing '='s
    {
      case 0:
        break; // No pad chars in this case
      case 2:
        base64 += "==";
        break; // Two pad chars
      case 3:
        base64 += "=";
        break; // One pad char
      default:
        throw new RuntimeException(
                "Illegal base64url string!");
    }
    return Base64.getDecoder().decode(base64);
  }

  public static void main(String[] args) {
    Properties properties = new Properties();
    try (BufferedReader reader = Files.newBufferedReader(Path.of("src/main/resources/conf/jpmc-oauth.conf"))) {
      properties.load(reader);
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      new KeycloakJwksReader(properties).getVerifiedToken("matt", "1234");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
