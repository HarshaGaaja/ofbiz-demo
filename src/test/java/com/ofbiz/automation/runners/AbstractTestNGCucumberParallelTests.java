package com.ofbiz.automation.runners;

import com.ofbiz.automation.libraries.ExamplesBuilder;
import com.ofbiz.automation.singletons.RuntimeSingleton;
import com.ofbiz.automation.utilities.MongoDBUtils;
import cucumber.api.testng.AbstractTestNGCucumberTests;
import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.PickleEventWrapper;
import gherkin.events.PickleEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.List;

public abstract class AbstractTestNGCucumberParallelTests extends AbstractTestNGCucumberTests implements ITest {
	private ThreadLocal<String> testName = new ThreadLocal<>();
	static Logger logger = LogManager.getLogger(MongoDBUtils.class);
	private static long duration;
	private String featureName;
	ITestContext context;

	@BeforeMethod
	public void BeforeMethod(Method method, Object[] testData) {
		testName.set(method.getName() + "_" + testData[0]);
		featureName = testData[0].toString();
		// System.out.println(featureName);
		testName.set(featureName);
	}

	@Override
	public String getTestName() {
		return testName.get();
	}

	@BeforeClass
	public void before(ITestContext testContext) {
		duration = System.currentTimeMillis();
		context = testContext;
		logger.info("Thread Id  | Scenario Num       | Step Count");
	}

	@AfterClass
	public void after() {
		duration = System.currentTimeMillis() - duration;
		logger.info("DURATION - " + duration);

	}

	@Override
	@DataProvider(parallel = true)
	public Object[][] scenarios() {
		return super.scenarios();
	}

	@Override
	@Test(groups = "cucumber", description = "Runs Cucumber Scenarios", dataProvider = "scenarios")
	public void runScenario(PickleEventWrapper pickleWrapper, CucumberFeatureWrapper featureWrapper) throws Throwable {
		PickleEvent pEvent = pickleWrapper.getPickleEvent();
		context.setAttribute("featureName", pickleWrapper.getPickleEvent().pickle.getName());
		context.setAttribute("tags", pickleWrapper.getPickleEvent().pickle.getTags());
		RuntimeSingleton.getInstance().setFeatureName(pickleWrapper.getPickleEvent().pickle.getName());
		ExamplesBuilder example = new ExamplesBuilder(pEvent);
		List<PickleEvent> pEvents = example.createPickle();

		logger.info(pEvents.stream().findFirst().get().pickle.getName() + "has " + pEvents.size() + "Events! ");
		RuntimeSingleton.getInstance().events = pEvents.size();

		for (PickleEvent pE : pEvents) {
			PickleEventWrapper yo = new ExamplesBuilder(pE);
			super.runScenario(yo, featureWrapper);
		}

//		if (RuntimeSingleton.getInstance().events < 8) {
//			RuntimeSingleton.getInstance().isMasterSet = false;
//		}

//		if (ConfigFileReader.getConfigFileReader().isDebugModeOn()) {
//			RuntimeSingleton.getInstance().isMasterSet = false;
//
//		}

	}

	@AfterSuite
	public void afterSuite() throws Exception {
		try {
			MongoDBUtils.storeResultsIntoMongoDB();
			logger.info("Results successfully inserted into mongodb");
		} catch (Exception e) {
			logger.warn("Failed to insert results into mongodb");
			// throw new Exception(e.getMessage());
		}
	}

}
