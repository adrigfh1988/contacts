package com.example.contactspost.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.util.UriBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@WebFluxTest
@AutoConfigureRestDocs(outputDir = "target/snippets")
@Import(PagedResourcesAssembler.class)
class ContactsControllerTest {

	private WebTestClient webTestClient;

	@MockBean
	private PersonService personService;

	@MockBean
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
	void testCreateEmployeeOkTest() {

		PersonDto personDto = PersonDto.builder().phone("+34-626120821").name("test User Name").email("test@test.com")
				.age(65).build();
		Person person = Person.builder().id(1L).timestamp(LocalDate.now()).phone("+34.626120821").name("test User Name")
				.operation("INSERT").email("test@test.com").age(65).confidentialityPassed(false).build();
		Mockito.when(personService.createPerson(personDto)).thenReturn(person);

		ResponseSpec created = webTestClient.post().uri("/create").contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(personDto)).exchange().expectStatus().isCreated();

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

	@Test
	void testDeleteOkTest() {

		webTestClient.delete().uri("/100").exchange().expectStatus().isNoContent();
		Mockito.verify(personService).deletePersonById(100L);
	}

	@Test
	void testGetAllPersonsOkTest() {

		List<Person> personList = new ArrayList<>();

		Person person = enhancedRandom.nextObject(Person.class);
		personList.add(person);
		personList.add(enhancedRandom.nextObject(Person.class));
		personList.add(enhancedRandom.nextObject(Person.class));

		List<PersonDto> personDtoList = new ArrayList<>();

		PersonDto personDto = enhancedRandom.nextObject(PersonDto.class);
		personDtoList.add(personDto);
		personDtoList.add(enhancedRandom.nextObject(PersonDto.class));
		personDtoList.add(enhancedRandom.nextObject(PersonDto.class));

		Page<Person> people = new PageImpl<>(personList, PageRequest.of(1, 10), 100);
		PagedModel<PersonDto> personDtos = PagedModel.of(personDtoList, new PageMetadata(1, 10, 100, 10));
		Mockito.when(personService.findAll(PageRequest.of(1, 10))).thenReturn(people);
		Mockito.when(pagedResourcesAssembler.toModel(people, albumModelAssembler)).thenReturn(personDtos);

		webTestClient.get().uri(
				(UriBuilder uriBuilder) -> uriBuilder.path("/").queryParam("page", 1).queryParam("size", 10).build())
				.exchange().expectStatus().isOk().expectBody().consumeWith(document("index"));

		Mockito.verify(personService).findAll(PageRequest.of(1, 10));
	}

}