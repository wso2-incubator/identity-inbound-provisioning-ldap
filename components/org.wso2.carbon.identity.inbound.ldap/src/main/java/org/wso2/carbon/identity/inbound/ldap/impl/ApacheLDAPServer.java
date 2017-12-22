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

package org.wso2.carbon.identity.inbound.ldap.impl;

import org.apache.axiom.om.util.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.directory.server.core.CoreSession;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.LdapPrincipal;
import org.apache.directory.server.core.factory.DirectoryServiceFactory;
import org.apache.directory.server.core.interceptor.Interceptor;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.ldap.handlers.bind.MechanismHandler;
import org.apache.directory.server.ldap.handlers.bind.cramMD5.CramMd5MechanismHandler;
import org.apache.directory.server.ldap.handlers.bind.digestMD5.DigestMd5MechanismHandler;
import org.apache.directory.server.ldap.handlers.bind.gssapi.GssapiMechanismHandler;
import org.apache.directory.server.ldap.handlers.bind.ntlm.NtlmMechanismHandler;
import org.apache.directory.server.ldap.handlers.bind.plain.PlainMechanismHandler;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.shared.ldap.constants.SupportedSaslMechanisms;
import org.apache.directory.shared.ldap.entry.DefaultServerAttribute;
import org.apache.directory.shared.ldap.entry.EntryAttribute;
import org.apache.directory.shared.ldap.entry.Modification;
import org.apache.directory.shared.ldap.entry.ModificationOperation;
import org.apache.directory.shared.ldap.entry.ServerModification;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.schema.registries.AttributeTypeRegistry;
import org.wso2.carbon.identity.inbound.ldap.interceptor.WSO2Interceptor;
import org.wso2.carbon.identity.inbound.ldap.utils.IdentityLdapException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An implementation of LDAP server. This wraps apacheds implementation and provides an
 * abstract interface to operate on directory server.
 */
public class ApacheLDAPServer implements LDAPServer {

    private static final Log log = LogFactory.getLog(ApacheLDAPServer.class);
    private static ApacheLDAPServer apacheLDAPServer = new ApacheLDAPServer();
    Map<String, MechanismHandler> mechanismHandlerMap;
    private DirectoryService service;
    private LdapServer ldapServer;
    private EndPointConfiguration endPointConfigurations;

    private ApacheLDAPServer() {
    }

    public static ApacheLDAPServer getApacheLDAPServer() {
        return apacheLDAPServer;
    }

    @Override
    public void init(EndPointConfiguration configurations) throws IdentityLdapException {

        if (configurations == null) {
            log.error("LDAP org.wso2.carbon.identity.inbound.ldap.provider initialization failed. " +
                    "LDAP org.wso2.carbon.identity.inbound.ldap.provider configuration is invalid.");
            throw new IdentityLdapException("Cannot initialize LDAP org.wso2.carbon.identity.inbound.ldap.provider " +
                    "Configuration is null");
        }

        this.endPointConfigurations = configurations;

        try {

            initializeDefaultDirectoryService();
            initializeLDAPServer();

        } catch (Exception e) {
            log.error("LDAP org.wso2.carbon.identity.inbound.ldap.provider initialization failed.", e);
            throw new IdentityLdapException("Error initializing ApacheLDAP org.wso2.carbon." +
                    "identity.inbound.ldap.provider. ", e);
        }

    }

    @Override
    public void start() throws IdentityLdapException {

        try {
            this.service.startup();
            this.ldapServer.start();

            log.info("LDAP org.wso2.carbon.identity.inbound.ldap.provider started.");
        } catch (Exception e) {

            log.error("Error starting LDAP org.wso2.carbon.identity.inbound.ldap.provider.", e);
            throw new IdentityLdapException("Can not start the ldap org.wso2.carbon.identity.inbound.ldap.provider ", e);
        }
    }

    @Override
    public void stop() throws IdentityLdapException {

        try {

            this.ldapServer.stop();
            this.service.shutdown();
            log.info("LDAP org.wso2.carbon.identity.inbound.ldap.provider stopped.");
        } catch (Exception e) {

            log.error("Error stopping LDAP org.wso2.carbon.identity.inbound.ldap.provider.", e);
            throw new IdentityLdapException("Can not start the ldapendpoint", e);
        }
    }

