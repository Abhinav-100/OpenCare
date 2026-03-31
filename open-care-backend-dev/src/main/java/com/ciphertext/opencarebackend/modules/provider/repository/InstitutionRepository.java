package com.ciphertext.opencarebackend.modules.provider.repository;

import com.ciphertext.opencarebackend.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Sadman
 */
@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Integer>, JpaSpecificationExecutor<Institution> {
    Optional<Institution> findByName(String name);

}