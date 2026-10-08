package ru.Perminov_Ilya.NauJava.Converters;

import ru.Perminov_Ilya.NauJava.BuisnesEntity.Plane;
import ru.Perminov_Ilya.NauJava.DBO.PlaneDBO;

public class PlaneConverter {
    public static Plane toBusinessEntity(final PlaneDBO dbo) {
        var plane = new Plane(
                dbo.getId(),
                dbo.getName(),
                dbo.getMaxEngineThrustInKgf(),
                dbo.getWepBoostPercent(),
                dbo.getMaxSpeedInKilometersPerHour(),
                dbo.getMassInKg(),
                dbo.getMaxAngleOfAttackInDegrees());
        plane.setCurrentAngleOfAttackInDegrees(dbo.getCurrentAngleOfAttackInDegrees());
        plane.setThrustInPercent(dbo.getThrustInPercent());
        plane.setWepActive(dbo.isWepActive());
        return plane;
    }

    public static PlaneDBO toDBO(final Plane plane) {
        return new PlaneDBO(
                plane.getId(),
                plane.getName(),
                plane.getMaxEngineThrustInKgf(),
                plane.isWepActive(),
                plane.getWepBoostPercent(),
                plane.getThrustInPercent(),
                plane.getMaxSpeedInKilometersPerHour(),
                plane.getMassInKg(),
                plane.getMaxAngleOfAttackInDegrees(),
                plane.getCurrentAngleOfAttackInDegrees());
    }
}
