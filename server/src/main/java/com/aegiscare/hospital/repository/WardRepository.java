package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WardRepository extends JpaRepository<Ward, String> {
    Optional<Ward> findByCode(String code);
}
