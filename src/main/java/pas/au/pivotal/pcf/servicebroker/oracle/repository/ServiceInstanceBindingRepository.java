package pas.au.pivotal.pcf.servicebroker.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pas.au.pivotal.pcf.servicebroker.oracle.model.ServiceInstanceBinding;

public interface  ServiceInstanceBindingRepository extends JpaRepository <ServiceInstanceBinding, String> {
}
