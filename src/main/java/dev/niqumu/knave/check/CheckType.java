package dev.niqumu.knave.check;

import lombok.Getter;

/**
 * An enum of check types/categories. Check types can be disabled and customized individually
 */
public enum CheckType {
	FLIGHT("Flight"),
	SPEED("Speed");

	/**
	 * The friendly name of the check type
	 */
	@Getter
	private final String name;

	CheckType(String name) {
		this.name = name;
	}
}
