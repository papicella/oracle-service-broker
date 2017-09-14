<h1>Oracle 12c Service Broker for Pivotal Cloud Foundry </h1>

The following example is a PCF 2.0 Service Broker written as a Spring Boot application. This is just an example and should
be evolved to match a production type setup in terms oracle requirements. This service broker simply creates USERS and 
assigns then 20M of quota against a known TABLESPACE as part of the service creation and binding.

<h2>Pre Steps </h2>

- Create a TABLESPACE for all CF database access 

```
CREATE TABLESPACE cf_tablespace
   DATAFILE '/u01/app/oracle/oradata/orcl12c/cf_tablespace_01.dbf' SIZE 150M AUTOEXTEND ON NEXT 20M
   ONLINE;
```

- Verify tablespace exists and is online 

```
SYSTEM@localhost:1521/orcl> select tablespace_name, status from dba_tablespaces;

TABLESPACE_NAME        STATUS
---------------------- ---------
SYSTEM		       ONLINE
SYSAUX		       ONLINE
TEMP		       ONLINE
USERS		       ONLINE
EXAMPLE 	       ONLINE
APEX_1851336378250219  ONLINE
APEX_5457999048253711  ONLINE
CF_TABLESPACE	       ONLINE

8 rows selected.
```

```
SYSTEM@localhost:1521/orcl> SELECT  FILE_NAME, BLOCKS, TABLESPACE_NAME, AUTOEXTENSIBLE, Round(bytes/1024/1024,2) "Size (M)", Status, online_status FROM DBA_DATA_FILES;

FILE_NAME							      BLOCKS TABLESPACE_NAME	    AUT   Size (M) STATUS    ONLINE_
----------------------------------------------------------------- ---------- ---------------------- --- ---------- --------- -------
/u01/app/oracle/oradata/orcl12c/orcl/system01.dbf		       46080 SYSTEM		    YES        360 AVAILABLE SYSTEM
/u01/app/oracle/oradata/orcl12c/orcl/sysaux01.dbf		      149760 SYSAUX		    YES       1170 AVAILABLE ONLINE
/u01/app/oracle/oradata/orcl12c/orcl/SAMPLE_SCHEMA_users01.dbf	       20000 USERS		    YES     156.25 AVAILABLE ONLINE
/u01/app/oracle/oradata/orcl12c/orcl/example01.dbf		      159200 EXAMPLE		    YES    1243.75 AVAILABLE ONLINE
/u01/app/oracle/oradata/orcl12c/orcl/APEX_1851336378250219.dbf		1288 APEX_1851336378250219  YES      10.06 AVAILABLE ONLINE
/u01/app/oracle/oradata/orcl12c/orcl/APEX_5457999048253711.dbf		 648 APEX_5457999048253711  YES       5.06 AVAILABLE ONLINE
/u01/app/oracle/oradata/orcl12c/cf_tablespace_01.dbf		       19200 CF_TABLESPACE	    YES        150 AVAILABLE ONLINE

7 rows selected.
```

- Install the Oracle JDBC driver locally for maven

```
$ mvn install:install-file -Dfile=/Users/pasapicella/pivotal/jdbcdrivers/12c/ojdbc7.jar -DgroupId=com.oracle.jdbc -DartifactId=oracle-jdbc -Dversion=12.1.0.1.0 -Dpackaging=jar
```

<h2> Install </h2>

- Clone as follows

```
git clone https://github.com/papicella/oracle-service-broker.git
```

- Edit src/main/resources/application.yml to provide the correct Oracle JDBC connection string to a user who can has "CREATE USER"
priviledge. Also ensure yu target the correct TABLESPACE_NAME and TEMP TABLESPACE names to be used as per the steps above 
The "security." properties use Spring Security BASIC HTTP Authentication to protect the service broker application endpoints 

```
spring:
  application:
    name: cf-oracle-service-broker
  datasource:
    url: jdbc:oracle:thin:@//pas-macbook.local:1521/orcl
    username: system
    password: oracle
    driver-class-name: oracle.jdbc.OracleDriver
  jpa:
    hibernate:
      ddl-auto: update
oracle:
  tablespaceName: CF_TABLESPACE
  tempTablespaceName: TEMP
security:
  user:
    name: user
    password: password
```
 
- Package as shown below

```
$ mvn -DskipTests=true package

...

[INFO] --- maven-jar-plugin:2.6:jar (default-jar) @ oracle-service-broker ---
[INFO] Building jar: /Users/pasapicella/pivotal/DemoProjects/spring-starter/pivotal/oracle-service-broker/target/oracle-service-broker-0.0.1-SNAPSHOT.jar
[INFO]
[INFO] --- spring-boot-maven-plugin:1.5.6.RELEASE:repackage (default) @ oracle-service-broker ---
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------


```

- Push to CF as follows

