package com.example.contactspost.components;

import com.example.contactspost.controller.ContactsController;
import com.example.contactspost.entity.Person;
import com.example.contactspost.models.PersonDto;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AlbumModelAssembler extends RepresentationModelAssemblerSupport<Person, PersonDto> {

	public AlbumModelAssembler() {
		super(ContactsController.class, PersonDto.class);
	}

	@Override
	public PersonDto toModel(Person entity) {
		PersonDto albumModel = instantiateModel(entity);
		albumModel.add(linkTo(methodOn(ContactsController.class).getPerson(entity.getId())).withSelfRel());

		albumModel.setAge(entity.getAge());
		albumModel.setEmail(entity.getEmail());
		albumModel.setPhone(entity.getPhone());
		albumModel.setName(entity.getName());
		return albumModel;
	}

	@Override
	public CollectionModel<PersonDto> toCollectionModel(Iterable<? extends Person> entities) {
		CollectionModel<PersonDto> actorModels = super.toCollectionModel(entities);
		actorModels.add(linkTo(methodOn(ContactsController.class).getAllPersons(1, 10)).withSelfRel());
		return actorModels;
	}

}