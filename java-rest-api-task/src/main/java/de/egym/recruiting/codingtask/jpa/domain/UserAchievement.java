package de.egym.recruiting.codingtask.jpa.domain;

import de.egym.recruiting.codingtask.rest.domain.AchievementType;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by apodznoev
 * date 18.12.2016.
 */
public class UserAchievement {

    @ApiModelProperty(value = "Type of the achievement.", example = "TRAINING_ADDICT")
    private final AchievementType getType;

    @ApiModelProperty(value = "Timestamp when achievements was obtained.", example = "1482008722959")
    private final long getAchievementTime;

    public UserAchievement(AchievementType getType, long getAchievementTime) {
        this.getType = getType;
        this.getAchievementTime = getAchievementTime;
    }

    public AchievementType getGetType() {
        return getType;
    }

    public long getGetAchievementTime() {
        return getAchievementTime;
    }
}