    protected void initializeDefaultDirectoryService() throws IdentityLdapException {
        try {

            DirectoryServiceFactory factory = CarbonDirectoryServiceFactory.DEFAULT;
            this.service = factory.getDirectoryService();

            configureDirectoryService();

            factory.init(this.endPointConfigurations.getInstanceId());

        } catch (Exception e) {
            throw new IdentityLdapException("Can not start the Default apacheds service ", e);
        }
    }

    private AttributeType getAttributeType(String attributeName) throws IdentityLdapException {
        if (this.service != null) {
            SchemaManager schemaManager = this.service.getSchemaManager();
            if (schemaManager != null) {
                AttributeTypeRegistry registry = schemaManager.getAttributeTypeRegistry();
                if (registry != null) {
                    try {
                        String oid = registry.getOidByName(attributeName);
                        return registry.lookup(oid);
                    } catch (Exception e) {
                        String msg = "An error occurred while querying attribute " + attributeName +
                                " from registry.";
                        log.error(msg, e);
                        throw new IdentityLdapException(msg, e);
                    }
                } else {
                    String msg = "Could not get attribute registry.";
                    log.error(msg);
                    throw new IdentityLdapException(msg);

                }

            } else {
                String msg = "Cannot access schema manager. Directory server may not have started.";
                log.error(msg);
                throw new IdentityLdapException(msg);

            }
        } else {
            String msg = "The directory service is null. LDAP server might not have started.";
            log.error(msg);
            throw new IdentityLdapException(msg);

        }
    }

    @Override
    public String getConnectionDomainName() throws IdentityLdapException {

        LdapPrincipal adminPrinciple = null;
        try {
            adminPrinciple = getAdminPrinciple();
        } catch (Exception e) {
            String msg="Can not get the connection domain name" ;
            throw new IdentityLdapException(msg);
        }
        return adminPrinciple.getClonedName().getName();
    }

    private LdapPrincipal getAdminPrinciple()
            throws Exception {

        if (this.service != null) {
            CoreSession adminSession;
                adminSession = getAdminSession(this.service);
            if (adminSession != null) {
                LdapPrincipal adminPrincipal = adminSession.getAuthenticatedPrincipal();
                if (adminPrincipal != null) {
                    return adminPrincipal;
                } else {
                    String msg = "Could not retrieve admin principle. Failed changing connection " +
                            "user password.";
                    log.error(msg);
                    throw new IdentityLdapException(msg);
                }
            } else {
                String msg = "Directory admin session is null. The LDAP server may not have " +
                        "started yet.";
                log.error(msg);
                throw new IdentityLdapException(msg);
            }
        } else {

            String msg = "Directory service is null. The LDAP server may not have started yet.";
            log.error(msg);
            throw new IdentityLdapException(msg);
        }

    }

    @Override
    public void changeConnectionUserPassword(String password) throws Exception {

        if (this.service != null) {
            CoreSession adminSession;

                adminSession = getAdminSession(this.service);

            if (adminSession != null) {
                LdapPrincipal adminPrincipal = adminSession.getAuthenticatedPrincipal();
                if (adminPrincipal != null) {

                    String passwordToStore = "{" + ConfigurationConstants.ADMIN_PASSWORD_ALGORITHM +
                            "}";

                    MessageDigest messageDigest;
                    try {
                        messageDigest = MessageDigest.getInstance(
                                ConfigurationConstants.ADMIN_PASSWORD_ALGORITHM);
                    messageDigest.update(password.getBytes());
                    byte[] bytes = messageDigest.digest();
                    String hash = Base64.encode(bytes);
                    passwordToStore = passwordToStore + hash;

                    adminPrincipal.setUserPassword(passwordToStore.getBytes());

                    EntryAttribute passwordAttribute = new DefaultServerAttribute(
                            getAttributeType("userPassword"));
                    passwordAttribute.add(passwordToStore.getBytes());

                    ServerModification serverModification =
                            new ServerModification(ModificationOperation.REPLACE_ATTRIBUTE,
                                    passwordAttribute);

                    List<Modification> modifiedList = new ArrayList<Modification>();
                    modifiedList.add(serverModification);
                        adminSession.modify(adminPrincipal.getClonedName(), modifiedList);
                    } catch (NoSuchAlgorithmException e) {
                        throw new IdentityLdapException(
                                "Could not find digest algorithm - " +
                                        ConfigurationConstants.ADMIN_PASSWORD_ALGORITHM, e);
                    } catch (Exception e) {
                        String msg = "Failed changing connection user password.";
                        log.error(msg, e);
                        throw new IdentityLdapException(msg, e);
                    }

                } else {
                    String msg = "Could not retrieve admin principle. Failed changing connection " +
                            "user password.";
                    log.error(msg);
                    throw new IdentityLdapException(msg);
                }
            } else {
                String msg = "Directory admin session is null. The LDAP server may not have " +
                        "started yet.";
                log.error(msg);
                throw new IdentityLdapException(msg);
            }
        } else {
            String msg = "Directory service is null. The LDAP server may not have started yet.";
            log.error(msg);
            throw new IdentityLdapException(msg);
        }

    }

