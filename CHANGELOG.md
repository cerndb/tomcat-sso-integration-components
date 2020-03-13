# Changelog

## 2.0.12 (10-03-2020)
- JEEDY-1680: AI_LOGIN_AS cookie accepts numbers in the username 

## 2.0.11 (26-02-2020)
- JEEDY-1624: Header spoof valve

## 2.0.9 (30-10-2020)
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
