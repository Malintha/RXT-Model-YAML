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

import java.util.ArrayList;
import java.util.Map;

public class Rxt {

    private Map<?,?> metaData;
    private Map<?,?> content;

    private ArrayList<Rxt> parents;
    public boolean isConcrete;
    public Map<?, ?> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<?, ?> metaData) {
        this.metaData = metaData;
    }

    public Map<?, ?> getContent() {
        return content;
    }

    public void setContent(Map<?, ?> content) {
        this.content = content;
    }

    public void addParent(Rxt parent) {
        parents.add(parent);
    }

    public ArrayList<Rxt> getParents() {
        return this.parents;
    }

}
