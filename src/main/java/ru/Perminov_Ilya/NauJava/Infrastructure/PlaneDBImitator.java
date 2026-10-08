package ru.Perminov_Ilya.NauJava.Infrastructure;

import ru.Perminov_Ilya.NauJava.DBO.PlaneDBO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Component
public class PlaneDBImitator {
    private final HashMap<UUID, PlaneDBO> planes;

    PlaneDBImitator() {
        planes = new HashMap<>();
    }

    public UUID createPlane(PlaneDBO dbo) {
        planes.put(dbo.getId(), dbo);
        return dbo.getId();
    }

    public PlaneDBO readPlane(UUID id) {
        if (!planes.containsKey(id))
            return null;
        return planes.get(id);
    }

    public List<PlaneDBO> readAllPlanes() {
        return new ArrayList<>(planes.values());
    }

    public UUID updatePlane(PlaneDBO dbo) {
        if (!planes.containsKey(dbo.getId()))
            return null;
        planes.put(dbo.getId(), dbo);
        return dbo.getId();
    }

    public boolean deletePlane(UUID id) {
        if (!planes.containsKey(id))
            return false;
        planes.remove(id);
        return true;
    }
}
