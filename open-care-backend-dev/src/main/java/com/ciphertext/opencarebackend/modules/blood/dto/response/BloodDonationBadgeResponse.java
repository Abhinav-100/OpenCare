package com.ciphertext.opencarebackend.modules.blood.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Response DTO for blood donation badge information.
 * Badge levels are calculated based on blood donation count.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BloodDonationBadgeResponse {
    private Integer bloodDonationCount;
    private String badgeLevel;
    private String badgeName;
    private String badgeColor;
    private Integer donationsToNextLevel;
    private Integer currentLevelMinDonations;
    private Integer nextLevelMinDonations;
    private Double progressPercentage;
    private Date lastDonationDate;
    private Boolean isEligibleToDonate;
    private Integer daysUntilNextEligible;

    /**
     * Factory method to create BloodDonationBadgeResponse from donation data.
     *
     * @param bloodDonationCount The total number of blood donations
     * @param lastDonationDate The date of last blood donation
     * @return BloodDonationBadgeResponse with calculated badge information
     */
    public static BloodDonationBadgeResponse fromBloodDonationData(Integer bloodDonationCount, Date lastDonationDate) {
        if (bloodDonationCount == null || bloodDonationCount < 0) {
            bloodDonationCount = 0;
        }

        BloodDonationBadgeResponse badge = new BloodDonationBadgeResponse();
        badge.setBloodDonationCount(bloodDonationCount);
        badge.setLastDonationDate(lastDonationDate);

        // Calculate eligibility (120 days / 4 months after last donation)
        if (lastDonationDate != null) {
            long daysSinceLastDonation = (System.currentTimeMillis() - lastDonationDate.getTime()) / (1000 * 60 * 60 * 24);
            badge.setIsEligibleToDonate(daysSinceLastDonation >= 120);

            if (!badge.getIsEligibleToDonate()) {
                badge.setDaysUntilNextEligible((int) (120 - daysSinceLastDonation));
            } else {
                badge.setDaysUntilNextEligible(0);
            }
        } else {
            badge.setIsEligibleToDonate(true);
            badge.setDaysUntilNextEligible(0);
        }

        // Define badge levels based on blood donation count
        if (bloodDonationCount == 0) {
            badge.setBadgeLevel("NONE");
            badge.setBadgeName("New Donor");
            badge.setBadgeColor("#9E9E9E"); // Gray
            badge.setCurrentLevelMinDonations(0);
            badge.setNextLevelMinDonations(1);
        } else if (bloodDonationCount < 5) {
            badge.setBadgeLevel("BEGINNER");
            badge.setBadgeName("Beginner Life Saver");
            badge.setBadgeColor("#CD7F32"); // Bronze
            badge.setCurrentLevelMinDonations(1);
            badge.setNextLevelMinDonations(5);
        } else if (bloodDonationCount < 10) {
            badge.setBadgeLevel("BRONZE");
            badge.setBadgeName("Bronze Life Saver");
            badge.setBadgeColor("#CD7F32");
            badge.setCurrentLevelMinDonations(5);
            badge.setNextLevelMinDonations(10);
        } else if (bloodDonationCount < 20) {
            badge.setBadgeLevel("SILVER");
            badge.setBadgeName("Silver Life Saver");
            badge.setBadgeColor("#C0C0C0");
            badge.setCurrentLevelMinDonations(10);
            badge.setNextLevelMinDonations(20);
        } else if (bloodDonationCount < 35) {
            badge.setBadgeLevel("GOLD");
            badge.setBadgeName("Gold Life Saver");
            badge.setBadgeColor("#FFD700");
            badge.setCurrentLevelMinDonations(20);
            badge.setNextLevelMinDonations(35);
        } else if (bloodDonationCount < 50) {
            badge.setBadgeLevel("PLATINUM");
            badge.setBadgeName("Platinum Life Saver");
            badge.setBadgeColor("#E5E4E2");
            badge.setCurrentLevelMinDonations(35);
            badge.setNextLevelMinDonations(50);
        } else if (bloodDonationCount < 75) {
            badge.setBadgeLevel("DIAMOND");
            badge.setBadgeName("Diamond Life Saver");
            badge.setBadgeColor("#B9F2FF");
            badge.setCurrentLevelMinDonations(50);
            badge.setNextLevelMinDonations(75);
        } else if (bloodDonationCount < 100) {
            badge.setBadgeLevel("HERO");
            badge.setBadgeName("Blood Donation Hero");
            badge.setBadgeColor("#FF4757");
            badge.setCurrentLevelMinDonations(75);
            badge.setNextLevelMinDonations(100);
        } else {
            badge.setBadgeLevel("LEGEND");
            badge.setBadgeName("Legendary Life Saver");
            badge.setBadgeColor("#FF6B6B");
            badge.setCurrentLevelMinDonations(100);
            badge.setNextLevelMinDonations(null); // Max level reached
        }

        // Calculate donations to next level
        if (badge.getNextLevelMinDonations() != null) {
            badge.setDonationsToNextLevel(badge.getNextLevelMinDonations() - bloodDonationCount);

            // Calculate progress percentage
            int donationsInCurrentLevel = bloodDonationCount - badge.getCurrentLevelMinDonations();
            int donationsNeededForLevel = badge.getNextLevelMinDonations() - badge.getCurrentLevelMinDonations();
            double progress = (donationsInCurrentLevel * 100.0) / donationsNeededForLevel;
            badge.setProgressPercentage(Math.round(progress * 100.0) / 100.0); // Round to 2 decimals
        } else {
            badge.setDonationsToNextLevel(0); // Max level
            badge.setProgressPercentage(100.0);
        }

        return badge;
    }
}
