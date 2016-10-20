/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwarecorporativo.realm;

import com.sun.appserv.connectors.internal.api.ConnectorRuntime;
import com.sun.appserv.security.AppservRealm;
import com.sun.enterprise.security.auth.realm.BadRealmException;
import com.sun.enterprise.security.auth.realm.InvalidOperationException;
import com.sun.enterprise.security.auth.realm.NoSuchRealmException;
import com.sun.enterprise.security.auth.realm.NoSuchUserException;
import com.sun.enterprise.security.common.Util;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 *
 * @author Edmilson Santana
 */
public class AccessRealm extends AppservRealm {

    private static final String JTA_DATA_SOURCE = "jta-data-source";
    private static final String HASH_ALGORITHM = "hash-algorithm";
    private static final String GROUPS_SQL_QUERY = "groups-sql-query";
    private static final String PASSWORD_SQL_QUERY = "password-sql-query";
    private static final String CHARSET = "charset";

    private DataSource dataSource;

    @Override
    protected void init(Properties properties) throws BadRealmException, NoSuchRealmException {
        setProperty(JAAS_CONTEXT_PARAM, properties.getProperty(JAAS_CONTEXT_PARAM));
        setProperty(JTA_DATA_SOURCE, properties.getProperty(JTA_DATA_SOURCE));
        setProperty(HASH_ALGORITHM, properties.getProperty(HASH_ALGORITHM));
        setProperty(PASSWORD_SQL_QUERY, properties.getProperty(PASSWORD_SQL_QUERY));
        setProperty(GROUPS_SQL_QUERY, properties.getProperty(GROUPS_SQL_QUERY));
        setProperty(CHARSET, properties.getProperty(CHARSET));
    }

    @Override
    public Enumeration getGroupNames(String username) throws InvalidOperationException, NoSuchUserException {
        List<String> groups = getGroupList(username);

        return Collections.enumeration(groups);
    }

    public boolean authenticateUser(String username, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean result = true;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(getPasswordSqlQuery());
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            System.out.println("Usuario: " + username);
            if (rs.next()) {
                String passwd = rs.getString(1);
                System.out.println("passwd: " + passwd);
                String salt = rs.getString(2);
                System.out.println("salt: " + salt);
                if (passwd == null || !passwd.equals(getHash(salt, password))) {
                    result = false;
                }
            } else {
                result = false;
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return result;
    }

    private Connection getConnection() {
        try {
            synchronized (this) {
                if (dataSource == null) {
                    ActiveDescriptor<ConnectorRuntime> cr = (ActiveDescriptor<ConnectorRuntime>) Util.getDefaultHabitat().getBestDescriptor(BuilderHelper.createContractFilter(ConnectorRuntime.class.getName()));
                    ConnectorRuntime connectorRuntime = Util.getDefaultHabitat().getServiceHandle(cr).getService();
                    dataSource = (DataSource) connectorRuntime.lookupNonTxResource(getJtaDataSource(), false);
                }
            }
            return dataSource.getConnection();
        } catch (SQLException | NamingException e) {
            throw new RuntimeException(e);
        }
    }


    public String getHash(String salt, String senha) {
        try {
            String passwd = salt.concat(senha);
            MessageDigest digest = MessageDigest.getInstance(getHashAlgorithm());
            digest.update(passwd.getBytes(Charset.forName(getCharset())));
            return Base64.getEncoder().encodeToString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getGroupList(String username) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<String> groups = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(getGroupsSqlQuery());
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String group = rs.getString(1);
                groups.add(group);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeResources(rs, stmt, conn);
        }
        return groups;
    }

    private void closeResources(ResultSet resultSet, Statement statement, Connection connection) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public synchronized String getJAASContext() {
        return "accessRealm";
    }

    @Override
    public String getAuthType() {
        return "jdbc";
    }

    public String getCharset() {
        return super.getProperty(CHARSET);
    }

    public String getJtaDataSource() {
        return super.getProperty(JTA_DATA_SOURCE);
    }

    public String getHashAlgorithm() {
        return super.getProperty(HASH_ALGORITHM);
    }

    public String getGroupsSqlQuery() {
        return super.getProperty(GROUPS_SQL_QUERY);
    }

    public String getPasswordSqlQuery() {
        return super.getProperty(PASSWORD_SQL_QUERY);
    }


}
