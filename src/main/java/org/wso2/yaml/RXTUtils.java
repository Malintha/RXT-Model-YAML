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
        return rxtConfigMaps;
    }

    //read the extends attribute
    public String getParentRxtName(Map<?,?> rxtConfigMap) {
        Map<?,?> metaDataMap = getMetadataMap(rxtConfigMap);
        return metaDataMap.get("extends").toString();
    }

    public boolean isConcrete(Map<?,?> rxtConfigMap) {
        Map<?,?> metaDataMap = getMetadataMap(rxtConfigMap);
        return (Boolean.parseBoolean(metaDataMap.get("concrete").toString()));
    }

    //create composite property bag
    public Map<?,?> getCompositeChildRXT(Map<?,?> parentRxt, Map<Object, Object> childRxt) {
        Map<?,?> parentContent = getContentMap(parentRxt);
        Map<Object, Object> childContent = (Map<Object, Object>) getContentMap(childRxt);
        Map<Object,Object> childMetadataMap = (Map<Object, Object>) getMetadataMap(childRxt);
        Iterator it = parentContent.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            childContent.put(pair.getKey(), pair.getValue());
            it.remove();
        }
//        Map<Object, Object> tempMetaDataMap = childMetadataMap;
        HashMap<Object, Object> tempMetaDataMap = new HashMap<>();
        tempMetaDataMap.putAll(childMetadataMap);
        childMetadataMap.clear();
        Set<Map.Entry<Object, Object>> childMetaDataEntrySet = tempMetaDataMap.entrySet();
        childMetadataMap.put("metadata", childMetaDataEntrySet);

//        childMetadataMap.put("metadata", tempMetaDataMap.entrySet());
        childMetadataMap.put("content",childContent);
        return childMetadataMap;
    }


    public Map<?,?> getMetadataMap(Map<?,?> rxtConfigMap) {
        return (Map<?, ?>) rxtConfigMap.get("metadata");
    }

    public Map<?,?> getContentMap(Map<?,?> rxtConfigMap) {
        return (Map<?, ?>) rxtConfigMap.get("content");
    }

}
