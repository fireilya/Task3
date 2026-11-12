package ru.Perminov_Ilya.NauJava;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.Perminov_Ilya.NauJava.entities.User;
import ru.Perminov_Ilya.NauJava.entities.enums.UserRole;
import ru.Perminov_Ilya.NauJava.repository.UserRepository;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("UI: вход и выход из FireBlob (Selenium WebDriver)")
class LoginLogoutUiTest {

    private static final String PASSWORD = "UiTestPassword123";
    private static final String WRONG_PASSWORD = "definitely-wrong-password";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private static final By LOGIN_INPUT = By.name("username");
    private static final By PASSWORD_INPUT = By.name("password");
    private static final By SUBMIT_BUTTON = By.cssSelector("button[type='submit']");
    private static final By PAGE_TITLE = By.cssSelector("h1");
    private static final By ERROR_BLOCK = By.cssSelector(".error");
    private static final By SUCCESS_BLOCK = By.cssSelector(".success");
    private static final By HEADER_TITLE = By.cssSelector(".header-title");
    private static final By USER_NAME = By.cssSelector(".user-name");
    private static final By LOGOUT_BUTTON = By.cssSelector(".logout-button");

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;
    private String userLogin;
    private UUID userId;

    @BeforeAll
    void startBrowserAndPrepareUser() {
        baseUrl = "http://localhost:" + port;

        var suffix = UUID.randomUUID().toString().substring(0, 8);
        userLogin = "ui-user-" + suffix + "@fireblob.test";

        var user = new User();
        user.setTag("ui-user-" + suffix);
        user.setLoginEmail(userLogin);
        user.setPasswordHash(passwordEncoder.encode(PASSWORD));
        user.setName("UI Test User");
        user.setRole(UserRole.USER);
        user.setRegistrationDate(Instant.parse("2026-01-01T10:00:00Z"));
        userId = userRepository.save(user).getId();

        driver = createDriver();
        wait = new WebDriverWait(driver, TIMEOUT);
    }

