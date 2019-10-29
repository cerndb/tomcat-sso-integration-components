package ch.cern.sso.tomcat.valves.mocks;

import ch.cern.sso.tomcat.common.utils.SsoClaims;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.catalina.connector.Request;
import org.apache.catalina.realm.GenericPrincipal;
import org.keycloak.adapters.saml.SamlPrincipal;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.dom.saml.v2.assertion.AssertionType;

/**
 *
 * @author lurodrig
 */
public class MockConstants {

    public static final String[] ROLES = {"dbondemand-users", "ca-allowed-user-certificate-mp", "sc-dep-dist-reports", "service-db-int-da", "LxPlus-Authorized-Users", "CERN Users", "timber-users-rw", "room-booking-users", "test-synchro-2-dyn", "club-running-relay2016-participants", "CMF FrontEnd Users", "NICE Enforce Password-protected Screensaver", "openlab-V-Collaboration", "it-dep-and-ais", "oracle-em-ais-mw-users", "timber-users", "eam-user-meetings", "ais-monitoring", "service-db-adm-mwm", "service-appserver-interactive", "lurodrig-bucket", "accsoft-devtools-authorized-groups", "es-ais-admins", "edms-users-all", "NICE Users", "it-service-backup-tsm615", "cert-security-info", "ais-members", "ais-incidents", "snow-users", "developers-forum-organizers-admin", "dba-phydb", "eam-users", "ggo-test", "itmon-es-public", "es-ais-users", "monit-users", "cernbox-project-it-db-jeedy-writers", "jeedy-cs-access-list", "Users IT-DB", "ap-it-staff", "lxadm-residual-users", "naming-key-users-admin", "Users by Home CERNHOMEL", "VC-all-users", "service-db-mysql-single", "snow-it-users", "controls-configuration-news", "VC-librarians-wlsSaml2slo", "club-running-relay2015-participants", "ords-rest-access-itdbtf", "it-dep-db-ims", "it-dep-full", "service-db-adm-wls", "Users IT", "atlas-webaccess-computing", "gs-and-it-snow", "nationality-ES-cern", "ESPG-Submission", "dad-edit-test", "it-dep-db-extended", "service-db-adm-paas", "cernbox-project-it-db-wls-pack-readers", "myall-cern", "IT Web IT", "cern-status-staf", "drupal-admins-db-blog", "test-sl-dynamic", "event-le-diner-francophone", "users-at-cern", "service-twiki-users", "cern-sci", "service-it-db-infrastructure", "occupants-bldg-31", "cern-accounts-primary", "ais-db-coord", "service-db-openstack", "spain-staff-personnel", "Twiki Users", "database-general-purpose-announcements", "it-dep-staff", "ELG-CERN", "Bike-2-Work-Meyrin", "service-j2ee-admins", "aisbi-all-users", "service-db-adm-dgtest", "es-ims-admins", "service-db-team12c", "it-service-backup-tsm521", "mwod-private-registry", "service-db-int-oracle-features", "it-dep-db-blog-authors", "oracle-em-test-users", "ai-playground", "es-cs-users", "nationality-all-es", "aisbi-tpr-users", "it-service-backup-b613", "oracle-em-monitoring-applications", "es-ims-users", "bo-cmms-users", "fp-pi-cds-cern", "asdf-members", "service-db-adm-conmgr", "cern-personnel", "atlas-external-computing", "it-dept", "jeedy-ims-access-list", "ai-admins", "service-db-int-firewall", "snow-egroup-whitelist", "snow-api-users", "computing-groups-administrators", "test-discovery-a-m", "it-dep-fas-shp-read", "snow-it-users-extended", "jpsx-cg-admins", "service-db-adm-data-analytics", "cernois-es", "ca-allowed-user-certificate", "event-le-diner-francophone-admin", "cern-personnel-comm-en", "puppet-users", "vidyo-users-bulk", "Facebook Workplace Users", "openlab-V-CERN-internal", "Bike-2-Work", "cern-oracle-admins", "JPS Admins", "ai-hadoop-admin", "foundservices-access-egroups", "aisbi-tpr-merittool-users", "VC-users-wlsSaml2slo", "CERN-Direct-Employees", "service-db-adm-replication", "oracle-general-purpose-users", "itssb-contributors", "c5-minutes", "e-procurement-supplier-web-service", "it-service-backup-b513", "service-db-int-hadoop", "software-developers", "service-db-int-oem", "edh-web-service-users", "cloud-infra-rhel-image-builders", "openshift-grafana-users", "fitness-club-zumba", "atlas-computing-denominator", "staff-all", "CERN-Employees", "service-db-linux-support", "atlas-readaccess-twiki-computing", "cern-oracle", "CERN-Pension-Fund-Survey", "ap-nonmembers-all", "Domain Users", "room-booking-users-all", "service-db-hadoop", "snow-itssb", "chis-membersE", "it-service-backup-tsm518", "ai-hadoop-users", "snow-news-membersD", "LxAdm-Authorized-Users", "it-service-backup-tsm516", "it-db-ims-dbtest-users", "snow-it-itil", "snow-news-members", "white-hats", "atlas-readaccess-twiki", "cloud-containers-discuss", "survey-citymobil", "service-db-int-timesten", "openlab-summerstudents-supervisors-2016", "it-dep-db-ims-extended", "it-db-ims-dbtest-pattern2-users", "developers-forum-contacts", "openlab-summerstudents-supervisors-2018", "openlab-summerstudents-supervisors-2017", "CSC-IMS-mddle-tier", "staf-fell-pjas-at-cern", "oracle-em-users", "VC-users-dbchange", "aisbi-tpr-dashboard-users", "es-edms-users", "atools-public", "ais-users", "it-dep-db", "esii-coordination", "occupants-bldg-31-floor-3", "impact-login-as-test", "gssb-contributors", "aisbi-domain-users", "it", "spain-staff-local", "it-dep-fas-shp-edit", "Users by Letter L", "CERNTS-cernts-Users", "cernbox-project-it-db-jeedy-readers", "swan-newinterface-test", "service-db-adm-timesten", "it-dep-ld-staff", "accsoft-devtools-ldap-root", "j2ee-public-service-users-test", "aisadm-acl", "atools-master", "agc-infra_oracle", "official-travellers-survey", "cs2hep", "service-db-int-dbod", "it-hadoop-service-development", "it-db-ims-dbtest-pattern1-users", "technical-review-list", "Rule-5", "vidyo-users", "service-db-adm-firewall", "software-developers-moderators", "atools-ais-customers", "CERN-Academic-Training-Program", "eduroam-users", "fax-candidates", "it-dep-des-ais-members", "cloud-infrastructure-users", "service-db-adm-dbod", "developers-forum-organizers", "VC-users-syscontrol", "lurodrig-playground-2", "lurodrig-playground-1", "club-football", "it-service-backup", "NICE Profile Redirection", "it-snow-fms-supporters", "service-j2ee", "ap-it-nonmem", "service-db-int-conmgr", "atc-enquiry", "strmmon-auth", "computing-groups", "all-mpe", "gis-gisportal", "service-db-adm-oem", "jeedy-ais-access-list", "Twiki Atlas web", "it-internal-staff", "service-db-int-dgtest", "oracle-administrative-databases-users", "test-egroup-expand-snow", "service-db-int-inmem", "ords-rest-access-rebekka-test", "dbondemand-infra", "ais-ws-foundservices-egroups", "service-db-systems", "NICE Tests GPC", "oracle-users", "syscontrol-users-accnt", "database-administrative_annoucements", "service-db-adm-da", "snow-ssb", "it-db-storage", "cern-personnel-on-site", "info-newphysics-workshop", "cern-staff", "service-db-int-castor", "asceri-info", "ais-ws-foundservices-edh", "VC-users-admin", "service-db-adm-hadoop", "bo-cvg-users", "ais-monitoring-viewers", "cnl-contributors", "it-tf", "drupal-site-admins", "es-cs-admins", "service-it-oracle-monitor-gsm", "service-db-interactive", "aisbo-domain-users", "edms-users", "GP Apply NoAdmin", "service-db-oracle-connmgr", "SC-IT-DEP", "spain-staff", "Atlas TWiki Users", "service-db-systems-dynamic", "foundservices-access-edh", "it-dep-full-dynamic", "ords-rest-access-adams-test", "atlas-readaccess-main", "fp-pi-cern-personnel", "service-it-oracle-monitor", "GP Apply Favorites Redirection", "VC-users-gtenagli", "All Exchange People", "compute-accounting-web", "openlab-V-fellows", "naming-project-team", "it-dep", "hr-ldic", "spain-staff-IT", "jps-cg-admins", "NICE MyDocuments Redirection (New)", "spain-asamblea", "edms-agile-users-arch", "es-edms-admins", "it-dep-dynamic", "snow-it-supporters", "it-dep-db-openlab", "pac-team", "oracle-devdb12-database-users", "cern-computing", "jeedy-edms-access-list", "naming-key-users", "snow-intervention-announcement", "ai-admins-crm", "service-it-oracle-monitor-dynamic", "developers-forum-announce", "agc-damadmin", "service-db-int-replication", "CSC-IMS-infra", "agc-system", "aisbi-tpr-dashboard-meritscatterplot-users", "IT-Xmas-picnic-2017", "project-lhcathome-es", "staff-cat1and2", "ords-rest-access-ims"};
    public static final String SAML_PRINCIPAL_ID = "_16ee8962-92f3-438c-b7a0-313e802268ac";
    public static final String SAML_PRINCIPAL_SUBJECT = "bob";
    public static final String SAML_PRINCIPAL_NAME_ID_FORMAT = "urn:oasis:names:tc:SAML:2.0:nameid-format:transient";
    public static final String PRINCIPAL_ROLES = "Roles";
    public static final String PRINCIPAL_NAME = "bob";
    public static final String PRINCIPAL_PASSWORD = "";

