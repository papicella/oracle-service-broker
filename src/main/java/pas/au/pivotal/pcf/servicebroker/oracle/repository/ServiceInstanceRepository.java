package pas.au.pivotal.pcf.servicebroker.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pas.au.pivotal.pcf.servicebroker.oracle.model.ServiceInstance;

public interface ServiceInstanceRepository extends JpaRepository <ServiceInstance, String> {
}
