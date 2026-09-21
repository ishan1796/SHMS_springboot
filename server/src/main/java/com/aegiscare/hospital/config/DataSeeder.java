package com.aegiscare.hospital.config;

import com.aegiscare.hospital.entity.*;
import com.aegiscare.hospital.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private NurseRepository nurseRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private WardRepository wardRepository;
    @Autowired private BedRepository bedRepository;
    @Autowired private MedicineRepository medicineRepository;
    @Autowired private MedicineBatchRepository medicineBatchRepository;
    @Autowired private LabTestRepository labTestRepository;
    @Autowired private ChargeRepository chargeRepository;
    @Autowired private InventoryItemRepository inventoryItemRepository;
    @Autowired private OxygenCylinderRepository oxygenCylinderRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            System.out.println(">> Database already initialized. Skipping seed.");
            return;
        }

        System.out.println(">> Seeding initial AegisCare hospital database...");

        String encodedPassword = passwordEncoder.encode("password123");

        // 1. Departments
        Department cardio = createDept("Cardiology", "CARD", "Cardiology Department");
        Department genMed = createDept("General Medicine", "GENMED", "General Medicine Department");
        Department neuro = createDept("Neurology", "NEURO", "Neurology Department");
        Department emer = createDept("Emergency", "EMERG", "Emergency & Trauma Center");
        Department labDept = createDept("Laboratory", "LAB", "Central Pathology & Diagnostics");

        // 2. Demo Users (All 9 roles)
        User admin = createUser("admin@hospital.com", encodedPassword, "Arthur", "Pendelton", Role.ADMIN, "9876543210");
        User docUser = createUser("doctor@hospital.com", encodedPassword, "Sarah", "Lin", Role.DOCTOR, "9876543211");
        User nurseUser = createUser("nurse@hospital.com", encodedPassword, "Emily", "Clarke", Role.NURSE, "9876543212");
        User pharmUser = createUser("pharmacy@hospital.com", encodedPassword, "David", "Kim", Role.PHARMACY, "9876543213");
        User labUser = createUser("lab@hospital.com", encodedPassword, "Marcus", "Brody", Role.LAB, "9876543214");
        User finUser = createUser("finance@hospital.com", encodedPassword, "Rachel", "Zane", Role.FINANCE, "9876543215");
        User hrmsUser = createUser("hrms@hospital.com", encodedPassword, "Harvey", "Specter", Role.HRMS, "9876543216");
        User messUser = createUser("mess@hospital.com", encodedPassword, "Gordon", "Ramsay", Role.MESS, "9876543217");
        User patUser = createUser("patient@hospital.com", encodedPassword, "James", "Holden", Role.PATIENT, "9876543218");

        // Doctor Profile
        Doctor doctor = new Doctor();
        doctor.setUser(docUser);
        doctor.setDepartment(cardio);
        doctor.setSpecialization("Cardiologist");
        doctor.setLicenseNumber("DOC-MED-88912");
        doctor.setConsultationFee(1500.0);
        doctor.setRoomNumber("Room 304");
        doctorRepository.save(doctor);

        // Nurse Profile
        Nurse nurse = new Nurse();
        nurse.setUser(nurseUser);
        nurse.setDepartment(genMed);
        nurse.setLicenseNumber("NUR-REG-44321");
        nurseRepository.save(nurse);

        // 3. Patients
        Patient p1 = new Patient();
        p1.setUhid("UHID-2026-0001");
        p1.setUser(patUser);
        p1.setFirstName("James");
        p1.setLastName("Holden");
        p1.setGender("MALE");
        p1.setDob(LocalDate.of(1985, 4, 12));
        p1.setBloodGroup("O+");
        p1.setPhone("9876543218");
        p1.setEmail("patient@hospital.com");
        p1.setAddress("12 Ceres Way, Sector 4");
        p1.setAllergies("Penicillin");
        patientRepository.save(p1);

        Patient p2 = new Patient();
        p2.setUhid("UHID-2026-0002");
        p2.setFirstName("Eleanor");
        p2.setLastName("Vance");
        p2.setGender("FEMALE");
        p2.setDob(LocalDate.of(1992, 8, 23));
        p2.setBloodGroup("A+");
        p2.setPhone("9876543299");
        p2.setEmail("eleanor.vance@example.com");
        p2.setAddress("45 Rosewood Blvd, North Hills");
        patientRepository.save(p2);

        // 4. Wards & Beds
        Ward icu = new Ward();
        icu.setName("Intensive Care Unit (ICU)");
        icu.setCode("ICU-W");
        icu.setType("ICU");
        icu.setFloor(2);
        icu.setCapacity(10);
        icu.setActive(true);
        wardRepository.save(icu);

        Ward genWard = new Ward();
        genWard.setName("General Ward A");
        genWard.setCode("GW-A");
        genWard.setType("GENERAL");
        genWard.setFloor(1);
        genWard.setCapacity(20);
        genWard.setActive(true);
        wardRepository.save(genWard);

        for (int i = 1; i <= 5; i++) {
            Bed bed = new Bed();
            bed.setBedNumber("ICU-" + String.format("%02d", i));
            bed.setWard(icu);
            bed.setType("ICU");
            bed.setStatus("AVAILABLE");
            bed.setDailyRate(5000.0);
            bedRepository.save(bed);
        }

        for (int i = 1; i <= 10; i++) {
            Bed bed = new Bed();
            bed.setBedNumber("GW-A-" + String.format("%02d", i));
            bed.setWard(genWard);
            bed.setType("GENERAL");
            bed.setStatus("AVAILABLE");
            bed.setDailyRate(1200.0);
            bedRepository.save(bed);
        }

        // 5. Medicines & Batches
        Medicine paracetamol = new Medicine();
        paracetamol.setName("Paracetamol 500mg");
        paracetamol.setGenericName("Acetaminophen");
        paracetamol.setForm("TABLET");
        paracetamol.setStrength("500mg");
        paracetamol.setCategory("ANALGESIC");
        paracetamol.setUnitPrice(3.0);
        paracetamol.setStockQuantity(500);
        paracetamol.setReorderLevel(100);
        medicineRepository.save(paracetamol);

        MedicineBatch b1 = new MedicineBatch();
        b1.setMedicine(paracetamol);
        b1.setBatchNumber("BATCH-PCM-2026");
        b1.setExpiryDate(LocalDate.of(2027, 12, 31));
        b1.setQuantity(500);
        b1.setPurchasePrice(1.50);
        b1.setSellingPrice(3.00);
        medicineBatchRepository.save(b1);

        Medicine amox = new Medicine();
        amox.setName("Amoxicillin 500mg");
        amox.setGenericName("Amoxicillin Trihydrate");
        amox.setForm("CAPSULE");
        amox.setStrength("500mg");
        amox.setCategory("ANTIBIOTIC");
        amox.setUnitPrice(15.0);
        amox.setStockQuantity(200);
        amox.setReorderLevel(50);
        medicineRepository.save(amox);

        MedicineBatch b2 = new MedicineBatch();
        b2.setMedicine(amox);
        b2.setBatchNumber("BATCH-AMX-2026");
        b2.setExpiryDate(LocalDate.of(2027, 8, 15));
        b2.setQuantity(200);
        b2.setPurchasePrice(8.00);
        b2.setSellingPrice(15.00);
        medicineBatchRepository.save(b2);

        // 6. Lab Tests
        createLabTest("Complete Blood Count (CBC)", "CBC-01", "HEMATOLOGY", 450.0, "EDTA Whole Blood", "4.5 - 11.0 x10^9 / L");
        createLabTest("Lipid Profile Panel", "LIP-01", "BIOCHEMISTRY", 850.0, "Serum", "Cholesterol < 200 mg/dL");
        createLabTest("Fasting Blood Sugar (FBS)", "FBS-01", "BIOCHEMISTRY", 200.0, "Fluoride Plasma", "70 - 99 mg/dL");
        createLabTest("Chest X-Ray PA View", "RAD-CXR", "RADIOLOGY", 750.0, "Digital Radiograph", "Clear lung fields");

        // 7. Charges
        createCharge(p1, "OPD", "General Doctor OPD Consultation", 1, 500.0);
        createCharge(p1, "EMERGENCY", "Trauma Triage & Stabilization", 1, 2500.0);
        createCharge(p1, "IPD", "ICU Daily Monitoring Charge", 1, 5000.0);

        // 8. Inventory items & Oxygen cylinders
        InventoryItem item1 = new InventoryItem();
        item1.setName("Sterile Surgical Gloves (M)");
        item1.setCode("INV-GLV-001");
        item1.setCategory("CONSUMABLE");
        item1.setCurrentStock(450);
        item1.setMinStockLevel(100);
        item1.setUnit("PAIRS");
        inventoryItemRepository.save(item1);

        OxygenCylinder cyl1 = new OxygenCylinder();
        cyl1.setCylinderCode("OXY-CYL-01");
        cyl1.setType("TYPE_D");
        cyl1.setCapacityLiters(1500);
        cyl1.setCurrentPressureBar(148.5);
        cyl1.setStatus("AVAILABLE");
        cyl1.setCurrentLocation("ICU Storage Bay 1");
        oxygenCylinderRepository.save(cyl1);

        System.out.println(">> AegisCare demo database successfully seeded with all 9 roles and reference data!");
    }

    private Department createDept(String name, String code, String desc) {
        Department d = new Department();
        d.setName(name);
        d.setCode(code);
        d.setDescription(desc);
        return departmentRepository.save(d);
    }

    private User createUser(String email, String pwd, String fn, String ln, Role role, String phone) {
        User u = new User();
        u.setEmail(email);
        u.setPasswordHash(pwd);
        u.setFirstName(fn);
        u.setLastName(ln);
        u.setRole(role);
        u.setPhone(phone);
        u.setActive(true);
        return userRepository.save(u);
    }

    private void createLabTest(String name, String code, String category, Double price, String unit, String normalRange) {
        LabTest t = new LabTest();
        t.setName(name);
        t.setCode(code);
        t.setCategory(category);
        t.setPrice(price);
        t.setUnit(unit);
        t.setNormalRange(normalRange);
        t.setActive(true);
        labTestRepository.save(t);
    }

    private void createCharge(Patient patient, String sourceModule, String serviceName, int qty, Double price) {
        Charge c = new Charge();
        c.setPatient(patient);
        c.setSourceModule(sourceModule);
        c.setServiceName(serviceName);
        c.setQuantity(qty);
        c.setUnitPrice(price);
        c.setTotalAmount(qty * price);
        c.setStatus("UNBILLED");
        chargeRepository.save(c);
    }
}