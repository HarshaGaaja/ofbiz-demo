@Ofbiz @UnitTesting2
  Feature: Verifying the Marketing Modules Functionality
    Scenario: Verifying the Marketing Modules Functionality

      Given I am on Ofbiz Login Page
      When I login Ofbiz with existing user
      Then I click on applications
      And I click on Marketing Module
      Then I verify Marketing Manager page is displayed
      And I click on each module to verify the pages got displayed
      Then I logout from Ofbiz application