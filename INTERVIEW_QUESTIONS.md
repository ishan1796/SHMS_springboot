# Technical Interview Guide: AI-Integrated Smart Hospital Management System
**Tech Stack**: React.js, Java, Spring Boot 3, PostgreSQL, Spring Data JPA, JWT Authentication, LLM API (Google Gemini)

---

## 1. Architecture & System Design Questions

### Q1: Can you walk me through the end-to-end architecture of your application?
**Expected Answer:**
- **Architecture Style**: 5-tier Enterprise Monolithic Architecture designed for high cohesion and low coupling.
- **Tiers**:
  1. **Presentation Layer**: React.js SPA (Vite, Tailwind CSS, Lucide icons, Axios/Fetch) running on client devices.
  2. **Security & Interceptor Layer**: `JwtAuthenticationFilter` intercepts incoming requests, parses and validates Bearer tokens using JJWT, and populates the `SecurityContextHolder`.
  3. **Controller Layer**: 13 `@RestController` classes handling HTTP verbs, request parsing, and input validation (`@Valid`).
  4. **Service Layer**: 13 `@Service` classes implementing domain business logic, transactional boundaries (`@Transactional`), audit logging, and LLM orchestration.
  5. **Data Access Layer**: 36 Spring Data JPA repositories interfacing with Hibernate ORM over PostgreSQL (with H2 in-memory mode for development).

### Q2: Why did you choose a monolithic architecture over microservices for this project?
**Expected Answer:**
- **Domain Transactional Integrity**: In hospital management, workflows like billing, pharmacy inventory decrement, and bed allotment require ACID compliance. A monolithic architecture allows atomic database transactions (`@Transactional`) without needing distributed transaction protocols (e.g., Two-Phase Commit or Saga pattern).
- **Lower Operational Overhead**: Single artifact deployment, reduced latency (no inter-service network hops), and simplified debugging.
- **Domain Boundaries**: Well-defined module boundaries (Clinical, Nursing, Billing, HRMS, Pharmacy) keep the codebase modular, allowing future extraction into microservices if scaling demands it.

---

## 2. Java, Spring Boot & Backend Deep-Dive Questions

### Q3: How did you implement Role-Based Access Control (RBAC) with Spring Security and JWT?
**Expected Answer:**
- Created custom `JwtAuthenticationFilter` extending `OncePerRequestFilter`.
- Extracted JWT claims (subject, role, userId, doctorId/nurseId).
- Configured `SecurityFilterChain` in `SecurityConfig.java` with stateless session management (`SessionCreationPolicy.STATELESS`).
- Applied fine-grained authorization using `.requestMatchers("/api/admin/**").hasRole("ADMIN")` or method-level security with `@PreAuthorize("hasRole('DOCTOR')")`.
- Passwords are encrypted using Spring Security's `BCryptPasswordEncoder` with salted hashing.

### Q4: How does Spring Data JPA handle entity relationships and avoid the N+1 Query Problem?
**Expected Answer:**
- Used `@ManyToOne(fetch = FetchType.LAZY)` and `@OneToMany(mappedBy = "...", fetch = FetchType.LAZY)` to avoid loading unwanted relation trees into memory.
- To prevent the **N+1 problem**, written custom JPQL queries with `JOIN FETCH` (e.g., fetching `Encounter` along with its `Doctor` and `Patient` in a single query).
- Used `@JsonIgnore` / `@JsonIgnoreProperties` to break bidirectional circular references between parent-child entities (e.g., `Ward` -> `Bed` -> `Ward`) during Jackson JSON serialization.

### Q5: How did you handle the automated data retention policy (deleting medical reports after 3 months)?
**Expected Answer:**
- **Date Calculation**: Used Java's `java.time.LocalDateTime.now().minusMonths(3)` to compute the cutoff threshold timestamp.
- **Scheduled Execution**: Enabled `@EnableScheduling` and created `@Scheduled(cron = "0 0 2 * * ?")` in `MedicalReportCleanupService` to run every night at 2:00 AM.
- **Database Optimization**: Implemented a bulk delete query in `LabResultRepository`:
  ```java
  @Modifying
  @Transactional
  @Query("DELETE FROM LabResult lr WHERE lr.verifiedAt < :cutoffDate")
  int deleteByVerifiedAtBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
  ```
  This performs a single direct SQL `DELETE` query rather than loading individual entities into memory.

