package ru.Perminov_Ilya.NauJava.Requests;

import jakarta.annotation.Nonnull;

public class PlaneCreateRequest {
    @Nonnull
    private final String name;
    private final double maxEngineThrustInKgf;
    private final double wepBoostPercent;
    private final double maxSpeedInKilometersPerHour;
    private final double massInKg;
    private final double maxAngleOfAttackInDegrees;

    public PlaneCreateRequest(
            @Nonnull String name,
            double maxEngineThrustInKgf,
            double wepBoostPercent,
            double maxSpeedInKilometersPerHour,
            double massInKg,
            double maxAngleOfAttackInDegrees) {
        this.name = name;
        this.maxEngineThrustInKgf = maxEngineThrustInKgf;
        this.wepBoostPercent = wepBoostPercent;
        this.maxSpeedInKilometersPerHour = maxSpeedInKilometersPerHour;
        this.massInKg = massInKg;
        this.maxAngleOfAttackInDegrees = maxAngleOfAttackInDegrees;
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

    public double getMaxSpeedInKilometersPerHour() {
        return maxSpeedInKilometersPerHour;
    }

    public double getMassInKg() {
        return massInKg;
    }

    public double getMaxAngleOfAttackInDegrees() {
        return maxAngleOfAttackInDegrees;
    }
}
