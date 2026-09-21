package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.Bed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BedRepository extends JpaRepository<Bed, String> {
    List<Bed> findByWardId(String wardId);
    List<Bed> findByStatus(String status);
    long countByStatus(String status);
    Optional<Bed> findByBedNumber(String bedNumber);
}
