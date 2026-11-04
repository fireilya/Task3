package ru.Perminov_Ilya.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.Perminov_Ilya.NauJava.entities.Report;

import java.util.UUID;

@RepositoryRestResource
public interface ReportRepository extends CrudRepository<Report, UUID> {

}
