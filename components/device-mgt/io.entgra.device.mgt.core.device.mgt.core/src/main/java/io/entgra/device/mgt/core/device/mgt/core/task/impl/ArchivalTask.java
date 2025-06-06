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

package io.entgra.device.mgt.core.device.mgt.core.task.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import io.entgra.device.mgt.core.device.mgt.core.archival.ArchivalException;
import io.entgra.device.mgt.core.device.mgt.core.archival.ArchivalService;
import io.entgra.device.mgt.core.device.mgt.core.archival.ArchivalServiceImpl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ArchivalTask extends RandomlyAssignedScheduleTask {

    private static final Log log = LogFactory.getLog(ArchivalTask.class);
    private static final String TASK_NAME = "DATA_ARCHIVAL_TASK";

    private ArchivalService archivalService;

    @Override
    public void setProperties(Map<String, String> map) {

    }

    @Override
    protected void setup() {
        this.archivalService = new ArchivalServiceImpl();
    }

    @Override
    protected void executeRandomlyAssignedTask() {
        log.info("Executing ArchivalTask at " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        long startTime = System.currentTimeMillis();
        try {
            archivalService.archiveTransactionalRecords();
        } catch (ArchivalException e) {
            log.error("An error occurred while running ArchivalTask", e);
        }
        long endTime = System.currentTimeMillis();
        long difference = endTime - startTime;
        log.info("ArchivalTask completed. Total execution time: " + getDurationBreakdown(difference));
    }

    @Override
    public String getTaskName() {
        return TASK_NAME;
    }

    private String getDurationBreakdown(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        return (days + " Days " + hours + " Hours " + minutes + " Minutes " + seconds + " Seconds");
    }

}
