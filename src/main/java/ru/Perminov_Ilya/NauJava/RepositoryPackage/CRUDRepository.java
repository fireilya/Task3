package ru.Perminov_Ilya.NauJava.RepositoryPackage;

public interface CRUDRepository<DBO, ID> {
    ID create(DBO createRequest);

    DBO read(ID id);

    ID update(DBO updateRequest);

    boolean delete(ID id);
}
