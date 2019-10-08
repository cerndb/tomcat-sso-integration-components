mvn -f keycloak/testsuite/utils/pom.xml exec:java -Pkeycloak-server -Dimport=test-suite/src/test/resources/keycloak-saml/testsaml-with-mappers.json -X
