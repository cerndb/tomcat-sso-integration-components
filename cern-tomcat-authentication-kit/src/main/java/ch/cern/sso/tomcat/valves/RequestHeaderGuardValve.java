package ch.cern.sso.tomcat.valves;

import ch.cern.sso.tomcat.common.utils.Constants;
import ch.cern.sso.tomcat.common.utils.InitParamsUtils;
import ch.cern.sso.tomcat.common.utils.MessagesKeys;
import ch.cern.sso.tomcat.wrappers.GuardedHeadersWrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.logging.Level;

/**
 *
 * @author jgraniec
 */
public class RequestHeaderGuardValve extends ValveBase {

    private GuardedHeadersWrapper guardedHeadersWrapper;
    private String[] guardedHeaderNames;
    private InitParamsUtils initParamsUtils;

    @Override
    protected void initInternal() {
        this.initParamsUtils = new InitParamsUtils();
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        initValveParameters(request);
        this.guardedHeadersWrapper = new GuardedHeadersWrapper(request.getRequest(), guardedHeaderNames);
        request.setRequest(this.guardedHeadersWrapper);
        this.getNext().invoke(request, response);
    }

    private void initValveParameters(Request request) throws ServletException {
        ServletContext servletContext = request.getServletContext();
        this.guardedHeaderNames = this.initParamsUtils.getInitParameter(
                servletContext.getInitParameter(Constants.GUARDED_HEADERS),
                Constants.GUARDED_HEADERS,
                ",",
                false,
                Level.FINEST,
                MessagesKeys.NO_GUARDED_HEADERS_CONFIGURED);
    }



}
