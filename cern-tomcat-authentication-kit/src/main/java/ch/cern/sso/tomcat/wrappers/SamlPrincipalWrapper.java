//        © Copyright 2020 CERN
//        This software is distributed under the terms of the GNU General Public Licence
//        version 3 (GPL Version 3), copied verbatim in the file "LICENSE". In applying
//        this licence, CERN does not waive the privileges and immunities granted to it
//        by virtue of its status as an Intergovernmental Organization or submit itself
//        to any jurisdiction.
package ch.cern.sso.tomcat.wrappers;

import ch.cern.sso.tomcat.common.utils.Constants;
import org.keycloak.adapters.saml.SamlPrincipal;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.dom.saml.v2.assertion.AssertionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jgraniec
 */
public class SamlPrincipalWrapper extends SamlPrincipal {
    private List<String> roles;
    public SamlPrincipalWrapper(SamlPrincipal samlPrincipal, List<String> roles){
        this(samlPrincipal.getAssertion(),
                samlPrincipal.getName(),
                samlPrincipal.getSamlSubject(),
                samlPrincipal.getNameIDFormat(),
                getAttributes(samlPrincipal),
                getFriendlyAttributes(samlPrincipal),
                roles);
    }

    public SamlPrincipalWrapper(AssertionType assertion, String name, String samlSubject, String nameIDFormat, MultivaluedHashMap<String, String> attributes, MultivaluedHashMap<String, String> friendlyAttributes, List<String> roles) {
        super(assertion, name, samlSubject, nameIDFormat, substituteAttributesRoles(attributes,roles), substituteAttributesRoles(friendlyAttributes,roles));
        this.roles = roles;
    }
    public String getFriendlyAttribute(String friendlyName) {
        if(friendlyName.equals(Constants.ROLES))
            return (this.roles != null && this.roles.size() > 0) ? this.roles.get(0) : null;
        return super.getFriendlyAttribute(friendlyName);
    }
    public String getAttribute(String name) {
        if(name.equals(Constants.ROLES))
            return (this.roles != null && this.roles.size() > 0) ? this.roles.get(0) : null;
        return super.getAttribute(name);
    }
    public List<String> getFriendlyAttributes(String friendlyName) {
        if(friendlyName.equals(Constants.ROLES))
            return this.roles;
        return super.getFriendlyAttributes(friendlyName);
    }
    public Map<String, List<String>> getAttributes() {
        Map<String, List<String>> attributes = new HashMap<>(super.getAttributes());
        attributes.put(Constants.ROLES,this.roles);
        return attributes;
    }
    public List<String> getAttributes(String name) {
        if(name.equals(Constants.ROLES))
            return this.roles;
        return super.getAttributes(name);
    }
    private static MultivaluedHashMap<String, String> substituteAttributesRoles(MultivaluedHashMap<String, String> attributes, List<String> roles){
        MultivaluedHashMap<String, String> newAttributes = new MultivaluedHashMap<>(attributes);
        newAttributes.remove(Constants.ROLES);
        newAttributes.put(Constants.ROLES, roles);
        return newAttributes;
    }
    public static MultivaluedHashMap<String, String> getAttributes(SamlPrincipal samlPrincipal){
        MultivaluedHashMap<String, String> attributes = new MultivaluedHashMap<>();
        attributes.putAll(samlPrincipal.getAttributes());
        return attributes;
    }
    public static MultivaluedHashMap<String, String> getFriendlyAttributes(SamlPrincipal samlPrincipal){
        MultivaluedHashMap<String, String> friendlyAttributes = new MultivaluedHashMap<>();
        for(String friendlyName : samlPrincipal.getFriendlyNames())
            friendlyAttributes.put(friendlyName, samlPrincipal.getFriendlyAttributes(friendlyName));
        return friendlyAttributes;
    }
}
