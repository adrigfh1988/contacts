package com.example.contactspost.controller;

import com.example.contactspost.components.AlbumModelAssembler;
import com.example.contactspost.entity.Person;
import com.example.contactspost.models.PersonDto;
import com.example.contactspost.service.PersonService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactsController {

	private final PersonService personService;

	private final PagedResourcesAssembler<Person> pagedResourcesAssembler;

	private final AlbumModelAssembler albumModelAssembler;

	public ContactsController(PersonService personService, PagedResourcesAssembler<Person> pagedResourcesAssembler,
			AlbumModelAssembler albumModelAssembler) {

		this.personService = personService;
		this.pagedResourcesAssembler = pagedResourcesAssembler;
		this.albumModelAssembler = albumModelAssembler;
	}

	@PostMapping(value = "/create")
	public ResponseEntity<PersonDto> createContactForPerson(@RequestBody PersonDto personDto) {

		Person person = personService.createPerson(personDto);
		PersonDto personDtoCreated = albumModelAssembler.toModel(person);
		return ResponseEntity.created(personDtoCreated.getRequiredLink(IanaLinkRelations.SELF).toUri())
				.body(personDtoCreated);
	}

	@DeleteMapping(value = "/{person_id}")
	public ResponseEntity<Void> deletePerson(@PathVariable(value = "person_id") Long personId) {

		personService.deletePersonById(personId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<PagedModel<PersonDto>> getAllPersons(@RequestParam("page") int pageIndex,
			@RequestParam("size") int pageSize) {

		Page<Person> personPage = this.personService.findAll(PageRequest.of(pageIndex, pageSize));
		PagedModel<PersonDto> personDtos = pagedResourcesAssembler.toModel(personPage, albumModelAssembler);
		return ResponseEntity.ok(personDtos);
	}

	@GetMapping(value = "/{person_id}")
	public ResponseEntity<PersonDto> getPerson(@PathVariable(value = "person_id") Long personId) {

		Person personById = this.personService.getPersonById(personId)
				.orElseThrow(() -> new IllegalArgumentException("NOT FOUND"));
		PersonDto personDto = albumModelAssembler.toModel(personById);
		return ResponseEntity.ok(personDto);

	}

}
