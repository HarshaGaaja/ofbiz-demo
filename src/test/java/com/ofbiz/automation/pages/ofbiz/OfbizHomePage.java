package com.ofbiz.automation.pages.ofbiz;

import com.ofbiz.automation.common.World;
import com.ofbiz.automation.pages.BasePage;
import com.ofbiz.automation.utilities.CommonUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OfbizHomePage extends BasePage {
    private World world;

    ResourceBundle homePageElements;
    Logger logger;
    By textHomePageTitle;
    By buttonSignout;
    By textApplication;
    By textAccountingAP;
    By textPassDueInvoices;
    By buttonMarketingModule;
    By buttonMarketingManager;
    By buttonData;
    By textDataSource;
    By buttonMarketing;
    By textMarketingTitle;
    By buttonTracking;
    By textTrackingListCode;
    By buttonSegment;
    By textSegmentGroup;
    By buttonContactList;
    By textContactListTitle;
    By buttonReports;
    By textReportsTitle;
    By buttonAccountingAR;
    By buttonAssertmaint;
    By buttonCatalog;
    By buttonContent;
    By buttonFacility;
    By buttonHr;
    By buttonManufacturing;
    By buttonMyPortal;
    By buttonParty;
    By buttonProject;
    By buttonSFA;
    By buttonScrum;
    By buttonWorkeffort;
    By textPastDueInvoice;
    By textView;
    By textSearchProducts;
    By textWebList;
    By buttonCompany;
    By textPartyGroup;
    By textSystemInfo;
    By textFindParty;
    By textQuickAddContact;
    By textFindProduct;
    By textNewTask;

    By menuOrderManager;
    By selectOrderList;
    By menuOrderList;
    By chkHeld;
    By chkCompleted;
    By chkRejected;
    By chkCanceled;
    By chkPurchaseOrder;
    By chkInventoryProblems;
    By chkAuthorisationProblems;
    By chkPartiallyReceived;
    By chkopenPast;
    By chkRejectedItems;
    By buttonFind;
    By buttonDemoOrdNum;
    By buttonDemOrdNum;
    By menuFindOrders;
    By txtCompleted;
    By buttonOtherOrd;
    By textApproved;
    By buttonOrder;


    public void InitElements() {
        logger = LogManager.getLogger(OfbizLoginPage.class);
        homePageElements = ResourceBundle.getBundle("com.ofbiz.automation.elementlib.OFBIZ.HomePage", world.getFormattedLocale());
        textHomePageTitle = By.xpath(homePageElements.getString("text_HomePageTitle"));
        buttonSignout = By.xpath(homePageElements.getString("button_Signout"));
        textApplication = By.xpath(homePageElements.getString("text_Application"));
        textAccountingAP = By.xpath(homePageElements.getString("text_AccountingAP"));
        textPassDueInvoices = By.xpath(homePageElements.getString("text_PassDueInvoices"));
        buttonAccountingAR = By.xpath(homePageElements.getString("button_AccountingAR"));
        buttonAssertmaint = By.xpath(homePageElements.getString("button_Assertmaint"));
        buttonCatalog = By.xpath(homePageElements.getString("button_Catalog"));
        buttonContent = By.xpath(homePageElements.getString("button_Content"));
        buttonFacility = By.xpath(homePageElements.getString("button_Facility"));
        buttonHr = By.xpath(homePageElements.getString("button_Hr"));
        buttonManufacturing = By.xpath(homePageElements.getString("button_Manufacturing"));
        buttonMyPortal = By.xpath(homePageElements.getString("button_MyPortal"));
        buttonParty = By.xpath(homePageElements.getString("button_Party"));
        buttonProject = By.xpath(homePageElements.getString("button_Project"));
        buttonSFA = By.xpath(homePageElements.getString("button_SFA"));
        buttonScrum = By.xpath(homePageElements.getString("button_Scrum"));
        buttonWorkeffort = By.xpath(homePageElements.getString("button_Workeffort"));
        textPastDueInvoice = By.xpath(homePageElements.getString("text_PastDueInvoice"));
        textView = By.xpath(homePageElements.getString("text_View"));
        textSearchProducts = By.xpath(homePageElements.getString("text_SearchProducts"));
        textWebList = By.xpath(homePageElements.getString("text_WebList"));
        buttonMarketingModule = By.xpath(homePageElements.getString("button_MarketingModule"));
        buttonMarketingManager = By.cssSelector(homePageElements.getString("button_MarketingManager"));
        buttonData = By.xpath(homePageElements.getString("button_Data"));
        textDataSource = By.xpath(homePageElements.getString("text_DataSource"));
        buttonMarketing = By.xpath(homePageElements.getString("button_Marketing"));
        textMarketingTitle = By.xpath(homePageElements.getString("text_MarketingTitle"));
        buttonTracking = By.xpath(homePageElements.getString("button_Tracking"));
        textTrackingListCode = By.xpath(homePageElements.getString("text_TrackingListCode"));
        buttonSegment = By.xpath(homePageElements.getString("button_Segment"));
        textSegmentGroup = By.xpath(homePageElements.getString("text_SegmentGroup"));
        buttonContactList = By.xpath(homePageElements.getString("button_ContactList"));
        buttonReports = By.xpath(homePageElements.getString("button_Reports"));
        textReportsTitle = By.xpath(homePageElements.getString("text_ReportsTitle"));
        menuOrderManager = By.cssSelector(homePageElements.getString("menu_OrderManager"));
        selectOrderList = By.xpath(homePageElements.getString("select_OrderList"));
        menuOrderList = By.cssSelector(homePageElements.getString("menu_OrderList"));
        chkHeld = By.xpath(homePageElements.getString("chk_Held"));
        chkCompleted = By.xpath(homePageElements.getString("chk_Completed"));
        chkRejected = By.xpath(homePageElements.getString("chk_Rejected"));
        chkCanceled = By.xpath(homePageElements.getString("chk_Canceled"));
        chkPurchaseOrder = By.xpath(homePageElements.getString("chk_PurchaseOrder"));
        chkInventoryProblems = By.xpath(homePageElements.getString("chk_InventoryProblems"));
        chkAuthorisationProblems = By.xpath(homePageElements.getString("chk_AuthorisationProblems"));
        chkPartiallyReceived = By.xpath(homePageElements.getString("chk_PartiallyReceived"));
        chkopenPast = By.xpath(homePageElements.getString("chk_openPast"));
        chkRejectedItems = By.xpath(homePageElements.getString("chk_RejectedItems"));
        buttonFind = By.xpath(homePageElements.getString("button_Find"));
        buttonDemoOrdNum = By.xpath(homePageElements.getString("button_DemoOrdNum"));
        buttonDemOrdNum = By.xpath(homePageElements.getString("button_DemOrdNum"));
        menuFindOrders = By.xpath(homePageElements.getString("menu_FindOrders"));
        txtCompleted = By.xpath(homePageElements.getString("txt_Completed"));
        buttonOtherOrd = By.xpath(homePageElements.getString("button_OtherOrd"));
        textApproved = By.xpath(homePageElements.getString("text_Approved"));
        buttonOrder = By.xpath(homePageElements.getString("button_Order"));
        buttonCompany = By.xpath(homePageElements.getString("button_Company"));
        textPartyGroup = By.xpath(homePageElements.getString("text_PartyGroup"));
        textSystemInfo = By.xpath(homePageElements.getString("text_SystemInfo"));
        textFindParty = By.xpath(homePageElements.getString("text_FindParty"));
        textQuickAddContact = By.xpath(homePageElements.getString("text_QuickAddContact"));
        textFindProduct = By.xpath(homePageElements.getString("text_FindProduct"));
        textNewTask = By.xpath(homePageElements.getString("text_NewTask"));
    }

    public OfbizHomePage(World world) {
        super(world, world.driver);
        this.world = world;
        InitElements();
    }

    public void clickLogout() {
        try {
            logger.info("Clicking on Logout Link");
            verifyElementDisplayed(buttonSignout);
            click(buttonSignout);
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(), e);
        }
    }

    public void verifyHomePageDisplayed() {
        try {
         logger.info("Verifying Home Page is displayed");
         verifyElementDisplayed(textHomePageTitle);
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
        }
    }

    public void clickOnApplication() {
        try {
            logger.info("Clicking on Application");
            onMouseOver(textApplication);
            click(textApplication);
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
        }
    }

    public void clickOnAccountingAP() {
        try {
            logger.info("Clicking on Application");
            onMouseOver(textAccountingAP);
            click(textAccountingAP);
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
        }
    }
    public void verifyAccountingAPPage() {
        try {
            logger.info("Clicking on Application");
            verifyElementPresence(textPassDueInvoices);
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
        }
    }
    public void verifyAndClickOnEachModule() {
        try {
            logger.info("Clicking and verifying each and every module");
            click(textApplication);
            click(buttonAccountingAR);
           verifyElementPresence(textPastDueInvoice);
           syncObjects("hcwait2");
            click(textApplication);
            click(buttonAssertmaint);
           verifyElementPresence(textView);
            syncObjects("hcwait2");
            click(textApplication);
            click(buttonCatalog);
           verifyElementPresence(textSearchProducts);
            syncObjects("hcwait2");
            click(textApplication);
            click(buttonContent);
            verifyElementPresence(textWebList);
            syncObjects("hcwait2");
            click(textApplication);
            clickByJS(buttonFacility);
           verifyElementPresence(textView);
            syncObjects("hcwait2");
            click(textApplication);
            click(buttonHr);
            syncObjects("hcwait1");
            click(textApplication);
            click(buttonManufacturing);
           verifyElementPresence(textMarketingTitle);
            syncObjects("hcwait2");
            click(textApplication);
            click(buttonMyPortal);
            verifyElementPresence(textSystemInfo);
            syncObjects("hcwait2");
            click(textApplication);
            click(buttonParty);
          verifyElementPresence(textFindParty);
            syncObjects("hcwait2");
            click(textApplication);
            click(buttonProject);
            verifyElementPresence(textWebList);
            syncObjects("hcwait2");
            click(textApplication);
            click(buttonSFA);
            verifyElementPresence(textQuickAddContact);
            syncObjects("hcwait2");
            click(textApplication);
            click(buttonScrum);
            verifyElementPresence(textFindProduct);
            syncObjects("hcwait2");
            click(textApplication);
            click(buttonWorkeffort);
           verifyElementPresence(textNewTask);
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
        }

    }

    public void clickOnMarketingModule() {
        try {
            logger.info("Clicking on Marketing Module");
            click(buttonMarketingModule);
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
        }
    }

    public void verifyMarketingManagerTitle() {
        try {
            logger.info("Verifying Marketing Manager Title");
            verifyElementDisplayed(buttonMarketingManager);
            click(buttonMarketingManager);
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
        }
    }

    public void clickOneachModuleAndVerifyPages() {
        try {
            logger.info("Clicking on Each Module and Verifying each module Pages");
            click(buttonData);
            verifyElementDisplayed(textDataSource);
            click(buttonMarketingManager);
            click(buttonMarketing);
            verifyElementDisplayed(textMarketingTitle);
            click(buttonMarketingManager);
            click(buttonTracking);
            verifyElementDisplayed(textTrackingListCode);
            click(buttonMarketingManager);
            click(buttonSegment);
            verifyElementDisplayed(textSegmentGroup);
            click(buttonMarketingManager);
            click(buttonContactList);
            verifyElementDisplayed(textMarketingTitle);
            click(buttonMarketingManager);
            click(buttonReports);
            verifyElementDisplayed(textReportsTitle);
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
        }
    }

    public void clickOnOrderModule() {
        try {
            logger.info("Clicking on Order Module");
            click(buttonOrder);
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
        }
    }

    public void clickOrderManagermenu() {
        try {
            logger.info("Clicking on Order Manager menu");
            click(menuOrderManager);
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
        }
    }

    public void clickOrderList() {
        try {
            logger.info("Clicking on Order List");
            click(selectOrderList);
            verifyElementDisplayed(menuOrderList);
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
        }
    }

    public void selectAllChkBoxesInOrdrList() {
        try {
            logger.info("Selecting All Check boxes In Orders List");
            click(chkHeld);
            click(chkCompleted);
            click(chkRejected);
            click(chkCanceled);
            click(chkPurchaseOrder);
            click(chkInventoryProblems);
            click(chkAuthorisationProblems);
            click(chkPartiallyReceived);
            click(chkopenPast);
            click(chkRejectedItems);
            click(buttonFind);
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
        }
    }

    public void clickOrderNumber() {
        try {
            logger.info("Clicking On Order number");
            click(buttonDemoOrdNum);
            verifyElementDisplayed(menuFindOrders);
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
        }
    }

    public void VerifyStatusHistoryASCompleted() {
        try {
            logger.info("Verifying Status As Completed");
            verifyElementDisplayed(txtCompleted);
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
        }
    }

    public void selectOtherOrders() {
        try {
            logger.info("Clicking on Other Orders button");
            click(buttonOtherOrd);
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
        }
    }

    public void clickOtherorderNumber() {
        try {
            logger.info("Clicking on Other Order Number");
            click(buttonDemOrdNum);
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
        }
    }

    public void verifyStatusHistoryAsApproved() {
        try {
            logger.info("Verifying Staus As Approved");
            click(textApproved);
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
        }
    }
    public void clickAndVerifyOnEachModule() {
        try {
            logger.info("Clicking on Account payable Manager");
            click(buttonMarketingManager);
            click(buttonData);
            verifyElementPresence(textMarketingTitle);
            click(buttonMarketingManager);
            click(buttonMarketing);
            verifyElementPresence(textMarketingTitle);
            click(buttonMarketingManager);
            click(buttonTracking);
            verifyElementPresence(textMarketingTitle);
            click(buttonMarketingManager);
            click(buttonSegment);
            verifyElementPresence(textMarketingTitle);
            click(buttonMarketingManager);
            click(buttonContactList);
            verifyElementPresence(textMarketingTitle);
            click(buttonMarketingManager);
            click(buttonReports);
            verifyElementPresence(textPassDueInvoices);
        } catch (Exception e) {
            failScenarioAndReportInfo(this.getClass().getSimpleName() + " >> " + world.getMyMethodName(),e);
        }
    }
}