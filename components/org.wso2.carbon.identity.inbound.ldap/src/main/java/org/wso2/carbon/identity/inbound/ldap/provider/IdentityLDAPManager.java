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
import org.wso2.carbon.identity.inbound.ldap.internal.LDAPEPComponentHolder;
import org.wso2.carbon.identity.inbound.ldap.utils.IdentityLdapException;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.core.UserStoreManager;
import org.wso2.carbon.user.core.claim.ClaimManager;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

public class IdentityLDAPManager {

    private static final Log log = LogFactory.getLog(IdentityLDAPManager.class);


    public LDAPUserManager getUserManager(String username) throws Exception {
        LDAPUserManager ldapUserManager = null;
        String tenantDomain = MultitenantUtils.getTenantDomain(username);
        String tenantLessUserName = MultitenantUtils.getTenantAwareUsername(username);

        try {

            //get super tenant context and get relam service which is an osgi service
            RealmService realmService = LDAPEPComponentHolder.getRealmService();
            if (realmService != null) {
                int tenantId = realmService.getTenantManager().getTenantId(tenantDomain);

                //get tenant's user realm
                UserRealm userRealm = realmService.getTenantUserRealm(tenantId);
                ClaimManager claimManager;
                if (userRealm != null) {
                    //get claim manager for manipulating attributes
                    claimManager = (ClaimManager) userRealm.getClaimManager();


                    String authenticatedUser = PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername();
                    if (authenticatedUser == null) {
                        PrivilegedCarbonContext.getThreadLocalCarbonContext().setUsername(tenantLessUserName);
                        if (log.isDebugEnabled()) {
                            log.debug("User read from carbon context is null, hence setting " +
                                    "authenticated user: " + tenantLessUserName);
                        }
                    }

                    ldapUserManager = new LDAPUserManager((UserStoreManager) userRealm.getUserStoreManager(), username, claimManager);
                    // ldapUserManager.createUser();
                }

            }


        } catch (Exception ex) {
            throw new IdentityLdapException("Error occur while getting userstore manager", ex);
        }

        return ldapUserManager;
    }
}
