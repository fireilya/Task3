package ru.Perminov_Ilya.NauJava.BuisnesEntity;

import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public class Plane {
    @Nonnull
    private final UUID id;
    @Nonnull
    private String name;
    private double maxEngineThrustInKgf;
    private boolean isWepActive;
    private double wepBoostPercent;
    private double thrustInPercent;
    private double maxSpeedInKilometersPerHour;
    private double massInKg;
    private double maxAngleOfAttackInDegrees;
    private double currentAngleOfAttackInDegrees;

    public Plane(
            @NonNull UUID id,
            @NonNull String name,
            double maxEngineThrustInKgf,
            double wepBoostPercent,
            double maxSpeedInKilometersPerHour,
            double massInKg,
            double maxAngleOfAttackInDegrees) {
        this.id = id;
        this.name = name;
        this.maxEngineThrustInKgf = maxEngineThrustInKgf;
        setWepBoostPercent(wepBoostPercent);
        this.maxSpeedInKilometersPerHour = maxSpeedInKilometersPerHour;
        this.massInKg = massInKg;
        this.maxAngleOfAttackInDegrees = maxAngleOfAttackInDegrees;
        isWepActive = false;
        thrustInPercent = 0;
        currentAngleOfAttackInDegrees = 0;
    }

    public double getMaxEngineThrustInKgf() {
        return maxEngineThrustInKgf;
    }

    public void setMaxEngineThrustInKgf(double maxEngineThrustInKgf) {
        this.maxEngineThrustInKgf = maxEngineThrustInKgf;
    }

    public double getMaxSpeedInKilometersPerHour() {
        return maxSpeedInKilometersPerHour;
    }

    public void setMaxSpeedInKilometersPerHour(double maxSpeedInKilometersPerHour) {
        this.maxSpeedInKilometersPerHour = maxSpeedInKilometersPerHour;
    }

    public double getMassInKg() {
        return massInKg;
    }

    public void setMassInKg(double massInKg) {
        this.massInKg = massInKg;
    }

    public @NonNull UUID getId() {
        return id;
    }

    public @NonNull String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public boolean isWepActive() {
        return isWepActive;
    }

    public void setWepActive(boolean wepActive) {
        if (thrustInPercent != 100)
            return;
        this.isWepActive = wepActive;
    }

    public double getWepBoostPercent() {
        return wepBoostPercent;
    }

    public void setWepBoostPercent(double wepBoostPercent) {
        this.wepBoostPercent = wepBoostPercent < 0
                ? 0
                : wepBoostPercent;
    }

    public double getThrustInPercent() {
        return thrustInPercent;
    }

    public void setThrustInPercent(double thrustInPercent) {
        this.thrustInPercent = Math.clamp(thrustInPercent, 0, 100);
    }

    public double getTotalThrustInPercent()
    {
        return thrustInPercent + (isWepActive ? wepBoostPercent : 0);
    }

    public double getCurrentEngineThrustInKgf()
    {
        return maxEngineThrustInKgf * getTotalThrustInPercent() / 100;
    }

    public double getMaxAngleOfAttackInDegrees() {
        return maxAngleOfAttackInDegrees;
    }

    public void setMaxAngleOfAttackInDegrees(double maxAngleOfAttackInDegrees) {
        this.maxAngleOfAttackInDegrees = maxAngleOfAttackInDegrees;
    }

    public double getCurrentSpeedInKilometersPerHour() {
        return maxSpeedInKilometersPerHour * getTotalThrustInPercent() / 100;
    }

    public double getReferenceTWR() {
        return maxEngineThrustInKgf / massInKg;
    }
    public double getCurrentTWR()
    {
        return maxEngineThrustInKgf * getTotalThrustInPercent() / 100 / massInKg;
    }

    public double getCurrentAngleOfAttackInDegrees() {
        return currentAngleOfAttackInDegrees;
    }

    public void setCurrentAngleOfAttackInDegrees(double currentAngleOfAttackInDegrees) {
        this.currentAngleOfAttackInDegrees = Math.clamp(currentAngleOfAttackInDegrees, 0, maxAngleOfAttackInDegrees);
    }
}
