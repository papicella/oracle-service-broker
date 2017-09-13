package pas.au.pivotal.pcf.servicebroker.oracle.service;

public interface Constants
{
    String NEW_USER =
            " CREATE USER %s\n" +
            "    IDENTIFIED BY %s\n" +
            "    DEFAULT TABLESPACE %s\n" +
            "    QUOTA 20M ON %s\n" +
            "    TEMPORARY TABLESPACE %s";

    String DROP_USER = "drop user %s cascade";

    String GRANT_CREATE_SESSION = "GRANT CREATE SESSION TO %s";

    String KILL_SESSIONS =
            "BEGIN\n" +
            "  FOR r IN (select sid,serial# from v$session where username='%s')\n" +
            "  LOOP\n" +
            "      EXECUTE IMMEDIATE 'alter system kill session ''' || r.sid  || ',' \n" +
            "        || r.serial# || ''' immediate';\n" +
            "  END LOOP;\n" +
            "END;";

    // TEST queries
    String USER_EXISTS =
            "select 'Y' from dba_users where username = '%s'";
}
