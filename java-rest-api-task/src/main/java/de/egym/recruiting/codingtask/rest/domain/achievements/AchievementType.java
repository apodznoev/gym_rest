package de.egym.recruiting.codingtask.rest.domain.achievements;

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
    },
    MARATHON() {
        @Override
        public AchievementAnalyser createAnalyser() {
            return new MarathonAnalyser();
        }
    },
    OLD_BUT_FASHIONED() {
        @Override
        public AchievementAnalyser createAnalyser() {
            return new OldButFashionedAnalyser();
        }
    },
    TRIATHLON() {
        @Override
        public AchievementAnalyser createAnalyser() {
            return new TriathlonAnalyser();
        }
    },
    NIGHT_SHIFT() {
        @Override
        public AchievementAnalyser createAnalyser() {
            return new NightShiftAnalyser();
        }
    },
    WANDERER() {
        @Override
        public AchievementAnalyser createAnalyser() {
            return new WandererAnalyser();
        }
    };

    public abstract AchievementAnalyser createAnalyser();

}
