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

package io.entgra.device.mgt.core.device.mgt.api.jaxrs.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Arrays;
import java.util.List;

@ApiModel(value = "ActivityIdList", description = "List of activity IDs")
public class ActivityIdList {

    @ApiModelProperty(
            name = "operationId",
            value = "operation Id",
            example = "1")
    private String ids;

    @ApiModelProperty(
            name = "activityId",
            value = "Activity identifiers",
            required = true,
            example = "ACTIVITY_1, ACTIVITY_2")
    private List<String> idList;

    public ActivityIdList(String ids) {
        this.ids = ids;
        if (ids != null) {
            String[] splits = ids.split(",");
            if (splits.length > 0 && splits.length < 11 && splits[0] != null && !splits[0].isEmpty()) {
                idList = Arrays.asList(splits);
            }
        }
    }

    public List<String> getIdList() {
        return idList;
    }

    public String getIds() {
        return ids;
    }
}