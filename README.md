# jpmc-oauth2-demo

This is a purpose-built neo4j Authentication plugin for a customer who wanted to use OAuth2.  They wished to use JacPol as a specification language to define how users should be mapped to Neo4j roles.  JacPol is spec only and does not have any actual implementations that I'm aware of, so this includes a partial implementation to match customer request.

To use, edit jpmc-oauth.conf to specify the realm, JWKS and token urls for the server (e.g. Keycloak) that dispenses authentication tokens.  Modify the oauth-jacpol.json file to define rules which map authenticated users to Neo4j roles. 

The neo4j authentication plugin works like this:
- On initialization it connects to the Authentication server based on properties defined in the jpmc-oauth.conf file and reads the JacPol policy definition
- When authenticating, the plugin calls the Authentication server to get a verified token for the user/pass provided
- Then the JacPol definition is used to determine a Neo4j role and a standard Neo4j AuthInfo object is returned.
