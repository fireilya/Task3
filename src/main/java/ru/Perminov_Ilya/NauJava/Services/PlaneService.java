package ru.Perminov_Ilya.NauJava.Services;

import ru.Perminov_Ilya.NauJava.BuisnesEntity.Plane;
import ru.Perminov_Ilya.NauJava.Converters.PlaneConverter;
import ru.Perminov_Ilya.NauJava.RepositoryPackage.PlaneRepository;
import ru.Perminov_Ilya.NauJava.Requests.PlaneCreateRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlaneService implements IPlaneService {
    private final PlaneRepository planeRepository;

    public PlaneService(PlaneRepository planeRepository) {
        this.planeRepository = planeRepository;
    }

    @Override
    public UUID createPlane(@NonNull PlaneCreateRequest createRequest) {
        var newPlane = new Plane(
                UUID.randomUUID(),
                createRequest.getName(),
                createRequest.getMaxEngineThrustInKgf(),
                createRequest.getWepBoostPercent(),
                createRequest.getMaxSpeedInKilometersPerHour(),
                createRequest.getMassInKg(),
                createRequest.getMaxAngleOfAttackInDegrees());

        return planeRepository.create(PlaneConverter.toDBO(newPlane));
    }

    @Override
    public String getPlanesList() {
        var planesList = planeRepository.readAll();
        var answer = new StringBuilder("Plane list:\n");
        for (var plane : planesList) {
            answer
                    .append("Plane id: \"")
                    .append(plane.getId())
                    .append("\", name: \"")
                    .append(plane.getName())
                    .append('\n');
        }
        return answer.toString();
    }

    @Override
    public String getAllPlaneInfo(@NonNull UUID id) {
        var planeDBO = planeRepository.read(id);
        if (planeDBO == null)
            return "Plane with id \"" + id + "\" not found";
        var plane = PlaneConverter.toBusinessEntity(planeDBO);
        return "Plane info:\n" +
                "Plane ID: " + plane.getId().toString() + '\n' +
                "Plane name: " + plane.getName() + "\n" +
                "Plane max engine thrust: " + plane.getMaxEngineThrustInKgf() + " kgf\n" +
                "Plane max speed: " + plane.getMaxSpeedInKilometersPerHour() + " km/h\n" +
                "Plane current speed: " + plane.getCurrentSpeedInKilometersPerHour() + " km/h\n" +
                "Plane mass: " + plane.getMassInKg() + " kg\n" +
                "Plane max angle of attack: " + plane.getMaxAngleOfAttackInDegrees() + " degrees\n" +
                "Plane current angle of attack: " + plane.getCurrentAngleOfAttackInDegrees() + " degrees\n" +
                "Plane current thrust: " + plane.getThrustInPercent() + "%\n" +
                "Plane current engine thrust: " + plane.getCurrentEngineThrustInKgf() + "kgf\n" +
                "Plane wep boost: " + plane.getWepBoostPercent() + "%\n" +
                "Plane wep active: " + (plane.isWepActive() ? "yes\n" : "no\n") +
                "Plane total thrust: " + plane.getTotalThrustInPercent() + "%\n" +
                "Plane reference TWR: " + plane.getReferenceTWR() + '\n' +
                "Plane current TWR: " + plane.getCurrentTWR() + '\n';
    }

    @Override
    public String getPlaneFM(@NonNull UUID id) {
        var planeDBO = planeRepository.read(id);
        if (planeDBO == null)
            return "Plane with id \"" + id + "\" not found";
        var plane = PlaneConverter.toBusinessEntity(planeDBO);
        return "Plane FM info:\n" +
                "Plane ID: " + plane.getId().toString() + '\n' +
                "Plane name: " + plane.getName() + "\n" +
                "Plane max engine thrust: " + plane.getMaxEngineThrustInKgf() + " kgf\n" +
                "Plane max speed: " + plane.getMaxSpeedInKilometersPerHour() + " km/h\n" +
                "Plane mass: " + plane.getMassInKg() + " kg\n" +
                "Plane max angle of attack: " + plane.getMaxAngleOfAttackInDegrees() + " degrees\n" +
                "Plane reference TWR: " + plane.getReferenceTWR() + '\n';
    }

    @Override
    public String getCurrentPlaneState(@NonNull UUID id) {
        var planeDBO = planeRepository.read(id);
        if (planeDBO == null)
            return "Plane with id \"" + id + "\" not found";
        var plane = PlaneConverter.toBusinessEntity(planeDBO);
        return "Plane current state:\n" +
                "Plane current speed: " + plane.getCurrentSpeedInKilometersPerHour() + " km/h\n" +
                "Plane current angle of attack: " + plane.getCurrentAngleOfAttackInDegrees() + " degrees\n" +
                "Plane current thrust: " + plane.getThrustInPercent() + "%\n" +
                "Plane current engine thrust: " + plane.getCurrentEngineThrustInKgf() + "kgf\n" +
                "Plane wep active: " + (plane.isWepActive() ? "yes\n" : "no\n") +
                "Plane total thrust: " + plane.getTotalThrustInPercent() + "%\n" +
                "Plane current TWR: " + plane.getCurrentTWR() + '\n';
    }

    @Override
    public String setPlaneThrust(@NonNull UUID id, double thrust) {
        if (thrust < 0 || thrust > 100)
            return "Thrust must be between 0 and 100";
        var planeDBO = planeRepository.read(id);
        if (planeDBO == null)
            return "Plane with id " + id + " not found";
        var plane = PlaneConverter.toBusinessEntity(planeDBO);
        plane.setThrustInPercent(thrust);
        var updatedPlaneId = planeRepository.update(PlaneConverter.toDBO(plane));
        return updatedPlaneId == null
                ? "Can't update thrust for plane with id " + id
                : "Thrust for plane with id " + id + " updated";
    }

    @Override
    public String setPlaneWep(@NonNull UUID id, boolean wepActive) {
        var planeDBO = planeRepository.read(id);
        if (planeDBO == null)
            return "Plane with id " + id + " not found";
        var plane = PlaneConverter.toBusinessEntity(planeDBO);
        if (wepActive && plane.getThrustInPercent() != 100)
            return "Can't activate wep while thrust is not 100%";
        if (wepActive && plane.isWepActive())
            return "Wep on plane with id " + id + " already activated";
        if (!wepActive && !plane.isWepActive())
            return "Wep on plane with id " + id + " already deactivated";
        plane.setWepActive(wepActive);
        var updatedPlaneId = planeRepository.update(PlaneConverter.toDBO(plane));
        return updatedPlaneId == null
                ? "Can't update wep on plane with id " + id
                : "Wep on plane with id " + id + (wepActive ? " activated" : " deactivated");
    }

    @Override
    public String setPlaneAngleOfAttack(@NonNull UUID id, double angleInDegrees) {
        if (angleInDegrees < 0)
            return "AoA can't be negative";
        var planeDBO = planeRepository.read(id);
        if (planeDBO == null)
            return "Plane with id " + id + " not found";
        var plane = PlaneConverter.toBusinessEntity(planeDBO);
        if (angleInDegrees > plane.getMaxAngleOfAttackInDegrees())
            return "AoA can't be more then available. Read plane documentation first";
        plane.setCurrentAngleOfAttackInDegrees(angleInDegrees);
        var updatedPlaneId = planeRepository.update(PlaneConverter.toDBO(plane));
        return updatedPlaneId == null
                ? "Can't update AoA for plane with id " + id
                : "AoA on plane with id " + id + " updated";
    }

    @Override
    public boolean deletePlane(@NonNull UUID id) {
        return planeRepository.delete(id);
    }
}
