$(document).ready(function() {var formatter = new CucumberHTML.DOMFormatter($('.cucumber-report'));formatter.uri("file:src/test/resources/features/e2e/TCID_C6019498.feature");
formatter.feature({
  "name": "Coach user checkout with Shakeology",
  "description": "",
  "keyword": "Feature",
  "tags": [
    {
      "name": "@C6019498"
    },
    {
      "name": "@E2E"
    },
    {
      "name": "@Phase1"
    }
  ]
});
formatter.scenarioOutline({
  "name": "Coach user checkout with Shakeology",
  "description": "",
  "keyword": "Scenario Outline"
});
formatter.step({
  "name": "I am on TBB home page",
  "keyword": "Given "
});
formatter.step({
  "name": "I click on become a coach from coach menu",
  "keyword": "When "
});
formatter.step({
  "name": "signup page should be displayed",
  "keyword": "Then "
});
formatter.step({
  "name": "I enter my information \"\u003ccountry\u003e\",\"\u003clanguage\u003e\",\"\u003cpswd\u003e\",\"\u003cphone\u003e\",\"\u003cmonth\u003e\",\"\u003cday\u003e\",\"\u003cyear\u003e\",\"\u003cgovtid\u003e\",\"\u003csign\u003e\"",
  "keyword": "And "
});
formatter.step({
  "name": "I click on Continue",
  "keyword": "When "
});
formatter.step({
  "name": "Coach registered sucessfully and navigated to challenge pack step",
  "keyword": "Then "
});
formatter.step({
  "name": "I click on No thanks button to navigate to shakeology step",
  "keyword": "And "
});
formatter.step({
  "name": "I select Shakeology pack \"\u003cpack\u003e\"",
  "keyword": "When "
});
formatter.step({
  "name": "I add to cart shakeology order with \"\u003cpack_size\u003e\",\"\u003cordere_type\u003e\" and \"\u003cqty\u003e\" options",
  "keyword": "And "
});
formatter.step({
  "name": "I click on No thanks buttons to navigate to offers page",
  "keyword": "Then "
});
formatter.step({
  "name": "I skip offers to continue shopping",
  "keyword": "Then "
});
formatter.step({
  "name": "I enter my shipping information \"\u003caddress\u003e\",\"\u003ccity\u003e\",\"\u003cstate\u003e\",\"\u003czip\u003e\"",
  "keyword": "Then "
});
formatter.step({
  "name": "I validates the QAS address",
  "keyword": "And "
});
formatter.step({
  "name": "I select shipping option \"\u003cshipping\u003e\" and click on continue",
  "keyword": "Then "
});
formatter.step({
  "name": "I enter payment details",
  "keyword": "And "
});
formatter.step({
  "name": "I submit order",
  "keyword": "And "
});
formatter.step({
  "name": "I should be able to see a successful order placement with an Order ID",
  "keyword": "Then "
});
formatter.step({
  "name": "I verify ATG, EBS, life ray, by design users are provisioned",
  "keyword": "And "
});
formatter.step({
  "name": "I verify user is syndicated in EBS",
  "keyword": "Then "
});
formatter.step({
  "name": "I verify order is syndicated in EBS",
  "keyword": "And "
});
formatter.step({
  "name": "I verify order is syndicated to COM through EBS",
  "keyword": "And "
});
formatter.step({
  "name": "I hit the byd soap request by placed orderNumber",
  "keyword": "Given "
});
formatter.step({
  "name": "I validate response code 200",
  "keyword": "And "
});
formatter.step({
  "name": "I save coach id using placed order response body",
  "keyword": "Then "
});
formatter.step({
  "name": "I get REP info response details using coach id",
  "keyword": "Given "
});
formatter.step({
  "name": "I validate response code 200",
  "keyword": "And "
});
formatter.step({
  "name": "I validate coach details billing Country \"\u003ccountry\u003e\" and Billing Street One \"\u003caddress\u003e\", billing city \"\u003ccity\u003e\" and ship state \"\u003cstate\u003e\", ship postal code \"\u003czip\u003e\" and phone number \"\u003cphone\u003e\"",
  "keyword": "And "
});
formatter.examples({
  "name": "Test data for the place shakeology order scenario",
  "description": "",
  "keyword": "Examples",
  "rows": [
    {
      "cells": [
        "country",
        "language",
        "pswd",
        "phone",
        "month",
        "day",
        "year",
        "govtid",
        "sign",
        "pack",
        "pack_size",
        "ordere_type",
        "qty",
        "address",
        "city",
        "state",
        "zip",
        "shipping"
      ]
    },
    {
      "cells": [
        "US",
        "en",
        "Coosign#123",
        "3456789012",
        "01",
        "22",
        "1993",
        "567895432",
        "/s/SMAPLE",
        "SHKCHVegan",
        "30",
        "Monthly",
        "1",
        "3301 Exposition Blvd",
        "Santa Monica",
        "CA",
        "90404",
        "1"
      ]
    }
  ]
});
formatter.scenario({
  "name": "Coach user checkout with Shakeology",
  "description": "",
  "keyword": "Scenario Outline",
  "tags": [
    {
      "name": "@C6019498"
    },
    {
      "name": "@E2E"
    },
    {
      "name": "@Phase1"
    }
  ]
});
formatter.before({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "I am on TBB home page",
  "keyword": "Given "
});
formatter.match({
  "location": "TBB_HomePageStepDefs.i_am_on_TBB_home_page()"
});
formatter.result({
  "status": "passed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "I click on become a coach from coach menu",
  "keyword": "When "
});
formatter.match({
  "location": "TBB_HomePageStepDefs.i_click_on_become_a_coach_from_coach_menu()"
});
formatter.result({
  "status": "passed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "signup page should be displayed",
  "keyword": "Then "
});
formatter.match({
  "location": "TBB_CoachSignUpPageStepDefs.signup_page_should_be_displayed()"
});
formatter.result({
  "status": "passed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "I enter my information \"US\",\"en\",\"Coosign#123\",\"3456789012\",\"01\",\"22\",\"1993\",\"567895432\",\"/s/SMAPLE\"",
  "keyword": "And "
});
formatter.match({
  "location": "TBB_CoachSignUpPageStepDefs.i_enter_my_information(String,String,String,String,String,String,String,String,String)"
});
formatter.result({
  "status": "passed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "I click on Continue",
  "keyword": "When "
});
formatter.match({
  "location": "TBB_CoachSignUpPageStepDefs.i_click_on_Continue()"
});
formatter.result({
  "status": "passed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "Coach registered sucessfully and navigated to challenge pack step",
  "keyword": "Then "
});
formatter.match({
  "location": "TBB_CoachSignUpPageStepDefs.coach_registered_sucessfully_and_navigated_to_challenge_pack_step()"
});
formatter.result({
  "status": "passed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "I click on No thanks button to navigate to shakeology step",
  "keyword": "And "
});
formatter.match({
  "location": "TBB_CoachSignUpPageStepDefs.i_click_on_No_thanks_button_to_navigate_to_shakeology_step()"
});
formatter.result({
  "status": "passed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "I select Shakeology pack \"SHKCHVegan\"",
  "keyword": "When "
});
formatter.match({
  "location": "TBB_HomePageStepDefs.i_select_Shakeology_pack(String)"
});
formatter.result({
  "status": "passed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "I add to cart shakeology order with \"30\",\"Monthly\" and \"1\" options",
  "keyword": "And "
});
formatter.match({
  "location": "TBB_HomePageStepDefs.i_add_to_cart_shakeology_order_with_and_options(String,String,String)"
});
formatter.result({
  "status": "passed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "I click on No thanks buttons to navigate to offers page",
  "keyword": "Then "
});
formatter.match({
  "location": "TBB_HomePageStepDefs.i_click_on_No_thanks_buttons_to_navigate_to_offers_page()"
});
formatter.result({
  "status": "passed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "I skip offers to continue shopping",
  "keyword": "Then "
});
formatter.match({
  "location": "TBB_HomePageStepDefs.i_skip_offers_to_continue_shopping()"
});
formatter.result({
  "status": "passed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "I enter my shipping information \"3301 Exposition Blvd\",\"Santa Monica\",\"CA\",\"90404\"",
  "keyword": "Then "
});
formatter.match({
  "location": "TBB_ShippingDetailsPageStepDefs.i_enter_my_shipping_information(String,String,String,String)"
});
formatter.result({
  "status": "passed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "I validates the QAS address",
  "keyword": "And "
});
formatter.match({
  "location": "TBB_ShippingDetailsPageStepDefs.i_validates_the_QAS_address()"
});
formatter.result({
  "status": "passed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "I select shipping option \"1\" and click on continue",
  "keyword": "Then "
});
formatter.match({
  "location": "TBB_ShippingDetailsPageStepDefs.i_select_shipping_option_and_click_on_continue(String)"
});
formatter.result({
  "status": "passed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "I enter payment details",
  "keyword": "And "
});
formatter.match({
  "location": "TBB_PaymentPageStepDefs.enterPaymentDetails()"
});
formatter.result({
  "status": "passed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "I submit order",
  "keyword": "And "
});
formatter.match({
  "location": "TBB_HomePageStepDefs.i_submit_order()"
});
formatter.result({
  "status": "passed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "I should be able to see a successful order placement with an Order ID",
  "keyword": "Then "
});
formatter.match({
  "location": "TBB_HomePageStepDefs.i_should_be_able_to_see_a_successful_order_placement_with_an_Order_ID()"
});
formatter.result({
  "status": "passed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "I verify ATG, EBS, life ray, by design users are provisioned",
  "keyword": "And "
});
formatter.match({
  "location": "DBValidationSteps.iVerifyATG_EBS_LifeRay_ByDesign_UsersAreProvisioned()"
});
formatter.result({
  "status": "passed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "I verify user is syndicated in EBS",
  "keyword": "Then "
});
formatter.match({
  "location": "DBValidationSteps.iVerifyUserIsSyndicatedInEBS()"
});
formatter.result({
  "status": "passed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "I verify order is syndicated in EBS",
  "keyword": "And "
});
formatter.match({
  "location": "DBValidationSteps.iVerifyOrderIsSyndicatedInEBS()"
});
formatter.result({
  "status": "passed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "passed"
});
formatter.step({
  "name": "I verify order is syndicated to COM through EBS",
  "keyword": "And "
});
formatter.match({
  "location": "DBValidationSteps.iVerifyOrderIsSyndicatedToCOMThroughEBS()"
});
formatter.result({
  "error_message": "java.sql.SQLSyntaxErrorException: ORA-00942: table or view does not exist\n\r\n\tat oracle.jdbc.driver.T4CTTIoer.processError(T4CTTIoer.java:447)\r\n\tat oracle.jdbc.driver.T4CTTIoer.processError(T4CTTIoer.java:396)\r\n\tat oracle.jdbc.driver.T4C8Oall.processError(T4C8Oall.java:951)\r\n\tat oracle.jdbc.driver.T4CTTIfun.receive(T4CTTIfun.java:513)\r\n\tat oracle.jdbc.driver.T4CTTIfun.doRPC(T4CTTIfun.java:227)\r\n\tat oracle.jdbc.driver.T4C8Oall.doOALL(T4C8Oall.java:531)\r\n\tat oracle.jdbc.driver.T4CStatement.doOall8(T4CStatement.java:195)\r\n\tat oracle.jdbc.driver.T4CStatement.executeForDescribe(T4CStatement.java:876)\r\n\tat oracle.jdbc.driver.OracleStatement.executeMaybeDescribe(OracleStatement.java:1175)\r\n\tat oracle.jdbc.driver.OracleStatement.doExecuteWithTimeout(OracleStatement.java:1296)\r\n\tat oracle.jdbc.driver.OracleStatement.doScrollExecuteCommon(OracleStatement.java:4721)\r\n\tat oracle.jdbc.driver.OracleStatement.doScrollStmtExecuteQuery(OracleStatement.java:4854)\r\n\tat oracle.jdbc.driver.OracleStatement.executeQuery(OracleStatement.java:1505)\r\n\tat oracle.jdbc.driver.OracleStatementWrapper.executeQuery(OracleStatementWrapper.java:406)\r\n\tat com.beachbody.automation.utilities.DBConnectionUtilities.executeQueryUsingScrollableResultSet(DBConnectionUtilities.java:115)\r\n\tat com.beachbody.automation.utilities.DBConnectionUtilities.waitForResult(DBConnectionUtilities.java:138)\r\n\tat com.beachbody.automation.stepdefs.dbstep.DBValidationSteps.iVerifyOrderIsSyndicatedToCOMThroughEBS(DBValidationSteps.java:100)\r\n\tat ✽.I verify order is syndicated to COM through EBS(file:src/test/resources/features/e2e/TCID_C6019498.feature:26)\r\n",
  "status": "failed"
});
formatter.afterstep({
  "status": "passed"
});
formatter.beforestep({
  "status": "skipped"
});
formatter.step({
  "name": "I hit the byd soap request by placed orderNumber",
  "keyword": "Given "
});
formatter.match({
  "location": "OnlineAPIServicesSteps.hitBydSoapRequestByPlacedOrderNumber()"
});
formatter.result({
  "status": "skipped"
});
formatter.afterstep({
  "status": "skipped"
});
formatter.beforestep({
  "status": "skipped"
});
formatter.step({
  "name": "I validate response code 200",
  "keyword": "And "
});
formatter.match({
  "location": "OnlineAPIServicesSteps.i_validate_response_code(Integer)"
});
formatter.result({
  "status": "skipped"
});
formatter.afterstep({
  "status": "skipped"
});
formatter.beforestep({
  "status": "skipped"
});
formatter.step({
  "name": "I save coach id using placed order response body",
  "keyword": "Then "
});
formatter.match({
  "location": "OnlineAPIServicesSteps.saveCaochIDFromPlacedOrderRequestBody()"
});
formatter.result({
  "status": "skipped"
});
formatter.afterstep({
  "status": "skipped"
});
formatter.beforestep({
  "status": "skipped"
});
formatter.step({
  "name": "I get REP info response details using coach id",
  "keyword": "Given "
});
formatter.match({
  "location": "OnlineAPIServicesSteps.get()"
});
formatter.result({
  "status": "skipped"
});
formatter.afterstep({
  "status": "skipped"
});
formatter.beforestep({
  "status": "skipped"
});
formatter.step({
  "name": "I validate response code 200",
  "keyword": "And "
});
formatter.match({
  "location": "OnlineAPIServicesSteps.i_validate_response_code(Integer)"
});
formatter.result({
  "status": "skipped"
});
formatter.afterstep({
  "status": "skipped"
});
formatter.beforestep({
  "status": "skipped"
});
formatter.step({
  "name": "I validate coach details billing Country \"US\" and Billing Street One \"3301 Exposition Blvd\", billing city \"Santa Monica\" and ship state \"CA\", ship postal code \"90404\" and phone number \"3456789012\"",
  "keyword": "And "
});
formatter.match({
  "location": "OnlineAPIServicesSteps.validateCoachDetails(String,String,String,String,String,String)"
});
formatter.result({
  "status": "skipped"
});
formatter.afterstep({
  "status": "skipped"
});
formatter.embedding("image/png", "embedded0.png");
formatter.after({
  "status": "passed"
});
formatter.after({
  "status": "passed"
});
});