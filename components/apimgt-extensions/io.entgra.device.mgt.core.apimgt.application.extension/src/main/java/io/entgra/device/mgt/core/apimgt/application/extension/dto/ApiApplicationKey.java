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

package io.entgra.device.mgt.core.apimgt.application.extension.dto;

import io.entgra.device.mgt.core.apimgt.application.extension.constants.ApiApplicationConstants;
import org.json.simple.JSONObject;

/**
 * This holds api application consumer key and secret.
 */
public class ApiApplicationKey {
    private String consumerKey;
    private String consumerSecret;

    public String getConsumerKey() {
        return this.consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return this.consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String toString() {
        JSONObject obj = new JSONObject();
        obj.put(ApiApplicationConstants.OAUTH_CLIENT_ID, this.getConsumerKey());
        obj.put(ApiApplicationConstants.OAUTH_CLIENT_SECRET, this.getConsumerSecret());
        return obj.toString();
    }
}
