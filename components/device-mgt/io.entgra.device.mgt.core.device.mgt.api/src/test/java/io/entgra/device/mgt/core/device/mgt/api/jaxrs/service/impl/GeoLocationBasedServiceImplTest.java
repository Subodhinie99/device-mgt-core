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

package io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.impl;

import io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.api.GeoLocationBasedService;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceManagementException;
import io.entgra.device.mgt.core.device.mgt.common.geo.service.GeoCluster;
import io.entgra.device.mgt.core.device.mgt.common.geo.service.GeoCoordinate;
import io.entgra.device.mgt.core.device.mgt.common.geo.service.GeoQuery;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.context.PrivilegedCarbonContext;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@PowerMockIgnore({"javax.ws.rs.*", "org.apache.log4j.*"})
public class GeoLocationBasedServiceImplTest {
    private DeviceManagementProviderService deviceManagementProviderService;
    private PrivilegedCarbonContext context;
    private GeoLocationBasedService geoLocationBasedService;

    @BeforeClass
    public void init() {
        deviceManagementProviderService = Mockito.mock(DeviceManagementProviderService.class);
        geoLocationBasedService = new GeoLocationBasedServiceImpl();
        context = Mockito.mock(PrivilegedCarbonContext.class);
        Mockito.doReturn("admin").when(context).getUsername();
    }

    @Test(description = "This method tests the behaviour of getGeoDeviceLocations when there are no devices" +
            "in the given map boundaries")
    public void testGetGeoDeviceLocations1() throws DeviceManagementException {
        GeoQuery geoQuery = new GeoQuery(Mockito.any(GeoCoordinate.class), Mockito.any(GeoCoordinate.class), Mockito.anyInt());
        Mockito.doReturn(new ArrayList<GeoCluster>()).when(deviceManagementProviderService)
                .findGeoClusters(geoQuery);
        Response response = geoLocationBasedService.getGeoDeviceLocations(null, 0.4, 15, 75.6,
                90.1, 6);
        Assert.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(),
                "getGeoDeviceLocations request failed with valid parameters");
    }

    @Test(description = "This method tests the behaviour of getGeoDeviceLocations when there are devices" +
            "in the given map boundaries")
    public void testGetGeoDeviceLocations2() throws DeviceManagementException {
        List<GeoCluster> geoClusters = new ArrayList<>();
        geoClusters.add(new GeoCluster(new GeoCoordinate(1.5, 80.7),
                new GeoCoordinate(1.1, 79.5), new GeoCoordinate(1.9, 82.1), 3,
                "tb32", null));
        geoClusters.add(new GeoCluster(new GeoCoordinate(10.2, 86.1),
                new GeoCoordinate(9.8, 84.7), new GeoCoordinate(11.1, 88.1), 4,
                "t1gd", null));

        GeoQuery geoQuery = new GeoQuery(Mockito.any(GeoCoordinate.class), Mockito.any(GeoCoordinate.class), Mockito.anyInt());
        Mockito.doReturn(geoClusters).when(deviceManagementProviderService)
                .findGeoClusters(geoQuery);
        Response response = geoLocationBasedService.getGeoDeviceLocations(null, 0.4, 15, 75.6,
                90.1, 6);
        Assert.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(),
                "getGeoDeviceLocations request failed with valid parameters");
    }
}
