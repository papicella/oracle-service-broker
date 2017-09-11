package pas.au.pivotal.pcf.servicebroker.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pas.au.pivotal.pcf.servicebroker.oracle.model.PasswordManager;

public interface PasswordManagerRepository extends JpaRepository<PasswordManager, String> {
}
