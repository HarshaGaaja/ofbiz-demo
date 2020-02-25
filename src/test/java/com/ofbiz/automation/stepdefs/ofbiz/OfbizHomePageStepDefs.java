package com.ofbiz.automation.stepdefs.ofbiz;

import com.ofbiz.automation.common.World;
import com.ofbiz.automation.pages.ofbiz.OfbizHomePage;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.ResourceBundle;

public class OfbizHomePageStepDefs {
    private World world;
    OfbizHomePage ofbizhomepage;
    ResourceBundle loginTestData;

    public OfbizHomePageStepDefs(World world) {
        this.world = world;
        ofbizhomepage = new OfbizHomePage(this.world);
        loginTestData = ResourceBundle.getBundle("com.ofbiz.automation.locales.Locale", world.getFormattedLocale());
    }

    @Then("I verify home page is displayed")
    public void iVerifyHomePageIsDisplayed() {
        try{
            ofbizhomepage.verifyHomePageDisplayed();
        } catch (Exception e) {
            ofbizhomepage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }
    }

    @And("I logout from Ofbiz application")
    public void iLogoutFromOfbizApplication() {
        try {
            ofbizhomepage.clickLogout();
        } catch (Exception e) {
            ofbizhomepage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }
    }

    @When("I click on applications")
    public void iClickOnApplications() {
        try {
            ofbizhomepage.clickOnApplication();
        } catch (Exception e) {
            ofbizhomepage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }
    }

    @And("I select the Accounting-AP")
    public void iSelectTheAccountingAP() {
        try {
            ofbizhomepage.clickOnAccountingAP();
        } catch (Exception e) {
            ofbizhomepage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }
    }

    @Then("Accounting-AP page should be displayed")
    public void accountingAPPageShouldBeDisplayed() {
        try {
            ofbizhomepage.verifyAccountingAPPage();
        } catch (Exception e) {
            ofbizhomepage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }
    }

    @And("I click on Marketing Module")
    public void iClickOnMarketingModule() {
        try {
            ofbizhomepage.clickOnMarketingModule();
        } catch (Exception e) {
            ofbizhomepage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }

    }

    @Then("I verify Marketing Manager page is displayed")
    public void iVerifyMarketingManagerPageIsDisplayed() {
        try {
            ofbizhomepage.verifyMarketingManagerTitle();
        } catch (Exception e) {
            ofbizhomepage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }
    }

    @And("I click on each module to verify the pages got displayed")
    public void iClickOnEachModuleToVerifyThePagesGotDisplayed() {
        try {
            ofbizhomepage.clickOneachModuleAndVerifyPages();
        } catch (Exception e) {
            ofbizhomepage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }
    }

    @And("I select Order Module in applications menu")
    public void iSelectOrderModuleInAppliactionsMenu() {
        try {
            ofbizhomepage.clickOnOrderModule();
        } catch (Exception e) {
            ofbizhomepage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }
    }

    @Then("I click on Order Manager menu")
    public void iClickOnOrderManagerMenu() {
        try {
            ofbizhomepage.clickOrderManagermenu();
        } catch (Exception e) {
            ofbizhomepage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }
    }

    @And("I select Orders List in Order Manager menu")
    public void iSelectOrdersListInOrderManagerMenu() {
        try {
            ofbizhomepage.clickOrderList();
        } catch (Exception e) {
            ofbizhomepage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }
    }

    @Then("I select all checkboxes in Order List Page")
    public void iSelectAllCheckboxesInOrderListPage() {
        try {
            ofbizhomepage.selectAllChkBoxesInOrdrList();
        } catch (Exception e) {
            ofbizhomepage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }
    }

    @When("I click on Order Number in Order List")
    public void iClickOnOrderNumberInOrderList() {
        try {
            ofbizhomepage.clickOrderNumber();
        } catch (Exception e) {
            ofbizhomepage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }
    }

    @Then("I verify status history as completed")
    public void iVerifyStatusHistoryAsCompleted() {
        try {
            ofbizhomepage.VerifyStatusHistoryASCompleted();
        } catch (Exception e) {
            ofbizhomepage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }
    }

    @And("I select on Other Orders")
    public void iSelectOnOtherOrders() {
        try {
            ofbizhomepage.selectOtherOrders();
        } catch (Exception e) {
            ofbizhomepage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }
    }

    @When("I click on Other Order Number in Order list")
    public void iClickOnOtherOrderNumberInOrderList() {
        try {
            ofbizhomepage.clickOtherorderNumber();
        } catch (Exception e) {
            ofbizhomepage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }
    }

    @Then("I verify status history as approved")
    public void iVerifyStatusHistoryAsApproved() {
        try {
            ofbizhomepage.verifyStatusHistoryAsApproved();
        } catch (Exception e) {
            ofbizhomepage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }
    }

    @Then("I click and verify each module")
    public void iClickAndVerifyEachModule() {
        try {
            ofbizhomepage.verifyAndClickOnEachModule();
        } catch (Exception e) {
            ofbizhomepage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }
    }

    @And("I click on each module to verify the pages are displayed")
    public void iClickOnEachModuleToVerifyThePagesAreDisplayed() {
        try {
            ofbizhomepage.clickAndVerifyOnEachModule();
        } catch (Exception e) {
            ofbizhomepage.failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }
    }
}

