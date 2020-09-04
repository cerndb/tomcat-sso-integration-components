/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//        © Copyright 2020 CERN
//        This software is distributed under the terms of the GNU General Public Licence
//        version 3 (GPL Version 3), copied verbatim in the file "LICENSE". In applying
//        this licence, CERN does not waive the privileges and immunities granted to it
//        by virtue of its status as an Intergovernmental Organization or submit itself
//        to any jurisdiction.
package ch.cern.sso.tomcat.common.utils;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.realm.GenericPrincipal;
import org.keycloak.adapters.saml.SamlPrincipal;

/**
 *
 * @author lurodrig
 */
public class PrincipalWrapper implements Serializable {

    public static final long serialVersionUID = 0L;

    private String name;
    private String hrId;
    private String preferredLanguage;
    private String identityClass;
    private List<String> roles;

    public PrincipalWrapper() {
    }

    public PrincipalWrapper(Principal principal) {
        String hrId = new String("0");
        String preferredLanguage = new String("EN");
        String identityClass = new String();
        List<String> roles = new ArrayList<>();
        if (principal instanceof SamlPrincipal) {
            hrId = ((SamlPrincipal) principal).getAttribute(SsoClaims.SSO_CLAIM_HR_ID);
            identityClass = ((SamlPrincipal) principal).getAttribute(SsoClaims.SSO_CLAIM_IDENTITY_CLASS);
            if (((SamlPrincipal) principal).getAttribute(SsoClaims.SSO_CLAIM_PREFERRED_LANGUAGE) != null) {
                preferredLanguage = String.valueOf(((SamlPrincipal) principal).getAttribute(SsoClaims.SSO_CLAIM_PREFERRED_LANGUAGE).charAt(0));
            }
            roles = ((SamlPrincipal) principal).getAttributes(Constants.ROLES);
        } else if (principal instanceof GenericPrincipal) {
            // This covers the case of accesing from non SSO protected context: for instance /Catalog
            GenericPrincipal p = (GenericPrincipal) principal;
            if (p.getUserPrincipal() instanceof SamlPrincipal) {
                SamlPrincipal userPrincipal = (SamlPrincipal) ((GenericPrincipal) principal).getUserPrincipal();
                hrId = userPrincipal.getAttribute(SsoClaims.SSO_CLAIM_HR_ID);
                identityClass = userPrincipal.getAttribute(SsoClaims.SSO_CLAIM_IDENTITY_CLASS);
                if (userPrincipal.getAttribute(SsoClaims.SSO_CLAIM_PREFERRED_LANGUAGE) != null) {
                    preferredLanguage = String.valueOf(userPrincipal.getAttribute(SsoClaims.SSO_CLAIM_PREFERRED_LANGUAGE).charAt(0));
                }
                roles = userPrincipal.getAttributes(Constants.ROLES);
            }
        }
        this.setName(principal.getName());
        this.setHrId(hrId);
        this.setPreferredLanguage(preferredLanguage);
        this.setIdentityClass(identityClass);
        this.setRoles(roles);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHrId() {
        return hrId;
    }

    public void setHrId(String hrId) {
        this.hrId = hrId;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getIdentityClass() {
        return identityClass;
    }

    public void setIdentityClass(String identityClass) {
        this.identityClass = identityClass;
    }
}
