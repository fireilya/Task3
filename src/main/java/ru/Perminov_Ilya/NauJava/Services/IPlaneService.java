package ru.Perminov_Ilya.NauJava.Services;

import ru.Perminov_Ilya.NauJava.Requests.PlaneCreateRequest;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public interface IPlaneService {
    UUID createPlane(@NonNull PlaneCreateRequest createRequest);
    String getPlanesList();
    String getAllPlaneInfo(@NonNull final UUID id);
    String getPlaneFM(@NonNull final UUID id);
    String getCurrentPlaneState(@NonNull final UUID id);
    String setPlaneThrust(@NonNull final UUID id, double thrust);
    String setPlaneWep(@NonNull final UUID id, boolean wepActive);
    String setPlaneAngleOfAttack(@NonNull final UUID id, double angleInDegrees);
    boolean deletePlane(@NonNull final UUID id);
}
