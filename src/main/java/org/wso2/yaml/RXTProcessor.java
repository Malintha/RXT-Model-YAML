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

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class RXTProcessor {

    static RXTUtils rxtUtils = new RXTUtils();

    public static void main(String[] args) {

//        RXTUtils rxtUtils = new RXTUtils();
        try {
            Map<String, Map<Object,Object>> rxtConfigs = rxtUtils.getRxtConfigMaps();

            //hard coding the rxt names
            Map<Object,Object> soapService = rxtConfigs.get("soapservice");

            String parentRxtName = rxtUtils.getParentRxtName(soapService);
            Map<?,?> parentRxt = rxtConfigs.get(parentRxtName);
            Map<?,?> compositeRxt = rxtUtils.getCompositeChildRXT(parentRxt, soapService);
//            System.out.println(compositeRxt.toString());
            getCmdInputs(compositeRxt);
        } catch (RXTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("#####Create new SOAP service######\n\n");

    }

    public static void getCmdInputs(Map<?,?> rxtAttrMap) throws IOException {

        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);
        Map<?,?> contentMap = rxtUtils.getContentMap(rxtAttrMap);
        HashMap<String, String> responseMap = new HashMap<>();

        ArrayList<String> contentElems = new ArrayList<>();
        Iterator it = contentMap.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<?,?> pair = (Map.Entry<?, ?>) it.next();
            contentElems.add((String)pair.getKey());
        }

        for(String field : contentElems) {
            Object fieldElement = rxtUtils.getFieldAttribute(contentMap, field);
            LinkedHashMap<?,?> dataSetMap = null;
            String dataSetStrValue = null;
            String dataSetStrKey = null;
            try {
                dataSetStrValue = (String) fieldElement;
                dataSetStrKey = field;
            }
            catch (ClassCastException e) {
                dataSetMap = (LinkedHashMap)fieldElement;
            }
//            LinkedHashMap<?,?> dataSet = rxtUtils.getFieldAttribute(contentMap, field);
            if(dataSetMap != null) {
                String label = (String) dataSetMap.get("Label");
                boolean isRequired;
                ArrayList<String> data;
                String validateRegex;
                String validateMessage;

                String type = (String) dataSetMap.get("type");
                if (dataSetMap.get("required") != null) {
                    isRequired = (Boolean) dataSetMap.get("required");
                }
                if (type.contains("option")) {
                    data = (ArrayList<String>) dataSetMap.get("data");
                }
                if (dataSetMap.get("validate") != null) {
                    validateRegex = (String) dataSetMap.get("validate");
                    validateMessage = (String) dataSetMap.get("validateMessage");
                }
                System.out.println("Please enter " + label);
            }
            else if (dataSetStrKey != null){
                System.out.println("Please enter "+dataSetStrKey);
            }
        }

        //input contacts

    }
}
