/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.tomcat.wrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author lurodrig
 */
public class SsoRemoteHeadersWrapper extends HttpServletRequestWrapper {

    private final ResourceBundle messages = ResourceBundle.getBundle("Messages");
    private final static Logger LOGGER = Logger.getLogger("ch.cern.sso");

    // Bunch of custom headers to be injected in the request
    private Map<String, String> headersToInject;

    public SsoRemoteHeadersWrapper(HttpServletRequest httpServletRequest, Map<String, String> headers) {
        super(httpServletRequest);
        this.headersToInject = headers;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> originalHeadersNames = new HashSet<String>(Collections.list(super.getHeaderNames()));
        Set<String> headersToInjectNames = headersToInject.keySet();
        originalHeadersNames.addAll(headersToInjectNames);
        return Collections.enumeration(originalHeadersNames);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> values = new ArrayList<String>();
        // If the target application is looking for one of our injected headers
        // do not look in the original one. This avoid header spoofing
        if (headersToInject.containsKey(name)) {
            values.add(headersToInject.get(name));
        } else {
            values = Collections.list(super.getHeaders(name));
        }
        return Collections.enumeration(values);
    }

    @Override
    public String getHeader(String name) {
        String headerValue = super.getHeader(name);
        // Our headers always prevail, so no possible spoofing
        if (headersToInject.containsKey(name)) {
            headerValue = headersToInject.get(name);
        }
        return headerValue;
    }

}
