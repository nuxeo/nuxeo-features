/*
 * (C) Copyright 2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.ecm.platform.oauth.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.platform.oauth.providers.NuxeoOAuthServiceProvider;
import org.nuxeo.ecm.platform.oauth.providers.OAuthServiceProviderRegistry;
import org.nuxeo.ecm.platform.oauth.providers.OAuthServiceProviderRegistryImpl;
import org.nuxeo.ecm.platform.oauth2.providers.OAuth2ServiceProviderRegistry;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(OAuthFeature.class)
public class TestServiceProviderService  {


   @Inject OAuthServiceProviderRegistry providerRegistry = Framework.getLocalService(OAuthServiceProviderRegistry.class);

   @Inject OAuth2ServiceProviderRegistry providerRegistry2 = Framework.getLocalService(OAuth2ServiceProviderRegistry.class);

    @Test
    public void testServiceLookup2() throws Exception {
        assertNotNull(providerRegistry);
    }

    @Test
    public void testServiceRW() throws Exception {

        NuxeoOAuthServiceProvider p = providerRegistry.addReadOnlyProvider("a",
                null, "b", null, null);
        assertNotNull(p);
        assertNotNull(p.getGadgetUrl());
        assertNotNull(p.getId());

        NuxeoOAuthServiceProvider p2 = providerRegistry.getProvider(
                p.getGadgetUrl(), p.getServiceName());
        assertEquals(p.getId(), p2.getId());

        DirectoryService ds = Framework.getService(DirectoryService.class);
        Session session = ds.open(OAuthServiceProviderRegistryImpl.DIRECTORY_NAME);
        Map<String, Object> init = new HashMap<String, Object>();

        init.put("gadgetUrl", "http://127.0.0.1:8080/nuxeo/gadget1");
        init.put("serviceName", "");
        init.put("consumerKey", "key1");
        init.put("consumerSecret", "secret");
        init.put("publicKey", "pk");
        DocumentModel entry = session.createEntry(init);
        session.updateEntry(entry);

        init.put("gadgetUrl", "http://127.0.0.1:8080/nuxeo/gadget2");
        init.put("serviceName", "");
        init.put("consumerKey", "key2");
        init.put("consumerSecret", "secret");
        init.put("publicKey", "pk");
        entry = session.createEntry(init);
        session.updateEntry(entry);

        init.put("gadgetUrl", "http://127.0.0.1:8080/nuxeo/gadget3");
        init.put("serviceName", "sn1");
        init.put("consumerKey", "key3");
        init.put("consumerSecret", "secret");
        init.put("publicKey", "pk");
        entry = session.createEntry(init);
        session.updateEntry(entry);

        init.put("gadgetUrl", "");
        init.put("serviceName", "sn2");
        init.put("consumerKey", "key4");
        init.put("consumerSecret", "secret");
        init.put("publicKey", "pk");
        entry = session.createEntry(init);
        session.updateEntry(entry);

        session.close();

        assertEquals(5, providerRegistry.listProviders().size());

        NuxeoOAuthServiceProvider p3 = providerRegistry.getProvider(
                "http://127.0.0.1:8080/nuxeo/gadget1", null);
        assertNotNull(p3);
        assertEquals("key1", p3.getConsumerKey());

        p3 = providerRegistry.getProvider(
                "http://localhost:8080/nuxeo/gadget1", null);
        assertNotNull(p3);
        assertEquals("key1", p3.getConsumerKey());

        p3 = providerRegistry.getProvider(
                "http://127.0.0.1:8080/nuxeo/gadget1", "undeclaredservice");
        assertNotNull(p3);
        assertEquals("key1", p3.getConsumerKey());

        p3 = providerRegistry.getProvider(
                "http://127.0.0.1:8080/nuxeo/gadget2", null);
        assertNotNull(p3);
        assertEquals("key2", p3.getConsumerKey());

        p3 = providerRegistry.getProvider(
                "http://127.0.0.1:8080/nuxeo/gadget3", null);
        assertNull(p3);

        p3 = providerRegistry.getProvider(
                "http://127.0.0.1:8080/nuxeo/gadget3", "sn1");
        assertNotNull(p3);
        assertEquals("key3", p3.getConsumerKey());

        p3 = providerRegistry.getProvider(null, "sn1");
        assertNull(p3);

        p3 = providerRegistry.getProvider(null, "sn2");
        assertNotNull(p3);
        assertEquals("key4", p3.getConsumerKey());

        p3 = providerRegistry.getProvider("undeclared gadget", "sn2");
        assertNotNull(p3);
        assertEquals("key4", p3.getConsumerKey());

    }

}