    public static void initSamlPrincipalAttributes(MultivaluedHashMap<String, String> attributes) {
        attributes.add(SsoClaims.SSO_CLAIM_UPN, "luis.rodriguez.fernandez@cern.ch");
        attributes.add(SsoClaims.SSO_CLAIM_PERSON_ID, "720335");
        attributes.add(SsoClaims.SSO_CLAIM_EMAIL_ADRESS, "luis.rodriguez.fernandez@cern.ch");
        attributes.add(SsoClaims.SSO_CLAIM_AUTH_LEVEL, "Normal");
        attributes.add(SsoClaims.SSO_CLAIM_PREFERRED_LANGUAGE, "FR");
        attributes.add(SsoClaims.SSO_CLAIM_IDENTITY_CLASS, "CERN Registered");
        attributes.addAll(PRINCIPAL_ROLES, ROLES);
        attributes.add(SsoClaims.SSO_CLAIM_2005_UPN, "luis.rodriguez.fernandez@cern.ch");
        attributes.add(SsoClaims.SSO_CLAIM_MOBILE_NUMBER, "+41754110558");
        attributes.add(SsoClaims.SSO_CLAIM_PHONE_NUMBER, "+4122767354");
        attributes.add(SsoClaims.SSO_CLAIM_FIRST_NAME, "Luis");
        attributes.add(SsoClaims.SSO_CLAIM_UID_NUMBER, "14825");
        attributes.add(SsoClaims.SSO_CLAIM_HOME_INSTITUTE, "CERN");
        attributes.add(SsoClaims.SSO_CLAIM_GID_NUMBER, "2763");
        attributes.add(SsoClaims.SSO_CLAIM_LAST_NAME, "Rodriguez Fernandez");
        attributes.add(SsoClaims.SSO_CLAIM_DEPARTMENT, "IT/DB");
        attributes.add(SsoClaims.SSO_CLAIM_BUILDING, "31 3-024");
        attributes.add(SsoClaims.SSO_CLAIM_COMMON_NAME, "lurodrig");
        attributes.add(SsoClaims.SSO_CLAIM_ROLE, "CERN Users");
        attributes.add(SsoClaims.SSO_CLAIM_FEDERATION, "CERN");
    }

    public static SamlPrincipal createSamlPrincipal() throws DatatypeConfigurationException {
        XMLGregorianCalendar issueInstant = DatatypeFactory.newInstance().newXMLGregorianCalendar();
        AssertionType assertion = new AssertionType(MockConstants.SAML_PRINCIPAL_ID, issueInstant);
        MultivaluedHashMap<String, String> attributes = new MultivaluedHashMap<>();
        MockConstants.initSamlPrincipalAttributes(attributes);
        MultivaluedHashMap<String, String> friendlyAttributes = new MultivaluedHashMap<>();
        return new SamlPrincipal(assertion, MockConstants.PRINCIPAL_NAME, MockConstants.SAML_PRINCIPAL_SUBJECT, MockConstants.SAML_PRINCIPAL_NAME_ID_FORMAT, attributes, friendlyAttributes);
    }
    
     public static GenericPrincipal createGenericPrincipal(Principal principal) {
        List<String> roles = Arrays.asList(MockConstants.ROLES);
        return new GenericPrincipal(PRINCIPAL_NAME, PRINCIPAL_PASSWORD, roles, principal);
    }
}
