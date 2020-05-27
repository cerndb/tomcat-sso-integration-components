# Changelog

## 2.0.15 (24-03-2020)
- JEEDY-1829 - ORDS valve now reads the groups prefix from context parameter(`ords.role.prefixes`). If not defined, all groups are taken

## 2.0.14 (24-03-2020)
- JEEDY-1830 - Valve to get groups from JNDIRealm while using Keycloak - KeycloakAuthenticatorJNDIGroupsValve

## 2.0.13 (24-03-2020)
- JEEDY-1690 AiCookiesValve apart from cookies directly, now overrides the headers as well
- JEEDY-1705 AiSession apart from cookies directly, now overrides the headers as well
- Additional tests for loginas for AiSessionValve and AiCookiesValve

## 2.0.12 (10-03-2020)
- JEEDY-1680: AI_LOGIN_AS cookie accepts numbers in the username

## 2.0.11 (26-02-2020)
- JEEDY-1624: Header spoof valve

## 2.0.10 (30-01-2019)
- JEEDY-1569: Global Logout hack for edh

## 2.0.9 (30-10-2019)
- JEEDY-1439: Ords jndi realm  

## 2.0.8 (11-12-2019)
- JEEDY-1444: multiple SSO remote user header issue (apex + qualiac) fix  

## 2.0.7 (30-10-2019)
- JEEDY-1326: Updated the following modules:
  - `cern-tomcat-authentication`
  - `cern-tomcat-sso-test-suite`
  `BasicAuthenticatorMockPrincipalInjectionValve` can be parametrized with the username and roles (comma separated list) **context params**. Previous version would require changes in the docker image.  

## 2.0.6 (30-10-2019)

- JEEDY-1326: Updated the following modules:
  - `cern-tomcat-authentication`
  - `cern-tomcat-sso-test-suite`
  `BasicAuthenticatorMockPrincipalInjectionValve` can be parametrized with the username and roles (comma separated list) **valve attributes**. 