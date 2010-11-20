/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
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
 * ====================================================================
 */

package org.jclouds.elastichosts;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.jclouds.rest.RestContextFactory.createContext;
import static org.testng.Assert.assertNotNull;

import java.util.Properties;
import java.util.Set;

import org.jclouds.Constants;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code ElasticHostsClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "elastichosts.ElasticHostsClientLiveTest")
public class ElasticHostsClientLiveTest {

   private ElasticHostsClient connection;
   private RestContext<ElasticHostsClient, ElasticHostsAsyncClient> context;

   protected String provider = "elastichosts";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = System.getProperty("test." + provider + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      if (credential != null)
         overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      context = createContext(
            contextSpec(provider, endpoint, "1.0", identity, credential, ElasticHostsClient.class,
                  ElasticHostsAsyncClient.class, (Class) ElasticHostsPropertiesBuilder.class,
                  (Class) ElasticHostsContextBuilder.class, ImmutableSet.<Module> of(new Log4JLoggingModule())),
            overrides);

      connection = context.getApi();
   }

   @AfterGroups(groups = "live")
   void tearDown() {
      if (context != null)
         context.close();
   }

   @Test
   public void testListDrives() throws Exception {
      Set<String> drives = connection.listDrives();
      assertNotNull(drives);
   }

   @Test
   public void testGetDrive() throws Exception {
      Set<String> drives = connection.listDrives();
      for (String driveUUID : drives) {
         assertNotNull(connection.getDriveInfo(driveUUID));
      }
   }
}