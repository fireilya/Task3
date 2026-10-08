package ru.Perminov_Ilya.NauJava.RepositoryPackage;

import ru.Perminov_Ilya.NauJava.DBO.PlaneDBO;
import ru.Perminov_Ilya.NauJava.Infrastructure.PlaneDBImitator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PlaneRepository implements
        CRUDRepository<PlaneDBO, UUID> {

    private final PlaneDBImitator db;

    public PlaneRepository(PlaneDBImitator db) {
        this.db = db;
    }

    @Override
    public UUID create(PlaneDBO dbo) {
        return db.createPlane(dbo);
    }

    @Override
    public PlaneDBO read(UUID id) {
        return db.readPlane(id);
    }

    public List<PlaneDBO> readAll() {
        return db.readAllPlanes();
    }

    @Override
    public UUID update(PlaneDBO dbo) {
        return db.updatePlane(dbo);
    }

    @Override
    public boolean delete(UUID id) {
        return db.deletePlane(id);
    }
}
