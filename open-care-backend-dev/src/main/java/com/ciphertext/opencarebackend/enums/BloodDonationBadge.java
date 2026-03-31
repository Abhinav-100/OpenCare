package com.ciphertext.opencarebackend.enums;

import com.ciphertext.opencarebackend.modules.blood.dto.response.enums.BloodDonationBadgeResponse;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BloodDonationBadge {
    FIRST_FLOW(1, 5, "Ã°Å¸Â©Â¸", "First Flow", "You've begun your life-saving journeyÃ¢â‚¬â€thank you!"),
    HOPE_GIVER(6, 10, "Ã°Å¸Å’Â±", "Hope Giver", "Each donation plants a seed of hope in someoneÃ¢â‚¬â„¢s life."),
    HEALING_HAND(11, 15, "Ã°Å¸Â¤Â²", "Healing Hand", "You're now a consistent force of healing and kindness."),
    VITAL_FLAME(16, 20, "Ã°Å¸â€Â¥", "Vital Flame", "Your generosity keeps the flame of life burning bright."),
    LIFE_DEFENDER(21, 25, "Ã°Å¸â€ºÂ¡Ã¯Â¸Â", "Life Defender", "A true protector through repeated acts of compassion."),
    BLOOD_CHAMPION(26, 30, "Ã°Å¸Ââ€¦", "Blood Champion", "Your dedication makes you a true champion of humanity."),
    GUARDIAN_SOUL(31, 35, "Ã°Å¸â€¢Å Ã¯Â¸Â", "Guardian Soul", "A soulful guardian spreading peace through giving."),
    BEACON_OF_LIFE(36, 40, "Ã¢Å“Â¨", "Beacon of Life", "You shine as a guiding light for those in darkness."),
    DONATION_ROYALTY(41, 45, "Ã°Å¸â€˜â€˜", "Donation Royalty", "Your legacy in donation is nothing short of royal."),
    ETERNAL_GIVER(46, 50, "Ã°Å¸â€¢Å Ã¯Â¸ÂÃ°Å¸Å’Â", "Eternal Giver", "Your endless giving echoes across generations."),
    DAWN_GIVER(51, 55, "Ã°Å¸Å’â€ž", "Dawn Giver", "A new dawn rises for many, thanks to your giving."),
    SOUL_HEALER(56, 60, "Ã°Å¸Â¦â€¹", "Soul Healer", "Your kindness brings healing to both body and soul."),
    LIFEBLOOD_ORACLE(61, 65, "Ã°Å¸â€Â®", "Lifeblood Oracle", "A visionary in the journey of saving lives."),
    HOPE_BEARER(66, 70, "Ã°Å¸â€¢Â¯Ã¯Â¸Â", "Hope Bearer", "Carrying the light of hope through every donation."),
    BLOOD_VOYAGER(71, 75, "Ã°Å¸Å’Å’", "Blood Voyager", "Exploring the universe of humanity through giving."),
    CRIMSON_KNIGHT(76, 80, "Ã¢Å¡â€Ã¯Â¸Â", "Crimson Knight", "A warrior in the noble battle for life."),
    PULSE_DRAGON(81, 85, "Ã°Å¸Ââ€°", "Pulse Dragon", "A legendary lifeforce protector with fierce compassion."),
    LIFE_ALCHEMIST(86, 90, "Ã°Å¸Å’Ë†", "Life Alchemist", "Turning drops of blood into miracles of life."),
    SACRED_VEIN(91, 95, "Ã°Å¸â€Â±", "Sacred Vein", "Honored and revered for your boundless generosity."),
    IMMORTAL_GIVER(96, Integer.MAX_VALUE, "Ã°Å¸â€˜ÂÃ¯Â¸ÂÃ¢â‚¬ÂÃ°Å¸â€”Â¨Ã¯Â¸Â", "Immortal Giver", "Your legacy is immortalized in every life saved.");

    private final int minDonations;
    private final int maxDonations;
    private final String icon;
    private final String levelName;
    private final String description;

    BloodDonationBadge(int minDonations, int maxDonations, String icon, String levelName, String description) {
        this.minDonations = minDonations;
        this.maxDonations = maxDonations;
        this.icon = icon;
        this.levelName = levelName;
        this.description = description;
    }

    public int getMinDonations() {
        return minDonations;
    }

    public int getMaxDonations() {
        return maxDonations;
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

    public static BloodDonationBadge getLevelByDonationCount(int donationCount) {
        for (BloodDonationBadge level : BloodDonationBadge.values()) {
            if (donationCount >= level.minDonations && donationCount <= level.maxDonations) {
                return level;
            }
        }
        return null;
    }

    public BloodDonationBadge getNextLevel() {
        BloodDonationBadge[] levels = BloodDonationBadge.values();
        int currentIndex = this.ordinal();
        if (currentIndex < levels.length - 1) {
            return levels[currentIndex + 1];
        }
        return null;
    }

    public int getDonationsToNextLevel(int currentDonations) {
        BloodDonationBadge nextLevel = getNextLevel();
        if (nextLevel != null) {
            return Math.max(0, nextLevel.getMinDonations() - currentDonations);
        }
        return 0;
    }

    public double getProgressPercentage(int currentDonations) {
        if (this == IMMORTAL_GIVER) {
            return 100.0;
        }
        int levelRange = maxDonations - minDonations + 1;
        int currentProgress = Math.min(currentDonations - minDonations + 1, levelRange);
        return (double) currentProgress / levelRange * 100.0;
    }

    @Override
    public String toString() {
        return String.format("%s %s (%d+ donations)", icon, levelName, minDonations);
    }

    public BloodDonationBadgeResponse toResponse() {
        return new BloodDonationBadgeResponse(
                this.name(),
                minDonations,
                maxDonations,
                icon,
                levelName,
                description
        );
    }
}
