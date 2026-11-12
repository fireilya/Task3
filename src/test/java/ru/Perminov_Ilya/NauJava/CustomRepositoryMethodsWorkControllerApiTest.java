package ru.Perminov_Ilya.NauJava;

import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.Perminov_Ilya.NauJava.entities.Attachment;
import ru.Perminov_Ilya.NauJava.entities.DirectChat;
import ru.Perminov_Ilya.NauJava.entities.Message;
import ru.Perminov_Ilya.NauJava.entities.User;
import ru.Perminov_Ilya.NauJava.entities.enums.AttachmentType;
import ru.Perminov_Ilya.NauJava.entities.enums.UserRole;
import ru.Perminov_Ilya.NauJava.repository.AttachmentRepository;
import ru.Perminov_Ilya.NauJava.repository.DirectChatRepository;
import ru.Perminov_Ilya.NauJava.repository.MessageRepository;
import ru.Perminov_Ilya.NauJava.repository.UserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("REST API /crmw: автотесты на RestAssured")
class CustomRepositoryMethodsWorkControllerApiTest {

    private static final String PASSWORD = "TestPassword123";
    private static final Instant CREATED_AT = Instant.parse("2026-01-01T10:00:00Z");

    private static final long SIZE_LOW = 7_000_000_001L;
    private static final long SIZE_MID = 7_000_000_002L;
    private static final long SIZE_HIGH = 7_000_000_003L;

    private static final String NAME_LOW = "edge-low.audio";
    private static final String NAME_MID = "edge-mid.audio";
    private static final String NAME_HIGH = "edge-high.audio";
    private static final String NAME_WRONG_TYPE = "wrong-type.photo";
    private static final String NAME_ZERO = "zero-size.file";

    private static final String CONTENT_FIRST = "Hello world";
    private static final String CONTENT_SECOND = "RestAssured check";

    private static final Pattern CSRF_PATTERN =
            Pattern.compile("name=\"_csrf\"[^>]*value=\"([^\"]+)\"");

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DirectChatRepository directChatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final List<UUID> userIds = new ArrayList<>();
    private final List<UUID> messageIds = new ArrayList<>();
    private final List<UUID> attachmentIds = new ArrayList<>();

    private User userA;
    private User userB;
    private User lonelyUser;
    private DirectChat directChat;
    private UUID chatId;

    private SessionFilter adminSession;
    private SessionFilter userSession;

    @BeforeAll
    void prepareDataAndSessions() {
        setUpRestAssured();

        var admin = saveUser("api-admin", UserRole.ADMIN);
        var regular = saveUser("api-user", UserRole.USER);
        userA = saveUser("api-user-a", UserRole.USER);
        userB = saveUser("api-user-b", UserRole.USER);
        lonelyUser = saveUser("api-lonely", UserRole.USER);

        directChat = new DirectChat();
        directChat.setTag("api-dc-" + UUID.randomUUID().toString().substring(0, 8));
        directChat.setTitle("API test direct chat");
        directChat.setCreatedAt(CREATED_AT);
        directChat.setUserA(userA);
        directChat.setUserB(userB);
        directChat = directChatRepository.save(directChat);
        chatId = directChat.getId();

        var firstMessage = saveMessage(CONTENT_FIRST);
        saveMessage(CONTENT_SECOND);

        saveAttachment(firstMessage, NAME_LOW, AttachmentType.AUDIO, SIZE_LOW);
        saveAttachment(firstMessage, NAME_MID, AttachmentType.AUDIO, SIZE_MID);
        saveAttachment(firstMessage, NAME_HIGH, AttachmentType.AUDIO, SIZE_HIGH);
        saveAttachment(firstMessage, NAME_WRONG_TYPE, AttachmentType.PHOTO, SIZE_MID);
        saveAttachment(firstMessage, NAME_ZERO, AttachmentType.FILE, 0L);

        adminSession = login(admin.getLoginEmail());
        userSession = login(regular.getLoginEmail());
    }

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterAll
    void cleanUp() {
        attachmentRepository.deleteAllById(attachmentIds);
        messageRepository.deleteAllById(messageIds);
        if (chatId != null)
            directChatRepository.deleteById(chatId);
        userRepository.deleteAllById(userIds);
    }

    private RequestSpecification asAdmin() {
        return given().filter(adminSession).redirects().follow(false);
    }

