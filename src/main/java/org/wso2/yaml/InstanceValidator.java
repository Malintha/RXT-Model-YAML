package org.wso2.yaml;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

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

public class InstanceValidator {

    public static void main(String[] args) throws EOFException {
        FileInputStream yamlResource = null;
        try {
            yamlResource = getYamlResource();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(yamlResource == null) {
            System.out.println("####Yaml input stream is null####");
            return;
        }

        Yaml yaml = new Yaml();
        Map<String, Map<String, String>> rxtConfig = yaml.loadAs(yamlResource, Map.class);
        Map<String, String> nameElem = rxtConfig.get("name");
        System.out.println(nameElem.toString());


    }

    private static FileInputStream getYamlResource() throws FileNotFoundException {
        FileInputStream yamlIs = new FileInputStream(new File("/home/malintha/Desktop/MetaC5/RXT-Model-YAML/src/main/resources/type.yml"));
        return yamlIs;
    }

}
