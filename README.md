# ejb-realm
- Crie o Realm em /$GLASSFISH_HOME/glassfish/domains/domain1/config/domain.xml
```
 <auth-realm classname="com.softwarecorporativo.realm.AccessRealm" name="accessRealm">
          <property name="jaas-context" value="accessRealm"></property>
          <property name="charset" value="UTF-8"></property>
          <property name="hash-algorithm" value="SHA-256"></property>
          <property name="groups-sql-query" value="SELECT G.TXT_NOME FROM MONITORIAIFPE.TB_USUARIO U, MONITORIAIFPE.TB_GRUPO G, MONITORIAIFPE.TB_USUARIO_GRUPO UG WHERE U.ID_USUARIO = UG.ID_USUARIO AND G.ID_GRUPO = UG.ID_GRUPO  AND U.TXT_EMAIL = ?"></property>
          <property name="jta-data-source" value="java:app/jdbc/Monitoria"></property>
          <property name="password-sql-query" value="SELECT U.TXT_SENHA, U.TXT_SAL FROM MONITORIAIFPE.TB_USUARIO AS U WHERE U.TXT_EMAIL = ?"></property>
 </auth-realm>
```
- Habilite o login pragmático em /$GLASSFISH_HOME/glassfish/domains/domain1/config/server.policy
```
grant codeBase "file:jar-file-path" {
  permission com.sun.appserv.security.ProgrammaticLoginPermission "login";
};
```
- Configure em /$GLASSFISH_HOME/glassfish/domains/domain1/config/login.conf; a classe que será utilizada para autenticação no Realm
```
accessRealm {
   com.softwarecorporativo.realm.AccessControl required;
};
```
