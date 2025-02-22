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

import io.entgra.device.mgt.core.device.mgt.api.jaxrs.beans.ErrorResponse;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.api.DeviceTypeManagementService;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.impl.util.InputValidationException;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.impl.util.RequestValidationUtil;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.util.DeviceMgtAPIUtils;
import io.entgra.device.mgt.core.device.mgt.common.Feature;
import io.entgra.device.mgt.core.device.mgt.common.FeatureManager;
import io.entgra.device.mgt.core.device.mgt.common.PaginationRequest;
import io.entgra.device.mgt.core.device.mgt.common.configuration.mgt.PlatformConfiguration;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceManagementException;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceTypeNotFoundException;
import io.entgra.device.mgt.core.device.mgt.common.push.notification.PushNotificationConfig;
import io.entgra.device.mgt.core.device.mgt.common.type.mgt.DeviceTypeMetaDefinition;
import io.entgra.device.mgt.core.device.mgt.core.dto.DeviceType;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/device-types")
public class DeviceTypeManagementServiceImpl implements DeviceTypeManagementService {

    private static Log log = LogFactory.getLog(DeviceTypeManagementServiceImpl.class);

    @GET
    @Override
    public Response getDeviceTypes(@HeaderParam("If-Modified-Since") String ifModifiedSince,
                                   @QueryParam("offset") int offset,
                                   @QueryParam("limit") int limit,
                                   @QueryParam("filter") String filter) {

        try {
            RequestValidationUtil.validatePaginationParameters(offset, limit);
            List<DeviceType> deviceTypes;
            if (offset == 0 && limit == 0 && StringUtils.isEmpty(filter)) {
                deviceTypes = DeviceMgtAPIUtils.getDeviceManagementService()
                        .getDeviceTypes();
            } else {
                PaginationRequest paginationRequest = new PaginationRequest(offset, limit);
                if (!StringUtils.isEmpty(filter)) {
                    paginationRequest.setFilter(filter);
                }
                deviceTypes = DeviceMgtAPIUtils.getDeviceManagementService()
                        .getDeviceTypes(paginationRequest);
            }

            List<DeviceType> filteredDeviceTypes = new ArrayList<>();
            for (DeviceType deviceType : deviceTypes) {
                filteredDeviceTypes.add(clearMetaEntryInfo(deviceType));
            }
            return Response.status(Response.Status.OK).entity(filteredDeviceTypes).build();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred at server side while fetching device type.";
            log.error(msg, e);
            return Response.serverError().entity(msg).build();
        }  catch (InputValidationException e) {
            String msg = "Invalid pagination parameters";
            log.error(msg, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
        }

    }

    @Override
    @GET
    @Path("/{type}")
    public Response getDeviceTypeByName(@PathParam("type") @Size(min = 2, max = 45) String type) {
        if (type != null && type.length() > 0) {
            try {
                DeviceType deviceType = DeviceMgtAPIUtils.getDeviceManagementService().getDeviceType(type);
                if (deviceType == null) {
                    String msg = "Device type does not exist, " + type;
                    return Response.status(Response.Status.NO_CONTENT).entity(msg).build();
                }
                return Response.status(Response.Status.OK).entity(clearMetaEntryInfo(deviceType)).build();
            } catch (DeviceManagementException e) {
                String msg = "Error occurred at server side while fetching device type.";
                log.error(msg, e);
                return Response.serverError().entity(msg).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Override
    @Path("/{type}/features")
    public Response getFeatures(@PathParam("type") @Size(max = 45) String type,
                                @QueryParam("featureType") String featureType,
                                @QueryParam("hidden") String hidden,
                                @HeaderParam("If-Modified-Since") String ifModifiedSince) {
        List<Feature> features;
        DeviceManagementProviderService dms;
        try {
            if (StringUtils.isEmpty(type)) {
                String msg = "Type cannot be empty.";
                return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
            }
            dms = DeviceMgtAPIUtils.getDeviceManagementService();
            FeatureManager fm = dms.getFeatureManager(type);

            if (fm == null) {
                String msg = "No feature manager is registered with the given type : " + type ;
                return Response.status(Response.Status.NOT_FOUND).entity(msg).build();
            }

            if (StringUtils.isEmpty(hidden)) {
                features = fm.getFeatures(featureType);
            } else {
                features = fm.getFeatures(featureType, Boolean.valueOf(hidden));
            }
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while retrieving the list of [" + type + "] features with params " +
                    "{featureType: " + featureType + ", hidden: " + hidden + "}";
            log.error(msg, e);
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(msg).build()).build();
        } catch (DeviceTypeNotFoundException e) {
            String msg = "No device type found with name : " + type ;
            log.error(msg, e);
            return Response.status(Response.Status.NOT_FOUND).entity(msg).build();
        }
        return Response.status(Response.Status.OK).entity(features).build();
    }

    @GET
    @Override
    @Path("/{type}/configs")
    public Response getConfigs(@PathParam("type") @Size(min = 2, max = 45) String type,
                               @HeaderParam("If-Modified-Since") String ifModifiedSince) {
        PlatformConfiguration platformConfiguration;
        try {
            platformConfiguration = DeviceMgtAPIUtils.getDeviceManagementService().getConfiguration(type);
            if (platformConfiguration == null) {
                platformConfiguration = new PlatformConfiguration();
                platformConfiguration.setType(type);
                platformConfiguration.setConfiguration(new ArrayList<>());
            }
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while retrieving the '" + type + "' platform configuration";
            log.error(msg, e);
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(msg).build()).build();
        }
        return Response.status(Response.Status.OK).entity(platformConfiguration).build();
    }

    /**
     * This cleans up the configs that should not be exposed to iot users.
     *
     * @param deviceType device type retrieved from service layer.
     * @return sanitized device type.
     */
    private DeviceType clearMetaEntryInfo(DeviceType deviceType) {
        DeviceTypeMetaDefinition metaDefinition = deviceType.getDeviceTypeMetaDefinition();
        if (metaDefinition != null) {
            metaDefinition.setInitialOperationConfig(null);
            if (metaDefinition.getPushNotificationConfig() != null) {
                metaDefinition.setPushNotificationConfig(new PushNotificationConfig(metaDefinition.
                        getPushNotificationConfig().getType(), false, null));
            }
            deviceType.setDeviceTypeMetaDefinition(metaDefinition);
        }
        return deviceType;
    }

}
