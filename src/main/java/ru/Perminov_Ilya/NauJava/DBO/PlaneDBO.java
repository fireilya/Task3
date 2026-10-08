package ru.Perminov_Ilya.NauJava.DBO;

import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public class PlaneDBO {
    @Nonnull
    private final UUID id;
    @Nonnull
    private final String name;
    private final double maxEngineThrustInKgf;
    private final boolean isWepActive;
    private final double wepBoostPercent;
    private final double thrustInPercent;
    private final double maxSpeedInKilometersPerHour;
    private final double massInKg;
    private final double maxAngleOfAttackInDegrees;
    private final double currentAngleOfAttackInDegrees;

    public PlaneDBO(
            @NonNull UUID id,
            @NonNull String name,
            double maxEngineThrustInKgf,
            boolean isWepActive,
            double wepBoostPercent,
            double thrustInPercent,
            double maxSpeedInKilometersPerHour,
            double massInKg,
            double maxAngleOfAttackInDegrees,
            double currentAngleOfAttackInDegrees) {
        this.id = id;
        this.name = name;
        this.maxEngineThrustInKgf = maxEngineThrustInKgf;
        this.isWepActive = isWepActive;
        this.wepBoostPercent = wepBoostPercent < 0 ? 0 : wepBoostPercent;
        this.thrustInPercent = Math.clamp(thrustInPercent, 0, 100);
        this.maxSpeedInKilometersPerHour = maxSpeedInKilometersPerHour;
        this.massInKg = massInKg;
        this.maxAngleOfAttackInDegrees = maxAngleOfAttackInDegrees;
        this.currentAngleOfAttackInDegrees = Math.clamp(currentAngleOfAttackInDegrees, 0, maxAngleOfAttackInDegrees);
    }

    @Nonnull
    public UUID getId() {
        return id;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public double getMaxEngineThrustInKgf() {
        return maxEngineThrustInKgf;
    }

    public double getWepBoostPercent() {
        return wepBoostPercent;
    }

    public double getThrustInPercent() {
        return thrustInPercent;
    }

    public double getMaxSpeedInKilometersPerHour() {
        return maxSpeedInKilometersPerHour;
    }

    public double getMassInKg() {
        return massInKg;
    }

    public double getMaxAngleOfAttackInDegrees() {
        return maxAngleOfAttackInDegrees;
    }

    public double getCurrentAngleOfAttackInDegrees() {
        return currentAngleOfAttackInDegrees;
    }

    public boolean isWepActive() {
        return isWepActive;
    }
}
