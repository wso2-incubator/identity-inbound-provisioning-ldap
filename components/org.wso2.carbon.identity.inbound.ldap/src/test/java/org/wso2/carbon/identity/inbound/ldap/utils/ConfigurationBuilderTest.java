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

package org.wso2.carbon.identity.inbound.ldap.utils;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.inbound.ldap.impl.EndPointConfiguration;

import java.io.File;

import static org.testng.Assert.assertEquals;

public class ConfigurationBuilderTest {

    private ConfigurationBuilder configurationBuilder;

    @BeforeTest
    public void setUp() throws Exception {
        configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.buildConfiguration(new File("src/test/resources/ldap-endpointconfig.xml"));
        configurationBuilder.buildLDAPEndpointConfigurartion();
    }

    @Test
    public void testBuildLDAPEndpointConfigurartion() throws Exception {

        EndPointConfiguration endPointConfiguration = configurationBuilder.getEndPointConfiguration();
        assertEquals(endPointConfiguration.getEndpointPort(), 10390);
        assertEquals(endPointConfiguration.getInstanceId(), "default");
        assertEquals(endPointConfiguration.getMaxPDUSize(), 2000000);
        assertEquals(endPointConfiguration.isEnable(), true);
        assertEquals(endPointConfiguration.getSaslHostName(), "localhost");
        assertEquals(endPointConfiguration.getSaslPrincipalName(), "ldap/localhost@EXAMPLE.COM");
        assertEquals(endPointConfiguration.isAllowAnonymousAccess(), false);
        assertEquals(endPointConfiguration.isDeNormalizedAttributesEnabled(), false);
        assertEquals(endPointConfiguration.isAccessControlOn(), true);

    }

    @Test
    public void testGetConnectionPassword() throws Exception {
        assertEquals(configurationBuilder.getConnectionPassword(), "admin");
    }

    @Test
    public void testIsEmbeddedLDAPEnabled() throws Exception {
        assertEquals(configurationBuilder.isLdapEndpointEnabled(), true);
    }

}
