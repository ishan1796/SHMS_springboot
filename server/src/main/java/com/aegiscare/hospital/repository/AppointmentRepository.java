package com.aegiscare.hospital.repository;

import com.aegiscare.hospital.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {
    List<Appointment> findByPatientIdOrderByAppointmentDateDesc(String patientId);
    List<Appointment> findByDoctorIdOrderByAppointmentDateDesc(String doctorId);
    List<Appointment> findByDoctorId(String doctorId);
    List<Appointment> findByPatientId(String patientId);
    List<Appointment> findByAppointmentDate(LocalDate date);
    List<Appointment> findByDoctorIdAndAppointmentDate(String doctorId, LocalDate date);
    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusNot(String doctorId, LocalDate date, String status);
    boolean existsByDoctorIdAndAppointmentDateAndTimeSlotAndStatusNot(String doctorId, LocalDate date, String timeSlot, String status);
    long countByAppointmentDate(LocalDate date);
}