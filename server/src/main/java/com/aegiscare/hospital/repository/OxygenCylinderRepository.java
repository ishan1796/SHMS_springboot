package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.OxygenCylinder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OxygenCylinderRepository extends JpaRepository<OxygenCylinder, String> {
    List<OxygenCylinder> findByStatus(String status);
}