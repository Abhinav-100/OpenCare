package com.ciphertext.opencarebackend.modules.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for user contribution badge information.
 * Badge levels are calculated based on contribution points.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContributionBadgeResponse {
    private Integer contributionPoints;
    private String badgeLevel;
    private String badgeName;
    private String badgeColor;
    private Integer pointsToNextLevel;
    private Integer currentLevelMinPoints;
    private Integer nextLevelMinPoints;
    private Double progressPercentage;

    /**
     * Factory method to create ContributionBadgeResponse from contribution points.
     *
     * @param contributionPoints The total contribution points
     * @return ContributionBadgeResponse with calculated badge information
     */
    public static ContributionBadgeResponse fromContributionPoints(Integer contributionPoints) {
        if (contributionPoints == null || contributionPoints < 0) {
            contributionPoints = 0;
        }

        ContributionBadgeResponse badge = new ContributionBadgeResponse();
        badge.setContributionPoints(contributionPoints);

        // Define badge levels based on contribution points
        if (contributionPoints < 100) {
            badge.setBadgeLevel("BEGINNER");
            badge.setBadgeName("Beginner Contributor");
            badge.setBadgeColor("#CD7F32"); // Bronze
            badge.setCurrentLevelMinPoints(0);
            badge.setNextLevelMinPoints(100);
        } else if (contributionPoints < 500) {
            badge.setBadgeLevel("BRONZE");
            badge.setBadgeName("Bronze Contributor");
            badge.setBadgeColor("#CD7F32");
            badge.setCurrentLevelMinPoints(100);
            badge.setNextLevelMinPoints(500);
        } else if (contributionPoints < 1000) {
            badge.setBadgeLevel("SILVER");
            badge.setBadgeName("Silver Contributor");
            badge.setBadgeColor("#C0C0C0");
            badge.setCurrentLevelMinPoints(500);
            badge.setNextLevelMinPoints(1000);
        } else if (contributionPoints < 2500) {
            badge.setBadgeLevel("GOLD");
            badge.setBadgeName("Gold Contributor");
            badge.setBadgeColor("#FFD700");
            badge.setCurrentLevelMinPoints(1000);
            badge.setNextLevelMinPoints(2500);
        } else if (contributionPoints < 5000) {
            badge.setBadgeLevel("PLATINUM");
            badge.setBadgeName("Platinum Contributor");
            badge.setBadgeColor("#E5E4E2");
            badge.setCurrentLevelMinPoints(2500);
            badge.setNextLevelMinPoints(5000);
        } else if (contributionPoints < 10000) {
            badge.setBadgeLevel("DIAMOND");
            badge.setBadgeName("Diamond Contributor");
            badge.setBadgeColor("#B9F2FF");
            badge.setCurrentLevelMinPoints(5000);
            badge.setNextLevelMinPoints(10000);
        } else {
            badge.setBadgeLevel("LEGEND");
            badge.setBadgeName("Legendary Contributor");
            badge.setBadgeColor("#FF6B6B");
            badge.setCurrentLevelMinPoints(10000);
            badge.setNextLevelMinPoints(null); // Max level reached
        }

        // Calculate points to next level
        if (badge.getNextLevelMinPoints() != null) {
            badge.setPointsToNextLevel(badge.getNextLevelMinPoints() - contributionPoints);

            // Calculate progress percentage
            int pointsInCurrentLevel = contributionPoints - badge.getCurrentLevelMinPoints();
            int pointsNeededForLevel = badge.getNextLevelMinPoints() - badge.getCurrentLevelMinPoints();
            double progress = (pointsInCurrentLevel * 100.0) / pointsNeededForLevel;
            badge.setProgressPercentage(Math.round(progress * 100.0) / 100.0); // Round to 2 decimals
        } else {
            badge.setPointsToNextLevel(0); // Max level
            badge.setProgressPercentage(100.0);
        }

        return badge;
    }
}
