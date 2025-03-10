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

package io.entgra.device.mgt.core.device.mgt.core.dao.impl.group;

import io.entgra.device.mgt.core.device.mgt.common.Device;
import io.entgra.device.mgt.core.device.mgt.core.dao.GroupManagementDAOException;
import io.entgra.device.mgt.core.device.mgt.core.dao.GroupManagementDAOFactory;
import io.entgra.device.mgt.core.device.mgt.core.dao.impl.AbstractGroupDAOImpl;
import io.entgra.device.mgt.core.device.mgt.core.dao.util.DeviceManagementDAOUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents implementation of GroupDAO
 */
public class GenericGroupDAOImpl extends AbstractGroupDAOImpl {

    @Override
    public List<Device> getDevices(int groupId, int startIndex, int rowCount, int tenantId)
            throws GroupManagementDAOException {
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Device> devices = null;
        try {
            conn = GroupManagementDAOFactory.getConnection();
            String sql = "SELECT d1.DEVICE_ID, d1.DESCRIPTION, d1.NAME AS DEVICE_NAME, e.DEVICE_TYPE, " +
                    "d1.DEVICE_IDENTIFICATION, d1.LAST_UPDATED_TIMESTAMP, e.OWNER, e.OWNERSHIP, e.STATUS, " +
                    "e.IS_TRANSFERRED, e.DATE_OF_LAST_UPDATE, " +
                    "e.DATE_OF_ENROLMENT, e.ID AS ENROLMENT_ID FROM DM_ENROLMENT e, " +
                    "(SELECT gd.DEVICE_ID, gd.DESCRIPTION, gd.NAME, gd.DEVICE_IDENTIFICATION, gd.LAST_UPDATED_TIMESTAMP " +
                    "FROM " +
                    "(SELECT d.ID AS DEVICE_ID, d.DESCRIPTION, d.NAME, d.DEVICE_IDENTIFICATION, d.LAST_UPDATED_TIMESTAMP FROM" +
                    " DM_DEVICE d, (" +
                    "SELECT dgm.DEVICE_ID FROM DM_DEVICE_GROUP_MAP dgm WHERE dgm.GROUP_ID = ?) dgm1 " +
                    "WHERE d.ID = dgm1.DEVICE_ID AND d.TENANT_ID = ?) gd) d1 " +
                    "WHERE d1.DEVICE_ID = e.DEVICE_ID AND TENANT_ID = ? LIMIT ? OFFSET ?";

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, groupId);
            stmt.setInt(2, tenantId);
            stmt.setInt(3, tenantId);
            //noinspection JpaQueryApiInspection
            stmt.setInt(4, rowCount);
            //noinspection JpaQueryApiInspection
            stmt.setInt(5, startIndex);
            rs = stmt.executeQuery();
            devices = new ArrayList<>();
            while (rs.next()) {
                Device device = DeviceManagementDAOUtil.loadDevice(rs);
                devices.add(device);
            }
        } catch (SQLException e) {
            throw new GroupManagementDAOException("Error occurred while retrieving information of all " +
                                                          "registered devices", e);
        } finally {
            DeviceManagementDAOUtil.cleanupResources(stmt, rs);
        }
        return devices;
    }
}
