package de.egym.recruiting.codingtask.rest.domain.achievements;

import de.egym.recruiting.codingtask.jpa.dao.ExerciseDao;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import de.egym.recruiting.codingtask.jpa.domain.User;
import de.egym.recruiting.codingtask.jpa.domain.UserAchievement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by apodznoev
 * date 18.12.2016.
 */
public class AchievementsFactory {
    private static final Logger log = LoggerFactory.getLogger(AchievementsFactory.class);
    //per user
    private final Map<Long, Collection<AchievementAnalyser>> achievementAnalysers = new ConcurrentHashMap<>();
    private final ExerciseDao exerciseDao;

    public AchievementsFactory(ExerciseDao exerciseDao) {
        this.exerciseDao = exerciseDao;
    }

    public void consume(Exercise exercise) {
        log.debug("Consuming exercise for analysis:{}", exercise);
        achievementAnalysers.computeIfAbsent(exercise.getUser().getId(), userId -> createAllAnalysers(exercise.getUser()))
                .forEach(analyser -> analyser.analyseExercise(exercise));
    }

    private Collection<AchievementAnalyser> createAllAnalysers(User user) {
        log.debug("Initializing analysers for user: {}", user.getId());
        Collection<AchievementAnalyser> achievementAnalysers = new ArrayList<>(AchievementType.values().length);

        for (AchievementType achievementType : AchievementType.values()) {
            try {
                AchievementAnalyser achievementAnalyser = achievementType.createAnalyser();
                achievementAnalyser.setUser(user);
                achievementAnalyser.setExerciseDao(exerciseDao);
                achievementAnalyser.loadInitialData();
                achievementAnalysers.add(achievementAnalyser);
            } catch (Exception e) {
                log.error("Unexpected problem with constructor for class:" + achievementType, e);
            }
        }
        return achievementAnalysers;
    }

    public List<UserAchievement> getAchievements(long userId) {
        log.debug("Achievements requested for user: {}", userId);
        return achievementAnalysers.getOrDefault(userId, Collections.emptyList())
                .stream()
                .map(AchievementAnalyser::observeAchievement)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
