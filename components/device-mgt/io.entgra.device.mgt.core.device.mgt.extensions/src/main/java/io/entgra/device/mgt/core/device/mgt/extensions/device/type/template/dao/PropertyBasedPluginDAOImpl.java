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

package io.entgra.device.mgt.core.device.mgt.extensions.device.type.template.dao;

import io.entgra.device.mgt.core.device.mgt.common.Device;
import io.entgra.device.mgt.core.device.mgt.extensions.device.type.template.config.DeviceDetails;
import io.entgra.device.mgt.core.device.mgt.extensions.device.type.template.exception.DeviceTypeMgtPluginException;
import io.entgra.device.mgt.core.device.mgt.extensions.device.type.template.util.DeviceTypeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements CRUD for Devices. This holds the generic implementation. An instance of this will be created for
 * each device type.
 */
public class PropertyBasedPluginDAOImpl implements PluginDAO {

    private static final Log log = LogFactory.getLog(PropertyBasedPluginDAOImpl.class);
    private DeviceTypeDAOHandler deviceTypeDAOHandler;
    private List<String> deviceProps;
    private String deviceType;
    private static final String PROPERTY_KEY_COLUMN_NAME = "PROPERTY_NAME";
    private static final String PROPERTY_VALUE_COLUMN_NAME = "PROPERTY_VALUE";

    public PropertyBasedPluginDAOImpl(DeviceDetails deviceDetails,
                                      DeviceTypeDAOHandler deviceTypeDAOHandler, String deviceType) {
        this.deviceTypeDAOHandler = deviceTypeDAOHandler;
        this.deviceProps = deviceDetails.getProperties().getProperty();
        this.deviceType = deviceType;
    }

    public Device getDevice(String deviceId) throws DeviceTypeMgtPluginException {
        Connection conn = null;
        PreparedStatement stmt = null;
        Device device = null;
        ResultSet resultSet = null;
        try {
            conn = deviceTypeDAOHandler.getConnection();
            stmt = conn.prepareStatement(
                    "SELECT * FROM DM_DEVICE_PROPERTIES WHERE DEVICE_TYPE_NAME = ? AND DEVICE_IDENTIFICATION = ? " +
                            "AND TENANT_ID = ?");
            stmt.setString(1, deviceType);
            stmt.setString(2, deviceId);
            stmt.setInt(3, PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId(true));
            resultSet = stmt.executeQuery();
            List<Device.Property> properties = new ArrayList<>();
            while (resultSet.next()) {
                Device.Property property = new Device.Property();
                property.setName(resultSet.getString(PROPERTY_KEY_COLUMN_NAME));
                property.setValue(resultSet.getString(PROPERTY_VALUE_COLUMN_NAME));
                properties.add(property);
            }
            if (properties.size() > 0) {
                device = new Device();
                device.setDeviceIdentifier(deviceId);
                device.setType(deviceType);
                device.setProperties(properties);
            }
        } catch (SQLException e) {
            String msg = "Error occurred while fetching device : '" + deviceId + "' type " + deviceType;
            log.error(msg, e);
            throw new DeviceTypeMgtPluginException(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, resultSet);
            deviceTypeDAOHandler.closeConnection();
        }

        return device;
    }

