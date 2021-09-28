/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.contactspost.controller;

import java.util.Optional;

import com.example.contactspost.components.AlbumModelAssembler;
import com.example.contactspost.entity.Person;
import com.example.contactspost.models.PersonDto;
import com.example.contactspost.service.PersonService;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.web.reactive.function.BodyInserters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WebFluxTest
class ContactsControllerTest {

	@Autowired
	private WebTestClient webClient;

	@MockBean
	private PersonService personService;

	@SpyBean
	private PagedResourcesAssembler<Person> pagedResourcesAssembler;

	@SpyBean
	private AlbumModelAssembler albumModelAssembler;

	EnhancedRandom enhancedRandom = EnhancedRandomBuilder.aNewEnhancedRandomBuilder().seed(123L).objectPoolSize(100)
			.stringLengthRange(4, 10).collectionSizeRange(1, 10).scanClasspathForConcreteTypes(true).build();

	@Test
	void testCreateEmployee() {
		PersonDto personDto = enhancedRandom.nextObject(PersonDto.class);
		Person person = enhancedRandom.nextObject(Person.class);
		Mockito.when(personService.createPerson(personDto)).thenReturn(person);

		ResponseSpec created = webClient.post().uri("/create").contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(personDto)).exchange().expectStatus().isCreated();

		PersonDto responseBody = created.expectBody(PersonDto.class).returnResult().getResponseBody();

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