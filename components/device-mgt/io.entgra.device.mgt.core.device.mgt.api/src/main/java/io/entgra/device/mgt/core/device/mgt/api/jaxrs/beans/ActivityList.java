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
package io.entgra.device.mgt.core.device.mgt.api.jaxrs.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import io.entgra.device.mgt.core.device.mgt.common.operation.mgt.Activity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "ListOfActivities", description = "This contains a set of activities that " +
        "matches a given"
        + " criteria as a collection")
public class ActivityList extends BasePaginatedResult {

    private List<Activity> activities;

    @ApiModelProperty(value = "List of activity Ids")
    @JsonProperty("activities")
    public List<Activity> getList() {
        return activities;
    }

    public void setList(List<Activity> activities) {
        this.activities = activities;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
