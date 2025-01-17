/*
 * Copyright (c) 2018 - 2023, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.entgra.device.mgt.core.device.mgt.core.permission.mgt;

import io.entgra.device.mgt.core.device.mgt.common.permission.mgt.Permission;

import java.util.*;

/**
 * This class represents the node of a permission tree.
 * It holds the current path name, list of permissions associated with URL
 * and the set of children.
 */
public class PermissionNode {

    private String pathName;
    private Map<String, Permission> permissions = new HashMap<String, Permission>();
    private List<PermissionNode> children = new ArrayList<PermissionNode>();

    public PermissionNode(String pathName) {
        this.pathName = pathName;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public List<PermissionNode> getChildren() {
        return children;
    }

    public PermissionNode getChild(String pathName) {
        PermissionNode child = null;
        for (PermissionNode node : children) {
            if (node.getPathName().equals(pathName)) {
                return node;
            }
        }
        return child;
    }

    public void addChild(PermissionNode node) {
        children.add(node);
    }

    public void addPermission(String httpMethod, Permission permission) {
        permissions.put(httpMethod, permission);
    }

    public Permission getPermission(String httpMethod) {
        return permissions.get(httpMethod);
    }

    public Collection<Permission> getPermissions() {
        return permissions.values();
    }
}
