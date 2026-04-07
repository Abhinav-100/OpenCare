package com.ciphertext.opencarebackend.modules.appointment.service.impl;

import com.ciphertext.opencarebackend.enums.AppointmentStatus;
import com.ciphertext.opencarebackend.enums.AppointmentType;
import com.ciphertext.opencarebackend.enums.DaysOfWeek;
import com.ciphertext.opencarebackend.enums.PaymentStatus;
import com.ciphertext.opencarebackend.enums.UserType;
import com.ciphertext.opencarebackend.exception.BadRequestException;
import com.ciphertext.opencarebackend.exception.ResourceNotFoundException;
import com.ciphertext.opencarebackend.modules.appointment.dto.filter.AppointmentFilter;
import com.ciphertext.opencarebackend.modules.appointment.repository.AppointmentRepository;
import com.ciphertext.opencarebackend.modules.appointment.service.AppointmentService;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorScheduleRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.DoctorWorkplaceRepository;
import com.ciphertext.opencarebackend.modules.provider.repository.HospitalRepository;
import com.ciphertext.opencarebackend.modules.shared.repository.specification.Filter;
import com.ciphertext.opencarebackend.modules.user.repository.ProfileRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.ciphertext.opencarebackend.entity.Appointment;
import com.ciphertext.opencarebackend.entity.Doctor;
import com.ciphertext.opencarebackend.entity.DoctorSchedule;
import com.ciphertext.opencarebackend.entity.DoctorWorkplace;
import com.ciphertext.opencarebackend.entity.Hospital;
import com.ciphertext.opencarebackend.entity.Profile;

import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryFilterUtils.generateIndividualFilter;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryFilterUtils.generateJoinTableFilter;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.QueryOperator.*;
import static com.ciphertext.opencarebackend.modules.shared.repository.specification.SpecificationBuilder.createSpecification;
import static org.springframework.data.jpa.domain.Specification.where;




