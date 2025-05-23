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

import io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.api.admin.DeviceTypeManagementAdminService;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.impl.admin.DeviceTypeManagementAdminServiceImpl;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.impl.util.DeviceMgtAPITestHelper;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.util.DeviceMgtAPIUtils;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceManagementException;
import io.entgra.device.mgt.core.device.mgt.common.spi.DeviceTypeGeneratorService;
import io.entgra.device.mgt.core.device.mgt.core.dto.DeviceType;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderServiceImpl;
import io.entgra.device.mgt.core.device.mgt.extensions.device.type.template.DeviceTypeGeneratorServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.testng.Assert;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;

import javax.ws.rs.core.Response;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * This class holds the unit tests for the class {@link DeviceTypeManagementAdminService}
 */
@PowerMockIgnore({"javax.ws.rs.*", "org.apache.log4j.*",  "org.mockito.*"})
@SuppressStaticInitializationFor({"io.entgra.device.mgt.core.device.mgt.api.jaxrs.util.DeviceMgtAPIUtils",
                                  "org.wso2.carbon.context.PrivilegedCarbonContext"})
@PrepareForTest({DeviceMgtAPIUtils.class, DeviceManagementProviderService.class, CarbonContext.class})
public class DeviceTypeManagementAdminServiceTest {

    private static final Log log = LogFactory.getLog(DeviceTypeManagementAdminService.class);
    private static final String TEST_DEVICE_TYPE = "TEST-DEVICE-TYPE";
    private static final String TEST_DEVICE_TYPE_1 = "DUMMY-DEVICE-TYPE-1";
    private static final String TEST_DEVICE_TYPE_2 = "DUMMY DEVICE TYPE";
    private static final int TEST_DEVICE_TYPE_ID = 12345;
    private static final int TEST_DEVICE_TYPE_ID_1 = 123452;
    private static final int TEST_DEVICE_TYPE_ID_2 = 121233452;
    private PrivilegedCarbonContext context;
    private DeviceTypeManagementAdminService deviceTypeManagementAdminService;
    private DeviceManagementProviderService deviceManagementProviderService;
    private DeviceTypeGeneratorService deviceTypeGeneratorService;

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    @BeforeClass
    public void init() throws DeviceManagementException {
        log.info("Initializing DeviceTypeManagementAdmin tests");
        initMocks(this);
        this.deviceManagementProviderService = Mockito
                .mock(DeviceManagementProviderServiceImpl.class, Mockito.RETURNS_MOCKS);
        this.deviceTypeGeneratorService = Mockito.mock(DeviceTypeGeneratorServiceImpl.class, Mockito.RETURNS_MOCKS);
        this.deviceTypeManagementAdminService = new DeviceTypeManagementAdminServiceImpl();
        context = Mockito.mock(PrivilegedCarbonContext.class);
        Mockito.doReturn(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME).when(context).getTenantDomain();
    }

