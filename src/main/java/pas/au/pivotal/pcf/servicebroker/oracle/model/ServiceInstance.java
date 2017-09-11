package pas.au.pivotal.pcf.servicebroker.oracle.model;

import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SI")
public class ServiceInstance
{
    @Id
    private String serviceInstanceId;

    private String serviceDefinitionId;
    private String planId;
    private String organizationGuid;
    private String spaceGuid;
    private String dashboardUrl;

    public ServiceInstance() {
    }

    public ServiceInstance(CreateServiceInstanceRequest request) {
        this.serviceDefinitionId = request.getServiceDefinitionId();
        this.planId = request.getPlanId();
        this.organizationGuid = request.getOrganizationGuid();
        this.spaceGuid = request.getSpaceGuid();
        this.serviceInstanceId = request.getServiceInstanceId();
    }

    public ServiceInstance(UpdateServiceInstanceRequest request) {
        this.serviceInstanceId = request.getServiceInstanceId();
        this.planId = request.getPlanId();
    }

    public String getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    public String getServiceDefinitionId() {
        return serviceDefinitionId;
    }

    public void setServiceDefinitionId(String serviceDefinitionId) {
        this.serviceDefinitionId = serviceDefinitionId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getOrganizationGuid() {
        return organizationGuid;
    }

    public void setOrganizationGuid(String organizationGuid) {
        this.organizationGuid = organizationGuid;
    }

    public String getSpaceGuid() {
        return spaceGuid;
    }

    public void setSpaceGuid(String spaceGuid) {
        this.spaceGuid = spaceGuid;
    }

    public String getDashboardUrl() {
        return dashboardUrl;
    }

    public void setDashboardUrl(String dashboardUrl) {
        this.dashboardUrl = dashboardUrl;
    }

    @Override
    public String toString() {
        return "ServiceInstance{" +
                "serviceInstanceId='" + serviceInstanceId + '\'' +
                ", serviceDefinitionId='" + serviceDefinitionId + '\'' +
                ", planId='" + planId + '\'' +
                ", organizationGuid='" + organizationGuid + '\'' +
                ", spaceGuid='" + spaceGuid + '\'' +
                ", dashboardUrl='" + dashboardUrl + '\'' +
                '}';
    }
}
