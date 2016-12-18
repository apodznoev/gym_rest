package de.egym.recruiting.codingtask.jpa.domain;

import de.egym.recruiting.codingtask.rest.domain.achievements.AchievementType;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by apodznoev
 * date 18.12.2016.
 */
public class UserAchievement {

    @ApiModelProperty(value = "Type of the achievement.", example = "TRAINING_ADDICT")
    private final AchievementType type;

    @ApiModelProperty(value = "Timestamp when achievements was obtained.", example = "1482008722959")
    private final long achievementTime;

    public UserAchievement(AchievementType type, long achievementTime) {
        this.type = type;
        this.achievementTime = achievementTime;
    }

    public AchievementType getType() {
        return type;
    }

    public long getAchievementTime() {
        return achievementTime;
    }
}
