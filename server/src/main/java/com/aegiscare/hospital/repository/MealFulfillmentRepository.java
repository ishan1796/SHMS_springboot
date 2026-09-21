package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.MealFulfillment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealFulfillmentRepository extends JpaRepository<MealFulfillment, String> {
    List<MealFulfillment> findByMealDate(LocalDate date);
    List<MealFulfillment> findByMealDateOrderByBedNumberAsc(LocalDate date);
    List<MealFulfillment> findByDietPlanId(String dietPlanId);
}