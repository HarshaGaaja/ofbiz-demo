package com.ofbiz.automation.libraries;

import cucumber.api.event.*;


public class CustomReporter implements ConcurrentEventListener{

	@Override
	public void setEventPublisher(EventPublisher publisher) {
		publisher.registerHandlerFor(TestRunStarted.class, getTestRunStartedHandler());
        publisher.registerHandlerFor(TestSourceRead.class, getTestSourceReadHandler());
        publisher.registerHandlerFor(TestCaseStarted.class, getTestCaseStartedHandler());
        publisher.registerHandlerFor(TestStepStarted.class, getTestStepStartedHandler());
        publisher.registerHandlerFor(TestStepFinished.class, getTestStepFinishedHandler());
        publisher.registerHandlerFor(TestCaseFinished.class, getTestCaseFinishedHandler());
        publisher.registerHandlerFor(TestRunFinished.class, getTestRunFinishedHandler());
        publisher.registerHandlerFor(EmbedEvent.class, getEmbedEventHandler());
        publisher.registerHandlerFor(WriteEvent.class, getWriteEventHandler());

	}

	private EventHandler<WriteEvent> getWriteEventHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	private EventHandler<EmbedEvent> getEmbedEventHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	private EventHandler<TestRunFinished> getTestRunFinishedHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	private EventHandler<TestCaseFinished> getTestCaseFinishedHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	private EventHandler<TestStepFinished> getTestStepFinishedHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	private EventHandler<TestStepStarted> getTestStepStartedHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	private EventHandler<TestCaseStarted> getTestCaseStartedHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	private EventHandler<TestRunStarted> getTestRunStartedHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	private EventHandler<TestSourceRead> getTestSourceReadHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