    private SessionFilter login(String loginEmail) {
        var filter = new SessionFilter();

        var loginPage = given()
                .filter(filter)
                .redirects().follow(false)
                .when()
                .get("/login")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        var matcher = CSRF_PATTERN.matcher(loginPage);
        if (!matcher.find())
            throw new IllegalStateException("Не удалось найти _csrf на странице /login");

        given()
                .filter(filter)
                .contentType(ContentType.URLENC)
                .formParam("username", loginEmail)
                .formParam("password", PASSWORD)
                .formParam("_csrf", matcher.group(1))
                .redirects().follow(false)
                .when()
                .post("/login")
                .then()
                .statusCode(302)
                .header("Location", endsWith("/"));

        return filter;
    }

    private User saveUser(String prefix, UserRole role) {
        var suffix = UUID.randomUUID().toString().substring(0, 8);
        var user = new User();
        user.setTag(prefix + "-" + suffix);
        user.setLoginEmail(prefix + "-" + suffix + "@fireblob.test");
        user.setPasswordHash(passwordEncoder.encode(PASSWORD));
        user.setName(prefix + " test user");
        user.setRole(role);
        user.setRegistrationDate(CREATED_AT);
        var saved = userRepository.save(user);
        userIds.add(saved.getId());
        return saved;
    }

    private Message saveMessage(String content) {
        var message = new Message();
        message.setChat(directChat);
        message.setSender(userA);
        message.setContent(content);
        message.setSentAt(CREATED_AT);
        var saved = messageRepository.save(message);
        messageIds.add(saved.getId());
        return saved;
    }

    private void saveAttachment(Message message, String name, AttachmentType type, long size) {
        var attachment = new Attachment();
        attachment.setMessage(message);
        attachment.setName(name);
        attachment.setType(type);
        attachment.setDataSizeBytes(size);
        attachment.setDataUrl("https://files.fireblob.test/" + name);
        var saved = attachmentRepository.save(attachment);
        attachmentIds.add(saved.getId());
    }

    @Test
    @DisplayName("attachment: границы BETWEEN включительны, тип учитывается -> 200")
    void attachmentShouldIncludeBothBoundsAndFilterByType() {
        asAdmin()
                .queryParam("type", "AUDIO")
                .queryParam("dataSizeBytesAfter", SIZE_LOW)
                .queryParam("dataSizeBytesBefore", SIZE_HIGH)
                .when()
                .get("/crmw/attachment")
                .then()
                .statusCode(200)
                .body(containsString(NAME_LOW))
                .body(containsString(NAME_MID))
                .body(containsString(NAME_HIGH))
                .body(not(containsString(NAME_WRONG_TYPE)));
    }

    @Test
    @DisplayName("attachment: after == before (одна точка диапазона) -> 200 и ровно одно имя")
    void attachmentShouldWorkWhenRangeIsSinglePoint() {
        asAdmin()
                .queryParam("type", "AUDIO")
                .queryParam("dataSizeBytesAfter", SIZE_MID)
                .queryParam("dataSizeBytesBefore", SIZE_MID)
                .when()
                .get("/crmw/attachment")
                .then()
                .statusCode(200)
                .body(equalTo(NAME_MID));
    }

    @Test
    @DisplayName("attachment: тот же размер, но другой тип -> 200 и только вложение этого типа")
    void attachmentShouldFilterByType() {
        asAdmin()
                .queryParam("type", "PHOTO")
                .queryParam("dataSizeBytesAfter", SIZE_MID)
                .queryParam("dataSizeBytesBefore", SIZE_MID)
                .when()
                .get("/crmw/attachment")
                .then()
                .statusCode(200)
                .body(equalTo(NAME_WRONG_TYPE));
    }

    @Test
    @DisplayName("attachment: размер 0 — нижняя граница допустимых значений -> 200")
    void attachmentShouldAcceptZeroSize() {
        asAdmin()
                .queryParam("type", "FILE")
                .queryParam("dataSizeBytesAfter", 0)
                .queryParam("dataSizeBytesBefore", 0)
                .when()
                .get("/crmw/attachment")
                .then()
                .statusCode(200)
                .body(containsString(NAME_ZERO));
    }

    @Test
    @DisplayName("attachment: перевёрнутый диапазон (after > before) -> 404")
    void attachmentShouldReturn404WhenRangeReversed() {
        asAdmin()
                .queryParam("type", "AUDIO")
                .queryParam("dataSizeBytesAfter", SIZE_HIGH)
                .queryParam("dataSizeBytesBefore", SIZE_LOW)
                .when()
                .get("/crmw/attachment")
                .then()
                .statusCode(404)
                .body(containsString("Oh no, you misclick somewhere!"));
    }

    @Test
    @DisplayName("attachment: диапазон, в котором ничего нет -> 404")
    void attachmentShouldReturn404WhenNothingFound() {
        asAdmin()
                .queryParam("type", "AUDIO")
                .queryParam("dataSizeBytesAfter", SIZE_HIGH + 1000)
                .queryParam("dataSizeBytesBefore", SIZE_HIGH + 2000)
                .when()
                .get("/crmw/attachment")
                .then()
                .statusCode(404)
                .body(containsString("Look at this:"));
    }

