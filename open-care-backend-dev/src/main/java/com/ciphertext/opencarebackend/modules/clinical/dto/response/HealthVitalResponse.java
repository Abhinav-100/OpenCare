package com.ciphertext.opencarebackend.modules.clinical.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class HealthVitalResponse {

    private Long id;
    private Long profileId;
    private LocalDateTime recordedAt;

    // Cardiovascular metrics
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private BigDecimal meanArterialPressure;
    private BigDecimal pulsePressure;
    private Integer heartRate;
    private BigDecimal heartRateVariability;
    private BigDecimal cardiacOutput;

    // Respiratory metrics
    private Integer respiratoryRate;
    private BigDecimal oxygenSaturation;
    private Integer peakFlowRate;
    private BigDecimal forcedVitalCapacity;
    private BigDecimal forcedExpiratoryVolume;

    // Temperature and metabolism
    private BigDecimal temperatureCelsius;
    private String temperatureSite;
    private Integer basalMetabolicRate;
    private Integer restingEnergyExpenditure;

    // Blood glucose and metabolic
    private BigDecimal bloodGlucoseMgDl;
    private BigDecimal bloodGlucoseMmolL;
    private String glucoseMeasurementType;
    private BigDecimal hoursSinceLastMeal;
    private BigDecimal ketones;

    // Body composition and anthropometry
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private BigDecimal bmi;
    private BigDecimal bodyFatPercentage;
    private BigDecimal muscleMassKg;
    private BigDecimal boneDensity;
    private BigDecimal waistCircumferenceCm;
    private BigDecimal hipCircumferenceCm;
    private BigDecimal waistHipRatio;
    private BigDecimal neckCircumferenceCm;
    private Integer visceralFatLevel;

    // Hydration and fluid balance
    private BigDecimal hydrationLevelPercentage;
    private Integer fluidIntakeMl24h;
    private Integer urineOutputMl24h;
    private BigDecimal urineSpecificGravity;

    // Pain and comfort assessment
    private Integer painScale0To10;
    private String painLocation;
    private String painType;

    // Sleep and activity
    private BigDecimal sleepHoursLastNight;
    private Integer sleepQuality1To5;
    private Integer steps24h;
    private Integer activeMinutes24h;
    private Integer caloriesBurned24h;

    // Mental health and stress
    private Integer stressLevel1To10;
    private Integer moodScore1To10;
    private Integer anxietyLevel1To10;

    // Additional biomarkers
    private BigDecimal cortisolLevel;
    private BigDecimal vitaminDLevel;
    private BigDecimal hemoglobinLevel;
    private BigDecimal whiteBloodCellCount;
    private Integer cholesterolTotal;
    private Integer cholesterolLdl;
    private Integer cholesterolHdl;
    private Integer triglycerides;

    // Measurement context and quality
    private String measurementMethod;
    private String measurementDevice;
    private String measurementLocation;
    private String measurementPosition;
    private String measurementAccuracy;
    private BigDecimal environmentalTemperature;
    private Integer environmentalHumidity;

    // Health status indicators
    private BigDecimal overallHealthScore;
    private BigDecimal cardiovascularRiskScore;
    private BigDecimal metabolicHealthScore;
    private String fitnessLevel;
    private String healthAlerts;

    // Medication and lifestyle context
    private String medicationsTaken;
    private Boolean recentExercise;
    private Boolean recentCaffeine;
    private Boolean recentAlcohol;
    private Boolean recentSmoking;
    private Integer menstrualCycleDay;

    // General notes and observations
    private String symptoms;
    private String notes;
    private Boolean followUpRequired;
    private String followUpPriority;

    // Audit fields
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

}