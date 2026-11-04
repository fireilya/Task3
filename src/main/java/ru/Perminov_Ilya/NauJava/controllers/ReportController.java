package ru.Perminov_Ilya.NauJava.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.Perminov_Ilya.NauJava.services.ReportService;

import java.util.UUID;

@Controller
@RequestMapping("/fireblob/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(final ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/new")
    public String createNewReport() {
        var newReportId = reportService.createReport();
        // Редиректим на наш эндпоинт просмотра, а не на /reports
        return "redirect:/fireblob/api/reports/" + newReportId;
    }

    @GetMapping("/{id}")
    public Object getReport(@PathVariable UUID id) {
        var report = reportService.getReportById(id);

        return switch (report.getStatus()) {
            case CREATED -> "redirect:/reports/report-in-progress.html";
            case ERROR -> "redirect:/reports/report-error.html";
            case READY -> ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(report.getContent());
        };
    }
}