package ru.Perminov_Ilya.NauJava;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.Perminov_Ilya.NauJava.criteriaApiRepository.IAttachmentRepositoryCriteriaApi;
import ru.Perminov_Ilya.NauJava.criteriaApiRepository.IMessageRepositoryCriteriaAPI;
import ru.Perminov_Ilya.NauJava.entities.*;
import ru.Perminov_Ilya.NauJava.entities.enums.AttachmentType;
import ru.Perminov_Ilya.NauJava.repository.*;
import ru.Perminov_Ilya.NauJava.services.IMessageService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NauJavaApplicationTests {

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private IMessageRepositoryCriteriaAPI messageRepositoryCriteriaAPI;

	@Autowired
	private IAttachmentRepositoryCriteriaApi attachmentRepositoryCriteriaApi;

	@Autowired
	private IMessageService messageService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ChatBaseRepository chatBaseRepository;

	private User testUser;
	private User testUser2;
	private ChatBase testChat;
	private Message testMessage;

	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setTag("testuser");
		testUser.setLoginEmail("test@example.com");
		testUser.setPasswordHash("hash123");
		testUser.setName("Test User");
		testUser.setRegistrationDate(Instant.now());
		testUser = userRepository.save(testUser);

		testUser2 = new User();
		testUser2.setTag("testuser2");
		testUser2.setLoginEmail("test2@example.com");
		testUser2.setPasswordHash("hash123");
		testUser2.setName("Test User 2");
		testUser2.setRegistrationDate(Instant.now());
		testUser2 = userRepository.save(testUser2);

		DirectChat directChat = new DirectChat();
		directChat.setTag("testchat");
		directChat.setTitle("Test Chat");
		directChat.setCreatedAt(Instant.now());
		directChat.setUserA(testUser);
		directChat.setUserB(testUser2);
		testChat = chatBaseRepository.save(directChat);

		testMessage = new Message();
		testMessage.setChat(testChat);
		testMessage.setSender(testUser);
		testMessage.setContent("Hello world");
		testMessage.setSentAt(Instant.now());
		testMessage = messageRepository.save(testMessage);
	}

	@AfterEach
	void tearDown() {
		messageRepository.deleteAll();
		chatBaseRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void testSearchInChat_JPQL_ShouldFindMessages() {
		List<Message> results = messageRepository.searchInChat(testChat.getId(), "Hello");
		assertFalse(results.isEmpty());
		assertEquals(1, results.size());
		assertEquals("Hello world", results.getFirst().getContent());
	}

	@Test
	void testSearchInChat_JPQL_ShouldReturnEmptyWhenNotFound() {
		List<Message> results = messageRepository.searchInChat(testChat.getId(), "nonexistent");
		assertTrue(results.isEmpty());
	}

	@Test
	void testSearchInChat_JPQL_ShouldSearchPartially() {
		Message message2 = new Message();
		message2.setChat(testChat);
		message2.setSender(testUser);
		message2.setContent("Goodbye world");
		message2.setSentAt(Instant.now());
		messageRepository.save(message2);
		List<Message> results = messageRepository.searchInChat(testChat.getId(), "world");
		assertEquals(2, results.size());
	}

	@Test
	void testSearchInChat_CriteriaAPI_ShouldFindMessages() {
		List<Message> results = messageRepositoryCriteriaAPI.searchInChat(testChat.getId(), "Hello");
		assertFalse(results.isEmpty());
		assertEquals(1, results.size());
		assertEquals("Hello world", results.getFirst().getContent());
	}

	@Test
	void testSearchInChat_CriteriaAPI_ShouldReturnEmptyWhenNotFound() {
		List<Message> results = messageRepositoryCriteriaAPI.searchInChat(testChat.getId(), "nonexistent");
		assertTrue(results.isEmpty());
	}

	@Test
	void testFindByTypeAndDataSizeBytesBetween_ShouldFindAttachments() {
		Attachment attachment1 = new Attachment();
		attachment1.setMessage(testMessage);
		attachment1.setName("file1.txt");
		attachment1.setType(AttachmentType.FILE);
		attachment1.setDataSizeBytes(100);
		attachment1.setDataUrl("url1");

		Attachment attachment2 = new Attachment();
		attachment2.setMessage(testMessage);
		attachment2.setName("file2.txt");
		attachment2.setType(AttachmentType.FILE);
		attachment2.setDataSizeBytes(200);
		attachment2.setDataUrl("url2");

		testMessage.setAttachments(List.of(attachment1, attachment2));
		messageRepository.save(testMessage);

		List<Attachment> results = attachmentRepositoryCriteriaApi
				.findByTypeAndDataSizeBytesBetween(AttachmentType.FILE, 50, 250);

		assertEquals(2, results.size());
	}

	@Test
	void testFindByTypeAndDataSizeBytesBetween_ShouldFilterByType() {
		Attachment fileAttachment = new Attachment();
		fileAttachment.setMessage(testMessage);
		fileAttachment.setName("file.txt");
		fileAttachment.setType(AttachmentType.FILE);
		fileAttachment.setDataSizeBytes(100);
		fileAttachment.setDataUrl("url1");

		Attachment photoAttachment = new Attachment();
		photoAttachment.setMessage(testMessage);
		photoAttachment.setName("photo.jpg");
		photoAttachment.setType(AttachmentType.PHOTO);
		photoAttachment.setDataSizeBytes(100);
		photoAttachment.setDataUrl("url2");

		testMessage.setAttachments(List.of(fileAttachment, photoAttachment));
		messageRepository.save(testMessage);

		List<Attachment> results = attachmentRepositoryCriteriaApi
				.findByTypeAndDataSizeBytesBetween(AttachmentType.FILE, 50, 150);

		assertEquals(1, results.size());
		assertEquals(AttachmentType.FILE, results.getFirst().getType());
	}

	@Test
	void testFindByTypeAndDataSizeBytesBetween_ShouldReturnEmptyWhenOutOfRange() {
		Attachment attachment = new Attachment();
		attachment.setMessage(testMessage);
		attachment.setName("file.txt");
		attachment.setType(AttachmentType.FILE);
		attachment.setDataSizeBytes(100);
		attachment.setDataUrl("url1");

		testMessage.setAttachments(List.of(attachment));
		messageRepository.save(testMessage);

		List<Attachment> results = attachmentRepositoryCriteriaApi
				.findByTypeAndDataSizeBytesBetween(AttachmentType.FILE, 200, 300);

		assertTrue(results.isEmpty());
	}

	@Test
	void testSendMessage_SuccessfulTransaction() {
		Attachment attachment = new Attachment();
		attachment.setName("test.txt");
		attachment.setType(AttachmentType.FILE);
		attachment.setDataSizeBytes(100);
		attachment.setDataUrl("test_url");

		messageService.sendMessage(
				testUser.getId(),
				testChat.getId(),
				"Test message",
				new Attachment[]{attachment}
		);

		List<Message> messages = messageRepository.searchInChat(testChat.getId(), "Test message");
		assertFalse(messages.isEmpty());
		assertEquals(1, messages.size());

		Message savedMessage = messages.getFirst();
		assertEquals("Test message", savedMessage.getContent());
		assertNotNull(savedMessage.getId());
		assertEquals(testUser.getId(), savedMessage.getSender().getId());
		assertEquals(testChat.getId(), savedMessage.getChat().getId());
	}

	@Test
	void testSendMessage_TransactionRollback_WhenSenderNotFound() {
		UUID nonExistentUserId = UUID.randomUUID();
		Attachment attachment = new Attachment();
		attachment.setName("test.txt");
		attachment.setType(AttachmentType.FILE);
		attachment.setDataSizeBytes(100);
		attachment.setDataUrl("test_url");

		long countBefore = messageRepository.count();

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			messageService.sendMessage(
					nonExistentUserId,
					testChat.getId(),
					"Test message",
					new Attachment[]{attachment}
			);
		});

		assertEquals("Sender not found", exception.getMessage());

		long countAfter = messageRepository.count();
		assertEquals(countBefore, countAfter);
	}

	@Test
	void testSendMessage_TransactionRollback_WhenChatNotFound() {
		UUID nonExistentChatId = UUID.randomUUID();
		Attachment attachment = new Attachment();
		attachment.setName("test.txt");
		attachment.setType(AttachmentType.FILE);
		attachment.setDataSizeBytes(100);
		attachment.setDataUrl("test_url");

		long countBefore = messageRepository.count();

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			messageService.sendMessage(
					testUser.getId(),
					nonExistentChatId,
					"Test message",
					new Attachment[]{attachment}
			);
		});

		assertEquals("Chat not found", exception.getMessage());

		long countAfter = messageRepository.count();
		assertEquals(countBefore, countAfter);
	}

	@Test
	void testSendMessage_WithoutAttachments_ShouldWork() {
		messageService.sendMessage(
				testUser.getId(),
				testChat.getId(),
				"Message without attachments",
				null
		);

		List<Message> messages = messageRepository.searchInChat(testChat.getId(), "without attachments");
		assertFalse(messages.isEmpty());
		assertEquals(1, messages.size());

		Message savedMessage = messages.getFirst();
		assertEquals("Message without attachments", savedMessage.getContent());
		assertNotNull(savedMessage.getId());
		assertEquals(testUser.getId(), savedMessage.getSender().getId());
		assertEquals(testChat.getId(), savedMessage.getChat().getId());
	}
}