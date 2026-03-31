package com.ciphertext.opencarebackend.enums;

import com.ciphertext.opencarebackend.modules.payment.dto.response.enums.ContributionBadgeResponse;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ContributionBadge {
    FIRST_STEP(1, 100, "Ã°Å¸â€˜Â£", "First Step", "You've started your journey to make healthcare smarter."),
    ACTIVE_SCOUT(101, 250, "Ã°Å¸â€¢ÂµÃ¯Â¸Â", "Active Scout", "You're actively scouting the medical world for improvements."),
    FACT_CHECKER(251, 500, "Ã°Å¸â€œËœ", "Fact Checker", "Your efforts ensure our data stays accurate and reliable."),
    HEALTH_EXPLORER(501, 1000, "Ã°Å¸â€”ÂºÃ¯Â¸Â", "Health Explorer", "You're helping others explore and access better healthcare options."),
    COMMUNITY_PILLAR(1001, 2000, "Ã°Å¸Ââ€ºÃ¯Â¸Â", "Community Pillar", "Your support is strengthening the healthcare network."),
    TRUSTED_CONTRIBUTOR(2001, 4000, "Ã°Å¸Â¤Â", "Trusted Contributor", "Your consistency makes you a dependable contributor."),
    HEALTH_INFLUENCER(4001, 6000, "Ã°Å¸â€œÂ£", "Health Influencer", "Your contributions influence healthcare choices."),
    KNOWLEDGE_HERO(6001, 8000, "Ã°Å¸Â§Â ", "Knowledge Hero", "You're building a reliable source of healthcare truth."),
    OPEN_CARE_CHAMPION(8001, 10000, "Ã°Å¸Ââ€ ", "OpenCare Champion", "Your impact in OpenCare is making lives easier."),
    LEGENDARY_CONTRIBUTOR(10001, Integer.MAX_VALUE, "Ã°Å¸Å’Å¸", "Legendary Contributor", "You're a true visionary of open healthcare.");

    private final int minPoints;
    private final int maxPoints;
    private final String icon;
    private final String levelName;
    private final String description;

    ContributionBadge(int minPoints, int maxPoints, String icon, String levelName, String description) {
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.icon = icon;
        this.levelName = levelName;
        this.description = description;
    }

    public int getMinPoints() {
        return minPoints;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public String getIcon() {
        return icon;
    }

    public String getLevelName() {
        return levelName;
    }

    public String getDescription() {
        return description;
    }

    public static ContributionBadge fromPoints(int points) {
        for (ContributionBadge badge : ContributionBadge.values()) {
            if (points >= badge.getMinPoints() && points <= badge.getMaxPoints()) {
                return badge;
            }
        }
        return null;
    }

    public ContributionBadgeResponse toResponse() {
        return new ContributionBadgeResponse(
                this.name(),
                this.minPoints,
                this.maxPoints,
                this.icon,
                this.levelName,
                this.description
        );
    }
}
