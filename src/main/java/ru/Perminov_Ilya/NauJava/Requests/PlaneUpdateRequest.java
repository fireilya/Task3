package ru.Perminov_Ilya.NauJava.Requests;

import jakarta.annotation.Nonnull;

import java.util.UUID;

public class PlaneUpdateRequest {
    @Nonnull
    private final UUID id;
    private final String name;
    private final Boolean isWepActive;
    private final Double thrustInPercent;
    private final Double currentAngleOfAttackInDegrees;

    public PlaneUpdateRequest(
            @Nonnull UUID id,
            String name,
            Boolean isWepActive,
            Double thrustInPercent,
            Double currentAngleOfAttackInDegrees) {
        this.id = id;
        this.name = name;
        this.isWepActive = isWepActive;
        this.thrustInPercent = thrustInPercent;
        this.currentAngleOfAttackInDegrees = currentAngleOfAttackInDegrees;
    }

    @Nonnull
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getWepActive() {
        return isWepActive;
    }

    public Double getThrustInPercent() {
        return thrustInPercent;
    }

    public Double getCurrentAngleOfAttackInDegrees() {
        return currentAngleOfAttackInDegrees;
    }
}
