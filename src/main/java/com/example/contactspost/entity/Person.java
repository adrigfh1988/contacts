package com.example.contactspost.entity;

import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.example.contactspost.components.NumericalStringRandomizer;
import io.github.benas.randombeans.annotation.Randomizer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "PERSON")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Person {

	@Id
	@Column(name = "id", nullable = false)
	@Randomizer(NumericalStringRandomizer.class)
	private Long id;

	private String name;

	private Integer age;

	private String email;

	private String phone;

	@Column(name = "operation")
	private String operation;

	@Column(name = "timestamp")
	private LocalDate timestamp;

	@Transient
	private boolean confidentialityPassed;

	@PrePersist
	public void onPrePersist() {
		audit("INSERT-UPDATE");
	}

	@PreRemove
	public void onPreRemove() {
		audit("DELETE");
	}

	private void audit(String operation) {
		setOperation(operation);
		setTimestamp(LocalDate.now());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Person person))
			return false;
		return Objects.equals(id, person.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
