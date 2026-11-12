package ru.Perminov_Ilya.NauJava.infrastructure;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;
import ru.Perminov_Ilya.NauJava.entities.Attachment;
import ru.Perminov_Ilya.NauJava.entities.Message;
import ru.Perminov_Ilya.NauJava.entities.enums.ReportStatus;
import ru.Perminov_Ilya.NauJava.repository.MessageRepository;
import ru.Perminov_Ilya.NauJava.repository.ReportRepository;
import ru.Perminov_Ilya.NauJava.repository.UserRepository;

import java.security.InvalidParameterException;
import java.time.Clock;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.FutureTask;

@Component
public class ReportFiller {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final Clock clock;

    private final DateTimeFormatter dateTimeFormatter;

    public ReportFiller(
            final ReportRepository reportRepository,
            final UserRepository userRepository,
            final MessageRepository messageRepository,
            final Clock clock) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.clock = clock;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(clock.getZone());
    }

    @Transactional
    public void fillReport(UUID reportId) {
        var report = reportRepository.findById(reportId);
        if (report.isEmpty()) {
            throw new InvalidParameterException("report with id " + reportId + " not found");
        }
        var reportEntity = report.get();
        if (reportEntity.getStatus() == ReportStatus.READY) {
            throw new InvalidParameterException("report with id " + reportId + " already filled");
        }

        long reportStartTime = System.currentTimeMillis();

        var numberOfRegisteredUsersTask = new FutureTask<>(this::getNumberOfRegisteredUsers);
        var messageListTask = new FutureTask<>(this::getMessagesList);
        var numberOfRegisteredUsersThread = new Thread(numberOfRegisteredUsersTask);
        var messageListThread = new Thread(messageListTask);

        try {
            numberOfRegisteredUsersThread.start();
            messageListThread.start();

            var numberOfRegisteredUsersBlock = numberOfRegisteredUsersTask.get();
            var messageListBlock = messageListTask.get();

            long totalTime = System.currentTimeMillis() - reportStartTime;

            String reportHtml = buildReportHtml(
                    numberOfRegisteredUsersBlock,
                    messageListBlock,
                    totalTime
            );

            reportEntity.setContent(reportHtml);
            reportEntity.setStatus(ReportStatus.READY);
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            numberOfRegisteredUsersTask.cancel(true);
            messageListTask.cancel(true);
            reportEntity.setStatus(ReportStatus.ERROR);
        } finally {
            reportRepository.save(reportEntity);
        }
    }

    private String buildReportHtml(
            ReportBlockResult usersBlock,
            ReportBlockResult messagesBlock,
            long totalTime
    ) {
        return "<!DOCTYPE html>" +
                "<html lang=\"ru\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>Отчёт приложения</title>" +
                "<link rel=\"stylesheet\" href=\"/css/report.css\">" +
                "</head>" +
                "<body>" +
                "<div class=\"report-container\">" +
                "<header class=\"report-header\">" +
                "<div>" +
                "<h1 class=\"report-title\">Отчёт приложения</h1>" +
                "<p class=\"report-subtitle\">Сформирован: " +
                HtmlUtils.htmlEscape(formatInstant(Instant.now(clock))) +
                "</p>" +
                "</div>" +
                "<div class=\"status-badge\">" +
                ReportStatus.READY.getUserFriendlyName() +
                "</div>" +
                "</header>" +
                "<section class=\"report-block total-block\">" +
                "<div class=\"metric-grid\">" +
                buildMetricCard(
                        "Общее время формирования отчёта",
                        totalTime + " мс",
                        "metric-total"
                ) +
                "</div>" +
                "</section>" +
                usersBlock.data() +
                messagesBlock.data() +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private String buildMetricCard(String label, String value, String cssClass) {
        return "<div class=\"metric-card " + cssClass + "\">" +
                "<div class=\"metric-label\">" + HtmlUtils.htmlEscape(label) + "</div>" +
                "<div class=\"metric-value\">" + HtmlUtils.htmlEscape(value) + "</div>" +
                "</div>";
    }

    private ReportBlockResult getNumberOfRegisteredUsers() {
        long startTime = System.currentTimeMillis();
        long usersCount = userRepository.count();
        long executionTime = System.currentTimeMillis() - startTime;

        String sb = "<section class=\"report-block users-block\">" +
                "<h2 class=\"section-title\">Пользователи</h2>" +
                "<div class=\"metric-grid\">" +
                buildMetricCard(
                        "Количество зарегистрированных пользователей",
                        String.valueOf(usersCount),
                        "metric-users"
                ) +
                buildMetricCard(
                        "Время получения количества пользователей",
                        executionTime + " мс",
                        "metric-time-users"
                ) +
                "</div>" +
                "</section>";

        return new ReportBlockResult(sb, executionTime);
    }

    private ReportBlockResult getMessagesList() {
        long startTime = System.currentTimeMillis();
        var messages = messageRepository.findAllByOrderBySentAtDesc();
        String tableHtml = buildMessagesTable(messages);
        long executionTime = System.currentTimeMillis() - startTime;

        int messagesCount = messages == null ? 0 : messages.size();

        String sb = "<section class=\"report-block messages-block\">" +
                "<h2 class=\"section-title\">Список сообщений</h2>" +
                "<div class=\"metric-grid\">" +
                buildMetricCard(
                        "Количество сообщений",
                        String.valueOf(messagesCount),
                        "metric-messages-count"
                ) +
                buildMetricCard(
                        "Время формирования списка сообщений",
                        executionTime + " мс",
                        "metric-time-messages"
                ) +
                "</div>" +
                "<div class=\"table-wrapper\">" +
                tableHtml +
                "</div>" +
                "</section>";

        return new ReportBlockResult(sb, executionTime);
    }

    private String buildMessagesTable(List<Message> messages) {
        var sb = new StringBuilder();
        sb.append("<table class=\"messages-table\">")
                .append("<thead>")
                .append("<tr>")
                .append("<th>ID сообщения</th>")
                .append("<th>ID чата</th>")
                .append("<th>ID отправителя</th>")
                .append("<th>Содержание</th>")
                .append("<th>Отправлено</th>")
                .append("<th>Отредактировано</th>")
                .append("<th>ID сообщения, на которое отвечает</th>")
                .append("<th>Вложения</th>")
                .append("</tr>")
                .append("</thead>")
                .append("<tbody>");

        if (messages == null || messages.isEmpty()) {
            sb.append("<tr><td colspan=\"8\" class=\"table-cell empty-cell\">-</td></tr>");
        } else {
            for (var message : messages) {
                sb.append("<tr>");
                appendCell(sb, message.getId());
                appendCell(
                        sb,
                        message.getChat() == null
                                ? null
                                : message.getChat().getId()
                );
                appendCell(
                        sb,
                        message.getSender() == null
                                ? null
                                : message.getSender().getId()
                );
                appendCell(sb, message.getContent());
                appendCell(sb, formatInstant(message.getSentAt()));
                appendCell(sb, formatInstant(message.getEditedAt()));
                appendCell(
                        sb,
                        message.getReplyToMessage() == null
                                ? null
                                : message.getReplyToMessage().getId()
                );
                sb.append("<td class=\"table-cell\">")
                        .append(buildAttachmentsCell(message.getAttachments()))
                        .append("</td>");
                sb.append("</tr>");
            }
        }

        sb.append("</tbody>")
                .append("</table>");
        return sb.toString();
    }

    private String formatInstant(Instant instant) {
        if (instant == null) {
            return null;
        }
        return dateTimeFormatter.format(instant);
    }

    private void appendCell(StringBuilder sb, Object value) {
        if (value == null || value.toString().isBlank()) {
            sb.append("<td class=\"table-cell empty-cell\">-</td>");
        } else {
            sb.append("<td class=\"table-cell\">")
                    .append(HtmlUtils.htmlEscape(value.toString()))
                    .append("</td>");
        }
    }

    private String buildAttachmentsCell(List<Attachment> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return "<span class=\"empty-cell\">-</span>";
        }
        var sb = new StringBuilder();
        sb.append("<ul class=\"attachments-list\">");
        for (var attachment : attachments) {
            var name = attachment == null ? null : attachment.getName();
            if (name == null || name.isBlank()) {
                sb.append("<li class=\"attachment-item empty-cell\">-</li>");
            } else {
                sb.append("<li class=\"attachment-item\">")
                        .append(HtmlUtils.htmlEscape(name))
                        .append("</li>");
            }
        }
        sb.append("</ul>");
        return sb.toString();
    }
}