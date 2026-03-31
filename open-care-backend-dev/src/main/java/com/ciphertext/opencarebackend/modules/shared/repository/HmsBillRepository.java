package com.ciphertext.opencarebackend.modules.shared.repository;

import com.ciphertext.opencarebackend.modules.shared.entity.HmsBillEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HmsBillRepository extends JpaRepository<HmsBillEntity, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select b from HmsBillEntity b where b.id = :id")
	Optional<HmsBillEntity> findByIdForUpdate(@Param("id") Long id);
}