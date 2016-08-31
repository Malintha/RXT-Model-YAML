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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class RXTProcessor {

    static RXTUtils rxtUtils = new RXTUtils();

    public static void main(String[] args) {

        try {
            Map<String, Map<Object, Object>> rxtConfigs = rxtUtils.getRxtConfigMaps();
            Map<Object, Object> soapService = rxtConfigs.get("soapservice");

            HashMap<String, Object> soapServiceFields = new HashMap<>();
            rxtUtils.resolveAllFields(soapService, soapServiceFields);
//            System.out.println(soapServiceFields.toString());
            getCmdInputs(soapServiceFields);
        } catch (RXTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getCmdInputs(Map<?, ?> rxtAttrMap) throws IOException {

        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);
        HashMap<String, Object> responseMap = new HashMap<>();
        ArrayList<String> contentElems = new ArrayList<>();
        Iterator it = rxtAttrMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<?, ?> pair = (Map.Entry<?, ?>) it.next();
            contentElems.add((String) pair.getKey());
        }

        for (String field : contentElems) {
            Object fieldElement = rxtAttrMap.get(field);
//            System.out.println(field+"*****"+fieldElement);
            cInput = "";
            Map<?, ?> dataSetMap = null;
            String dataSetStrValue = null;
            String dataSetStrKey = null;
            try {
                dataSetStrValue = (String) fieldElement;
                dataSetStrKey = field;
            } catch (ClassCastException e) {
                dataSetMap = (Map<?, ?>) fieldElement;
            }
            if (dataSetMap != null) {
                Map<?, ?> processMap = null;
//                System.out.println(dataSetMap);
                int lowerBound = 1, upperBound = 1;
                if (dataSetMap.containsKey("occurrences")) {
                    String occ = (String) dataSetMap.get("occurrences").toString();
                    if (occ.matches("([0-9]+\\.{2}\\S+)")) {
                        lowerBound = Integer.parseInt(occ.split("..")[0]);
                        upperBound = Integer.parseInt(occ.split("..")[1]);
                    } else {
                        upperBound = Integer.parseInt(occ);
                    }
                }

                if (dataSetMap.containsKey("composedField")) {
                    dataSetMap.remove("composedField");
                    Iterator innerIt = dataSetMap.entrySet().iterator();
                    System.out.println("\n***Enter " + field + "***");
                    for (int i = 0; i < upperBound; i++) {
                        while (innerIt.hasNext()) {
                            Map.Entry innerPair = (Map.Entry) innerIt.next();
                            Map<?, ?> innerMap = (Map<?, ?>) innerPair.getValue();
                            responseMap = validateAndGetInput(innerMap, responseMap, field, br, true, i);

                        }
                    }
                    System.out.println("\t\t***\n");
                } else {
                    for (int i = lowerBound; i <= upperBound; i++) {
                        processMap = dataSetMap;
                        responseMap = validateAndGetInput(processMap, responseMap, field, br, false, i);
                    }
                }
            } else if (dataSetStrKey != null) {
                System.out.println("Please enter " + dataSetStrKey);
                responseMap.put(field, br.readLine());
            }
        }
        Iterator rit = responseMap.entrySet().iterator();
        System.out.println("###### Asset Details ######\n");
        while (rit.hasNext()) {
            Map.Entry pair = (Map.Entry) rit.next();
            System.out.println(pair.getKey() + " : " + pair.getValue());
        }
    }

    static String cInput = "";

    public static HashMap<String, Object> validateAndGetInput(Map<?, ?> processMap, HashMap<String, Object> responseMap,
            String field, BufferedReader br, boolean composite, int iteration) throws IOException {
        String label = (String) processMap.get("label");
        boolean isRequired = false;
        ArrayList<String> data = null;
        String validateRegex = null;
        String validateMessage = null;

        String type = (String) processMap.get("type");
        if (processMap.get("required") != null) {
            isRequired = (Boolean) processMap.get("required");
        }
        if (type != null)
            if (type.contains("select")) {
                data = (ArrayList<String>) processMap.get("values");
            }
        if (processMap.get("regex") != null) {
            validateRegex = (String) processMap.get("regex");
            validateMessage = (String) processMap.get("validationMessage");
        }
        System.out.println("Please enter " + label);
        if (data != null) {
            for (String s : data)
                System.out.println(" * "+s);
        }
        String input = null;
        if (isRequired) {
            while ((input = br.readLine()).length() == 0) {
                System.out.println("Please enter " + label);
            }
        } else {
            input = br.readLine();
        }
        if (validateRegex != null) {
            if (!input.matches(validateRegex)) {
                System.out.println(validateMessage);
                while (!(input = br.readLine()).matches(validateRegex)) {
                    System.out.println(validateMessage);
                }
            }
        }
        if(composite) {
            cInput += input+"\t";
        }
        else
        cInput = input;
        if(iteration>1) {
            responseMap.put(field + " [" + iteration+"] ", cInput);
        }
        else {
            responseMap.put(field, cInput);
        }
        return responseMap;
    }
}
