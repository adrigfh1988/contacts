package com.example.contactspost.components;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.example.contactspost.entity.Person;
import com.example.contactspost.models.PersonDto;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.hateoas.CollectionModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlbumModelAssemblerTest {

	private final AlbumModelAssembler albumModelAssembler = new AlbumModelAssembler();

	EnhancedRandom enhancedRandom = EnhancedRandomBuilder.aNewEnhancedRandomBuilder().seed(123L).objectPoolSize(100)
			.stringLengthRange(4, 10).collectionSizeRange(1, 10).scanClasspathForConcreteTypes(true).build();

	@ParameterizedTest
	@MethodSource("personProvider")
	@DisplayName("This test is going to validate the conversion from Entity to DTO with Links")
	void toModelTestOk(Person person) {

		PersonDto personDto = albumModelAssembler.toModel(person);

		assertEquals(person.getAge(), personDto.getAge());
		assertEquals(person.getEmail(), personDto.getEmail());
		assertEquals(person.getPhone(), personDto.getPhone());
		assertEquals(person.getName(), personDto.getName());
		assertTrue(personDto.getLink("self").isPresent());
		assertEquals("/" + person.getId(), personDto.getLink("self").get().getHref());

	}

	private static Stream<Arguments> personProvider() {
		return Stream.of(
				Arguments.of(Person.builder().id(1L).age(30).confidentialityPassed(false).email("Test@Test.com")
						.name("testName").operation("INSERT").phone("+34-612310123").timestamp(LocalDate.now())
						.build()),
				Arguments.of(Person.builder().id(1L).age(30).confidentialityPassed(false).email("Test@Test.com")
						.name("testName").timestamp(LocalDate.now()).build()),
				Arguments.of(Person.builder().id(1L).age(30).confidentialityPassed(false).email("Test@Test.com")
						.phone("+34-612310123").timestamp(LocalDate.now()).build()),
				Arguments.of(Person.builder().id(1L).build()), Arguments.of(
						Person.builder().id(566L).age(30).phone("+34-612310123").timestamp(LocalDate.now()).build()));
	}

	@Test
	@DisplayName("This test will validate the collections transformation of Entities")
	void toCollectionOkTest() {

		List<Person> personList = new ArrayList<>();

		Person person = enhancedRandom.nextObject(Person.class);
		personList.add(person);
		personList.add(enhancedRandom.nextObject(Person.class));
		personList.add(enhancedRandom.nextObject(Person.class));

		CollectionModel<PersonDto> personDtos = albumModelAssembler.toCollectionModel(personList);

		PersonDto personDto = albumModelAssembler.toModel(person);
		assertEquals(3, personDtos.getContent().size());
		List<PersonDto> actual = new ArrayList<>(personDtos.getContent());
		assertEquals(personDto, actual.get(0));
	}

}