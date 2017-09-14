package pas.au.pivotal.pcf.servicebroker.oracle.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.stereotype.Service;
import pas.au.pivotal.pcf.servicebroker.oracle.model.PasswordManager;
import pas.au.pivotal.pcf.servicebroker.oracle.model.ServiceInstanceBinding;
import pas.au.pivotal.pcf.servicebroker.oracle.repository.PasswordManagerRepository;
import pas.au.pivotal.pcf.servicebroker.oracle.repository.ServiceInstanceBindingRepository;

import java.util.HashMap;
import java.util.Map;

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

        String bindingId = createServiceInstanceBindingRequest.getBindingId();
        String serviceInstanceId = createServiceInstanceBindingRequest.getServiceInstanceId();

        ServiceInstanceBinding binding = serviceInstanceBindingRepository.findOne(bindingId);

        if (binding != null) {
            throw new ServiceInstanceBindingExistsException(serviceInstanceId, bindingId);
        }

        PasswordManager passwordManager = passwordManagerRepository.findOne(serviceInstanceId);
        String username = "CF_" + serviceInstanceId.replace('-', '_').substring(1, 27);
        String uriFormat = "oracle://%s:%s@%s";
        int index = oracleManager.getJdbcUrl().indexOf('@');

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("jdbcUrl", oracleManager.getJdbcUrl());
        credentials.put("username", username);
        credentials.put("password", passwordManager.getPassword());

        credentials.put("uri",
                        String.format(uriFormat,
                                      username,
                                      passwordManager.getPassword(),
                                      oracleManager.getJdbcUrl().substring(index + 3)));

        return new CreateServiceInstanceAppBindingResponse().withCredentials(credentials);

    }

    @Override
    public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest deleteServiceInstanceBindingRequest) {
        logger.info("Delete service instance binding requested ...");

        String bindingId = deleteServiceInstanceBindingRequest.getBindingId();
        ServiceInstanceBinding binding = getServiceInstanceBinding(bindingId);

        if (binding == null) {
            throw new ServiceInstanceBindingDoesNotExistException(bindingId);
        }

        serviceInstanceBindingRepository.delete(bindingId);
    }

    protected ServiceInstanceBinding getServiceInstanceBinding(String id) {
        return serviceInstanceBindingRepository.findOne(id);
    }
}
