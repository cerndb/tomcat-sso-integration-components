# CERN Apache Tomcat SSO integration components

## What?

Set of [valves](https://tomcat.apache.org/tomcat-9.0-doc/config/valve.html) classes that helps CERN applications with the integration in the [CERN Authentication](https://espace.cern.ch/authentication/default.aspx) aka **CERN SSO**.        

## Why?

There are few scenarios where these components can be applied:

1. Applications composed by multiple [contexts](https://tomcat.apache.org/tomcat-9.0-doc/config/context.html) which make requests between them from the browser. For instance the [edh travel request document](https://edh.cern.ch/Document/Claims/TravelRequest) (/Document/Claims context) makes a request to the ROOT context (/) to get the [celebration dates](https://edh.cern.ch/Info/CelebrationDates) (CERN official holidays).
2. Applications requiring custom [cookies](https://tomcat.apache.org/tomcat-9.0-doc/servletapi/javax/servlet/http/Cookie.html) in the [request](https://tomcat.apache.org/tomcat-9.0-doc/servletapi/javax/servlet/http/HttpServletRequest.html) . Some examples: 
  * **AI_SESSION** for edh.cern.ch
  * **AI_USER** for e-groups.cern.ch
3. Applications requiring custom [HTTP header](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers) for identifying the authenticated user or authorize it. This is the case of the [CERN Oracle APEX applications](https://cern.service-now.com/service-portal/article.do?n=KB0000175) and the [CERN ERP](https://wos.cern.ch/) (aka Qualiac but recently aquired by CEGID, see https://www.cegid.com/fr/produits/cegid-xrp-ultimate/)
4. Applications exposing documents with white spaces in the document name, using multiple proxies. The [URI](https://www.ietf.org/rfc/rfc2396.txt), and more specifically the document name has to be scaped. This was the case of some document listings for the treasury team. Hopefully this scenario has been deprecated with the removal of the `weblogic.servlet.proxy.HttpProxyServlet` from our infrastructure.
                  
## How?

Just declare in the [application context definition](https://tomcat.apache.org/tomcat-9.0-doc/config/context.html#Defining_a_context) the required valve(s) that implements your scenario. The valves are executed in the order their declarations are defined in the context configuration file. For instance:

```xml
<Context path="/web-module-4" crossContext="true">
    <Valve className="ch.cern.sso.tomcat.valves.mocks.AuthenticatorMockValve"/>
    <Valve className="ch.cern.sso.tomcat.valves.AiCookiesValve"/>
    <Parameter name="groups.loginas" value="edh-team,it-dep-db-dar" override="true"/>
    <Parameter name="aicookies" value="AI_USERNAME,AI_USER,AI_IDENTITY_CLASS,AI_LANG,AI_HRID" override="true"/>
</Context>   
````

### Java 7???

## License

