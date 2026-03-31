package com.ciphertext.opencarebackend.modules.clinical.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface LatestHealthVitalProjection {

    // Base fields
    Long getId();
    Long getProfileId();
    LocalDateTime getRecordedAt();

    // Cardiovascular metrics
    Integer getBloodPressureSystolic();
    Integer getBloodPressureDiastolic();
    BigDecimal getMeanArterialPressure();
    BigDecimal getPulsePressure();
    Integer getHeartRate();
    BigDecimal getHeartRateVariability();
    BigDecimal getCardiacOutput();

    // Respiratory metrics
    Integer getRespiratoryRate();
    BigDecimal getOxygenSaturation();
    Integer getPeakFlowRate();
    BigDecimal getForcedVitalCapacity();
    BigDecimal getForcedExpiratoryVolume();

    // Temperature and metabolism
    BigDecimal getTemperatureCelsius();
    String getTemperatureSite();
    Integer getBasalMetabolicRate();
    Integer getRestingEnergyExpenditure();

    // Blood glucose and metabolic
    BigDecimal getBloodGlucoseMgDl();
    BigDecimal getBloodGlucoseMmolL();
    String getGlucoseMeasurementType();
    BigDecimal getHoursSinceLastMeal();
    BigDecimal getKetones();

    // Body composition and anthropometry
    BigDecimal getHeightCm();
    BigDecimal getWeightKg();
    BigDecimal getBmi();
    BigDecimal getBodyFatPercentage();
    BigDecimal getMuscleMassKg();
    BigDecimal getBoneDensity();
    BigDecimal getWaistCircumferenceCm();
    BigDecimal getHipCircumferenceCm();
    BigDecimal getWaistHipRatio();
    BigDecimal getNeckCircumferenceCm();
    Integer getVisceralFatLevel();

    // Hydration and fluid balance
    BigDecimal getHydrationLevelPercentage();
    Integer getFluidIntakeMl24h();
    Integer getUrineOutputMl24h();
    BigDecimal getUrineSpecificGravity();

    // Pain and comfort assessment
    Integer getPainScale0To10();
    String getPainLocation();
    String getPainType();

    // Sleep and activity
    BigDecimal getSleepHoursLastNight();
    Integer getSleepQuality1To5();
    Integer getSteps24h();
    Integer getActiveMinutes24h();
    Integer getCaloriesBurned24h();

    // Mental health and stress
    Integer getStressLevel1To10();
    Integer getMoodScore1To10();
    Integer getAnxietyLevel1To10();

    // Additional biomarkers
    BigDecimal getCortisolLevel();
    BigDecimal getVitaminDLevel();
    BigDecimal getHemoglobinLevel();
    BigDecimal getWhiteBloodCellCount();
    Integer getCholesterolTotal();
    Integer getCholesterolLdl();
    Integer getCholesterolHdl();
    Integer getTriglycerides();

    // Measurement context and quality
    String getMeasurementMethod();
    String getMeasurementDevice();
    String getMeasurementLocation();
    String getMeasurementPosition();
    String getMeasurementAccuracy();
    BigDecimal getEnvironmentalTemperature();
    Integer getEnvironmentalHumidity();

    // Health status indicators
    BigDecimal getOverallHealthScore();
    BigDecimal getCardiovascularRiskScore();
    BigDecimal getMetabolicHealthScore();
    String getFitnessLevel();
    String getHealthAlerts();

    // Medication and lifestyle context
    String getMedicationsTaken();
    Boolean getRecentExercise();
    Boolean getRecentCaffeine();
    Boolean getRecentAlcohol();
    Boolean getRecentSmoking();
    Integer getMenstrualCycleDay();

    // General notes and observations
    String getSymptoms();
    String getNotes();
    Boolean getFollowUpRequired();
    String getFollowUpPriority();

    // Audit fields
    String getCreatedBy();
    LocalDateTime getCreatedAt();
    String getUpdatedBy();
    LocalDateTime getUpdatedAt();

    // Calculated fields from the view
    Integer getDaysSinceLastRecording();
    Boolean getIsFullMetabolicPanel();
    Boolean getIsRecentContextKnown();
    String getBloodPressureStatus();
    Boolean getIsIsolatedSystolicHypertension();
    Boolean getIsWidePulsePressure();
    Boolean getIsTachycardia();
    Boolean getIsBradycardia();
    Boolean getIsLowOxygen();
    Boolean getIsObstructiveLungDisease();
    Boolean getIsFever();
    Boolean getIsHypothermia();
    Boolean getIsHyperglycemia();
    Boolean getIsHypoglycemia();
    Boolean getIsObesity();
    Boolean getIsUnderweight();
    BigDecimal getTcHdlRatio();
}