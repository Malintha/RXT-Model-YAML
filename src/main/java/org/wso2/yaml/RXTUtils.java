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

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class RXTUtils {

    final static String rxtDirPath = "/home/malintha/Desktop/MetaC5/RXT-Model-YAML/src/main/resources";
    final static String yamlExtension = ".yml";

    private Map<String, Map<Object, Object>> rxtConfigs;

    //return all the yaml files in the resources directory
    public ArrayList<String> retrieveRxtConfigFilePaths() throws IOException {
        ArrayList<String> rxtList = new ArrayList<>();
        Files.walk(Paths.get(rxtDirPath)).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                rxtList.add(filePath.toString());
            }
        });
        return rxtList;
    }

    //return a map of all yaml maps against name.
    public Map<String, Map<Object, Object>> getRxtConfigMaps() throws RXTException {
        ArrayList<String> rxtConfigsFilePaths = null;
        try {
            rxtConfigsFilePaths = retrieveRxtConfigFilePaths();
        } catch (IOException e) {
            throw new RXTException("Problem in retrieving rxt file paths", e);
        }
        HashMap<String, Map<Object, Object>> rxtConfigMaps = new HashMap<>();
        Yaml yaml = new Yaml();
        for (String path : rxtConfigsFilePaths) {
            File rxtConfigFile = new File(path);
            Map<Object, Object> rxtConfig = null;
            try {
                rxtConfig = yaml.loadAs(new FileInputStream(rxtConfigFile), Map.class);
            } catch (FileNotFoundException e) {
                throw new RXTException("Problem in building yaml document", e);
            }
            String fname = rxtConfigFile.getName();
            rxtConfigMaps.put(fname.substring(0, fname.indexOf(yamlExtension)), rxtConfig);
        }
        this.rxtConfigs = rxtConfigMaps;
        return rxtConfigMaps;
    }

    private Map<Object, Object> getRxt(String rxtName) {
        return this.rxtConfigs.get(rxtName);
    }

    public boolean isConcrete(Map<?, ?> rxtConfigMap) {
        return (Boolean.parseBoolean(getMetadata(rxtConfigMap, "isConcrete").toString()));
    }

    public HashMap<String, Object> resolveAllFields(Map<?, ?> rxtConfig, HashMap<String, Object> fields) {
        if (rxtConfig == null)
            return fields;
        ArrayList<String> parents = getParentRxtNames(rxtConfig);
        if (parents != null) {
            for (String parent : parents) {
                Map<?, ?> parentMap = getRxt(parent);
                if (parentMap != null)
                    resolveAllFields(parentMap, fields);
                HashMap<String, Object> tempFields = getAllInheritingFields(rxtConfig);
                Iterator it = tempFields.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Map<?, ?>> pair = (Map.Entry<String, Map<?, ?>>) it.next();
                    fields.put(pair.getKey(), pair.getValue());
                }
                resolveAllFields(parentMap, fields);
            }
        } else {
            HashMap<String, Object> tempFields = getAllInheritingFields(rxtConfig);
            Iterator it = tempFields.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Map<?, ?>> pair = (Map.Entry<String, Map<?, ?>>) it.next();
                fields.put(pair.getKey(), pair.getValue());
            }
        }
        return fields;
    }

    public HashMap<String, Object> getAllInheritingFields(Map<?, ?> rxtConfig) {
        Map<?, ?> contentMap = getContentMap(rxtConfig);
        HashMap<String, Object> fields = new HashMap<>();
        Iterator it = contentMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> pair = (Map.Entry) it.next();
            if (pair.getValue() == null) {
                fields.put(pair.getKey(), "");
            } else {
                Map<?, ?> fieldAttributes = (Map<?, ?>) pair.getValue();
                Object inheritsOM = fieldAttributes.get("inherits");
                boolean inherit = true;
                if (inheritsOM != null) {
                    inherit = (Boolean) inheritsOM;
                }
                if (inherit) {
                    Map<Object, Object> composedField = null;
                    if (fieldAttributes.containsKey("importField")) {
                        composedField = (Map<Object, Object>) resolveComposedField((String) fieldAttributes.get("importField"));
                        composedField.put("composedField","true");
                        fields.put(pair.getKey(), composedField);
//                            String occ = (String) fieldAttributes.get("occurrences").toString();
                    } else {
                        fields.put(pair.getKey(), fieldAttributes);
                    }
                }
            }
        }
        return fields;
    }

    public int[] getOccurrences(String occ) {
        int[] occurrences = new int[2];
        occurrences[0] = Integer.parseInt(occ.split("..")[0]);
        occurrences[1] = Integer.parseInt(occ.split("..")[1]);
        return occurrences;
    }

    public Map<?, ?> resolveComposedField(String fieldName) {
        Map<?, ?> remoteField = (Map<?, ?>) rxtConfigs.get(fieldName).get(fieldName);
        return (Map<?, ?>) remoteField.get("content");
    }

    //read the extends attribute
    public ArrayList<String> getParentRxtNames(Map<?, ?> rxtConfigMap) {
        ArrayList<String> extendsMap = (ArrayList<String>) getMetadata(rxtConfigMap, "extends");
        return extendsMap;
    }

    public boolean isExtended(Map<?, ?> rxtConfig) {
        ArrayList<String> parents = getParentRxtNames(rxtConfig);
        return parents != null;
    }

    public Object getMetadata(Map<?, ?> rxtConfigMap, String attribute) {
        Map<?, ?> attrMap = getAttributeMap(rxtConfigMap);
        return attrMap.get(attribute);
    }

    public Map<?, ?> getContentMap(Map<?, ?> rxtConfigMap) {
        Map<?, ?> attrMap = getAttributeMap(rxtConfigMap);
        Map<?, ?> rxtContent = (Map<?, ?>) attrMap.get("content");
        return rxtContent;
    }

    public String getRxtName(Map<?, ?> rxtConfigMap) {
        Iterator it = rxtConfigMap.entrySet().iterator();
        Map.Entry pair = (Map.Entry) it.next();
        return (String) pair.getKey();
    }

    public Map<?, ?> getAttributeMap(Map<?, ?> rxtConfigMap) {
        Iterator it = rxtConfigMap.entrySet().iterator();
        Map.Entry pair = (Map.Entry) it.next();
        Map<?, ?> rxtAttributes = (Map<?, ?>) pair.getValue();
        return rxtAttributes;
    }

    public Object getFieldAttribute(Map<?, ?> contentMap, String attribute) {
        return contentMap.get((Object) attribute);
    }

}