    private CoreSession getAdminSession(DirectoryService service) throws Exception {
        try {
            return service.getAdminSession();
        } catch (Exception e) {
            String msg = "An error occurred while retraining admin session.";
            log.error(msg, e);
            throw new IdentityLdapException(msg, e);
        }
    }

    private void configureDirectoryService() throws IdentityLdapException {

        if (null == this.endPointConfigurations) {
            throw new IdentityLdapException("Directory service is not initialized.");
        }

        System.setProperty("workingDirectory", this.endPointConfigurations.getWorkingDirectory());

        this.service.setShutdownHookEnabled(false);

        this.service.setInstanceId(this.endPointConfigurations.getInstanceId());
        this.service.setAllowAnonymousAccess(this.endPointConfigurations.isAllowAnonymousAccess());
        this.service.setAccessControlEnabled(this.endPointConfigurations.isAccessControlOn());
        this.service.setDenormalizeOpAttrsEnabled(
                this.endPointConfigurations.isDeNormalizedAttributesEnabled());
        this.service.setMaxPDUSize(this.endPointConfigurations.getMaxPDUSize());


        // Add interceptors
        List<Interceptor> list = this.service.getInterceptors();
        list.add(0, new WSO2Interceptor());
        this.service.setInterceptors(list);
    }

    protected void initializeLDAPServer() throws IdentityLdapException {

        try {
            if (null == this.service || null == this.endPointConfigurations) {
                throw new Exception(
                        "The default apacheds service is not initialized. " +
                                "Make sure apacheds service is initialized first.");
            }

            this.ldapServer = new LdapServer();

            this.ldapServer.setTransports(new TcpTransport(this.endPointConfigurations.getEndpointPort()));

            // set server initial properties
            this.ldapServer.setAllowAnonymousAccess(true);
            this.ldapServer.setSaslHost(this.endPointConfigurations.getSaslHostName());
            this.ldapServer.setSaslPrincipal(this.endPointConfigurations.getSaslPrincipalName());
            this.ldapServer.setDirectoryService(this.service);

            setupSaslMechanisms();

        } catch (Exception e) {
            throw new IdentityLdapException("can not add the extension handlers ", e);
        }
    }

    private void setupSaslMechanisms() {
        mechanismHandlerMap = new HashMap<String, MechanismHandler>();

        mechanismHandlerMap.put(SupportedSaslMechanisms.PLAIN, new PlainMechanismHandler());

        CramMd5MechanismHandler cramMd5MechanismHandler = new CramMd5MechanismHandler();
        mechanismHandlerMap.put(SupportedSaslMechanisms.CRAM_MD5, cramMd5MechanismHandler);

        DigestMd5MechanismHandler digestMd5MechanismHandler = new DigestMd5MechanismHandler();
        mechanismHandlerMap.put(SupportedSaslMechanisms.DIGEST_MD5, digestMd5MechanismHandler);

        GssapiMechanismHandler gssapiMechanismHandler = new GssapiMechanismHandler();
        mechanismHandlerMap.put(SupportedSaslMechanisms.GSSAPI, gssapiMechanismHandler);

        NtlmMechanismHandler ntlmMechanismHandler = new NtlmMechanismHandler();

        mechanismHandlerMap.put(SupportedSaslMechanisms.NTLM, ntlmMechanismHandler);
        mechanismHandlerMap.put(SupportedSaslMechanisms.GSS_SPNEGO, ntlmMechanismHandler);

        this.ldapServer.setSaslMechanismHandlers(mechanismHandlerMap);
    }

}
