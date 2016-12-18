package de.egym.recruiting.codingtask.rest.domain;

import io.swagger.annotations.ApiModel;

/**
 * Created by apodznoev
 * date 18.12.2016.
 */
@ApiModel
public enum AchievementType {
    TRAINING_ADDICT() {
        @Override
        public AchievementAnalyser createAnalyser() {
            return new TrainingAddictAnalyser();
        }
    };

    public abstract AchievementAnalyser createAnalyser();

}
