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

package org.jclouds.aws.ec2.xml;

import javax.inject.Inject;

import org.jclouds.aws.Region;
import org.jclouds.aws.ec2.domain.PlacementGroup;
import org.jclouds.aws.ec2.domain.PlacementGroup.State;
import org.jclouds.aws.ec2.util.EC2Utils;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;

/**
 * 
 * @author Adrian Cole
 */
public class PlacementGroupHandler extends
         ParseSax.HandlerForGeneratedRequestWithResult<PlacementGroup> {
   private StringBuilder currentText = new StringBuilder();

   @Inject
   protected DateService dateService;
   @Inject
   @Region
   String defaultRegion;

   private String name;
   private String strategy = "cluster";
   private State state;

   public PlacementGroup getResult() {
      String region = EC2Utils.findRegionInArgsOrNull(getRequest());
      if (region == null)
         region = defaultRegion;
      PlacementGroup returnVal = new PlacementGroup(region, name, strategy, state);
      this.name = null;
      this.strategy = "cluster";
      this.state = null;
      return returnVal;
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("groupName")) {
         this.name = currentText.toString().trim();
      } else if (qName.equals("strategy")) {
         strategy = currentText.toString().trim();
      } else if (qName.equals("state")) {
         state = PlacementGroup.State.fromValue(currentText.toString().trim());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
