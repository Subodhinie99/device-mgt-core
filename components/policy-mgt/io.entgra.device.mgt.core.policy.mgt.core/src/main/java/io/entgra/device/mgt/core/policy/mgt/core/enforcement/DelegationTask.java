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

package io.entgra.device.mgt.core.policy.mgt.core.enforcement;

import io.entgra.device.mgt.core.device.mgt.common.Device;
import io.entgra.device.mgt.core.device.mgt.common.EnrolmentInfo;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceManagementException;
import io.entgra.device.mgt.core.device.mgt.core.config.DeviceConfigurationManager;
import io.entgra.device.mgt.core.device.mgt.core.config.policy.PolicyConfiguration;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;
import io.entgra.device.mgt.core.device.mgt.core.task.impl.DynamicPartitionedScheduleTask;
import io.entgra.device.mgt.core.policy.mgt.common.PolicyManagementException;
import io.entgra.device.mgt.core.policy.mgt.core.cache.impl.PolicyCacheManagerImpl;
import io.entgra.device.mgt.core.policy.mgt.core.internal.PolicyManagementDataHolder;
import io.entgra.device.mgt.core.policy.mgt.core.mgt.PolicyManager;
import io.entgra.device.mgt.core.policy.mgt.core.mgt.bean.UpdatedPolicyDeviceListBean;
import io.entgra.device.mgt.core.policy.mgt.core.mgt.impl.PolicyManagerImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class DelegationTask extends DynamicPartitionedScheduleTask {

    private static final Log log = LogFactory.getLog(DelegationTask.class);
    private final PolicyConfiguration policyConfiguration = DeviceConfigurationManager.getInstance()
            .getDeviceManagementConfig().getPolicyConfiguration();

    @Override
    public void executeDynamicTask() {
        try {
            PolicyManager policyManager = new PolicyManagerImpl();
            UpdatedPolicyDeviceListBean updatedPolicyDeviceList = policyManager.applyChangesMadeToPolicies();
            List<String> deviceTypes = updatedPolicyDeviceList.getChangedDeviceTypes();
            if (policyConfiguration.getCacheEnable()) {
                PolicyCacheManagerImpl.getInstance().rePopulateCache();
            }
            if (log.isDebugEnabled()) {
                log.debug("Number of device types which policies are changed .......... : " + deviceTypes.size());
            }
            if (!deviceTypes.isEmpty()) {
                DeviceManagementProviderService service = PolicyManagementDataHolder.getInstance().
                        getDeviceManagementService();
                List<Device> devices;
                List<Device> toBeNotified;
                for (String deviceType : deviceTypes) {
                    try {
                        devices = new ArrayList<>();
                        toBeNotified = new ArrayList<>();
                        if(getTaskContext() != null && getTaskContext().isPartitioningEnabled()){
                            devices.addAll(service.getAllocatedDevices(deviceType,
                                                                       getTaskContext().getActiveServerCount(),
                                                                       getTaskContext().getServerHashIndex()));
                        } else {
                            devices.addAll(service.getAllDevices(deviceType, false));
                        }
                        for (Device device : devices) {
                            if (device != null && device.getEnrolmentInfo() != null
                                && device.getEnrolmentInfo().getStatus() != EnrolmentInfo.Status.REMOVED) {
                                toBeNotified.add(device);
                                if (log.isDebugEnabled()) {
                                    log.debug("Adding policy operation to device : " + device.getDeviceIdentifier());
                                }
                            }
                        }
                        if (!toBeNotified.isEmpty()) {
                            PolicyEnforcementDelegator enforcementDelegator = new PolicyEnforcementDelegatorImpl(
                                    toBeNotified, updatedPolicyDeviceList.getUpdatedPolicyIds());
                            enforcementDelegator.delegate();
                        }
                    } catch (DeviceManagementException e) {
                        throw new PolicyManagementException("Error occurred while fetching the devices", e);
                    } catch (PolicyDelegationException e) {
                        throw new PolicyManagementException("Error occurred while running the delegation task on " +
                                                            "device-type : " + deviceType, e);
                    }
                }
            }
        } catch (PolicyManagementException e) {
            log.error("Error occurred while getting the policies applied to devices.", e);
        }
    }

    @Override
    protected void setup() {

    }
}
