package pas.au.pivotal.pcf.servicebroker.oracle.model;

import javax.persistence.*;

@Entity
@Table (name = "SI_PASSWORD")
public class PasswordManager
{
    @Id
    private String serviceInstanceId;

    private String password;

    public PasswordManager() {
    }

    public PasswordManager(String serviceInstanceId, String password) {
        this.serviceInstanceId = serviceInstanceId;
        this.password = password;
    }

    public String getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "PasswordManager{" +
                "serviceInstanceId='" + serviceInstanceId + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
