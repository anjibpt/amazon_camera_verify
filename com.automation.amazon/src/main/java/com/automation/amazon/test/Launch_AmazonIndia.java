package com.automation.amazon.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.support.ui.Select;

import com.google.common.io.Files;

public class Launch_AmazonIndia {
	@SuppressWarnings("unused")
	public static void main(String[] args) throws InterruptedException, IOException {
		
		DecimalFormat df = new DecimalFormat(".00");
		WebElement elmList = null;
		WebElement elmListItem = null;
		WebElement elmRow = null;
		WebElement elmQtyFnd = null;
		WebElement elmFndDiv = null;
		String productQty = "3";
		String getPrdFinalPrice = null;
		boolean bllnQty = false;
		boolean blnCnt = false;
		
		String chromeDriverPath = System.getProperty("user.dir") + "\\chromedriver.exe";
		String driverFilePath = "C:\\Softwares\\ChomeDriver\\chromedriver.exe";
		
		File fFile = new File(driverFilePath);
		File fFolder = new File("C:\\Softwares\\ChomeDriver");
				
		if(!fFile.exists()) {
			if(!fFolder.exists()) {
				fFolder.mkdirs();
			}
			
			File fFrom = new File(System.getProperty("user.dir") + "\\amazon_resources\\chromeDriver\\chromedriver.exe");
			File fTo = new File("C:\\Softwares\\ChomeDriver\\chromedriver.exe");
			
			Files.copy(fFrom, fTo);
		}
				
		System.setProperty("webdriver.chrome.driver","/usr/bin/google-chrome");
		System.setProperty("webdriver.chrome.driver", "C:\\Softwares\\ChomeDriver\\chromedriver.exe");
		
		//WebDriver driver = new FirefoxDriver();
		WebDriver driver = new ChromeDriver();
		
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		driver.get("https://www.amazon.com/");
		driver.manage().window().maximize();
		
		//search for an item
		WebElement elmSearch = driver.findElement(By.id("twotabsearchtextbox"));
		elmSearch.sendKeys("best selling digital camera");
		
		//click search
		List<WebElement> elmClickSearch = driver.findElements(By.tagName("input"));
		
		for(WebElement list:elmClickSearch) {
			if(list.getAttribute("value").contentEquals("Go")) {
				list.click();
				break;
			}
		}
		
		//search for fifth digital camera;find the camera list
		List<WebElement> ulElm = driver.findElements(By.tagName("ul"));
		
		for(WebElement list:ulElm) {
			if(list.getAttribute("id").contentEquals("s-results-list-atf")) {
				elmList = list;
				break;
			}
		}
		
		//find the fifth item from camera list
		List<WebElement> ulItems = elmList.findElements(By.tagName("li"));
		
		for(WebElement item: ulItems) {
			if(item.getAttribute("id").contentEquals("result_4")) {
				elmListItem = item;
				break;
			}
		}
		
		//click the fifth item from the list
		WebElement elmLink = elmListItem.findElement(By.tagName("h2"));
		elmLink.click();
		
		//Set<String> handler = driver.getWindowHandles();
		//Iterator<String> it = handler.iterator();
		
		//String parentWinId = it.next();
		//String childWinId = it.next();
		
		//switch to child window
		//driver.switchTo().window(childWinId);
		//driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		
		//get item selling price
		List<WebElement> elmPriceTable = driver.findElements(By.tagName("table"));
		
		for(WebElement tab: elmPriceTable) {
			if(tab.getAttribute("class").contentEquals("a-lineitem")) {
				elmRow = tab;
				break;
			}
		}
		
		WebElement elmPrice = elmRow.findElement(By.id("priceblock_ourprice"));
		String getPrdPrice = elmPrice.getText().substring(1); 
		
		//select the quantity
		List<WebElement> elmQty = driver.findElements(By.tagName("select"));
		
		for(WebElement elmSelect: elmQty) {
			if(elmSelect.getAttribute("id").contentEquals("quantity")) {
				elmQtyFnd = elmSelect;
				bllnQty = true;
				break;
			}
		}
		
		if(bllnQty == true) {
			Select qtySelect = new Select(elmQtyFnd);
			qtySelect.selectByValue(productQty);
		}else productQty = "1";
		
		//click add to cart button
		List<WebElement> elmAddToCart = driver.findElements(By.tagName("input"));
		
		for(WebElement addBttn:elmAddToCart) {
			if(addBttn.getAttribute("id").contentEquals("add-to-cart-button")) {
				addBttn.click();
				break;
			}
		}
		
		do {
			//get sub total of the product * 3
			List<WebElement> elmDivList = driver.findElements(By.tagName("div"));
			
			for(WebElement divList: elmDivList) {
				if(divList.getAttribute("class").contentEquals("a-section a-spacing-none a-padding-base attach-primary-atc-confirm-box")) {
					elmFndDiv = divList;
					blnCnt = true;
					break;
					}
				}
			}while(blnCnt != true);
		
		//get sub total of the product * 3
		List<WebElement> elmDivList = driver.findElements(By.tagName("div"));
		
		for(WebElement divList: elmDivList) {
			if(divList.getAttribute("class").contentEquals("a-section a-spacing-none a-padding-base attach-primary-atc-confirm-box")) {
				elmFndDiv = divList;
				break;
			}
		}
			
		List<WebElement> elmSpanList = elmFndDiv.findElements(By.tagName("span"));
		
		for(WebElement spanList: elmSpanList) {
			if(spanList.getAttribute("id").contentEquals("attach-accessory-cart-subtotal")) {
				getPrdFinalPrice = spanList.getText().substring(1); 
				break;
			}
		}
		
		//calculate the sub total
		double prdSellingPrice = Double.parseDouble(getPrdPrice);
		Integer prdQty = Integer.parseInt(productQty);
		double prdFinalPrice = Double.parseDouble(getPrdFinalPrice);
		
		System.out.println("product selling price: " +df.format(prdSellingPrice));
		System.out.println("product quantity: " +prdQty);
		System.out.println("product final price: " +df.format(prdFinalPrice));
		
		double expectedActualPrice = prdSellingPrice * prdQty;
		System.out.println("calculated final price: " +df.format(expectedActualPrice));
		
		if(prdFinalPrice == expectedActualPrice) {
			System.out.println("Actual ["+df.format(prdFinalPrice)+"] and Expected ["+df.format(expectedActualPrice)+"] final price matched!");
		}else {
			System.out.println("Actual ["+df.format(prdFinalPrice)+"] and Expected ["+df.format(expectedActualPrice)+"] final price did not matched!");
		}
		
		//close the browser
		driver.quit();
	}
}
