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

package io.entgra.device.mgt.core.device.mgt.core.config.metadata.mgt;

import io.entgra.device.mgt.core.device.mgt.core.config.metadata.mgt.whitelabel.WhiteLabelConfiguration;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "MetaDataConfiguration")
public class MetaDataConfiguration {
    private WhiteLabelConfiguration whiteLabelConfiguration;

    @XmlElement(name = "WhiteLabelConfiguration", required = true)
    public WhiteLabelConfiguration getWhiteLabelConfiguration() {
        return whiteLabelConfiguration;
    }

    public void setWhiteLabelConfiguration(WhiteLabelConfiguration whiteLabelConfiguration) {
        this.whiteLabelConfiguration = whiteLabelConfiguration;
    }
}
