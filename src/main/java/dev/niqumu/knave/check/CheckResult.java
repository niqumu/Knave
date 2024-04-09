package dev.niqumu.knave.check;

import lombok.Getter;

import javax.annotation.Nullable;

/**
 * A class used to represent the result of a check
 * @see Check
 */
public class CheckResult {

	/**
	 * Whether the check's player passed the check
	 */
	@Getter
	private final boolean passed;

	/**
	 * Any message or optional information that the check may decide to provide to staff, usually when the
	 * check is failed
	 */
	@Getter
	private final String message;

	/**
	 * Create a new check result given the outcome and an optional message with further information
	 * @param passed Whether the check's player passed the check
	 * @param message Optional - any additional information on the outcome of the check
	 */
	public CheckResult(boolean passed, @Nullable String message) {
		this.passed = passed;
		this.message = message;
	}
}
