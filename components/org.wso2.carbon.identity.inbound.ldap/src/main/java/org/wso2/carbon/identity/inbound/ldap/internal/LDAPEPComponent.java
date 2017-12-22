/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.identity.inbound.ldap.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.identity.inbound.ldap.impl.ApacheLDAPServer;
import org.wso2.carbon.identity.inbound.ldap.impl.EndPointConfiguration;
import org.wso2.carbon.identity.inbound.ldap.impl.LDAPServer;
import org.wso2.carbon.identity.inbound.ldap.utils.ConfigurationBuilder;
import org.wso2.carbon.identity.inbound.ldap.utils.IdentityLdapException;
import org.wso2.carbon.user.core.service.RealmService;

import java.io.File;


@Component(
        name = "org.wso2.carbon.identity.inbound.ldap.internal.LDAPEPComponent",
        immediate = true)
public class LDAPEPComponent {

    private static final Log log = LogFactory.getLog(LDAPEPComponent.class);

    private LDAPServer ldapServer;
    private static final String APACHEDS_SCHEMA_LOCATION= "apacheds.schema.zip.store.location";

    @Activate
    public void activate(ComponentContext context) {

        try {

            /*Read the ldap-org.wso2.carbon.identity.inbound.ldap.endpoint configuration file.*/

            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

            configurationBuilder.buildConfiguration(getLdapConfigurationFile());

            /*Make relevant objects that encapsulate different parts of config file.*/
            configurationBuilder.buildLDAPEndpointConfigurartion();

            boolean LdapEndpointEnabled = configurationBuilder.isLdapEndpointEnabled();
            //expose LDAP Endpoint only if ldap-org.wso2.carbon.identity.inbound.ldap.endpoint is enabled.
            if (LdapEndpointEnabled) {

                EndPointConfiguration endPointConfiguration = configurationBuilder.getEndPointConfiguration();

                /*set the embedded-apacheds's schema location which is: carbon-home/repository/data/
                  is-default-schema.zip
                */
                setSchemaLocation();

                /* Set working directory where schema directory and ldap partitions are created*/
                setWorkingDirectory(endPointConfiguration);

                startLdapServer(endPointConfiguration);

                /* replace default password with that is provided in the configuration file.*/
                this.ldapServer.changeConnectionUserPassword(configurationBuilder.getConnectionPassword());


                if (log.isDebugEnabled()) {
                    log.debug("apacheds-enpoint component started.");
                }

            } else if (!LdapEndpointEnabled) {
                //if needed, create a dummy tenant manager service and register it.
                log.info("LDAP Endpoint is disabled.");
            }

        } catch (Exception e) {
            log.error("Could not start the ldap-org.wso2.carbon.identity.inbound.ldap.endpoint. ", e);
        }

    }

    @Deactivate
    public void deactivate(ComponentContext context)
            throws Exception {
        if (this.ldapServer != null) {

            this.ldapServer.stop();
        }
    }

    private void setSchemaLocation() throws Exception {
        String schemaLocation = "repository" + File.separator + "data" + File.separator +
                "is-default-schema.zip";
        File dataDir = new File(getCarbonHome(), schemaLocation);

        // Set schema location
        System.setProperty(APACHEDS_SCHEMA_LOCATION, dataDir.getAbsolutePath());
    }

    private String getCarbonHome() throws Exception {
        String carbonHome = System.getProperty("carbon.home");
        if (carbonHome == null) {
            String msg = "carbon.home property not set. Cannot find carbon home directory.";
            log.error(msg);
            throw new IdentityLdapException(msg);
        }

        return carbonHome;
    }

    private File getLdapConfigurationFile() throws Exception {

        String configurationFilePath = "repository" + File.separator + "conf" + File.separator + "identity"
                + File.separator + "identity.xml";
        return new File(getCarbonHome(), configurationFilePath);
    }

    private void setWorkingDirectory(EndPointConfiguration endPointConfiguration) throws Exception {

        if (".".equals(endPointConfiguration.getWorkingDirectory())) {
            File dataDir = new File(getCarbonHome(), "repository/data");
            if (!dataDir.exists() && !dataDir.mkdir()) {
                String msg = "Unable to create data directory at " + dataDir.getAbsolutePath();
                log.error(msg);
                throw new IdentityLdapException(msg);
            }

            File bundleDataDir = new File(dataDir, "org.wso2.carbon.directory");
            if (!bundleDataDir.exists() && !bundleDataDir.mkdirs()) {
                String msg = "Unable to create schema data directory at " + bundleDataDir.
                        getAbsolutePath();
                log.error(msg);
                throw new IdentityLdapException(msg);


            }

            endPointConfiguration.setWorkingDirectory(bundleDataDir.getAbsolutePath());
        }
    }

    private void startLdapServer(EndPointConfiguration endPointConfiguration) throws Exception {

        this.ldapServer = ApacheLDAPServer.getApacheLDAPServer();

        if (log.isDebugEnabled()) {
            log.debug("Initializing Directory Server with working directory " + endPointConfiguration.
                    getWorkingDirectory() + " and port " + endPointConfiguration.getEndpointPort());
        }

        this.ldapServer.init(endPointConfiguration);

        this.ldapServer.start();
    }

    @Reference(
            name = "user.realmservice.default",
            service = RealmService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRealmService")
    protected void setRealmService(RealmService realmService) {

        if (log.isDebugEnabled()) {
            log.debug("realmService set in LDAPEndPointCommonComponent bundle");
        }
        LDAPEPComponentHolder.setRealmService(realmService);
    }

    /**
     * Unset realm service implementation
     */
    protected void unsetRealmService(RealmService realmService) {

        if (log.isDebugEnabled()) {
            log.debug("realmService unset in  LDAPEndPointComponent bundle");
        }
        LDAPEPComponentHolder.setRealmService(null);
    }

}
