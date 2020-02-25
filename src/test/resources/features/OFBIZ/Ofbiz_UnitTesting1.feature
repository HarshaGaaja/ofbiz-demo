 @UnitTesting1
  Feature: Verifying the Application Modules Functionality
    Scenario: Verifying the Application Modules Functionality

      Given I am on Ofbiz Login Page
      When I login Ofbiz with existing user
      Then I verify home page is displayed
      And I click on applications
      Then I click and verify each module
      And I logout from Ofbiz application

