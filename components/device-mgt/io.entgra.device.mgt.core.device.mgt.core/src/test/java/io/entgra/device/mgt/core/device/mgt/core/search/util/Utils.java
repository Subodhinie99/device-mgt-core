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
package io.entgra.device.mgt.core.device.mgt.core.search.util;

import io.entgra.device.mgt.core.device.mgt.common.DeviceIdentifier;
import io.entgra.device.mgt.core.device.mgt.common.device.details.DeviceInfo;
import io.entgra.device.mgt.core.device.mgt.common.device.details.DeviceLocation;
import io.entgra.device.mgt.core.device.mgt.core.common.TestDataHolder;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static DeviceInfo getDeviceInfo() {
        DeviceInfo deviceInfo = new DeviceInfo();

        deviceInfo.setSsid("FAFDA");
        deviceInfo.setAvailableRAMMemory(1.24);
        deviceInfo.setBatteryLevel(40.0);
        deviceInfo.setConnectionType("GSM");
        deviceInfo.setCpuUsage(82.34);
        deviceInfo.setDeviceModel("SM-T520");
        deviceInfo.setExternalAvailableMemory(2.45);
        deviceInfo.setExternalTotalMemory(16.23);
        deviceInfo.setInternalAvailableMemory(3.56);
        deviceInfo.setInternalTotalMemory(7.89);
        deviceInfo.setMobileSignalStrength(0.67);
        deviceInfo.setOperator("Dialog");
        deviceInfo.setOsVersion("Lolipop");
        deviceInfo.setOsBuildDate("1467366458");
        deviceInfo.setPluggedIn(true);
        deviceInfo.setSsid("SSSSSS");
        deviceInfo.setTotalRAMMemory(4.00);
        deviceInfo.setVendor("SAMSUNG");
        deviceInfo.setLocation(getSampleDeviceLocation());

        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put("BATTERY_VOLTAGE", "40");
        propertyMap.put("BATTERY_HEALTH", "Good");
        propertyMap.put("BATTERY_STATUS", "SWElLED");
        propertyMap.put("LOW_MEMORY", "false");
        propertyMap.put("MEMORY_THRESHOLD", "100663296");
        propertyMap.put("CPU_IOW", "12");
        propertyMap.put("CPU_IRQ", "1");
        propertyMap.put("IMEI", "e6f236ac82537a8e");
        propertyMap.put("IMSI", "432659632123654845");
        deviceInfo.setDeviceDetailsMap(propertyMap);
        return deviceInfo;
    }

    public static DeviceLocation getSampleDeviceLocation(){
        DeviceLocation deviceLocation = new DeviceLocation();
        deviceLocation.setDeviceIdentifier(Utils.getDeviceIdentifier());
        deviceLocation.setLatitude(76.2422);
        deviceLocation.setLongitude(81.43);
        deviceLocation.setStreet1("4");
        deviceLocation.setStreet2("Isuru Uyana");
        deviceLocation.setCity("Karanadeniya");
        deviceLocation.setState("Karandeniya");
        deviceLocation.setZip("80360");
        deviceLocation.setCountry("Sri Lanka");
        deviceLocation.setDeviceId(1);
        return deviceLocation;
    }

    public static DeviceIdentifier getDeviceIdentifier(){
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setType(TestDataHolder.TEST_DEVICE_TYPE);
        deviceIdentifier.setId("12345");
        return deviceIdentifier;
    }
}
