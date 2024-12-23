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

package io.entgra.device.mgt.core.device.mgt.core.otp.mgt.dao.impl;

import io.entgra.device.mgt.core.device.mgt.common.DeviceManagementConstants;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DBConnectionException;
import io.entgra.device.mgt.core.device.mgt.common.otp.mgt.dto.OneTimePinDTO;
import io.entgra.device.mgt.core.device.mgt.core.otp.mgt.dao.AbstractDAOImpl;
import io.entgra.device.mgt.core.device.mgt.core.otp.mgt.dao.OTPManagementDAO;
import io.entgra.device.mgt.core.device.mgt.core.otp.mgt.exception.OTPManagementDAOException;import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

public class GenericOTPManagementDAOImpl extends AbstractDAOImpl implements OTPManagementDAO {

    private static final Log log = LogFactory.getLog(GenericOTPManagementDAOImpl.class);

    @Override
    public void addOTPData(List<OneTimePinDTO> oneTimePinDTOList) throws OTPManagementDAOException {
        if (log.isDebugEnabled()) {
            log.debug("Request received in DAO Layer to create an OTP data entry");
            log.debug("OTP Details : ");
            for(OneTimePinDTO oneTimePinDTO: oneTimePinDTOList){
                log.debug("OTP key : " + oneTimePinDTO.getOtpToken() + " Email : " + oneTimePinDTO.getEmail());
            }
        }

        String sql = "INSERT INTO DM_OTP_DATA "
                + "(OTP_TOKEN, "
                + "EMAIL, "
                + "EMAIL_TYPE, "
                + "META_INFO, "
                + "CREATED_AT,"
                + "TENANT_ID,"
                + "USERNAME, "
                + "EXPIRY_TIME) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = this.getDBConnection();
            Calendar calendar = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (OneTimePinDTO oneTimePinDTO : oneTimePinDTOList) {
                    stmt.setString(1, oneTimePinDTO.getOtpToken());
                    stmt.setString(2, oneTimePinDTO.getEmail());
                    stmt.setString(3, oneTimePinDTO.getEmailType());
                    stmt.setString(4, oneTimePinDTO.getMetaInfo());
                    stmt.setTimestamp(5, timestamp);
                    stmt.setInt(6, oneTimePinDTO.getTenantId());
                    stmt.setString(7, oneTimePinDTO.getUsername());
                    stmt.setInt(8, oneTimePinDTO.getExpiryTime() == 0
                            ? DeviceManagementConstants.OTPProperties.OTP_DEFAULT_EXPIRY_SECONDS : oneTimePinDTO.getExpiryTime());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining the DB connection to create an opt entry.";
            log.error(msg, e);
            throw new OTPManagementDAOException(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred while executing SQL to create an otp entry";
            log.error(msg, e);
            throw new OTPManagementDAOException(msg, e);
        }
    }

    @Override
    public OneTimePinDTO getOTPDataByToken (String oneTimeToken) throws OTPManagementDAOException {

        if (log.isDebugEnabled()) {
            log.debug("Request received in DAO Layer to get an OTP data entry for OTP");
            log.debug("OTP Details : OTP key : " + oneTimeToken );
        }

        String sql = "SELECT "
                + "ID, "
                + "OTP_TOKEN, "
                + "EMAIL, "
                + "EMAIL_TYPE, "
                + "META_INFO, "
                + "CREATED_AT, "
                + "EXPIRY_TIME, "
                + "IS_EXPIRED, "
                + "TENANT_ID, "
                + "USERNAME FROM DM_OTP_DATA "
                + "WHERE OTP_TOKEN = ?";

        try {
            Connection conn = this.getDBConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, oneTimeToken);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        OneTimePinDTO oneTimePinDTO = new OneTimePinDTO();
                        oneTimePinDTO.setId(rs.getInt("ID"));
                        oneTimePinDTO.setOtpToken(rs.getString("OTP_TOKEN"));
                        oneTimePinDTO.setEmail(rs.getString("EMAIL"));
                        oneTimePinDTO.setEmailType(rs.getString("EMAIL_TYPE"));
                        oneTimePinDTO.setMetaInfo(rs.getString("META_INFO"));
                        oneTimePinDTO.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                        oneTimePinDTO.setExpiryTime(rs.getInt("EXPIRY_TIME"));
                        oneTimePinDTO.setExpired(rs.getBoolean("IS_EXPIRED"));
                        oneTimePinDTO.setTenantId(rs.getInt("TENANT_ID"));
                        oneTimePinDTO.setUsername(rs.getString("USERNAME"));
                        return oneTimePinDTO;
                    }
                    return null;
                }
            }
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining the DB connection to get OPT data for given OTP. OTP:  "
                    + oneTimeToken;
            log.error(msg, e);
            throw new OTPManagementDAOException(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred while executing SQL to get OTP data for OTP. One time token: " + oneTimeToken;
            log.error(msg, e);
            throw new OTPManagementDAOException(msg, e);
        }
    }

    @Override
    public boolean expireOneTimeToken(String oneTimeToken) throws OTPManagementDAOException {
        if (log.isDebugEnabled()) {
            log.debug("Request received in DAO Layer to update an OTP data entry for OTP");
            log.debug("OTP Details : OTP key : " + oneTimeToken );
        }

        String sql = "UPDATE DM_OTP_DATA "
                + "SET "
                + "IS_EXPIRED = ? "
                + "WHERE OTP_TOKEN = ?";

        try {
            Connection conn = this.getDBConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setBoolean(1, true);
                stmt.setString(2, oneTimeToken);
                return stmt.executeUpdate() == 1;
            }
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining the DB connection to update the OTP token validity.";
            log.error(msg, e);
            throw new OTPManagementDAOException(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred when obtaining database connection for updating the OTP token validity.";
            log.error(msg, e);
            throw new OTPManagementDAOException(msg, e);
        }
    }

    @Override
    public void renewOneTimeToken(int id, String oneTimeToken) throws OTPManagementDAOException {
        if (log.isDebugEnabled()) {
            log.debug("Request received in DAO Layer to update an OTP data entry for OTP");
            log.debug("OTP Details : OTP key : " + oneTimeToken );
        }

        String sql = "UPDATE DM_OTP_DATA "
                + "SET "
                + "OTP_TOKEN = ?, "
                + "CREATED_AT = ? "
                + "WHERE ID = ?";

        try {
            Connection conn = this.getDBConnection();
            Calendar calendar = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, oneTimeToken);
                stmt.setTimestamp(2, timestamp);
                stmt.setInt(3, id);
                stmt.executeUpdate();
            }
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining the DB connection to update the OTP token.";
            log.error(msg, e);
            throw new OTPManagementDAOException(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred when executing sql query to update the OTP token.";
            log.error(msg, e);
            throw new OTPManagementDAOException(msg, e);
        }
    }

    public void restoreOneTimeToken(int id, String oneTimeToken) throws OTPManagementDAOException {
        if (log.isDebugEnabled()) {
            log.debug("Request received in DAO Layer to update an OTP data entry for OTP");
            log.debug("OTP Details : OTP key : " + oneTimeToken );
        }

        String sql = "UPDATE DM_OTP_DATA "
                + "SET "
                + "OTP_TOKEN = ?, "
                + "CREATED_AT = ?, "
                + "IS_EXPIRED = false "
                + "WHERE ID = ?";

        try {
            Connection conn = this.getDBConnection();
            Calendar calendar = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, oneTimeToken);
                stmt.setTimestamp(2, timestamp);
                stmt.setInt(3, id);
                stmt.executeUpdate();
            }
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining the DB connection to update the OTP token.";
            log.error(msg, e);
            throw new OTPManagementDAOException(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred when executing sql query to update the OTP token.";
            log.error(msg, e);
            throw new OTPManagementDAOException(msg, e);
        }
    }


    @Override
    public boolean isEmailExist (String email, String emailType) throws OTPManagementDAOException {

        if (log.isDebugEnabled()) {
            log.debug("Request received in DAO Layer to verify whether email was registed with emai type in OTP");
            log.debug("OTP Details : email : " + email + " email type: " + emailType );
        }

        String sql = "SELECT "
                + "ID "
                + "FROM DM_OTP_DATA "
                + "WHERE EMAIL = ? AND "
                + "EMAIL_TYPE = ?";

        try {
            Connection conn = this.getDBConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                stmt.setString(2, emailType);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining the DB connection to verify email and email type exist in OTP."
                    + " Email: " + email + "Email Type: " + emailType;
            log.error(msg, e);
            throw new OTPManagementDAOException(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred while executing SQL to verify email and email type exist in OTP. Email: "
                    + email + "Email Type: " + emailType;
            log.error(msg, e);
            throw new OTPManagementDAOException(msg, e);
        }
    }

    @Override
    public OneTimePinDTO getOtpDataByEmailAndMailType(String email, String emailType) throws OTPManagementDAOException {

        if (log.isDebugEnabled()) {
            log.debug("Request received in DAO Layer to verify whether email was registed with emai type in OTP");
            log.debug("OTP Details : email : " + email + " email type: " + emailType );
        }

        String sql = "SELECT "
                + "ID, "
                + "OTP_TOKEN, "
                + "EMAIL, "
                + "EMAIL_TYPE, "
                + "META_INFO, "
                + "CREATED_AT, "
                + "EXPIRY_TIME, "
                + "IS_EXPIRED, "
                + "TENANT_ID, "
                + "USERNAME "
                + "FROM DM_OTP_DATA "
                + "WHERE EMAIL = ? AND "
                + "EMAIL_TYPE = ?";

        try {
            Connection conn = this.getDBConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                stmt.setString(2, emailType);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        OneTimePinDTO oneTimePinDTO = new OneTimePinDTO();
                        oneTimePinDTO.setId(rs.getInt("ID"));
                        oneTimePinDTO.setOtpToken(rs.getString("OTP_TOKEN"));
                        oneTimePinDTO.setEmail(rs.getString("EMAIL"));
                        oneTimePinDTO.setEmailType(rs.getString("EMAIL_TYPE"));
                        oneTimePinDTO.setMetaInfo(rs.getString("META_INFO"));
                        oneTimePinDTO.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                        oneTimePinDTO.setExpiryTime(rs.getInt("EXPIRY_TIME"));
                        oneTimePinDTO.setExpired(rs.getBoolean("IS_EXPIRED"));
                        oneTimePinDTO.setTenantId(rs.getInt("TENANT_ID"));
                        oneTimePinDTO.setUsername(rs.getString("USERNAME"));
                        return oneTimePinDTO;
                    }
                    return null;                }
            }
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining the DB connection to verify email and email type exist in OTP."
                    + " Email: " + email + "Email Type: " + emailType;
            log.error(msg, e);
            throw new OTPManagementDAOException(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred while executing SQL to verify email and email type exist in OTP. Email: "
                    + email + "Email Type: " + emailType;
            log.error(msg, e);
            throw new OTPManagementDAOException(msg, e);
        }
    }
}