@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final ProfileRepository profileRepository;
    private final HospitalRepository hospitalRepository;
    private final DoctorWorkplaceRepository doctorWorkplaceRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;

    private static final List<AppointmentStatus> CANCELLED_STATUSES = List.of(AppointmentStatus.CANCELLED, AppointmentStatus.NO_SHOW);
    private static final int DEFAULT_SLOT_DURATION = 30; // minutes

    @Override
    @Transactional(readOnly = true)
    public Page<Appointment> getPaginatedDataWithFilters(AppointmentFilter filter, Pageable pageable) {
        log.info("Fetching appointments with filters: {}", filter);
        Profile currentProfile = requireCurrentProfile();
        if (filter == null) {
            filter = AppointmentFilter.builder().build();
        }

        // Admins can query globally; non-admins are automatically scoped to own data.
        if (isAdmin(currentProfile, SecurityContextHolder.getContext().getAuthentication())) {
            return fetchAppointments(filter, pageable);
        }

        if (currentProfile.getUserType() == UserType.DOCTOR) {
            Doctor doctor = requireDoctorByKeycloakUserId(currentProfile.getKeycloakUserId());
            AppointmentFilter scopedFilter = buildScopedFilter(filter, doctor.getId(), null);
            return fetchAppointments(scopedFilter, pageable);
        }

        if (currentProfile.getUserType() == UserType.USER) {
            AppointmentFilter scopedFilter = buildScopedFilter(filter, null, currentProfile.getId());
            return fetchAppointments(scopedFilter, pageable);
        }

        throw new AccessDeniedException("You are not allowed to list appointments");
    }

    private Page<Appointment> fetchAppointments(AppointmentFilter filter, Pageable pageable) {
        List<Filter> filterList = generateQueryFilters(filter);
        Specification<Appointment> specification = where(null);
        if (!filterList.isEmpty()) {
            specification = where(createSpecification(filterList.removeFirst()));
            for (Filter input : filterList) {
                specification = specification.and(createSpecification(input));
            }
        }
        return appointmentRepository.findAll(specification, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Appointment getAppointmentById(Long id) {
        validatePositiveId(id, "Appointment");
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        authorizeAppointmentAccess(appointment);
        return appointment;
    }

    @Override
    @Transactional(readOnly = true)
    public Appointment getAppointmentByNumber(String appointmentNumber) {
        Appointment appointment = appointmentRepository.findByAppointmentNumber(appointmentNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with number: " + appointmentNumber));
        authorizeAppointmentAccess(appointment);
        return appointment;
    }

    @Override
    public Appointment createAppointment(Appointment appointment, String keycloakUserId) {
        if (appointment == null) {
            throw new BadRequestException("Appointment payload is required");
        }

        // Set patient profile from Keycloak user ID
        Profile patientProfile = profileRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));

        if (patientProfile.getUserType() != UserType.USER) {
            throw new AccessDeniedException("Only patient users can create appointments");
        }

        appointment.setPatientProfile(patientProfile);

        // Validation order matters: structure -> references -> business rules -> slot availability.
        validateRequiredFields(appointment);
        hydrateReferences(appointment);
        validateDoctorBookingEligibility(appointment);
        validateDoctorHospitalConsistency(appointment);

        // Validate slot availability
        if (!isSlotAvailable(appointment.getDoctor().getId(), appointment.getAppointmentDate(),
                appointment.getStartTime(), appointment.getEndTime())) {
            throw new BadRequestException("Selected time slot is not available");
        }

        // Generate readable booking ID shown to users/admin.
        appointment.setAppointmentNumber(generateAppointmentNumber());

        // Fill server-side defaults to keep API payload minimal.
        applyDefaults(appointment);

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment updateAppointment(Appointment appointment, Long id) {
        if (appointment == null) {
            throw new BadRequestException("Appointment payload is required");
        }
        Appointment existing = getAppointmentById(id);
        Profile actor = requireCurrentProfile();
        if (!isAdmin(actor, SecurityContextHolder.getContext().getAuthentication()) && actor.getUserType() != UserType.USER) {
            throw new AccessDeniedException("Only patients can update appointment details");
        }

        // Don't allow updating cancelled or completed appointments
        if (existing.getStatus() == AppointmentStatus.CANCELLED || existing.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Cannot update a " + existing.getStatus().name().toLowerCase() + " appointment");
        }

        Long incomingDoctorId = appointment.getDoctor() != null ? appointment.getDoctor().getId() : null;
        if (incomingDoctorId != null && !incomingDoctorId.equals(existing.getDoctor().getId())) {
            throw new BadRequestException("Changing appointment doctor is not supported");
        }

        hydrateReferences(appointment);

        // Update allowed fields
        existing.setAppointmentDate(appointment.getAppointmentDate());
        existing.setStartTime(appointment.getStartTime());
        existing.setEndTime(appointment.getEndTime());
        existing.setDurationMinutes(appointment.getDurationMinutes());
        existing.setAppointmentType(appointment.getAppointmentType());
        existing.setConsultationFee(appointment.getConsultationFee());
        existing.setHospital(appointment.getHospital());
        existing.setDoctorWorkplace(appointment.getDoctorWorkplace());
        existing.setMeetingLink(appointment.getMeetingLink());
        existing.setSymptoms(appointment.getSymptoms());
        existing.setNotes(appointment.getNotes());

        validateDoctorBookingEligibility(existing);
        validateDoctorHospitalConsistency(existing);

        return appointmentRepository.save(existing);
    }

    @Override
    public Appointment updateAppointmentStatus(Long id, AppointmentStatus status) {
        Appointment appointment = getAppointmentById(id);
        Profile actor = requireCurrentProfile();
        if (!isAdmin(actor, SecurityContextHolder.getContext().getAuthentication()) && actor.getUserType() != UserType.DOCTOR) {
            throw new AccessDeniedException("Only doctors or admins can update appointment status");
        }

        // Doctors have a narrower status transition set than admins.
        if (actor.getUserType() == UserType.DOCTOR
                && !EnumSet.of(AppointmentStatus.CONFIRMED, AppointmentStatus.COMPLETED, AppointmentStatus.NO_SHOW)
                .contains(status)) {
            throw new AccessDeniedException("Doctors can only set status to CONFIRMED, COMPLETED, or NO_SHOW");
        }

        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    @Override
    public void cancelAppointment(Long id, String reason, String cancelledBy) {
        Appointment appointment = getAppointmentById(id);
        Profile actor = requireCurrentProfile();
        if (!isAdmin(actor, SecurityContextHolder.getContext().getAuthentication())
                && actor.getUserType() != UserType.USER
                && actor.getUserType() != UserType.DOCTOR) {
            throw new AccessDeniedException("Only appointment owners or admins can cancel appointments");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BadRequestException("Appointment is already cancelled");
        }
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a completed appointment");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason);
        appointment.setCancelledBy(cancelledBy);
        appointment.setCancelledAt(LocalDateTime.now());
        appointmentRepository.save(appointment);
    }

    @Override
    public void deleteAppointmentById(Long id) {
        requireAdminRole();
        Appointment appointment = getAppointmentById(id);
        appointmentRepository.delete(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getMyAppointments(String keycloakUserId) {
        Profile currentProfile = profileRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));

        if (currentProfile.getUserType() == UserType.USER) {
            return appointmentRepository.findByPatientKeycloakUserId(keycloakUserId);
        }

        if (currentProfile.getUserType() == UserType.DOCTOR) {
            return appointmentRepository.findByDoctorKeycloakUserId(keycloakUserId);
        }

        if (isAdmin(currentProfile, SecurityContextHolder.getContext().getAuthentication())) {
            return appointmentRepository.findAll(Sort.by(Sort.Order.desc("appointmentDate"), Sort.Order.desc("startTime")));
        }

        throw new AccessDeniedException("You are not allowed to access personal appointments");
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailableSlot> getAvailableSlots(Long doctorId, LocalDate date) {
        validatePositiveId(doctorId, "Doctor");

        doctorRepository.findById(doctorId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        // 1) Pull doctor's configured schedule for the requested day.
        DaysOfWeek dayOfWeek = DaysOfWeek.valueOf(date.getDayOfWeek().name());
        List<DoctorSchedule> schedules = doctorScheduleRepository.findByDoctorWorkplace_Doctor_IdAndDaysOfWeek(doctorId, dayOfWeek);

        if (schedules.isEmpty()) {
            return List.of(); // Doctor not available on this day
        }

        // 2) Pull already-booked appointments to mark collisions.
        List<Appointment> existingAppointments = appointmentRepository.findActiveAppointmentsByDoctorAndDate(
                doctorId, date, CANCELLED_STATUSES);

        List<AvailableSlot> slots = new ArrayList<>();

        for (DoctorSchedule schedule : schedules) {
            LocalTime scheduleStart = schedule.getStartTime().toLocalTime();
            LocalTime scheduleEnd = schedule.getEndTime().toLocalTime();

            // 3) Split schedule windows into fixed-size slots.
            LocalTime slotStart = scheduleStart;
            while (slotStart.plusMinutes(DEFAULT_SLOT_DURATION).isBefore(scheduleEnd) ||
                   slotStart.plusMinutes(DEFAULT_SLOT_DURATION).equals(scheduleEnd)) {
                LocalTime slotEnd = slotStart.plusMinutes(DEFAULT_SLOT_DURATION);

                // 4) A slot is available only if not booked and not in the past.
                boolean isBooked = isSlotBooked(existingAppointments, slotStart, slotEnd);
                boolean isPast = date.equals(LocalDate.now()) && slotStart.isBefore(LocalTime.now());

                slots.add(new AvailableSlot(slotStart, slotEnd, !isBooked && !isPast));
                slotStart = slotEnd;
            }
        }

        return slots;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSlotAvailable(Long doctorId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return !appointmentRepository.existsOverlappingAppointment(doctorId, date, startTime, endTime, CANCELLED_STATUSES);
    }

    private boolean isSlotBooked(List<Appointment> appointments, LocalTime slotStart, LocalTime slotEnd) {
        for (Appointment apt : appointments) {
            // Check for overlap
            if (!(slotEnd.isBefore(apt.getStartTime()) || slotEnd.equals(apt.getStartTime()) ||
                  slotStart.isAfter(apt.getEndTime()) || slotStart.equals(apt.getEndTime()))) {
                return true;
            }
        }
        return false;
    }

    private List<Filter> generateQueryFilters(AppointmentFilter filter) {
        List<Filter> filters = new ArrayList<>();

        if (StringUtils.hasText(filter.getAppointmentNumber())) {
            filters.add(generateIndividualFilter("appointmentNumber", LIKE, filter.getAppointmentNumber()));
        }

        if (filter.getPatientProfileId() != null) {
            filters.add(generateJoinTableFilter("id", "patientProfile", JOIN, filter.getPatientProfileId()));
        }

        if (filter.getDoctorId() != null) {
            filters.add(generateJoinTableFilter("id", "doctor", JOIN, filter.getDoctorId()));
        }

        if (StringUtils.hasText(filter.getAppointmentType())) {
            filters.add(generateIndividualFilter("appointmentType", EQUALS, AppointmentType.valueOf(filter.getAppointmentType())));
        }

        if (filter.getAppointmentDateFrom() != null) {
            filters.add(generateIndividualFilter("appointmentDate", DATE_GREATER_THAN_EQUALS, filter.getAppointmentDateFrom()));
        }

        if (filter.getAppointmentDateTo() != null) {
            filters.add(generateIndividualFilter("appointmentDate", DATE_LESS_THAN_EQUALS, filter.getAppointmentDateTo()));
        }

        if (StringUtils.hasText(filter.getStatus())) {
            filters.add(generateIndividualFilter("status", EQUALS, AppointmentStatus.valueOf(filter.getStatus())));
        }

        if (StringUtils.hasText(filter.getPaymentStatus())) {
            filters.add(generateIndividualFilter("paymentStatus", EQUALS, PaymentStatus.valueOf(filter.getPaymentStatus())));
        }

        if (filter.getHospitalId() != null) {
            filters.add(generateJoinTableFilter("id", "hospital", JOIN, filter.getHospitalId()));
        }

        return filters;
    }

    private AppointmentFilter buildScopedFilter(AppointmentFilter baseFilter, Long doctorId, Long patientProfileId) {
        return AppointmentFilter.builder()
                .appointmentNumber(baseFilter.getAppointmentNumber())
                .patientProfileId(patientProfileId)
                .doctorId(doctorId)
                .appointmentType(baseFilter.getAppointmentType())
                .appointmentDateFrom(baseFilter.getAppointmentDateFrom())
                .appointmentDateTo(baseFilter.getAppointmentDateTo())
                .status(baseFilter.getStatus())
                .paymentStatus(baseFilter.getPaymentStatus())
                .hospitalId(baseFilter.getHospitalId())
                .doctorWorkplaceId(baseFilter.getDoctorWorkplaceId())
                .build();
    }

    private void hydrateReferences(Appointment appointment) {
        // MapStruct can create nested reference shells; drop empty shells to avoid transient entity errors.
        if (appointment.getHospital() != null && appointment.getHospital().getId() == null) {
            appointment.setHospital(null);
        }
        if (appointment.getDoctorWorkplace() != null && appointment.getDoctorWorkplace().getId() == null) {
            appointment.setDoctorWorkplace(null);
        }

        Long doctorId = appointment.getDoctor() != null ? appointment.getDoctor().getId() : null;
        Integer hospitalId = appointment.getHospital() != null ? appointment.getHospital().getId() : null;
        Long workplaceId = appointment.getDoctorWorkplace() != null ? appointment.getDoctorWorkplace().getId() : null;

        if (doctorId != null) {
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
            appointment.setDoctor(doctor);
        }

        if (hospitalId != null) {
            Hospital hospital = hospitalRepository.findById(hospitalId)
                    .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + hospitalId));
            appointment.setHospital(hospital);
        }

        if (workplaceId != null) {
            DoctorWorkplace workplace = doctorWorkplaceRepository.findById(workplaceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor workplace not found with id: " + workplaceId));
            appointment.setDoctorWorkplace(workplace);
        }
    }

    private void applyDefaults(Appointment appointment) {
        if (appointment.getStatus() == null) {
            appointment.setStatus(AppointmentStatus.PENDING);
        }
        if (appointment.getPaymentStatus() == null) {
            appointment.setPaymentStatus(PaymentStatus.PENDING);
        }
        if (appointment.getDurationMinutes() == null) {
            appointment.setDurationMinutes(DEFAULT_SLOT_DURATION);
        }
        if (appointment.getEndTime() == null && appointment.getStartTime() != null) {
            appointment.setEndTime(appointment.getStartTime().plusMinutes(appointment.getDurationMinutes()));
        }
        if (appointment.getReminderSent() == null) {
            appointment.setReminderSent(false);
        }
    }

    private void validateRequiredFields(Appointment appointment) {
        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null) {
            throw new BadRequestException("Doctor is required");
        }
        if (appointment.getAppointmentDate() == null) {
            throw new BadRequestException("Appointment date is required");
        }
        if (appointment.getStartTime() == null) {
            throw new BadRequestException("Start time is required");
        }
        if (appointment.getAppointmentType() == null) {
            throw new BadRequestException("Appointment type is required");
        }
        if (appointment.getConsultationFee() == null) {
            throw new BadRequestException("Consultation fee is required");
        }
    }

    private void validateDoctorBookingEligibility(Appointment appointment) {
        Doctor doctor = appointment.getDoctor();
        if (doctor == null) {
            return;
        }

        if (!Boolean.TRUE.equals(doctor.getIsActive())) {
            throw new BadRequestException("Doctor is not active for bookings");
        }

        if (!Boolean.TRUE.equals(doctor.getIsVerified())) {
            throw new BadRequestException("Doctor is not approved for bookings");
        }
    }

    private void validateDoctorHospitalConsistency(Appointment appointment) {
        Doctor doctor = appointment.getDoctor();
        if (doctor == null || doctor.getHospital() == null || doctor.getHospital().getId() == null) {
            return;
        }

        if (appointment.getHospital() == null || appointment.getHospital().getId() == null) {
            throw new BadRequestException("Appointment hospital is required for this doctor");
        }

        if (!doctor.getHospital().getId().equals(appointment.getHospital().getId())) {
            throw new BadRequestException("Appointment hospital must match doctor's hospital");
        }
    }

    private String generateAppointmentNumber() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "APT-" + uuid;
    }

    private void validatePositiveId(Long id, String name) {
        if (id == null || id <= 0) {
            throw new BadRequestException(name + " ID must be positive");
        }
    }

    private void authorizeAppointmentAccess(Appointment appointment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication is required");
        }

        String keycloakUserId = resolveCurrentUserId(authentication);
        if (!StringUtils.hasText(keycloakUserId)) {
            throw new AccessDeniedException("Unable to resolve authenticated user");
        }

        Profile currentProfile = profileRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new AccessDeniedException("Authenticated profile not found"));

        // Access matrix:
        // - admin/super-admin: full access
        // - doctor: only assigned appointments
        // - user(patient): only own appointments
        if (isAdmin(currentProfile, authentication)) {
            return;
        }

        if (currentProfile.getUserType() == UserType.DOCTOR) {
            String appointmentDoctorKeycloakId = getAppointmentDoctorKeycloakUserId(appointment);
            if (keycloakUserId.equals(appointmentDoctorKeycloakId)) {
                return;
            }
            throw new AccessDeniedException("Doctors can access only their assigned appointments");
        }

        if (currentProfile.getUserType() == UserType.USER) {
            Long currentProfileId = currentProfile.getId();
            Long appointmentPatientProfileId = appointment.getPatientProfile() != null ? appointment.getPatientProfile().getId() : null;
            if (currentProfileId != null && currentProfileId.equals(appointmentPatientProfileId)) {
                return;
            }
            throw new AccessDeniedException("Patients can access only their own appointments");
        }

        throw new AccessDeniedException("You are not allowed to access this appointment");
    }

    private boolean isAdmin(Profile profile, Authentication authentication) {
        if (profile.getUserType() == UserType.ADMIN || profile.getUserType() == UserType.SUPER_ADMIN) {
            return true;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(StringUtils::hasText)
                .map(value -> value.toLowerCase(java.util.Locale.ROOT))
                .anyMatch(authority -> authority.equals("admin")
                        || authority.equals("super-admin")
                        || authority.equals("role_admin")
                        || authority.equals("role_super_admin")
                        || authority.equals("role_super-admin"));
    }

    private String resolveCurrentUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
            return jwt.getSubject();
        }

        String authenticationName = authentication.getName();
        if (StringUtils.hasText(authenticationName)) {
            return authenticationName;
        }

        return null;
    }

    private String getAppointmentDoctorKeycloakUserId(Appointment appointment) {
        if (appointment.getDoctor() == null || appointment.getDoctor().getProfile() == null) {
            return null;
        }
        return appointment.getDoctor().getProfile().getKeycloakUserId();
    }

    private Profile requireCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication is required");
        }

        String keycloakUserId = resolveCurrentUserId(authentication);
        if (!StringUtils.hasText(keycloakUserId)) {
            throw new AccessDeniedException("Unable to resolve authenticated user");
        }

        return profileRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new AccessDeniedException("Authenticated profile not found"));
    }

    private Doctor requireDoctorByKeycloakUserId(String keycloakUserId) {
        return doctorRepository.findByProfileKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new AccessDeniedException("Doctor profile is not linked to a doctor account"));
    }

    private void requireAdminRole() {
        Profile currentProfile = requireCurrentProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAdmin(currentProfile, authentication)) {
            throw new AccessDeniedException("Only admins can perform this operation");
        }
    }
}