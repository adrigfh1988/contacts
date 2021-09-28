package com.example.contactspost.service;

import java.util.List;
import java.util.Optional;

import com.example.contactspost.entity.Person;
import com.example.contactspost.models.PersonDto;
import com.example.contactspost.repository.PersonRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

	private final PersonRepository personRepository;

	public PersonService(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	public Page<Person> findAll(Pageable pageable) {
		return personRepository.findAll(pageable);
	}

	public Person createPerson(PersonDto personDto) {
		Person person = new Person();
		person.setAge(personDto.getAge());
		person.setName(personDto.getName());
		person.setEmail(personDto.getEmail());
		person.setPhone(personDto.getPhone());

		PageRequest pageable = PageRequest.of(0, 1, Sort.by("id").descending());
		List<Person> content = this.findAll(pageable).getContent();

		long id = 1;
		if (!content.isEmpty()) {
			id = content.get(0).getId() + 1;
		}

		person.setId(id);
		return personRepository.save(person);
	}

	public Optional<Person> getPersonById(Long personId) {
		return personRepository.findById(personId);
	}

	public void deletePersonById(Long personId) {
		personRepository.deleteById(personId);
	}

}
