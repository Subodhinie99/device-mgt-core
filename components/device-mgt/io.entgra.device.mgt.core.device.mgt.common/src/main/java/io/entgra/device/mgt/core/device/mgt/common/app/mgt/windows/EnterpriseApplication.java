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


package io.entgra.device.mgt.core.device.mgt.common.app.mgt.windows;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * This class represents the Windows Enterprise App Types information.
 */
public class EnterpriseApplication implements Serializable {

    private HostedAppxApplication hostedAppxApplication;
    private HostedMSIApplication hostedMSIApplication;

    public HostedAppxApplication getHostedAppxApplication() {
        return hostedAppxApplication;
    }

    public void setHostedAppxApplication(HostedAppxApplication hostedAppxApplication) {
        this.hostedAppxApplication = hostedAppxApplication;
    }

    public HostedMSIApplication getHostedMSIApplication() {
        return hostedMSIApplication;
    }

    public void setHostedMSIApplication(HostedMSIApplication hostedMSIApplication) {
        this.hostedMSIApplication = hostedMSIApplication;
    }

    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