    @Test(description = "Test get all the device types.")
    public void testGetDeviceTypes() {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        Response response = this.deviceTypeManagementAdminService.getDeviceTypes();
        Assert.assertNotNull(response, "The response should not be null");
        Assert.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(),
                "The Response status code should be 200.");
    }

    @Test(description = "Test the error scenario of getting all the device types.")
    public void testGetDeviceTypesError() throws DeviceManagementException {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        Mockito.when(deviceManagementProviderService.getDeviceTypes()).thenThrow(new DeviceManagementException());
        Response response = this.deviceTypeManagementAdminService.getDeviceTypes();
        Assert.assertNotNull(response, "The response should not be null");
        Assert.assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                "The expected status code is 500.");
        Mockito.reset(deviceManagementProviderService);
    }

    @Test(description = "Test the new device type creation scenario.")
    public void testAddDeviceTypeWithExistingName() throws DeviceManagementException {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        PowerMockito.stub(PowerMockito.method(PrivilegedCarbonContext.class, "getThreadLocalCarbonContext"))
                .toReturn(context);
        DeviceType deviceType = DeviceMgtAPITestHelper.getDummyDeviceType(TEST_DEVICE_TYPE_1, TEST_DEVICE_TYPE_ID_1);
        Response response = this.deviceTypeManagementAdminService.addDeviceType(deviceType);
        Assert.assertNotNull(response, "The response should not be null");
        Assert.assertEquals(response.getStatus(), Response.Status.CONFLICT.getStatusCode(),
                "The Response Status code should be 409.");
    }

    @Test(description = "Test the new device type creation scenario when device type name is unqualified.")
    public void testAddDeviceTypeWithUnqualifiedName() throws DeviceManagementException {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        PowerMockito.stub(PowerMockito.method(PrivilegedCarbonContext.class, "getThreadLocalCarbonContext"))
                .toReturn(context);
        Mockito.when(deviceManagementProviderService.getDeviceType(Mockito.anyString())).thenReturn(null);
        DeviceType deviceType = DeviceMgtAPITestHelper.getDummyDeviceType(TEST_DEVICE_TYPE_2, TEST_DEVICE_TYPE_ID_2);
        Response response = this.deviceTypeManagementAdminService.addDeviceType(deviceType);
        Assert.assertNotNull(response, "The response should not be null");
        Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode(),
                "The Response Status code should be 400.");
        Mockito.reset(deviceManagementProviderService);
    }

    @Test(description = "Test creating a new device type success scenario.")
    public void testAddDeviceType() throws DeviceManagementException {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceTypeGeneratorService"))
                .toReturn(this.deviceTypeGeneratorService);
        PowerMockito.stub(PowerMockito.method(PrivilegedCarbonContext.class, "getThreadLocalCarbonContext"))
                .toReturn(context);
        Mockito.when(deviceManagementProviderService.getDeviceType(Mockito.anyString())).thenReturn(null);
        DeviceType deviceType = DeviceMgtAPITestHelper.getDummyDeviceType(TEST_DEVICE_TYPE, TEST_DEVICE_TYPE_ID);
        Response response = this.deviceTypeManagementAdminService.addDeviceType(deviceType);
        Assert.assertNotNull(response, "The response should not be null");
        Assert.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(),
                "The Response Status code should be 200.");
        Mockito.reset(deviceManagementProviderService);
    }

    @Test(description = "Test the create device type scenario when the device type is null.")
    public void testAddDeviceTypeWithNoDeviceType() {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        Response response = this.deviceTypeManagementAdminService.addDeviceType(null);
        Assert.assertNotNull(response, "The response should not be null");
        Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode(),
                "The Response Status code should be 409.");
    }

    @Test(description = "Test the device type creation scenario with Device Management exception.")
    public void testAddDeviceTypeWithException() throws DeviceManagementException {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        PowerMockito.stub(PowerMockito.method(PrivilegedCarbonContext.class, "getThreadLocalCarbonContext"))
                .toReturn(context);
        Mockito.when(this.deviceManagementProviderService.getDeviceType(Mockito.anyString())).thenThrow(new
                DeviceManagementException());
        DeviceType deviceType = DeviceMgtAPITestHelper.getDummyDeviceType(TEST_DEVICE_TYPE, TEST_DEVICE_TYPE_ID);
        Response response = this.deviceTypeManagementAdminService.addDeviceType(deviceType);
        Assert.assertNotNull(response, "The response should not be null");
        Assert.assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                "The Response Status code should be 500.");
        Mockito.reset(deviceManagementProviderService);
    }

    @Test(description = "Test the update device type scenario.")
    public void testUpdateDeviceType() throws DeviceManagementException {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceTypeGeneratorService"))
                .toReturn(this.deviceTypeGeneratorService);
        DeviceType deviceType = DeviceMgtAPITestHelper.getDummyDeviceType(TEST_DEVICE_TYPE, TEST_DEVICE_TYPE_ID);
        Response response = this.deviceTypeManagementAdminService.updateDeviceType(TEST_DEVICE_TYPE, deviceType);
        Assert.assertNotNull(response, "The response should not be null");
        Assert.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode(),
                "The Response Status code should be 200.");
    }

    @Test(description = "Test the update device type scenario.")
    public void testUpdateNonExistingDeviceType() throws DeviceManagementException {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceTypeGeneratorService"))
                .toReturn(this.deviceTypeGeneratorService);
        Mockito.when(deviceManagementProviderService.getDeviceType(Mockito.anyString())).thenReturn(null);
        DeviceType deviceType = DeviceMgtAPITestHelper.getDummyDeviceType(TEST_DEVICE_TYPE, TEST_DEVICE_TYPE_ID);
        Response response = this.deviceTypeManagementAdminService.updateDeviceType(TEST_DEVICE_TYPE, deviceType);
        Assert.assertNotNull(response, "The response should not be null");
        Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode(),
                "The Response Status code should be 400.");
        Mockito.reset(deviceManagementProviderService);
    }

    @Test(description = "Test update device Type when device type is null")
    public void testUpdateDeviceTypeWithNullDeviceType() {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        Response response = this.deviceTypeManagementAdminService.updateDeviceType(TEST_DEVICE_TYPE, null);
        Assert.assertNotNull(response, "The response should not be null");
        Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode(),
                "The Response Status code should be 400.");
        Mockito.reset(deviceManagementProviderService);
    }

    @Test(description = "Test update device Type with DeviceManagementException")
    public void testUpdateDeviceTypeWithException() throws DeviceManagementException {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(this.deviceManagementProviderService);
        Mockito.when(this.deviceManagementProviderService.getDeviceType(Mockito.anyString()))
                .thenThrow(new DeviceManagementException());
        DeviceType deviceType = DeviceMgtAPITestHelper.getDummyDeviceType(TEST_DEVICE_TYPE, TEST_DEVICE_TYPE_ID);
        Response response = this.deviceTypeManagementAdminService.updateDeviceType(TEST_DEVICE_TYPE, deviceType);
        Assert.assertNotNull(response, "The response should not be null");
        Assert.assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                "The Response Status code should be 500.");
        Mockito.reset(deviceManagementProviderService);
    }

    @Test(description = "Test delete device type with correct request.")
    public void testDeleteDeviceType() throws DeviceManagementException {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(deviceManagementProviderService);
        Mockito.when(deviceManagementProviderService.
                deleteDeviceType(Mockito.anyString(), Mockito.any(DeviceType.class))).
                thenReturn(true);
        Response response = deviceTypeManagementAdminService.deleteDeviceType(TEST_DEVICE_TYPE);
        Assert.assertEquals(response.getStatus(), Response.Status.ACCEPTED.getStatusCode());
    }

    @Test(description = "Test delete device type when unavailable.")
    public void testDeleteNonExistingDeviceType() throws DeviceManagementException {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(deviceManagementProviderService);
        Mockito.when(deviceManagementProviderService.getDeviceType(Mockito.anyString())).thenReturn(null);
        Response response = deviceTypeManagementAdminService.deleteDeviceType(TEST_DEVICE_TYPE);
        Assert.assertEquals(response.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
        Mockito.reset(deviceManagementProviderService);
    }

    @Test(description = "Test delete device type when DeviceManagementException is thrown.")
    public void testDeleteDeviceTypeWithException() throws DeviceManagementException {
        PowerMockito.stub(PowerMockito.method(DeviceMgtAPIUtils.class, "getDeviceManagementService"))
                .toReturn(deviceManagementProviderService);
        Mockito.when(deviceManagementProviderService.
                deleteDeviceType(Mockito.anyString(), Mockito.any(DeviceType.class))).
                thenThrow(new DeviceManagementException());
        Response response = deviceTypeManagementAdminService.deleteDeviceType(TEST_DEVICE_TYPE);
        Assert.assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        Mockito.reset(deviceManagementProviderService);
    }
}
