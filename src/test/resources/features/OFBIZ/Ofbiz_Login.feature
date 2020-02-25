@Ofbiz @Login
  Feature: Login Functionality
    Scenario: Login Functionality

      Given I am on Ofbiz Login Page
      When I login Ofbiz with existing user
      Then I verify home page is displayed
      And I logout from Ofbiz application