package ch.cern.sso.tomcat.valves;

import ch.cern.sso.tomcat.common.utils.Constants;
import ch.cern.sso.tomcat.common.utils.InitParamsUtils;
import ch.cern.sso.tomcat.common.utils.MessagesKeys;
import ch.cern.sso.tomcat.wrappers.RequestUriWrapper;
import java.io.IOException;
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
public class RequestUriValve extends ValveBase {

    private RequestUriWrapper requestUriWrapper;
    private boolean isUriEncoded;
    private InitParamsUtils initParamsUtils;

    @Override
    protected void initInternal() throws LifecycleException {
        this.initParamsUtils = new InitParamsUtils();
    }
    
    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        initValveParameters(request);
        this.requestUriWrapper = new RequestUriWrapper(request.getRequest(), this.isUriEncoded);
        request.setRequest((HttpServletRequest) this.requestUriWrapper);
        this.getNext().invoke(request, response);
    }
    
    private void initValveParameters(Request request) throws ServletException {
        ServletContext servletContext = request.getServletContext();
        this.isUriEncoded = Boolean.parseBoolean(initParamsUtils.getInitParameter(servletContext.getInitParameter(Constants.URI_ENCODE), Constants.URI_ENCODE, false, Level.INFO, MessagesKeys.NO_URI_ENCODED_CONFIGURED));
    }
    
}
