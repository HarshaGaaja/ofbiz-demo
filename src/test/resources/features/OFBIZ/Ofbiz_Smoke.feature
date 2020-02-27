@Ofbiz @Smoke @Demo
  Feature: Verifying the Accounting-AP functionality
    Scenario: Verifying the Accounting-AP functionality

      Given I am on Ofbiz Login Page
      When I login Ofbiz with existing user
      When I click on applications
      And I select the Accounting-AP
      Then Accounting-AP page should be displayed