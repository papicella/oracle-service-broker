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

}
