/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.yaml;

import java.util.Map;

public class RXTProcessor {

    public static void main(String[] args) {

        RXTUtils rxtUtils = new RXTUtils();
        try {
            Map<String, Map<Object,Object>> rxtConfigs = rxtUtils.getRxtConfigMaps();

            //hard coding the rxt names
            Map<Object,Object> soapService = rxtConfigs.get("soapservice");

            String parentRxtName = rxtUtils.getParentRxtName(soapService);
            Map<?,?> parentRxt = rxtConfigs.get(parentRxtName);
            Map<?,?> compositeRxt = rxtUtils.getCompositeChildRXT(parentRxt, soapService);
            System.out.println(compositeRxt.toString());
        } catch (RXTException e) {
            e.printStackTrace();
        }

    }
}
