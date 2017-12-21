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

package org.wso2.carbon.identity.inbound.ldap.provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.inbound.ldap.utils.IdentityLdapException;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.UserStoreManager;
import org.wso2.carbon.user.core.claim.ClaimManager;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.util.HashMap;

public class LDAPUserManager {

    private static final Log log = LogFactory.getLog(LDAPUserManager.class);
    private UserStoreManager carbonUM = null;
    private ClaimManager carbonClaimManager = null;
    private String consumerName;


    public LDAPUserManager(UserStoreManager carbonUserStoreManager, String username, ClaimManager claimManager) {
        carbonUM = carbonUserStoreManager;
        carbonClaimManager = claimManager;
        consumerName = username;
        startTenantFlow();
    }

    private void startTenantFlow() {
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        carbonContext.setTenantDomain(MultitenantUtils.getTenantDomain("carbon.super"));
        carbonContext.getTenantId(true);
        carbonContext.setUsername(MultitenantUtils.getTenantAwareUsername("carbon.super"));
    }

    public void createUser(LDAPUser ldapUser) throws IdentityLdapException { //LDAPUser ldapuser, boolean isBulkAdd
        try {

            String username = ldapUser.getUserName();

            LDAPAttributeMapper ldapAttributeMapper = new LDAPAttributeMapper();
            HashMap<String, String> claimsMap = (HashMap<String, String>) ldapAttributeMapper.getAddUserClaimsMap
                    (ldapUser.getUserAttributes());

            if (carbonUM.isExistingUser(username)) {
                String error = "User with the name : " + username + " already exist in the system";
                log.info(error);
                throw new IdentityLdapException(error);
            }

            if (claimsMap.containsKey(LDAPConstants.LDAPSchemaConstants.PASSWORD_URI)) {
                claimsMap.remove(LDAPConstants.LDAPSchemaConstants.PASSWORD_URI);
            }

            carbonUM.addUser(ldapUser.getUserName(), ldapUser.getPassword(), null, claimsMap, null);
            log.info("User: " + ldapUser.getUserName() + " is created through LDAP protocol.");

        } catch (UserStoreException e) {
            throw new IdentityLdapException("Error occurred while adding user", e);
        }
    }

    public void deleteUser(String userId) throws IdentityLdapException {
        try {
            // Here assume (since id is unique per user) only one user exists for a given id
            carbonUM.deleteUser(userId);
            log.info("User: " + userId + " is deleted through LDAP.");

        } catch (UserStoreException ex) {
            throw new IdentityLdapException("Error occurred while deleting user", ex);
        }
    }

}
