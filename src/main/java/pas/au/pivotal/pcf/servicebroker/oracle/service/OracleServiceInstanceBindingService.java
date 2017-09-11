package pas.au.pivotal.pcf.servicebroker.oracle.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.stereotype.Service;
import pas.au.pivotal.pcf.servicebroker.oracle.model.ServiceInstanceBinding;
import pas.au.pivotal.pcf.servicebroker.oracle.repository.PasswordManagerRepository;
import pas.au.pivotal.pcf.servicebroker.oracle.repository.ServiceInstanceBindingRepository;

@Service
public class OracleServiceInstanceBindingService implements ServiceInstanceBindingService {

    private Logger logger = LoggerFactory.getLogger(OracleServiceInstanceBindingService.class);

    private OracleManager oracleManager;
    private PasswordManagerRepository passwordManagerRepository;
    private ServiceInstanceBindingRepository serviceInstanceBindingRepository;

    @Autowired
    public OracleServiceInstanceBindingService(OracleManager oracleManager, PasswordManagerRepository passwordManagerRepository, ServiceInstanceBindingRepository serviceInstanceBindingRepository) {
        this.oracleManager = oracleManager;
        this.passwordManagerRepository = passwordManagerRepository;
        this.serviceInstanceBindingRepository = serviceInstanceBindingRepository;
    }

    @Override
    public CreateServiceInstanceBindingResponse createServiceInstanceBinding(CreateServiceInstanceBindingRequest createServiceInstanceBindingRequest) {
        logger.info("Create service instance binding requested ...");
        return null;
    }

    @Override
    public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest deleteServiceInstanceBindingRequest) {
        logger.info("Delete service instance binding requested ...");
    }
}
