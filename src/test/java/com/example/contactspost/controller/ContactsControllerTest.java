package com.example.contactspost.controller;

import java.time.LocalDate;
import java.util.Optional;

import com.example.contactspost.components.AlbumModelAssembler;
import com.example.contactspost.entity.Person;
import com.example.contactspost.models.PersonDto;
import com.example.contactspost.service.PersonService;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.web.reactive.function.BodyInserters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@WebFluxTest
@AutoConfigureRestDocs(outputDir = "target/snippets")
class ContactsControllerTest {

	private WebTestClient webTestClient;

	@MockBean
	private PersonService personService;

	@SpyBean
	private PagedResourcesAssembler<Person> pagedResourcesAssembler;

	@SpyBean
	private AlbumModelAssembler albumModelAssembler;

	@RegisterExtension
	final RestDocumentationExtension restDocumentation = new RestDocumentationExtension("target/snippets");

	EnhancedRandom enhancedRandom = EnhancedRandomBuilder.aNewEnhancedRandomBuilder().seed(123L).objectPoolSize(100)
			.stringLengthRange(4, 10).collectionSizeRange(1, 10).scanClasspathForConcreteTypes(true).build();

	@BeforeEach
	public void setUp(ApplicationContext applicationContext, RestDocumentationContextProvider restDocumentation) {
		this.webTestClient = WebTestClient.bindToApplicationContext(applicationContext).configureClient()
				.baseUrl("https://api.example.com").filter(documentationConfiguration(restDocumentation)
						.operationPreprocessors().withResponseDefaults(prettyPrint()))
				.build();
	}

	@Test
	void testCreateEmployee() {

		PersonDto personDto = PersonDto.builder().phone("+34-626120821").name("test User Name").email("test@test.com")
				.age(65).build();
		Person person = Person.builder().id(1L).timestamp(LocalDate.now()).phone("+34.626120821").name("test User Name")
				.operation("INSERT").email("test@test.com").age(65).confidentialityPassed(false).build();
		Mockito.when(personService.createPerson(personDto)).thenReturn(person);

		ResponseSpec created = webTestClient.post().uri("/create").contentType(MediaType.APPLICATION_JSON)
				.header("X-Forwarded-Host", "example-doc.com").header("X-Forwarded-Proto", "https")
				.header("X-Forwarded-Port", "443").body(BodyInserters.fromValue(personDto)).exchange().expectStatus()
				.isCreated();

		PersonDto responseBody = created.expectBody(PersonDto.class).consumeWith(document("index")).returnResult()
				.getResponseBody();

		assertNotNull(responseBody);
		assertEquals(person.getAge(), responseBody.getAge());
		assertEquals(person.getEmail(), responseBody.getEmail());
		assertEquals(person.getPhone(), responseBody.getPhone());
		assertEquals(person.getName(), responseBody.getName());
		Optional<Link> self = responseBody.getLink("self");
		assertTrue(self.isPresent());
		assertEquals("/" + person.getId(), self.get().getHref());

		Mockito.verify(personService).createPerson(personDto);
	}

}