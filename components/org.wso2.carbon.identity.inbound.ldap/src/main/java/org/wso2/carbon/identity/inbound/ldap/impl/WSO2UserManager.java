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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.directory.server.core.interceptor.context.AddOperationContext;
import org.apache.directory.server.core.interceptor.context.DeleteOperationContext;
import org.apache.directory.shared.ldap.entry.Entry;
import org.wso2.carbon.identity.inbound.ldap.provider.IdentityLDAPManager;
import org.wso2.carbon.identity.inbound.ldap.provider.LDAPConstants;
import org.wso2.carbon.identity.inbound.ldap.provider.LDAPUser;
import org.wso2.carbon.identity.inbound.ldap.provider.LDAPUserManager;
import org.wso2.carbon.identity.inbound.ldap.utils.IdentityLdapException;

public class WSO2UserManager {

    private static final Log log = LogFactory.getLog(WSO2UserManager.class);
    private LDAPUserManager ldapUserManager = null;
    private IdentityLDAPManager identityLDAPManager = new IdentityLDAPManager();

    public void addUser(AddOperationContext addOperationContext) throws IdentityLdapException {
        try {
            ldapUserManager = identityLDAPManager.getUserManager("admin");
            Entry entry = addOperationContext.getEntry();
            LDAPUser ldapUser = new LDAPUser();
            ldapUser.setUsername(entry.get(LDAPConstants.LDAPSchemaConstants.USERNAME).get().getString());
            ldapUser.setPassword(entry.get(LDAPConstants.LDAPSchemaConstants.PASSWORD).get().getString());
            ldapUser.setUserAttributes(entry);
            ldapUserManager.createUser(ldapUser);
        } catch (Exception ex) {
            String msg = "An error occurred while performing add operation";
            throw new IdentityLdapException(msg, ex);
        }
    }

    public void deleteUser(DeleteOperationContext deleteOperationContext) throws IdentityLdapException {
        try {
            ldapUserManager = identityLDAPManager.getUserManager("admin");
            String dn = deleteOperationContext.getDn().toString();
            String username = getDeleteUsername(dn);
            ldapUserManager.deleteUser(username);
        } catch (Exception ex) {
            String msg = "An error occurred while performing delete operation";
            throw new IdentityLdapException(msg, ex);
        }
    }

    private String getDeleteUsername(String dn) {
        String[] splitDn = dn.split(",");
        String[] uid = null;
        if (splitDn != null) {
            uid = splitDn[0].split("=");
        }
        return uid[1];
    }

}