```

$ cf push oracle-service-broker-pas -p ./target/oracle-service-broker-0.0.1-SNAPSHOT.jar -m 1g

Successfully destroyed container

0 of 1 instances running, 1 starting
0 of 1 instances running, 1 starting
0 of 1 instances running, 1 starting
1 of 1 instances running

App started


OK

     state     since                    cpu    memory       disk             details
#0   running   2017-09-05 02:04:34 AM   0.0%   358M of 1G   154.2M of 512M

```

- View pushed application

```
pasapicella@pas-macbook:~$ cf apps
Getting apps in org pcfdev-org / space pcfdev-space as admin...
OK

name                        requested state   instances   memory   disk   urls
pas-springboot-pcf          started           1/1         750M     512M   pas-springboot-pcf-pseudogenerical-microcosm.local.pcfdev.io
oracle-service-broker-pas   started           1/1         1G       512M   oracle-service-broker-pas.local.pcfdev.io

```

- Create a service broker 

Note: The username and password is what is in your application.yml file for the spring.security properties

```
pasapicella@pas-macbook:~$ cf create-service-broker oracle user password http://oracle-service-broker-pas.local.pcfdev.io
Creating service broker oracle as admin...
OK

```

- View service brokers to ensure it now exists

```
pasapicella@pas-macbook:~$ cf service-brokers
Getting service brokers as admin...

name           url
local-volume   http://localbroker.local.pcfdev.io
oracle         http://oracle-service-broker-pas.local.pcfdev.io
p-mysql        http://mysql-broker.local.pcfdev.io
p-rabbitmq     http://rabbitmq-broker.local.pcfdev.io
p-redis        http://redis-broker.local.pcfdev.io

```

- Enable the service broker

```
pasapicella@pas-macbook:~$ cf enable-service-access oracle
Enabling access to all plans of service oracle for all orgs as admin...
OK
```

- Log into Application Manager UI and verify service appears in Market Place

![alt tag](https://image.ibb.co/cZe7oa/oracle_sb_1.png)
![alt tag](https://image.ibb.co/g1Dp1v/oracle_sb_2.png)
![alt tag](https://image.ibb.co/nNctTa/oracle_sb_3.png)


- View marketplace from command line

```
pasapicella@pas-macbook:~$ cf marketplace
Getting services from marketplace in org pcfdev-org / space pcfdev-space as admin...
OK

service        plans             description
local-volume   free-local-disk   Local service docs: https://github.com/cloudfoundry-incubator/local-volume-release/
oracle         default*          A simple Oracle 12c service broker implementation
p-mysql        512mb, 1gb        MySQL databases on demand
p-rabbitmq     standard          RabbitMQ is a robust and scalable high-performance multi-protocol messaging broker.
p-redis        shared-vm         Redis service to provide a key-value store


```

- Create a service from the command line as follows

```
pasapicella@pas-macbook:~$ cf create-service oracle default oracle-service
Creating service instance oracle-service in org pcfdev-org / space pcfdev-space as admin...
OK

Attention: The plan `default` of service `oracle` is not free.  The instance `oracle-service` will incur a cost.  Contact your administrator if you think this is in error.
```

- View services

```
pasapicella@pas-macbook:~$ cf services
Getting services in org pcfdev-org / space pcfdev-space as admin...
OK

name             service   plan      bound apps   last operation
oracle-service   oracle    default                create succeeded
```

- Verify oracle user was created with prefix CF_

```
SYSTEM@localhost:1521/orcl> select username from all_users where username like 'CF_%';

USERNAME
--------------------------------------------------------------------------------------------------------------------------------
CF_DBF3B56_1540_4527_AA8F_586

```

- Bind service to an application

```
pasapicella@pas-macbook:~$ cf bind-service pas-springboot-pcf oracle-service
Binding service oracle-service to app pas-springboot-pcf in org pcfdev-org / space pcfdev-space as admin...
OK
TIP: Use 'cf restage pas-springboot-pcf' to ensure your env variable changes take effect
```

- Verify bound service injects oracle jdbcURL and username and password into VCAP_SERVICES

```
pasapicella@pas-macbook:~$ cf env pas-springboot-pcf
Getting env variables for app pas-springboot-pcf in org pcfdev-org / space pcfdev-space as admin...
OK

System-Provided:
{
 "VCAP_SERVICES": {
  "oracle": [
   {
    "credentials": {
     "jdbcUrl": "jdbc:oracle:thin:@//pas-macbook.local:1521/orcl",
     "password": "GCzRmaKLi1EurWkyFvGxYob6s9RigV",
     "uri": "oracle://CF_d7a1f50_39dc_42c4_a609_cfd:GCzRmaKLi1EurWkyFvGxYob6s9RigV@pas-macbook.local:1521/orcl",
     "username": "CF_d7a1f50_39dc_42c4_a609_cfd"
    },
    "label": "oracle",
    "name": "oracle-service",
    "plan": "default",
    "provider": null,
    "syslog_drain_url": null,
    "tags": [
     "oracle",
     "Database storage"
    ],
    "volume_mounts": []
   }
  ]
 }
}

...

```

<hr />
Pas Apicella [papicella at pivotal.io] is a Senior Platform Architect at Pivotal Australia 