    @BeforeEach
    void openLoginPageAsAnonymous() {
        driver.manage().deleteAllCookies();
        driver.get(baseUrl + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_INPUT));
    }

    @AfterAll
    void stopBrowserAndCleanUp() {
        if (driver != null)
            driver.quit();
        if (userId != null)
            userRepository.deleteById(userId);
    }

    private WebDriver createDriver() {
        var options = new ChromeOptions();
        if (Boolean.getBoolean("headless"))
            options.addArguments("--headless=new");
        options.addArguments("--window-size=1280,900");
        options.addArguments("--remote-allow-origins=*");

        var driverPath = System.getProperty("chrome.driver");
        if (driverPath != null && !driverPath.isBlank())
            return new ChromeDriver(chromeDriverService(driverPath), options);

        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(options);
    }

    private ChromeDriverService chromeDriverService(String driverPath) {
        var driverFile = new File(driverPath);
        if (!driverFile.isFile())
            throw new IllegalStateException(
                    "Не найден chromedriver.exe по пути " + driverFile.getAbsolutePath());

        return new ChromeDriverService.Builder()
                .usingDriverExecutable(driverFile)
                .build();
    }

    private void submitLoginForm(String login, String password) {
        var loginInput = driver.findElement(LOGIN_INPUT);
        loginInput.clear();
        loginInput.sendKeys(login);

        var passwordInput = driver.findElement(PASSWORD_INPUT);
        passwordInput.clear();
        passwordInput.sendKeys(password);

        driver.findElement(SUBMIT_BUTTON).click();
    }

    private void loginAsTestUser() {
        submitLoginForm(userLogin, PASSWORD);
        wait.until(ExpectedConditions.urlContains("/fireblob"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(USER_NAME));
    }

    @Test
    @DisplayName("Успешный вход: страница входа -> корректные данные -> домашняя страница с именем пользователя")
    void shouldLoginWithValidCredentials() {
        assertAll(
                () -> assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).endsWith("/login"),
                        "тест должен стартовать со страницы входа"),
                () -> assertEquals("Вход", driver.findElement(PAGE_TITLE).getText().trim()),
                () -> assertTrue(driver.findElement(PASSWORD_INPUT).isDisplayed())
        );

        submitLoginForm(userLogin, PASSWORD);

        wait.until(ExpectedConditions.urlContains("/fireblob"));
        var userNameText = wait
                .until(ExpectedConditions.visibilityOfElementLocated(USER_NAME))
                .getText();
        var headerText = driver.findElement(HEADER_TITLE).getText();

        assertAll(
                () -> assertTrue(driver.getCurrentUrl().endsWith("/fireblob"),
                        "ожидали домашнюю страницу /fireblob, но были на " + driver.getCurrentUrl()),
                () -> assertEquals("FireBlob Messenger", headerText),
                () -> assertTrue(userNameText.contains(userLogin),
                        "в шапке должен быть логин пользователя, но было: " + userNameText),
                () -> assertTrue(driver.findElement(LOGOUT_BUTTON).isDisplayed(),
                        "на домашней странице должна быть кнопка выхода")
        );
    }

    @Test
    @DisplayName("Выход: авторизованный пользователь жмёт «Выйти» -> страница входа, доступ к /fireblob закрыт")
    void shouldLogoutAndLoseAccessToProtectedPage() {
        loginAsTestUser();

        driver.findElement(LOGOUT_BUTTON).click();

        wait.until(ExpectedConditions.urlContains("/login"));
        var successText = wait
                .until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_BLOCK))
                .getText();

        assertAll(
                () -> assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains("logout"),
                        "ожидали /login?logout, но были на " + driver.getCurrentUrl()),
                () -> assertTrue(successText.contains("Вы вышли"),
                        "ожидали сообщение о выходе, но было: " + successText),
                () -> assertTrue(driver.findElement(LOGIN_INPUT).isDisplayed(),
                        "форма входа должна снова отображаться"),
                () -> assertTrue(driver.findElement(PASSWORD_INPUT).isDisplayed()),
                () -> assertTrue(driver.findElements(LOGOUT_BUTTON).isEmpty(),
                        "кнопки выхода у анонимного пользователя быть не должно")
        );

        driver.get(baseUrl + "/fireblob");
        wait.until(ExpectedConditions.urlContains("/login"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_INPUT));

        assertAll(
                () -> assertTrue(driver.getCurrentUrl().contains("/login"),
                        "после выхода /fireblob должен редиректить на /login"),
                () -> assertTrue(driver.findElements(USER_NAME).isEmpty(),
                        "шапка авторизованного пользователя не должна отображаться")
        );
    }

    @Test
    @DisplayName("Неверный пароль -> /login?error с сообщением, вход не выполнен")
    void shouldStayOnLoginPageWhenPasswordIsWrong() {
        submitLoginForm(userLogin, WRONG_PASSWORD);

        wait.until(ExpectedConditions.urlContains("error"));
        var errorText = wait
                .until(ExpectedConditions.visibilityOfElementLocated(ERROR_BLOCK))
                .getText();

        assertAll(
                () -> assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains("/login?error"),
                        "ожидали /login?error, но были на " + driver.getCurrentUrl()),
                () -> assertTrue(errorText.contains("Неверный логин или пароль"),
                        "ожидали сообщение об ошибке, но было: " + errorText),
                () -> assertTrue(driver.findElements(LOGOUT_BUTTON).isEmpty()),
                () -> assertTrue(driver.findElement(LOGIN_INPUT).isDisplayed())
        );
    }

    @Test
    @DisplayName("Анонимный заход на /fireblob -> редирект на страницу входа")
    void shouldRedirectAnonymousFromProtectedPageToLogin() {
        driver.get(baseUrl + "/fireblob");

        wait.until(ExpectedConditions.urlContains("/login"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_INPUT));

        assertAll(
                () -> assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains("/login")),
                () -> assertEquals("Вход", driver.findElement(PAGE_TITLE).getText().trim()),
                () -> assertTrue(driver.findElements(LOGOUT_BUTTON).isEmpty())
        );
    }
}