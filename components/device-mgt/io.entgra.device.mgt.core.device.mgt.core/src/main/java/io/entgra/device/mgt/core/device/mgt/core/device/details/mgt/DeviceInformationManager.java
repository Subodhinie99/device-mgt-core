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


package io.entgra.device.mgt.core.device.mgt.core.device.details.mgt;

import io.entgra.device.mgt.core.device.mgt.common.Device;
import io.entgra.device.mgt.core.device.mgt.common.DeviceIdentifier;
import io.entgra.device.mgt.core.device.mgt.common.device.details.DeviceInfo;
import io.entgra.device.mgt.core.device.mgt.common.device.details.DeviceLocation;

import java.util.List;

/**
 * This class will manage the storing of device details related generic information such as cpu/memory utilization, battery level,
 * plugged in to a power source or operation on battery.
 * In CDM framework, we only keep the snapshot of the device information. So previous details are deleted as soon as new
 * data is arrived.
 */

public interface DeviceInformationManager {

    /**
     * This method will manage the storing of the device information as key value pairs.
     * @param deviceInfo - Device info object.
     * @throws DeviceDetailsMgtException
     */
    @Deprecated
    void addDeviceInfo(DeviceIdentifier deviceId, DeviceInfo deviceInfo) throws DeviceDetailsMgtException;

    /**
     * This method will manage the storing of the device information as key value pairs.
     * @param deviceInfo - Device info object.
     * @throws DeviceDetailsMgtException
     */
    void addDeviceInfo(Device device, DeviceInfo deviceInfo) throws DeviceDetailsMgtException;

    /**
     * This method will return the device information.
     * @param deviceIdentifier - Device identifier, device type.
     * @return - Device information object.
     * @throws DeviceDetailsMgtException
     */
    DeviceInfo getDeviceInfo(DeviceIdentifier deviceIdentifier) throws DeviceDetailsMgtException;

    DeviceInfo getDeviceInfo(Device device) throws DeviceDetailsMgtException;

    /**
     * This method will return device information for the supplied devices list.
     * @param deviceIdentifiers
     * @return List of device info objects
     * @throws DeviceDetailsMgtException
     */
    List<DeviceInfo> getDevicesInfo(List<DeviceIdentifier> deviceIdentifiers) throws DeviceDetailsMgtException;

    /**
     * This method will manage storing the device location as latitude, longitude, address, zip, country etc..
     * @param deviceLocation - Device location object.
     * @throws DeviceDetailsMgtException
     */
    @Deprecated
    void addDeviceLocation(DeviceLocation deviceLocation) throws DeviceDetailsMgtException;

    void addDeviceLocation(Device device, DeviceLocation deviceLocation) throws DeviceDetailsMgtException;

    void deleteDeviceLocation(Device device) throws DeviceDetailsMgtException;

    void addDeviceLocations(Device device, List<DeviceLocation> deviceLocations) throws
            DeviceDetailsMgtException;

    /**
     * This method will return the device location with latitude, longitude, address etc..
     * @param deviceIdentifier  - Device identifier, device type.
     * @return Device location object.
     * @throws DeviceDetailsMgtException
     */
    DeviceLocation getDeviceLocation(DeviceIdentifier deviceIdentifier) throws DeviceDetailsMgtException;

    /**
     * This method will return the device location with latitude, longitude, address etc.. of supplied devices.
     * @param deviceIdentifiers - List of Device identifier and device type.
     * @return Device Location list.
     * @throws DeviceDetailsMgtException
     */
    List<DeviceLocation> getDeviceLocations(List<DeviceIdentifier> deviceIdentifiers) throws DeviceDetailsMgtException;

    /**
     * Send events to reporting backend
     * @param deviceId device identifier of the reporting device
     * @param deviceType device type of an device
     * @param payload payload of the event
     * @param eventType Event type being sent
     * @return Http status code if a call is made and if failed to make a call 0
     * @throws DeviceDetailsMgtException
     */
     int publishEvents(String deviceId, String deviceType, String payload, String eventType)
             throws DeviceDetailsMgtException;

//    /**
//     * This method will manage the storing of device application list.
//     * @param deviceApplication - Device application list.
//     * @throws DeviceDetailsMgtException
//     */
//    void addDeviceApplications(DeviceApplication deviceApplication) throws DeviceDetailsMgtException;
//
//    /**
//     * This method will return the application list of the device.
//     * @param deviceIdentifier - Device identifier, device type.
//     * @return - Device application list with device identifier.
//     * @throws DeviceDetailsMgtException
//     */
//    DeviceApplication getDeviceApplication(DeviceIdentifier deviceIdentifier) throws DeviceDetailsMgtException;
}
