package com.zoxis.qa.automation.seleniumframework.core.commons;

import java.util.ArrayList;
import java.util.Date;

public final class SeleniumResult implements SeleniumData {
	private boolean isPassed = false;
	private String message = "Null";
	private String testName;
	private long duration = -1;
	private long startTime = -1;
	private Object data;
	private ArrayList<SeleniumStep> steps;
	private SeleniumStep currentStep;

	public SeleniumResult(String testName) {
		this.testName = testName;
	}

	public boolean isPassed() {
		return this.isPassed;
	}

	public boolean isFailed() {
		return !this.isPassed;
	}

	public void setStatus(boolean status) {
		this.isPassed = status;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

	public void PASSED() {
		this.isPassed = true;
		this.message = (getTestName() + " was successfull!");
	}

	public String getTestName() {
		return this.testName;
	}

	public void FAILED(Exception e) {
		this.isPassed = false;
		this.message = (getTestName() + "was unsuccessfull , because: " + e
				.getMessage());
	}

	public void FAILED(String message) {
		this.isPassed = false;
		this.message = (getTestName() + " was unsuccessfull, because " + message);
	}

	public void timerStart() {
		this.startTime = new Date().getTime();
	}

	public void timerEnd() {
		if (this.startTime > 0L)
			;
		long time = new Date().getTime() - this.startTime;
		if (time >= 0L)
			this.duration += time;
		else
			this.duration = 0L;
	}

	public long getDuration() {
		return this.duration;
	}

	public Object getData() {
		return this.data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void STEP(String title) {
		if (this.steps == null) {
			this.steps = new ArrayList();
		}
		SeleniumStep step = new SeleniumStep(this.steps.size() + 1 + ", "
				+ title);
		this.steps.add(step);
	}

	public ArrayList<SeleniumStep> getSteps() {
		return this.steps;
	}

	public String currentStep() {
		if (this.currentStep != null) {
			return this.currentStep.toString();
		}
		return "UNKNOWN";
	}
}