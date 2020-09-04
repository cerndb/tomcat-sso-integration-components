//        © Copyright 2020 CERN
//        This software is distributed under the terms of the GNU General Public Licence
//        version 3 (GPL Version 3), copied verbatim in the file "LICENSE". In applying
//        this licence, CERN does not waive the privileges and immunities granted to it
//        by virtue of its status as an Intergovernmental Organization or submit itself
//        to any jurisdiction.
package ch.cern.sso.tomcat.valves;

import ch.cern.sso.tomcat.common.utils.Constants;
import ch.cern.sso.tomcat.common.utils.InitParamsUtils;
import ch.cern.sso.tomcat.common.utils.MessagesKeys;
import ch.cern.sso.tomcat.wrappers.SsoRemoteHeadersWrapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

/**
 *
 * @author lurodrig
 */
public class SsoHeadersValve extends ValveBase {

    private SsoRemoteHeadersWrapper ssoRemoteHeadersWrapper;
    private String[] ssoHeadersNames;
    private InitParamsUtils initParamsUtils;

    @Override
    protected void initInternal() throws LifecycleException {
        this.initParamsUtils = new InitParamsUtils();
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        initValveParameters(request);
        if (request.getUserPrincipal() != null) {
            Map<String, String> headersToInject = createHeaders(request);
            this.ssoRemoteHeadersWrapper = new SsoRemoteHeadersWrapper(request.getRequest(), headersToInject);
            request.setRequest((HttpServletRequest) this.ssoRemoteHeadersWrapper);
        }
        this.getNext().invoke(request, response);
    }

    private void initValveParameters(Request request) throws ServletException {
        ServletContext servletContext = request.getServletContext();
        this.ssoHeadersNames = this.initParamsUtils.getInitParameter(servletContext.getInitParameter(Constants.SSO_REMOTE_HEADERS), Constants.SSO_REMOTE_HEADERS, ",", false, Level.FINEST, MessagesKeys.NO_SSO_REMOTE_HEADERS_CONFIGURED);
    }

    private Map<String, String> createHeaders(Request request) {
        Map<String, String> result = new HashMap<String, String>();
        for (String name : this.ssoHeadersNames) {
            switch (name) {
                case Constants.SSO_REMOTE_USER:
                    result.put(Constants.SSO_REMOTE_USER, request.getUserPrincipal().getName());
                    break;
                case Constants.REMOTE_USER:
                    result.put(Constants.REMOTE_USER, request.getUserPrincipal().getName());
                    break;
                case Constants.SSO_REMOTE_HOST:
                    result.put(Constants.SSO_REMOTE_HOST, request.getRemoteAddr());
                    break;
            }
        }
        return result;
    }

}
