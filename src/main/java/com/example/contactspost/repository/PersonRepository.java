package com.example.contactspost.repository;

import com.example.contactspost.entity.Person;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

	Page<Person> findPersonByName(String name, Pageable pageable);

}
