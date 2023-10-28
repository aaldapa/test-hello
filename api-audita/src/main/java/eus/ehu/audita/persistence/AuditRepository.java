package eus.ehu.audita.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import eus.ehu.audita.model.AuditEntity;

@Repository
public interface AuditRepository extends JpaRepository<AuditEntity, String>, JpaSpecificationExecutor<AuditEntity>{

}
