package pas.au.pivotal.pcf.servicebroker.oracle.service;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.model.*;
import pas.au.pivotal.pcf.servicebroker.oracle.model.PasswordManager;
import pas.au.pivotal.pcf.servicebroker.oracle.model.ServiceInstance;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Service;
import pas.au.pivotal.pcf.servicebroker.oracle.repository.PasswordManagerRepository;
import pas.au.pivotal.pcf.servicebroker.oracle.repository.ServiceInstanceRepository;

@Service
public class OracleServiceInstanceService implements ServiceInstanceService
{
    private Logger logger = LoggerFactory.getLogger(OracleServiceInstanceService.class);

    private ServiceInstanceRepository serviceInstanceRepository;
    private PasswordManagerRepository passwordManagerRepository;
    private OracleManager oracleManager;

    @Autowired
    public OracleServiceInstanceService(ServiceInstanceRepository serviceInstanceRepository, PasswordManagerRepository passwordManagerRepository, OracleManager oracleManager) {
        this.serviceInstanceRepository = serviceInstanceRepository;
        this.passwordManagerRepository = passwordManagerRepository;
        this.oracleManager = oracleManager;
    }

    @Override
    public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest createServiceInstanceRequest) {
        logger.info("Create service instance requested ...");

        ServiceInstance instance = serviceInstanceRepository.findOne(createServiceInstanceRequest.getServiceInstanceId());

        if (instance != null) {
            throw new ServiceInstanceExistsException
                    (createServiceInstanceRequest.getServiceInstanceId(), createServiceInstanceRequest.getServiceDefinitionId());
        }

        instance = new ServiceInstance(createServiceInstanceRequest);

        String password = RandomStringUtils.randomAlphanumeric(30);
        String username = "CF_" + instance.getServiceInstanceId().replace('-', '_').substring(1, 27);

        try
        {
            oracleManager.createUserForService(username, password);
        }
        catch (Exception ex)
        {
            throw new ServiceBrokerException("Failed to create new Oracle User: " + createServiceInstanceRequest.getServiceInstanceId());
        }

        serviceInstanceRepository.save(instance);
        passwordManagerRepository.save(new PasswordManager(instance.getServiceInstanceId(), password));

        return new CreateServiceInstanceResponse();
    }

    @Override
    public GetLastServiceOperationResponse getLastOperation(GetLastServiceOperationRequest getLastServiceOperationRequest) {
        return new GetLastServiceOperationResponse().withOperationState(OperationState.SUCCEEDED);
    }

    @Override
    public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest deleteServiceInstanceRequest) {
        logger.info("Delete service instance requested ...");

        String instanceId = deleteServiceInstanceRequest.getServiceInstanceId();
        ServiceInstance instance = serviceInstanceRepository.findOne(instanceId);
        if (instance == null) {
            throw new ServiceInstanceDoesNotExistException(instanceId);
        }

        String username = "CF_" + instance.getServiceInstanceId().replace('-', '_').substring(1, 27);

        try {
            oracleManager.deleteUser(username);
            serviceInstanceRepository.delete(instanceId);
            passwordManagerRepository.delete(instanceId);
        }
        catch (Exception ex)
        {
            throw new ServiceBrokerException("Failed to delete Oracle User: " + username);
        }

        return new DeleteServiceInstanceResponse();
    }

    @Override
    public UpdateServiceInstanceResponse updateServiceInstance(UpdateServiceInstanceRequest updateServiceInstanceRequest) {
        logger.info("Update service instance requested ...");

        String instanceId = updateServiceInstanceRequest.getServiceInstanceId();
        ServiceInstance instance = serviceInstanceRepository.findOne(instanceId);
        if (instance == null) {
            throw new ServiceInstanceDoesNotExistException(instanceId);
        }

        serviceInstanceRepository.delete(instanceId);
        ServiceInstance updatedInstance = new ServiceInstance(updateServiceInstanceRequest);
        serviceInstanceRepository.save(updatedInstance);

        return new UpdateServiceInstanceResponse();
    }
}
