/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.contactspost.components;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NumericalStringRandomizerTest {

	private final NumericalStringRandomizer numericalStringRandomizer = new NumericalStringRandomizer();

	@Test
	void longValueOkTest() {
		Long randomValue = numericalStringRandomizer.getRandomValue();
		assertNotNull(randomValue);
		assertTrue(randomValue > 1000);
	}

}