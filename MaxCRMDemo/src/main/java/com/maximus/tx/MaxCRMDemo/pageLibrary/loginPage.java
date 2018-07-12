package com.maximus.tx.MaxCRMDemo.pageLibrary;

import com.maximus.tx.MaxCRMDemo.testBase.TestBase;

public class loginPage extends TestBase {
	
	private void setMockUserName(String strUserName) {
		getWebelement(maxcrmdemo.loginpage.username).sendKeys(strUserName);
    }

    private void setMockPassword(String strPassword) {
    	getWebelement(maxcrmdemo.loginpage.password).sendKeys(strPassword);
    }

    private void clickMockSubmit() {
        mockSubmit.click();
    }

    public void loginToMockPage(String strUserName, String strPassword) {
        this.setMockUserName(strUserName);
        this.setMockPassword(strPassword);
        this.clickMockSubmit();
    }

}