### Q6: How are database transactions managed during critical actions like Pharmacy Dispensing or Patient Billing?
**Expected Answer:**
- Annotated service methods with Spring's `@Transactional`.
- In Pharmacy Dispensing:
  1. Validates batch stock quantity.
  2. Decrements batch inventory.
  3. Creates `PharmacyDispensing` and `PharmacyDispensingItem` records.
  4. Automatically emits an unbilled `Charge` for the Finance module.
- If any step fails (e.g., insufficient stock or DB constraint violation), the entire transaction rolls back, preventing partial inventory deductions or phantom charges.

---

## 3. LLM API & AI Integration Questions

### Q7: How does your system integrate LLMs (Google Gemini) with relational database workflows?
**Expected Answer:**
- **Context Extraction**: The backend fetches real-time structured data from PostgreSQL (e.g., total unbilled charges, clinical diagnoses, ICU occupancy, or payroll liabilities).
- **Prompt Engineering**: The structured data is aggregated into domain-specific system prompts (e.g., Doctor Briefing Prompt, CFO Financial Risk Prompt, HRMS Liability Prompt).
- **LLM Orchestration**: Sent via REST payload to the Gemini API (`gemini-2.5-flash`), receiving Markdown-structured summaries and risk alerts.
- **On-Demand Execution**: Designed briefings as user-initiated on-demand actions (clicks) to prevent unnecessary token consumption and stay within API rate limits.

### Q8: What security and cost-control considerations did you implement for the LLM integration?
**Expected Answer:**
- **Secret Isolation**: Injected the API key via system environment variables (`GEMINI_API_KEY`) and Spring `@Value("${app.gemini.api-key}")`, ensuring zero hardcoded credentials in version control.
- **PII Protection**: Aggregated telemetry data before prompting the model to minimize transmission of sensitive identifiable patient information.
- **Fallback / Resilience**: Wrapped LLM HTTP calls with `try-catch` blocks returning structured fallbacks so third-party API downtime never breaks core hospital clinical operations.

---

## 4. PostgreSQL & Database Design Questions

### Q9: How did you design the schema to support 9 different hospital roles and diverse domain entities?
**Expected Answer:**
- Normalized relational schema with 40 JPA Entities.
- Centralized `User` table for authentication, linked 1-to-1 to role profiles (`Doctor`, `Nurse`, `Employee`, `Patient`).
- Separation of clinical workflows (`Encounter`, `Diagnosis`, `Vital`, `Prescription`, `LabOrder`) from administrative workflows (`BedAssignment`, `MealFulfillment`, `Attendance`, `PayrollRecord`, `Invoice`).
- Used UUIDs (`@GeneratedValue(strategy = GenerationType.UUID)`) for primary keys to ensure non-guessable, collision-resistant identifiers across distributed environments.

### Q10: How did you handle SQL reserved keyword conflicts in JPA entity mappings?
**Expected Answer:**
- Attributes like `month`, `year`, and `value` are reserved keywords in SQL/PostgreSQL.
- Used explicit JPA `@Column(name = "...")` mappings (e.g., `@Column(name = "payroll_month")`, `@Column(name = "payroll_year")`, `@Column(name = "discount_value")`) to prevent SQL syntax errors across different database engines.

---

## 5. React.js Frontend Questions

### Q11: How does the React frontend maintain state and handle role-based navigation?
**Expected Answer:**
- **Auth State Management**: React Context (`AuthContext`) stores the JWT token, decoded user role, and user profile in `localStorage` / memory.
- **Protected Routes**: Custom Route Guards (`ProtectedRoute`) inspect user roles before rendering views, redirecting unauthorized users to login.
- **Dynamic Dashboards**: A unified layout renders role-specific sidebars, widgets, and statistics based on the authenticated user's permissions (`ADMIN`, `DOCTOR`, `NURSE`, `PHARMACY`, `LAB`, `FINANCE`, `HRMS`, `MESS`, `PATIENT`).

### Q12: How do you handle API communication and token expiration in the client?
**Expected Answer:**
- Centralized API service with request interceptors attaching the `Authorization: Bearer <token>` header to all outgoing requests.
- Response interceptors inspect HTTP status codes: on `401 Unauthorized` or `403 Forbidden`, the client automatically clears stored session data and redirects the user to the login screen.
