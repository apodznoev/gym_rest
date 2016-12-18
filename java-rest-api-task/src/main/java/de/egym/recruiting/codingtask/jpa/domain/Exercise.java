package de.egym.recruiting.codingtask.jpa.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by apodznoev
 * date 17.12.2016.
 */
@Entity
public class Exercise extends AbstractEntity {

    @ApiModelProperty(required = true, value = "Id of the user, who've completed the exercise.", example = "123", dataType = "java.lang.Long" )
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private User user;

    @ApiModelProperty(required = true, value = "Type of the exercise from dictionary.", example = "CYCLING")
    private Type type;

    private long startTimestamp;

    @ApiModelProperty(required = true, value = "Duration of the exercise in seconds.", example = "3000")
    private int durationSecs;

    @ApiModelProperty(required = true, value = "Amount of calories burned within exercies.", example = "534")
    private int caloriesBurned;

    @ApiModelProperty(required = false, value = "Distance in meters got over during the exercise.", example = "7566")
    @Column(nullable = true)
    private Integer distanceMeters;

    @JsonIgnore
    public long getEndTimestamp() {
        return startTimestamp + TimeUnit.SECONDS.toMillis(durationSecs);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    @ApiModelProperty(required = true, value = "Timestamp, start of the exercise.", example = "2016-12-17 10:00:14")
    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    @JsonProperty
    public void setStartTimestamp(Date date) {
        this.startTimestamp = date.getTime();
    }

    @JsonIgnore
    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public int getDurationSecs() {
        return durationSecs;
    }

    public void setDurationSecs(int durationSecs) {
        this.durationSecs = durationSecs;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public Integer getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(Integer distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    @ApiModel
    public enum Type {
        RUNNING,
        CYCLING,
        SWIMMING,
        ROWING,
        WALKING,
        CIRCUIT_TRAINING,
        STRENGTH_TRAINING,
        FITNESS_COURSE,
        SPORTS,
        OTHER

    }
}
