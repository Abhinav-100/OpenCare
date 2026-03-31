package com.ciphertext.opencarebackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "health_vital")
public class HealthVital extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    // Cardiovascular metrics
    @Column(name = "blood_pressure_systolic")
    private Integer bloodPressureSystolic;

    @Column(name = "blood_pressure_diastolic")
    private Integer bloodPressureDiastolic;

    @Column(name = "mean_arterial_pressure")
    private BigDecimal meanArterialPressure;

    @Column(name = "pulse_pressure")
    private BigDecimal pulsePressure;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "heart_rate_variability")
    private BigDecimal heartRateVariability;

    @Column(name = "cardiac_output")
    private BigDecimal cardiacOutput;

    // Respiratory metrics
    @Column(name = "respiratory_rate")
    private Integer respiratoryRate;

    @Column(name = "oxygen_saturation")
    private BigDecimal oxygenSaturation;

    @Column(name = "peak_flow_rate")
    private Integer peakFlowRate;

    @Column(name = "forced_vital_capacity")
    private BigDecimal forcedVitalCapacity;

    @Column(name = "forced_expiratory_volume")
    private BigDecimal forcedExpiratoryVolume;

    // Temperature and metabolism
    @Column(name = "temperature_celsius")
    private BigDecimal temperatureCelsius;

    @Column(name = "temperature_site")
    private String temperatureSite;

    @Column(name = "basal_metabolic_rate")
    private Integer basalMetabolicRate;

    @Column(name = "resting_energy_expenditure")
    private Integer restingEnergyExpenditure;

    // Blood glucose and metabolic
    @Column(name = "blood_glucose_mg_dl")
    private BigDecimal bloodGlucoseMgDl;

    @Column(name = "blood_glucose_mmol_l")
    private BigDecimal bloodGlucoseMmolL;

    @Column(name = "glucose_measurement_type")
    private String glucoseMeasurementType;

    @Column(name = "hours_since_last_meal")
    private BigDecimal hoursSinceLastMeal;

    @Column(name = "ketones")
    private BigDecimal ketones;

    // Body composition and anthropometry
    @Column(name = "height_cm")
    private BigDecimal heightCm;

    @Column(name = "weight_kg")
    private BigDecimal weightKg;

    @Column(name = "bmi")
    private BigDecimal bmi;

    @Column(name = "body_fat_percentage")
    private BigDecimal bodyFatPercentage;

    @Column(name = "muscle_mass_kg")
    private BigDecimal muscleMassKg;

    @Column(name = "bone_density")
    private BigDecimal boneDensity;

    @Column(name = "waist_circumference_cm")
    private BigDecimal waistCircumferenceCm;

    @Column(name = "hip_circumference_cm")
    private BigDecimal hipCircumferenceCm;

    @Column(name = "waist_hip_ratio")
    private BigDecimal waistHipRatio;

    @Column(name = "neck_circumference_cm")
    private BigDecimal neckCircumferenceCm;

    @Column(name = "visceral_fat_level")
    private Integer visceralFatLevel;

    // Hydration and fluid balance
    @Column(name = "hydration_level_percentage")
    private BigDecimal hydrationLevelPercentage;

    @Column(name = "fluid_intake_ml_24h")
    private Integer fluidIntakeMl24h;

    @Column(name = "urine_output_ml_24h")
    private Integer urineOutputMl24h;

    @Column(name = "urine_specific_gravity")
    private BigDecimal urineSpecificGravity;

    // Pain and comfort assessment
    @Column(name = "pain_scale_0_10")
    private Integer painScale0To10;

    @Column(name = "pain_location")
    private String painLocation;

    @Column(name = "pain_type")
    private String painType;

    // Sleep and activity
    @Column(name = "sleep_hours_last_night")
    private BigDecimal sleepHoursLastNight;

    @Column(name = "sleep_quality_1_5")
    private Integer sleepQuality1To5;

    @Column(name = "steps_24h")
    private Integer steps24h;

    @Column(name = "active_minutes_24h")
    private Integer activeMinutes24h;

    @Column(name = "calories_burned_24h")
    private Integer caloriesBurned24h;

    // Mental health and stress
    @Column(name = "stress_level_1_10")
    private Integer stressLevel1To10;

    @Column(name = "mood_score_1_10")
    private Integer moodScore1To10;

    @Column(name = "anxiety_level_1_10")
    private Integer anxietyLevel1To10;

    // Additional biomarkers
    @Column(name = "cortisol_level")
    private BigDecimal cortisolLevel;

    @Column(name = "vitamin_d_level")
    private BigDecimal vitaminDLevel;

    @Column(name = "hemoglobin_level")
    private BigDecimal hemoglobinLevel;

    @Column(name = "white_blood_cell_count")
    private BigDecimal whiteBloodCellCount;

    @Column(name = "cholesterol_total")
    private Integer cholesterolTotal;

    @Column(name = "cholesterol_ldl")
    private Integer cholesterolLdl;

    @Column(name = "cholesterol_hdl")
    private Integer cholesterolHdl;

    @Column(name = "triglycerides")
    private Integer triglycerides;

    // Measurement context and quality
    @Column(name = "measurement_method")
    private String measurementMethod;

    @Column(name = "measurement_device")
    private String measurementDevice;

    @Column(name = "measurement_location")
    private String measurementLocation;

    @Column(name = "measurement_position")
    private String measurementPosition;

    @Column(name = "measurement_accuracy")
    private String measurementAccuracy;

    @Column(name = "environmental_temperature")
    private BigDecimal environmentalTemperature;

    @Column(name = "environmental_humidity")
    private Integer environmentalHumidity;

    // Health status indicators
    @Column(name = "overall_health_score")
    private BigDecimal overallHealthScore;

    @Column(name = "cardiovascular_risk_score")
    private BigDecimal cardiovascularRiskScore;

    @Column(name = "metabolic_health_score")
    private BigDecimal metabolicHealthScore;

    @Column(name = "fitness_level")
    private String fitnessLevel;

    @Column(name = "health_alerts", columnDefinition = "TEXT")
    private String healthAlerts;

    // Medication and lifestyle context
    @Column(name = "medications_taken", columnDefinition = "TEXT")
    private String medicationsTaken;

    @Column(name = "recent_exercise")
    private Boolean recentExercise;

    @Column(name = "recent_caffeine")
    private Boolean recentCaffeine;

    @Column(name = "recent_alcohol")
    private Boolean recentAlcohol;

    @Column(name = "recent_smoking")
    private Boolean recentSmoking;

    @Column(name = "menstrual_cycle_day")
    private Integer menstrualCycleDay;

    // General notes and observations
    @Column(name = "symptoms")
    private String symptoms;

    @Column(name = "notes")
    private String notes;

    @Column(name = "follow_up_required")
    private Boolean followUpRequired = false;

    @Column(name = "follow_up_priority")
    private String followUpPriority;
}
