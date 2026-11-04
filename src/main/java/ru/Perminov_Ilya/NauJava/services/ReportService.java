package ru.Perminov_Ilya.NauJava.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import ru.Perminov_Ilya.NauJava.entities.Report;
import ru.Perminov_Ilya.NauJava.entities.enums.ReportStatus;
import ru.Perminov_Ilya.NauJava.infrastructure.ReportFiller;
import ru.Perminov_Ilya.NauJava.repository.ReportRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final ReportFiller reportFiller;

    public ReportService(ReportRepository reportRepository, ReportFiller reportFiller) {
        this.reportRepository = reportRepository;
        this.reportFiller = reportFiller;
    }

    public Report getReportById(UUID reportId) {
        var report = reportRepository.findById(reportId);
        if (report.isEmpty()) {
            throw new EntityNotFoundException("Report with id " + reportId + " not found");
        }
        return report.get();
    }

    public UUID createReport() {
        var report = new Report();
        report.setStatus(ReportStatus.CREATED);
        reportRepository.save(report);
        CompletableFuture.runAsync(() -> reportFiller.fillReport(report.getId()));
        return report.getId();
    }
}
