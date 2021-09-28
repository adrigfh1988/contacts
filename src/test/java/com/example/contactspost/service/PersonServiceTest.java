/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.contactspost.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.example.contactspost.config.PostgresIntegrationSetup;
import com.example.contactspost.entity.Person;
import com.example.contactspost.models.PersonDto;
import com.example.contactspost.repository.PersonRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
class PersonServiceTest extends PostgresIntegrationSetup {

	@Autowired
	private PersonService personService;

	@SpyBean
	private PersonRepository personRepository;

	@Test
	@DisplayName("This test is for finding all elements in an unPaged manner")
	@Sql({ "/sql/person-init.sql" })
	void findAllPersonsUnPaged() {
		Pageable pageable = Pageable.unpaged();
		Page<Person> personServiceAll = personService.findAll(pageable);
		Assertions.assertEquals(10, personServiceAll.getTotalElements());
		Mockito.verify(personRepository).findAll(pageable);
	}

	@Test
	@DisplayName("This test is to validate the correct pagination")
	@Sql({ "/sql/person-init.sql" })
	void findAllPersonsSize2Page1() {
		PageRequest pageable = PageRequest.of(1, 2);
		Page<Person> personServiceAll = personService.findAll(pageable);
		Assertions.assertEquals(10, personServiceAll.getTotalElements());
		Assertions.assertEquals(2, personServiceAll.get().count());
		Assertions.assertEquals(1, personServiceAll.getNumber());
		Mockito.verify(personRepository).findAll(pageable);
	}

	@Test
	@DisplayName("This test is to validate the correct pagination and sorting")
	@Sql({ "/sql/person-init.sql" })
	void findAllPersonsSize2Page1Sort() {
		PageRequest pageable = PageRequest.of(0, 2, Sort.by("id").descending());
		Page<Person> personServiceAll = personService.findAll(pageable);
		Assertions.assertEquals(10, personServiceAll.getTotalElements());
		Assertions.assertEquals(2, personServiceAll.get().count());
		Assertions.assertEquals(0, personServiceAll.getNumber());
		List<Person> content = personServiceAll.getContent();
		Assertions.assertEquals(10, content.get(0).getId());
		Assertions.assertEquals(9, content.get(1).getId());
		Mockito.verify(personRepository).findAll(pageable);
	}

	@Test
	@DisplayName("Test for validating correct deletion of an Element")
	@Sql({ "/sql/person-init.sql" })
	void deletePerson() {
		long idToDelete = 10L;
		Optional<Person> personExisting = personRepository.findById(idToDelete);
		personService.deletePersonById(idToDelete);
		Optional<Person> personNotExisting = personRepository.findById(idToDelete);
		Assertions.assertFalse(personExisting.isEmpty());
		Assertions.assertTrue(personNotExisting.isEmpty());
		Mockito.verify(personRepository).deleteById(idToDelete);
	}

	@ParameterizedTest
	@MethodSource("personProvider")
	@DisplayName("Creating Multiple persons")
	@Sql({ "/sql/person-init.sql" })
	void createPersonOkTest(PersonDto personDto, long id) {

		Person person = personService.createPerson(personDto);
		Assertions.assertEquals(id, person.getId());
		Assertions.assertEquals(personDto.getAge(), person.getAge());
		Assertions.assertEquals(personDto.getPhone(), person.getPhone());
		Assertions.assertEquals(personDto.getEmail(), person.getEmail());
		Assertions.assertEquals(personDto.getName(), person.getName());

	}

	private static Stream<Arguments> personProvider() {
		return Stream.of(
				Arguments.of(PersonDto.builder().age(32).email("test@email1.com").name("testName1").phone("testPhone1")
						.build(), 11L),
				Arguments.of(PersonDto.builder().age(22).email("test@email2.com").name("testName2").phone("testPhone2")
						.build(), 11L),
				Arguments.of(PersonDto.builder().age(76).email("test@email.3com").name("testName3").phone("testPhone3")
						.build(), 11L),
				Arguments.of(PersonDto.builder().age(12).email("test@email4.com").name("testName4").phone("testPhone4")
						.build(), 11L));
	}

}