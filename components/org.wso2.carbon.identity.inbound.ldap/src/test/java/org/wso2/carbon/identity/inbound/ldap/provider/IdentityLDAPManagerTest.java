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

import org.apache.axis2.util.XMLUtils;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.base.CarbonBaseConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.inbound.ldap.internal.LDAPEPComponentHolder;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.claim.ClaimManager;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.user.core.tenant.TenantManager;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.nio.file.Paths;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@PrepareForTest({MultitenantUtils.class, LDAPEPComponentHolder.class, XMLUtils.class})
@PowerMockIgnore({"javax.net.*", "javax.security.*", "javax.crypto.*", "javax.xml.*", "XMLUtils.java"})
public class IdentityLDAPManagerTest extends PowerMockTestCase {

    @Mock
    RealmService realmService;
    @Mock
    UserRealm userRealm;
    @Mock
    ClaimManager claimManager;
    @Mock
    TenantManager tenantManager;
    private IdentityLDAPManager identityLDAPManager;

    @BeforeTest
    public void setUp() throws Exception {
        identityLDAPManager = new IdentityLDAPManager();
        String carbonHome = Paths.get(System.getProperty("user.dir"), "target").toString();
        System.setProperty(CarbonBaseConstants.CARBON_HOME, carbonHome);
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setUsername("admin");
        identityLDAPManager = new IdentityLDAPManager();
    }

    @Test
    public void testGetUserManager() throws Exception {
        mockStatic(MultitenantUtils.class);
        when(MultitenantUtils.getTenantDomain(anyString())).thenReturn("tenantDomain");
        when(MultitenantUtils.getTenantAwareUsername(anyString())).thenReturn("tenantLessname");
        mockStatic(LDAPEPComponentHolder.class);
        when(LDAPEPComponentHolder.getRealmService()).thenReturn(realmService);
        when(realmService.getTenantManager()).thenReturn(tenantManager);
        when(tenantManager.getTenantId(anyString())).thenReturn(1);
        when(realmService.getTenantUserRealm(anyInt())).thenReturn(userRealm);
        when(userRealm.getClaimManager()).thenReturn(claimManager);
        String authenticatedUser = PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername();
        identityLDAPManager.getUserManager("admin");
        assertNotNull(authenticatedUser);
        assertEquals(authenticatedUser, "admin", "usernames are not matched");

    }

}
