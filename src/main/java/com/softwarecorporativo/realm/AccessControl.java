/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwarecorporativo.realm;

import com.sun.appserv.security.AppservPasswordLoginModule;
import java.util.List;
import javax.security.auth.login.LoginException;

/**
 *
 * @author Edmilson Santana
 */
public class AccessControl extends AppservPasswordLoginModule {

    @Override
    protected void authenticateUser() throws LoginException {
        AccessRealm realm = (AccessRealm) _currentRealm;
        if (realm.authenticateUser(_username, _password)) {
            List<String> groupsList = realm.getGroupList(_username);
            String[] groups = groupsList.toArray(new String[groupsList.size()]);
            commitUserAuthentication(groups);
        } else {
            throw new LoginException("Login inválido!");
        }
    }

}
