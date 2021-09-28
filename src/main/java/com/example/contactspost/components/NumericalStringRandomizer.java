package com.example.contactspost.components;

import java.util.Random;

import io.github.benas.randombeans.api.Randomizer;

public class NumericalStringRandomizer implements Randomizer<Long> {

	@Override
	public Long getRandomValue() {
		return new Random().nextLong(99000) + 1000;
	}

}