    public boolean addDevice(Device device) throws DeviceTypeMgtPluginException {
        boolean status = false;
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = deviceTypeDAOHandler.getConnection();
            stmt = conn.prepareStatement(
                    "INSERT INTO DM_DEVICE_PROPERTIES(DEVICE_TYPE_NAME, DEVICE_IDENTIFICATION, PROPERTY_NAME, " +
                            "PROPERTY_VALUE, TENANT_ID) VALUES (?, ?, ?, ?, ?)");
            for (String propertyKey : deviceProps) {
                stmt.setString(1, deviceType);
                stmt.setString(2, device.getDeviceIdentifier());
                stmt.setString(3, propertyKey);
                stmt.setString(4, getPropertyValue(device.getProperties(), propertyKey));
                stmt.setInt(5, PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId(true));
                stmt.addBatch();
            }
            stmt.executeBatch();
            status = true;
        } catch (SQLException e) {
            String msg = "Error occurred while adding the device '" +
                    device.getDeviceIdentifier() + "' to the type " + deviceType + " db.";
            log.error(msg, e);
            throw new DeviceTypeMgtPluginException(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
        return status;
    }

    public boolean updateDevice(Device device) throws DeviceTypeMgtPluginException {
        Connection conn;
        PreparedStatement stmt = null;
        try {
            conn = deviceTypeDAOHandler.getConnection();
            stmt = conn.prepareStatement(
                    "UPDATE DM_DEVICE_PROPERTIES SET PROPERTY_VALUE = ? WHERE  DEVICE_TYPE_NAME = ? AND "
                            + "DEVICE_IDENTIFICATION = ? AND PROPERTY_NAME = ? AND TENANT_ID= ?");

            String propValue;
            for (Device.Property property : device.getProperties()) {
                if (!deviceProps.contains(property.getName())) {
                    continue;
                }
                propValue = property.getValue();
                if (propValue != null && propValue.length() > 100) {
                    propValue = "Value too long";
                }
                stmt.setString(1, propValue);
                stmt.setString(2, deviceType);
                stmt.setString(3, device.getDeviceIdentifier());
                stmt.setString(4, property.getName());
                stmt.setInt(5, PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId(true));
                stmt.addBatch();
            }
            stmt.executeBatch();
            return true;
        } catch (SQLException e) {
            String msg = "Error occurred while modifying the device '" + device.getDeviceIdentifier() + "' data on"
                    + deviceType;
            log.error(msg, e);
            throw new DeviceTypeMgtPluginException(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
    }

    public List<Device> getAllDevices() throws DeviceTypeMgtPluginException {
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        Map<String, Device> deviceMap = new HashMap<>();
        try {
            conn = deviceTypeDAOHandler.getConnection();
            stmt = conn.prepareStatement("SELECT DEVICE_IDENTIFICATION, PROPERTY_NAME, PROPERTY_VALUE FROM " +
                                                 "DM_DEVICE_PROPERTIES WHERE DEVICE_TYPE_NAME = ? AND TENANT_ID = ?");
            stmt.setString(1, deviceType);
            stmt.setInt(2, PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId(true));
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String deviceId = resultSet.getString("DEVICE_IDENTIFICATION");
                Device deviceInMap = deviceMap.get(deviceId);
                if (deviceInMap == null) {
                    deviceInMap = new Device();
                    deviceInMap.setDeviceIdentifier(deviceId);
                    deviceInMap.setType(deviceType);
                    List<Device.Property> properties = new ArrayList<>();
                    deviceInMap.setProperties(properties);
                    deviceMap.put(deviceId, deviceInMap);
                }
                Device.Property prop = new Device.Property();
                prop.setName(resultSet.getString(PROPERTY_KEY_COLUMN_NAME));
                prop.setName(resultSet.getString(PROPERTY_VALUE_COLUMN_NAME));
                deviceInMap.getProperties().add(prop);
            }
            if (log.isDebugEnabled()) {
                log.debug(
                        "All device details have fetched from " + deviceType + " table.");
            }
            return new ArrayList<>(deviceMap.values());
        } catch (SQLException e) {
            String msg =
                    "Error occurred while fetching all " + deviceType + " device data'";
            log.error(msg, e);
            throw new DeviceTypeMgtPluginException(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, resultSet);
            deviceTypeDAOHandler.closeConnection();
        }
    }

    @Override
    public boolean deleteDevices(List<String> deviceIdentifiers) throws DeviceTypeMgtPluginException {
        try {
            Connection conn = deviceTypeDAOHandler.getConnection();
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM DM_DEVICE_PROPERTIES WHERE DEVICE_IDENTIFICATION = ?")) {
                if (conn.getMetaData().supportsBatchUpdates()) {
                    for (String deviceId : deviceIdentifiers) {
                        ps.setString(1, deviceId);
                        ps.addBatch();
                    }
                    for (int i : ps.executeBatch()) {
                        if (i == Statement.SUCCESS_NO_INFO || i == Statement.EXECUTE_FAILED) {
                            return false;
                        }
                    }
                } else {
                    for (String deviceId : deviceIdentifiers) {
                        ps.setString(1, deviceId);
                        ps.executeUpdate();
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            String msg = "Error occurred while deleting the data of the devices: '" + deviceIdentifiers + "'of type:  "
                    + deviceType;
            log.error(msg, e);
            throw new DeviceTypeMgtPluginException(msg, e);
        }
    }


    private String getPropertyValue(List<Device.Property> properties, String propertyName) {
        for (Device.Property property : properties) {
            if (property.getName() != null && property.getName().equals(propertyName)) {
                String value = property.getValue();
                if (value != null && value.length() > 100) {
                    return "Value too long";
                }
                return value;
            }
        }
        return null;
    }

}