    @Test
    @DisplayName("attachment: отрицательный диапазон не валидируется контроллером -> 404")
    void attachmentShouldReturn404ForNegativeRange() {
        asAdmin()
                .queryParam("type", "AUDIO")
                .queryParam("dataSizeBytesAfter", -100)
                .queryParam("dataSizeBytesBefore", -1)
                .when()
                .get("/crmw/attachment")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("attachment: не передан обязательный type -> 400")
    void attachmentShouldReturn400WhenTypeMissing() {
        asAdmin()
                .queryParam("dataSizeBytesAfter", SIZE_LOW)
                .queryParam("dataSizeBytesBefore", SIZE_HIGH)
                .when()
                .get("/crmw/attachment")
                .then()
                .statusCode(400)
                .body(containsString("type"));
    }

    @Test
    @DisplayName("attachment: не переданы оба размера -> 400")
    void attachmentShouldReturn400WhenSizesMissing() {
        asAdmin()
                .queryParam("type", "AUDIO")
                .when()
                .get("/crmw/attachment")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("attachment: неизвестное значение enum -> 500 (перехватывается общим хендлером)")
    void attachmentShouldReturn500ForUnknownEnumValue() {
        asAdmin()
                .queryParam("type", "UNKNOWN_TYPE")
                .queryParam("dataSizeBytesAfter", SIZE_LOW)
                .queryParam("dataSizeBytesBefore", SIZE_HIGH)
                .when()
                .get("/crmw/attachment")
                .then()
                .statusCode(500)
                .body(containsString("Oh, it looks like our server is broken."));
    }

    @Test
    @DisplayName("attachment: enum в нижнем регистре не распознаётся -> 500")
    void attachmentShouldReturn500ForLowerCaseEnumValue() {
        asAdmin()
                .queryParam("type", "audio")
                .queryParam("dataSizeBytesAfter", SIZE_LOW)
                .queryParam("dataSizeBytesBefore", SIZE_HIGH)
                .when()
                .get("/crmw/attachment")
                .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("attachment: нечисловой размер -> 500")
    void attachmentShouldReturn500ForNonNumericSize() {
        asAdmin()
                .queryParam("type", "AUDIO")
                .queryParam("dataSizeBytesAfter", "abc")
                .queryParam("dataSizeBytesBefore", SIZE_HIGH)
                .when()
                .get("/crmw/attachment")
                .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("DC: чат существует -> 200 и id чата в теле")
    void directChatShouldReturnId() {
        asAdmin()
                .queryParam("userA", userA.getId())
                .queryParam("userB", userB.getId())
                .when()
                .get("/crmw/DC")
                .then()
                .statusCode(200)
                .body(equalTo(chatId.toString()));
    }

    @Test
    @DisplayName("DC: один и тот же пользователь дважды -> 400")
    void directChatShouldReturn400ForSameUser() {
        asAdmin()
                .queryParam("userA", userA.getId())
                .queryParam("userB", userA.getId())
                .when()
                .get("/crmw/DC")
                .then()
                .statusCode(400)
                .body(containsString("User can't have direct chat with yourself"));
    }

    @Test
    @DisplayName("DC: userA не существует -> 404 userA not found")
    void directChatShouldReturn404WhenUserAMissing() {
        asAdmin()
                .queryParam("userA", UUID.randomUUID())
                .queryParam("userB", userB.getId())
                .when()
                .get("/crmw/DC")
                .then()
                .statusCode(404)
                .body(containsString("userA not found"));
    }

    @Test
    @DisplayName("DC: userB не существует -> 404 userB not found")
    void directChatShouldReturn404WhenUserBMissing() {
        asAdmin()
                .queryParam("userA", userA.getId())
                .queryParam("userB", UUID.randomUUID())
                .when()
                .get("/crmw/DC")
                .then()
                .statusCode(404)
                .body(containsString("userB not found"));
    }

    @Test
    @DisplayName("DC: пользователи перепутаны местами -> 404 (порядок в findByUserAAndUserB важен)")
    void directChatShouldReturn404WhenUsersSwapped() {
        asAdmin()
                .queryParam("userA", userB.getId())
                .queryParam("userB", userA.getId())
                .when()
                .get("/crmw/DC")
                .then()
                .statusCode(404)
                .body(containsString("direct chat not found"));
    }

    @Test
    @DisplayName("DC: оба пользователя есть, но чата между ними нет -> 404")
    void directChatShouldReturn404WhenChatAbsent() {
        asAdmin()
                .queryParam("userA", userA.getId())
                .queryParam("userB", lonelyUser.getId())
                .when()
                .get("/crmw/DC")
                .then()
                .statusCode(404)
                .body(containsString("direct chat not found"));
    }

    @Test
    @DisplayName("DC: невалидный UUID -> 500")
    void directChatShouldReturn500ForInvalidUuid() {
        asAdmin()
                .queryParam("userA", "not-a-uuid")
                .queryParam("userB", userB.getId())
                .when()
                .get("/crmw/DC")
                .then()
                .statusCode(500)
                .body(containsString("Oh, it looks like our server is broken."));
    }

    @Test
    @DisplayName("DC: не передан userB -> 400")
    void directChatShouldReturn400WhenParamMissing() {
        asAdmin()
                .queryParam("userA", userA.getId())
                .when()
                .get("/crmw/DC")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("message: подстрока найдена -> 200 и содержимое сообщения")
    void messageShouldReturnFoundContent() {
        asAdmin()
                .queryParam("chatID", chatId)
                .queryParam("messagePart", "Hello")
                .when()
                .get("/crmw/message")
                .then()
                .statusCode(200)
                .body(containsString(CONTENT_FIRST))
                .body(not(containsString(CONTENT_SECOND)));
    }

    @Test
    @DisplayName("message: пустая подстрока (параметр передан) -> 200 и все сообщения чата")
    void messageShouldReturnAllMessagesForEmptySearch() {
        asAdmin()
                .queryParam("chatID", chatId)
                .queryParam("messagePart", "")
                .when()
                .get("/crmw/message")
                .then()
                .statusCode(200)
                .body(containsString(CONTENT_FIRST))
                .body(containsString(CONTENT_SECOND));
    }

    @Test
    @DisplayName("message: символ % работает как wildcard LIKE -> 200 и все сообщения")
    void messageShouldTreatPercentAsLikeWildcard() {
        asAdmin()
                .queryParam("chatID", chatId)
                .queryParam("messagePart", "%")
                .when()
                .get("/crmw/message")
                .then()
                .statusCode(200)
                .body(containsString(CONTENT_FIRST))
                .body(containsString(CONTENT_SECOND));
    }

    @Test
    @DisplayName("message: совпадений нет -> 200 и текст-заглушка, а не 404")
    void messageShouldReturn200WithPlaceholderWhenNothingFound() {
        asAdmin()
                .queryParam("chatID", chatId)
                .queryParam("messagePart", "definitely-absent-text")
                .when()
                .get("/crmw/message")
                .then()
                .statusCode(200)
                .body(equalTo("no messages found"));
    }

    @Test
    @DisplayName("message: LIKE регистрозависим -> 200 no messages found")
    void messageSearchShouldBeCaseSensitive() {
        asAdmin()
                .queryParam("chatID", chatId)
                .queryParam("messagePart", "hello world")
                .when()
                .get("/crmw/message")
                .then()
                .statusCode(200)
                .body(equalTo("no messages found"));
    }

    @Test
    @DisplayName("message: чат не существует -> 404")
    void messageShouldReturn404WhenChatMissing() {
        asAdmin()
                .queryParam("chatID", UUID.randomUUID())
                .queryParam("messagePart", "Hello")
                .when()
                .get("/crmw/message")
                .then()
                .statusCode(404)
                .body(containsString("Requested chat is not exist"));
    }

    @Test
    @DisplayName("message: невалидный chatID -> 500")
    void messageShouldReturn500ForInvalidUuid() {
        asAdmin()
                .queryParam("chatID", "broken-uuid")
                .queryParam("messagePart", "Hello")
                .when()
                .get("/crmw/message")
                .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("message: не передан messagePart -> 400")
    void messageShouldReturn400WhenParamMissing() {
        asAdmin()
                .queryParam("chatID", chatId)
                .when()
                .get("/crmw/message")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("без аутентификации -> 302 на /login (form-login, а не 401)")
    void shouldRedirectAnonymousToLogin() {
        given()
                .redirects().follow(false)
                .queryParam("chatID", chatId)
                .queryParam("messagePart", "Hello")
                .when()
                .get("/crmw/message")
                .then()
                .statusCode(302)
                .header("Location", endsWith("/login"));
    }

    @Test
    @DisplayName("роль USER на /crmw/** -> 403")
    void shouldForbidRegularUser() {
        given()
                .filter(userSession)
                .redirects().follow(false)
                .queryParam("chatID", chatId)
                .queryParam("messagePart", "Hello")
                .when()
                .get("/crmw/message")
                .then()
                .statusCode(403);
    }
}