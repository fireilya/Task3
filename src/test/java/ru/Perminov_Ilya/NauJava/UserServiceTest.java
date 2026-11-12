package ru.Perminov_Ilya.NauJava;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.Perminov_Ilya.NauJava.entities.User;
import ru.Perminov_Ilya.NauJava.entities.enums.UserRole;
import ru.Perminov_Ilya.NauJava.repository.UserRepository;
import ru.Perminov_Ilya.NauJava.services.UserService;

import java.security.InvalidParameterException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("UserService: unit-тесты")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final Instant FIXED_NOW = Instant.parse("2026-01-01T10:00:00Z");
    private static final Clock FIXED_CLOCK = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String TAG = "ilya";
    private static final String LOGIN = "ilya@fireblob.ru";
    private static final String RAW_PASSWORD = "qwerty123";
    private static final String PASSWORD_HASH = "$2a$10$someBcryptHashValue";
    private static final String NAME = "Илья Перминов";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder, FIXED_CLOCK);
    }

    private User existingUser() {
        var user = new User();
        user.setId(USER_ID);
        user.setTag(TAG);
        user.setLoginEmail(LOGIN);
        user.setPasswordHash(PASSWORD_HASH);
        user.setName(NAME);
        user.setRole(UserRole.USER);
        user.setRegistrationDate(FIXED_NOW);
        return user;
    }

    private void stubSuccessfulChecks() {
        when(userRepository.existsByLoginEmail(LOGIN)).thenReturn(false);
        when(userRepository.existsByTag(TAG)).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(PASSWORD_HASH);
    }

    private void stubSaveAssigningId() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            var toSave = invocation.getArgument(0, User.class);
            toSave.setId(USER_ID);
            return toSave;
        });
    }

    @Nested
    @DisplayName("registerUser: положительные сценарии")
    class RegisterUserPositive {

        @Test
        @DisplayName("успешная регистрация: все поля заполнены, пароль закодирован, дата взята из Clock")
        void shouldSaveUserWithAllFieldsFilled() {
            stubSuccessfulChecks();
            stubSaveAssigningId();

            var result = userService.registerUser(TAG, LOGIN, RAW_PASSWORD, NAME);

            var captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            var saved = captor.getValue();

            assertAll(
                    () -> assertEquals(TAG, saved.getTag()),
                    () -> assertEquals(LOGIN, saved.getLoginEmail()),
                    () -> assertEquals(PASSWORD_HASH, saved.getPasswordHash()),
                    () -> assertEquals(NAME, saved.getName()),
                    () -> assertEquals(UserRole.USER, saved.getRole()),
                    () -> assertEquals(FIXED_NOW, saved.getRegistrationDate()),
                    () -> assertNull(saved.getAvatarUrl(), "avatarUrl при регистрации не заполняется"),
                    () -> assertSame(saved, result, "сервис должен вернуть сохранённую сущность"),
                    () -> assertEquals(USER_ID, result.getId())
            );
        }

        @Test
        @DisplayName("пароль не попадает в БД в открытом виде и кодируется ровно один раз")
        void shouldNeverStoreRawPassword() {
            stubSuccessfulChecks();
            stubSaveAssigningId();

            var result = userService.registerUser(TAG, LOGIN, RAW_PASSWORD, NAME);

            assertNotEquals(RAW_PASSWORD, result.getPasswordHash());
            assertEquals(PASSWORD_HASH, result.getPasswordHash());
            verify(passwordEncoder, times(1)).encode(RAW_PASSWORD);
            verifyNoMoreInteractions(passwordEncoder);
        }

        @Test
        @DisplayName("возвращается именно тот объект, который вернул репозиторий")
        void shouldReturnEntityFromRepository() {
            stubSuccessfulChecks();
            var persisted = existingUser();
            when(userRepository.save(any(User.class))).thenReturn(persisted);

            var result = userService.registerUser(TAG, LOGIN, RAW_PASSWORD, NAME);

            assertSame(persisted, result);
        }

        @Test
        @DisplayName("порядок действий: проверка логина -> проверка тэга -> кодирование -> сохранение")
        void shouldCheckUniquenessBeforeEncodingAndSaving() {
            stubSuccessfulChecks();
            stubSaveAssigningId();

            userService.registerUser(TAG, LOGIN, RAW_PASSWORD, NAME);

            var inOrder = inOrder(userRepository, passwordEncoder);
            inOrder.verify(userRepository).existsByLoginEmail(LOGIN);
            inOrder.verify(userRepository).existsByTag(TAG);
            inOrder.verify(passwordEncoder).encode(RAW_PASSWORD);
            inOrder.verify(userRepository).save(any(User.class));
            inOrder.verifyNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("registerUser: негативные сценарии (валидация параметров)")
    class RegisterUserValidation {

        @ParameterizedTest(name = "tag = [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        @DisplayName("пустой tag -> InvalidParameterException, к БД и энкодеру обращений нет")
        void shouldRejectBlankTag(String tag) {
            var ex = assertThrows(
                    InvalidParameterException.class,
                    () -> userService.registerUser(tag, LOGIN, RAW_PASSWORD, NAME));

            assertEquals("tag must not be empty", ex.getMessage());
            verifyNoInteractions(userRepository, passwordEncoder);
        }

        @ParameterizedTest(name = "login = [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        @DisplayName("пустой login -> InvalidParameterException, к БД и энкодеру обращений нет")
        void shouldRejectBlankLogin(String login) {
            var ex = assertThrows(
                    InvalidParameterException.class,
                    () -> userService.registerUser(TAG, login, RAW_PASSWORD, NAME));

            assertEquals("login must not be empty", ex.getMessage());
            verifyNoInteractions(userRepository, passwordEncoder);
        }

        @ParameterizedTest(name = "password = [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        @DisplayName("пустой password -> InvalidParameterException, пароль не кодируется")
        void shouldRejectBlankPassword(String password) {
            var ex = assertThrows(
                    InvalidParameterException.class,
                    () -> userService.registerUser(TAG, LOGIN, password, NAME));

            assertEquals("password must not be empty", ex.getMessage());
            verifyNoInteractions(userRepository, passwordEncoder);
        }

        @ParameterizedTest(name = "name = [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        @DisplayName("пустой name -> InvalidParameterException, пользователь не сохраняется")
        void shouldRejectBlankName(String name) {
            var ex = assertThrows(
                    InvalidParameterException.class,
                    () -> userService.registerUser(TAG, LOGIN, RAW_PASSWORD, name));

            assertEquals("name must not be empty", ex.getMessage());
            verifyNoInteractions(userRepository, passwordEncoder);
        }
    }

    @Nested
    @DisplayName("registerUser: негативные сценарии (уникальность)")
    class RegisterUserUniqueness {

        @Test
        @DisplayName("логин занят -> InvalidParameterException, тэг даже не проверяется")
        void shouldRejectAlreadyUsedLogin() {
            when(userRepository.existsByLoginEmail(LOGIN)).thenReturn(true);

            var ex = assertThrows(
                    InvalidParameterException.class,
                    () -> userService.registerUser(TAG, LOGIN, RAW_PASSWORD, NAME));

            assertEquals("User with this login already exists", ex.getMessage());
            verify(userRepository, never()).existsByTag(anyString());
            verify(userRepository, never()).save(any(User.class));
            verifyNoInteractions(passwordEncoder);
        }

        @Test
        @DisplayName("тэг занят -> InvalidParameterException, пользователь не сохраняется")
        void shouldRejectAlreadyUsedTag() {
            when(userRepository.existsByLoginEmail(LOGIN)).thenReturn(false);
            when(userRepository.existsByTag(TAG)).thenReturn(true);

            var ex = assertThrows(
                    InvalidParameterException.class,
                    () -> userService.registerUser(TAG, LOGIN, RAW_PASSWORD, NAME));

            assertEquals("User with this tag already exists", ex.getMessage());
            verify(userRepository, never()).save(any(User.class));
            verifyNoInteractions(passwordEncoder);
        }

        @Test
        @DisplayName("заняты и логин, и тэг -> сообщение про логин (проверка логина идёт первой)")
        void shouldReportLoginConflictFirst() {
            when(userRepository.existsByLoginEmail(LOGIN)).thenReturn(true);

            var ex = assertThrows(
                    InvalidParameterException.class,
                    () -> userService.registerUser(TAG, LOGIN, RAW_PASSWORD, NAME));

            assertEquals("User with this login already exists", ex.getMessage());
            verify(userRepository, never()).existsByTag(anyString());
            verifyNoInteractions(passwordEncoder);
        }
    }

    @Nested
    @DisplayName("registerUser: исключения нижнего слоя пробрасываются наверх")
    class RegisterUserRepositoryErrors {

        @Test
        @DisplayName("save падает на unique-констрейнте -> DataIntegrityViolationException не глотается")
        void shouldPropagateExceptionFromSave() {
            stubSuccessfulChecks();
            var failure = new DataIntegrityViolationException(
                    "duplicate key value violates unique constraint \"users_login_email_key\"");
            when(userRepository.save(any(User.class))).thenThrow(failure);

            var ex = assertThrows(
                    DataIntegrityViolationException.class,
                    () -> userService.registerUser(TAG, LOGIN, RAW_PASSWORD, NAME));

            assertSame(failure, ex);
        }

        @Test
        @DisplayName("БД недоступна на проверке логина -> исключение пробрасывается, save не вызывается")
        void shouldPropagateExceptionFromUniquenessCheck() {
            var failure = new DataAccessResourceFailureException("connection refused");
            when(userRepository.existsByLoginEmail(LOGIN)).thenThrow(failure);

            var ex = assertThrows(
                    DataAccessResourceFailureException.class,
                    () -> userService.registerUser(TAG, LOGIN, RAW_PASSWORD, NAME));

            assertSame(failure, ex);
            verify(userRepository, never()).save(any(User.class));
            verifyNoInteractions(passwordEncoder);
        }
    }

    @Nested
    @DisplayName("getUserByLogin")
    class GetUserByLogin {

        @Test
        @DisplayName("пользователь найден -> возвращается сущность из репозитория")
        void shouldReturnFoundUser() {
            var user = existingUser();
            when(userRepository.findByLoginEmail(LOGIN)).thenReturn(Optional.of(user));

            var result = userService.getUserByLogin(LOGIN);

            assertSame(user, result);
            verify(userRepository).findByLoginEmail(LOGIN);
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(passwordEncoder);
        }

        @ParameterizedTest(name = "login = [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        @DisplayName("пустой login -> InvalidParameterException без обращения к БД")
        void shouldRejectBlankLogin(String login) {
            var ex = assertThrows(
                    InvalidParameterException.class,
                    () -> userService.getUserByLogin(login));

            assertEquals("login must not be empty", ex.getMessage());
            verifyNoInteractions(userRepository, passwordEncoder);
        }

        @Test
        @DisplayName("пользователь не найден -> ResourceNotFoundException(\"User not found\")")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findByLoginEmail(LOGIN)).thenReturn(Optional.empty());

            var ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> userService.getUserByLogin(LOGIN));

            assertEquals("User not found", ex.getMessage());
            verify(userRepository).findByLoginEmail(LOGIN);
        }
    }
}