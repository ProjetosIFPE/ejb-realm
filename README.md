# ejb-realm

<glassfish-home>\domains\domain1\config\domain.xml

<auth-realm classname="com.softwarecorporativo.realm.AccessRealm" name="accessRealm">
          <property name="jaas-context" value="accessRealm"></property>
          <property name="charset" value="UTF-8"></property>
          <property name="hash-algorithm" value="SHA-256"></property>
          <property name="groups-sql-query" value="SELECT G.GRUPO_NOME FROM TB_USUARIO U, TB_GRUPO G, TB_USUARIO_GRUPO UG WHERE U.USUARIO_ID = UG.USUARIO_ID AND G.GRUPO_ID = UG.GRUPO_ID  AND U.USUARIO_LOGIN = ?"></property>
          <property name="jta-data-source" value="jdbc/Monitoria"></property>
          <property name="password-sql-query" value="SELECT U.USUARIO_SENHA FROM TB_USUARIO AS U WHERE U.USUARIO_LOGIN = ?"></property>
		  <property name="authentication-data-props" value="java:app/custom/authentication-data"></property>
</auth-realm>

<glassfish-home>\domains\domain1\config\server.policy

grant codeBase "file:jar-file-path" {
  permission com.sun.appserv.security.ProgrammaticLoginPermission "login";
};

<glassfish-home>\domains\domain1\config\login.conf

accessRealm {
   com.softwarecorporativo.realm.AccessControl required;
};
