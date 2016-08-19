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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RXTUtils {

    //return all the yaml files in the resources directory
    public ArrayList<String> retrieveRxtConfigFilePaths() throws IOException {
        ArrayList<String> rxtList = new ArrayList<>();
        Files.walk(Paths.get("/home/malintha/Desktop/MetaC5/RXT-Model-YAML/src/main/resources")).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                rxtList.add(filePath.toString());
            }
        });
        return rxtList;
    }

    //return a map of all yaml maps against name.
    public Map<String, Map<?,?>> getRxtConfigMaps() throws RXTException {
        ArrayList<String> rxtConfigsFilePaths = null;
        try {
            rxtConfigsFilePaths = retrieveRxtConfigFilePaths();
        } catch (IOException e) {
            throw new RXTException("Problem in retrieving rxt file paths", e);
        }
        HashMap<String, Map<?,?>> rxtConfigMaps = new HashMap<>();
        Yaml yaml = new Yaml();
        for(String path : rxtConfigsFilePaths) {
            File rxtConfigFile = new File(path);
            Map<?,?> rxtConfig = null;
            try {
                rxtConfig = yaml.loadAs(new FileInputStream(rxtConfigFile), Map.class);
            } catch (FileNotFoundException e) {
                throw new RXTException("Problem in building yaml document", e);
            }
            String fname = rxtConfigFile.getName();
            rxtConfigMaps.put(fname.substring(0,fname.indexOf(".yml")), rxtConfig);
        }
        return rxtConfigMaps;
    }

}
