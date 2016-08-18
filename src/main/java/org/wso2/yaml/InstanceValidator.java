package org.wso2.yaml;

import org.ho.yaml.YamlDecoder;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by malintha on 8/18/16.
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
            System.out.println("####Yaml Input stream is null####");
            return;
        }

        YamlDecoder yamlDecoder = new YamlDecoder(yamlResource);
        Object obj = yamlDecoder.readObject();
        System.out.println(obj);


    }

    private static FileInputStream getYamlResource() throws FileNotFoundException {
        FileInputStream yamlIs = new FileInputStream(new File("/home/malintha/Desktop/MetaC5/RXT-Model-YAML/src/main/resources/soapservice.yml"));
        return yamlIs;
    }

}
