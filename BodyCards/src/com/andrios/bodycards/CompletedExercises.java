package com.andrios.bodycards;

import java.io.Serializable;

public class CompletedExercises implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7173677931181122963L;

	String name;
	int count;
	Double multiplier;

	public CompletedExercises(String e, Double multiplier) {
		name = e;
		count = 0;
		this.multiplier = multiplier;
	}

	public void increment(int n) {
		count += n;
	}

	public void setName(String e) {
		name = e;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return name + " completed: " + count;
	}

}
