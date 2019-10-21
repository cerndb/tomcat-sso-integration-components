# CERN Apache Tomcat SSO integration components

## What?

Set of [valves](https://tomcat.apache.org/tomcat-9.0-doc/config/valve.html) classes that helps CERN applications with the integration in the [CERN Authentication](https://espace.cern.ch/authentication/default.aspx) aka **CERN SSO**.

## Why?

There are few scenarios where these components can be applied:

1. **Authentication**: Applications composed by multiple [contexts](https://tomcat.apache.org/tomcat-9.0-doc/config/context.html) which make requests between them from the browser. For instance the [edh travel request document](https://edh.cern.ch/Document/Claims/TravelRequest) (/Document/Claims context) makes a request to the ROOT context (/) to get the [celebration dates](https://edh.cern.ch/Info/CelebrationDates) (CERN official holidays).
2. Applications requiring custom [cookies](https://tomcat.apache.org/tomcat-9.0-doc/servletapi/javax/servlet/http/Cookie.html) in the [request](https://tomcat.apache.org/tomcat-9.0-doc/servletapi/javax/servlet/http/HttpServletRequest.html) . Some examples: 
  * **AI_SESSION** for edh.cern.ch
  * **AI_USER** for e-groups.cern.ch
3. Applications requiring custom [HTTP headers](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers) for identifying the authenticated user or authorize it. This is the case of the [CERN Oracle APEX applications](https://cern.service-now.com/service-portal/article.do?n=KB0000175) and the [CERN ERP](https://wos.cern.ch/) (aka Qualiac but recently aquired by CEGID, see https://www.cegid.com/fr/produits/cegid-xrp-ultimate/)
4. Applications exposing documents with white spaces in the document name, using multiple proxies. The [URI](https://www.ietf.org/rfc/rfc2396.txt), and more specifically the document name has to be scaped. This was the case of some document listings for the treasury team. Hopefully this scenario has been deprecated with the removal of the `weblogic.servlet.proxy.HttpProxyServlet` from our infrastructure.
                  
## How?

### Building from source

Ensure you have JDK 7 (or newer), Maven 3 (or newer) and Git installed:

```bash
java -version
mvn -version
git --version
```

Add the [jeedy-applications maven repository](https://jeedy-nexus-repo.web.cern.ch/#browse/browse:jeedy-applications) to your settings.xml:

```xml
    <profile>
      <id>jeedy-applications</id>
      <repositories>
        <repository>
          <id>jeedy-applications</id>
          <name>Repository for the CERN JEEDY applications (CERN SSO integration components)</name>
          <url>https://jeedy-nexus-repo.web.cern.ch/repository/jeedy-applications/</url>
          <layout>default</layout>
          <snapshotPolicy>always</snapshotPolicy>
        </repository>
      </repositories>
    </profile>
``` 
The reason for this is that the **cern-tomcat-sso-test-suite** has a dependency with the **[cern-servlet-basic-checks](https://gitlab.cern.ch/jeedy/sso-integrations/commons/common-sso-utils) ** one.

Clone and build:

```bash
git clone https://:@gitlab.cern.ch:8443/jeedy/sso-integrations/tomcat-components/tomcat-sso-integration-components.git
cd tomcat-sso-integration-components/
mvn clean package
```
Once completed you will find the .jar files in the **target** folder of each module.

### Run

Just declare in the [application context definition](https://tomcat.apache.org/tomcat-9.0-doc/config/context.html#Defining_a_context) the required valve(s) that implements your scenario. The valves are executed in the order their declarations are defined in the context configuration file. For instance:

```xml
<Context path="/web-module-4" crossContext="true">
    <Valve className="ch.cern.sso.tomcat.valves.mocks.KeycloakAuthenticatorValve"/>
    <Valve className="ch.cern.sso.tomcat.valves.AiCookiesValve"/>
    <Parameter name="groups.loginas" value="edh-team,it-dep-db-dar" override="true"/>
    <Parameter name="aicookies" value="AI_USERNAME,AI_USER,AI_IDENTITY_CLASS,AI_LANG,AI_HRID" override="true"/>
</Context>
```

`KeycloakAuthenticatorValve` will be executed in the first place and  `AiCookiesValve` right after. Contrary to [servlet filters](https://tomcat.apache.org/tomcat-9.0-doc/servletapi/javax/servlet/Filter.html) where you can define `url-pattern` (see [servlet spec 6.2.4 chapter](https://javaee.github.io/servlet-spec/downloads/servlet-4.0/servlet-4_0_FINAL.pdf)), the valves are always inserted and executed in the request pipeline.

The **cern-tomcat-authentication-kit.jar**, **cern-tomcat-session-utils.jar** and the **[keycloak tomcat saml adapter libraries](https://www.keycloak.org/downloads.html#saml)** must be added to the [common-loader](https://tomcat.apache.org/tomcat-9.0-doc/class-loader-howto.html). Just edit the `$CATALINA_BASE/conf/catalina.properties` and update the `common.loader` entry:

```bash
common.loader="${catalina.base}/lib","${catalina.base}/lib/*.jar","${catalina.home}/lib","${catalina.home}/lib/*.jar","${catalina.home}/sso/keycloak/lib/7.0.0/*.jar","${catalina.home}/sso/cern-custom/lib/*.jar"
```

**IMPORTANT**: ensure that you declare [org.apache.catalina.authenticator.SingleSignOn](https://tomcat.apache.org/tomcat-9.0-doc/config/valve.html#Single_Sign_On_Valve) in your `$CATALINA_BASE/conf/server.xml`. This valves keeps the tomcat SSO session that allows the calls between contexts withouth being redirected to the authentication page (login.cern.ch).

```xml
<Valve className="org.apache.catalina.authenticator.SingleSignOn"/>
```

### Tests

You can find them at [cern-tomcat-sso-test-suite](https://gitlab.cern.ch/jeedy/sso-integrations/tomcat-components/tomcat-sso-integration-components/tree/master/cern-tomcat-sso-test-suite). Each of them run an [embedded tomcat](https://devcenter.heroku.com/articles/create-a-java-web-application-using-embedded-tomcat) and make use of the [AuthenticatorMockValve](https://gitlab.cern.ch/jeedy/sso-integrations/tomcat-components/tomcat-sso-integration-components/blob/master/cern-tomcat-authentication-kit/src/main/java/ch/cern/sso/tomcat/valves/mocks/AuthenticatorMockValve.java). This class creates an instance of [org.apache.catalina.realm.GenericPrincipal](https://tomcat.apache.org/tomcat-9.0-doc/api/org/apache/catalina/realm/GenericPrincipal.html) which contains an instance of [org.keycloak.adapters.saml.SamlPrincipal](https://access.redhat.com/webassets/avalon/d/red-hat-single-sign-on/version-7.0.0/javadocs/org/keycloak/adapters/saml/SamlPrincipal.html) with all the [attributes](https://gitlab.cern.ch/jeedy/sso-integrations/tomcat-components/tomcat-sso-integration-components/blob/master/cern-tomcat-authentication-kit/src/main/java/ch/cern/sso/tomcat/valves/mocks/MockConstants.java) attributes of the authenticated user.

The context.xml of each test webapp can be found under [src/test/resources/keycloak-saml](https://gitlab.cern.ch/jeedy/sso-integrations/tomcat-components/tomcat-sso-integration-components/tree/master/cern-tomcat-sso-test-suite/src/test/resources/keycloak-saml): 

```xml
<Context path="/web-module-3" crossContext="true">
    <Valve className="ch.cern.sso.tomcat.valves.mocks.AuthenticatorMockValve"/>
    <Valve className="ch.cern.sso.tomcat.valves.AiSessionValve"/>
</Context>
```

For instance in the above example we want to test the different use cases of the **AiSessionValve**. For checking them we make use of the servlets created in the [cern-servlet-basic-checks](https://gitlab.cern.ch/jeedy/sso-integrations/commons/common-sso-utils):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://java.sun.com/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
         version="3.0">

    <module-name>web-module-3</module-name>

    <servlet>
        <servlet-name>CookieInfoServlet</servlet-name>
        <servlet-class>ch.cern.sso.sp.examples.CookieInfoServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>CookieInfoServlet</servlet-name>
        <url-pattern>/cookie-info</url-pattern>
    </servlet-mapping>
    
    <security-constraint>
        <web-resource-collection>
            <web-resource-name>secure</web-resource-name>
            <url-pattern>/*</url-pattern>
        </web-resource-collection>
        <auth-constraint>
            <role-name>*</role-name>
        </auth-constraint>
    </security-constraint>
    <security-role>
        <role-name>*</role-name>
    </security-role>
    
</web-app>
```
You can always skip them with the **-DskipTests=true** maven option.

### Versions and CI

The `<version>` element in the parent pom declares current **SNAPSHOT** version we are working on. The sub-modules inherit from the parent:

```xml
    <parent>
        <groupId>ch.cern.sso.sp.tomcat</groupId>
        <artifactId>tomcat-sso-integration-components</artifactId>
        <version>2.0.4-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath>
    </parent>
```

When you start to work in a new feature you can:

1. Create a new branch linked with the [corresponding JIRA issue](https://its.cern.ch/jira/browse/JEEDY-543) `git checkout -b jeedy-1289-readme`  
2. Increase the SNAPSHOT version. You can use `mvn versions:set -DnextSnapshot=true`. 

Pushing your changes to the branch will trigger the **snapshot-package-build** in the [gitlab-ci job](https://gitlab.cern.ch/jeedy/sso-integrations/commons/common-sso-utils/blob/master/.gitlab-ci.yml), uploading the target jars to the [snapshots nexus repository](https://jeedy-nexus-repo.web.cern.ch/#browse/browse:jeedy-applications-snapshot). Once the changes are merged, **release-package-build** will be triggered and the jars will be uploaded to the [release nexus repository](https://jeedy-nexus-repo.web.cern.ch/#browse/browse:jeedy-applications-release).
             
### Implementation

#### Authentication: KeycloakAuthenticatorValve

Extends from [SamlAuthenticatorValve](https://github.com/keycloak/keycloak/blob/master/adapters/saml/tomcat/tomcat/src/main/java/org/keycloak/adapters/saml/tomcat/SamlAuthenticatorValve.java). 

##### doAuthenticate

It checks if the user has been authenticated and if it has a valid tomcat session, if not it invokes the **authenticate** method of the parent SamlAuthenticatorValve. At the first login it creates an instance of **org.apache.catalina.realm.GenericPrincipal** setting to this an instance of **org.keycloak.adapters.saml.SamlPrincipal** with all the user attributes. Finally it register this principal in the current session and **with our SingleSignOn valve**. 

##### invoke

If the URI request match the logout one (**/saml2slo/saml**) the [SAML logout](https://github.com/keycloak/keycloak/blob/5ad05c93177882cf456439232803d4dca879625c/adapters/saml/tomcat/tomcat7/src/main/java/org/keycloak/adapters/saml/tomcat/SamlAuthenticatorValve.java#L60) is called. This one kills the local tomcat session and invokes the next application in the *SSO logout chain*. 

#### Cookie injection: AiSessionValve

It adds to the [request](https://github.com/apache/tomcat/blob/master/java/org/apache/catalina/connector/Request.java) the **AI_SESSION** cookie based in some authenticated user's attributes[some authenticated user's attributes](https://gitlab.cern.ch/jeedy/sso-integrations/tomcat-components/tomcat-sso-integration-components/tree/master/cern-tomcat-session-utils/src/main/java/ch/cern/sso/tomcat/common/aisession). AI_SESSION is never set in the response, so it will never be present in the user browser. If an AI_SESSION is present in the user request it will be dropped.

If the `status.loginas=true` and the user is member of any of the `groups.loginas` ([context parameters](https://tomcat.apache.org/tomcat-9.0-doc/config/context.html#Context_Parameters) ) the value of the AI_SESSION cookie will be updated according with the AI_LOGIN_AS cookie value. 

#### Cookie injection: AiCookiesValve

In the same fashion as the AiSessionValve this class will add the set of cookies declared in the `aicookies` context param. As in the AiSessionValve, if present in the original user request these cookies will be dropped. 

#### Header injection: SsoHeadersValve

Some applications like the [ERP](wos.cern.ch) (formerly known as [Qualiac](https://www.cegid.com/fr/produits/cegid-xrp-ultimate/) Qualiac) and [CERN APEX applications](https://apex-sso.cern.ch/pls/htmldb_devdb11/f?p=265:1) CERN APEX applications require the injection of a header in the request with the authenticated user name. SsoHeadersValve [decorates](https://www.oracle.com/technetwork/testcontent/decorators-099517.html) decorates the http request **overridinng** the different **getHeader** methods of the [HttpServletRequest](https://tomcat.apache.org/tomcat-9.0-doc/servletapi/javax/servlet/http/HttpServletRequest.html). In this way when the application invokes any of these methods it will be invoking our code.

#### RequestUriValve

The [ERP](wos.cern.ch) expose some URLs with listings of documents. In the current configuration these URLs are behind two proxies. This is causing some issues when the document names contain blank spaces. In order to solve this issues we have to provide an implementation of [getRequestURI](https://tomcat.apache.org/tomcat-9.0-doc/servletapi/javax/servlet/http/HttpServletRequest.html#getRequestURI). Hopefully with the new simplified architecture this valve is not needed anymore.  

### Java 7???

Yes, as there are few applications still using Java7 I have declared both `maven.compiler.source` and `maven.compiler.target` `1.7` :(   

## License

Copyright (c) 2019 CERN