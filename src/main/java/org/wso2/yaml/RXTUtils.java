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

    private Map<String, Map<Object,Object>> rxtConfigs;

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
    public Map<String, Map<Object,Object>> getRxtConfigMaps() throws RXTException {
        ArrayList<String> rxtConfigsFilePaths = null;
        try {
            rxtConfigsFilePaths = retrieveRxtConfigFilePaths();
        } catch (IOException e) {
            throw new RXTException("Problem in retrieving rxt file paths", e);
        }
        HashMap<String, Map<Object,Object>> rxtConfigMaps = new HashMap<>();
        Yaml yaml = new Yaml();
        for(String path : rxtConfigsFilePaths) {
            File rxtConfigFile = new File(path);
            Map<Object,Object> rxtConfig = null;
            try {
                rxtConfig = yaml.loadAs(new FileInputStream(rxtConfigFile), Map.class);
            } catch (FileNotFoundException e) {
                throw new RXTException("Problem in building yaml document", e);
            }
            String fname = rxtConfigFile.getName();
            rxtConfigMaps.put(fname.substring(0,fname.indexOf(yamlExtension)), rxtConfig);
        }
        this.rxtConfigs = rxtConfigMaps;
        return rxtConfigMaps;
    }

    private Map<Object, Object> getRxt(String rxtName) {
        return this.rxtConfigs.get(rxtName);
    }

    public boolean isConcrete(Map<?,?> rxtConfigMap) {
        return (Boolean.parseBoolean(getMetadata(rxtConfigMap, "isConcrete").toString()));
    }


    public HashMap<String, Object> resolveAllFields(Map<?,?> rxtConfig, HashMap<String, Object> fields) {
        if (rxtConfig == null)
            return fields;
        ArrayList<String> parents = getParentRxtNames(rxtConfig);
//        for (String parent : parents) {
            Map<?, ?> parentMap = null;
            if (parents != null)
                parentMap = getRxt(parents.get(0));

            if (parentMap != null)
//                if (!isExtended(parentMap)) {
                    resolveAllFields(parentMap, fields);
//                }
            Map<?, ?> currentRxt = parentMap;
            if (currentRxt == null)
                currentRxt = rxtConfig;
            HashMap<String, Object> tempFields = getAllInheritingFields(rxtConfig);
            Iterator it = tempFields.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Map<?, ?>> pair = (Map.Entry<String, Map<?, ?>>) it.next();
                fields.put(pair.getKey(), pair.getValue());
            }
            return resolveAllFields(parentMap, fields);
            //        }
//            return fields;
//        }
//        return fields;
    }


    public HashMap<String, Object> getAllInheritingFields(Map<?,?> rxtConfig) {
        Map<?,?> contentMap = getContentMap(rxtConfig);
        HashMap<String, Object> fields = new HashMap<>();
        Iterator it = contentMap.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, Object> pair = (Map.Entry) it.next();
            if(pair.getValue() == null) {
                fields.put(pair.getKey(), "");
            }
            else{
                Map<?,?> fieldAttributes = (Map<?, ?>) pair.getValue();
                Object inheritFalseOM = fieldAttributes.get("inherits");
                boolean inheritFalse = false;
                if(inheritFalseOM != null) {
                    inheritFalse = (Boolean) inheritFalseOM;
                }
                if(!inheritFalse) {
                    Map<?, ?> composedField = null;
                    if (fieldAttributes.containsKey("importField")) {
//                        composedField = resolveComposedField((String) fieldAttributes.get("importField"));
                        fields.put(pair.getKey(), "composedField");
                    }
                    else {
                        fields.put(pair.getKey(), fieldAttributes);
                    }
                }
            }
        }
        return fields;
    }



    //read the extends attribute
    public ArrayList<String> getParentRxtNames(Map<?,?> rxtConfigMap) {
        ArrayList<String> extendsMap = (ArrayList<String>) getMetadata(rxtConfigMap, "extends");
        return extendsMap;
    }

    public boolean isExtended(Map<?,?> rxtConfig) {
        ArrayList<String> parents = getParentRxtNames(rxtConfig);
        return parents != null;
    }

    //create composite property bag
//    public Map<?,?> getCompositeChildRXT(Map<?,?> parentRxt, Map<Object, Object> childRxt) {
//        Map<?,?> parentContent = getContentMap(parentRxt);
//        Map<Object, Object> childContent = (Map<Object, Object>) getContentMap(childRxt);
//        Map<Object,Object> childMetadataMap = (Map<Object, Object>) getMetadata(childRxt);
//        Iterator it = parentContent.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            Map<?,?> attrMap = (Map<?, ?>) pair.getValue();
//            Object inherits;
//            boolean boolInherits;
//            if(attrMap != null) {
//                inherits = attrMap.get("inherits");
//                if(inherits == null) {
//                    boolInherits = true;
//                }
//                else {
//                    boolInherits = (Boolean) inherits;
//                }
//                    if(boolInherits) {
//                        childContent.put(pair.getKey(), pair.getValue());
//                        it.remove();
//                    }
//
//            }
//            else {
//                childContent.put(pair.getKey(), pair.getValue());
//                it.remove();
//            }
//        }
//        HashMap<Object, Object> tempMetaDataMap = new HashMap<>();
//        tempMetaDataMap.putAll(childMetadataMap);
//        childMetadataMap.clear();
//        Set<Map.Entry<Object, Object>> childMetaDataEntrySet = tempMetaDataMap.entrySet();
//        childMetadataMap.put("metadata", childMetaDataEntrySet);
//
//        childMetadataMap.put("content", childContent);
//        return childMetadataMap;
//    }

    public Object getMetadata(Map<?,?> rxtConfigMap, String attribute) {
        Map<?,?> attrMap = getAttributeMap(rxtConfigMap);
        return attrMap.get(attribute);
    }

    public Map<?,?> getContentMap(Map<?,?> rxtConfigMap) {
        Map<?,?> attrMap = getAttributeMap(rxtConfigMap);
        Map<?,?> rxtContent = (Map<?,?>) attrMap.get("content");
        return rxtContent;
    }

    public String getRxtName(Map<?,?> rxtConfigMap) {
        Iterator it = rxtConfigMap.entrySet().iterator();
        Map.Entry pair = (Map.Entry) it.next();
        return (String) pair.getKey();
    }

    public Map<?,?> getAttributeMap(Map<?,?> rxtConfigMap) {
        Iterator it = rxtConfigMap.entrySet().iterator();
        Map.Entry pair = (Map.Entry) it.next();
        Map<?,?> rxtAttributes = (Map<?, ?>) pair.getValue();
        return rxtAttributes;
    }

    public Object getFieldAttribute(Map<?,?> contentMap, String attribute) {
        return contentMap.get((Object)attribute);
    }


}
