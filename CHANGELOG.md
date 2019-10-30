# Changelog

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