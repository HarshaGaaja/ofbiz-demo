@Ofbiz @UnitTesting3
Feature: Verifying the Accounting-AP Functionality
  Scenario: Verifying the Accounting-AP Functionality

    Given I am on Ofbiz Login Page
    When I login Ofbiz with existing user
    Then I click on applications
    And I select the Accounting-AP
    Then Accounting-AP page should be displayed
    And I click on each module to verify the pages are displayed
    Then I logout from Ofbiz application
