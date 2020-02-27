@Ofbiz @SystemTesting @Demo
  Feature: Verifying the Order Functionality
    Scenario: Verifying the Order Functionality

      Given I am on Ofbiz Login Page
      When I login Ofbiz with existing user
      Then I click on applications
      And I select Order Module in applications menu
      Then I click on Order Manager menu
      And I select Orders List in Order Manager menu
      Then I select all checkboxes in Order List Page
      When I click on Order Number in Order List
      Then I verify status history as completed
      And I select on Other Orders
      When I click on Other Order Number in Order list
      Then I verify status history as approved
      And I logout from Ofbiz application
