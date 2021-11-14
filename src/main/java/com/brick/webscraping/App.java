package com.brick.webscraping;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WaitingRefreshHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.opencsv.CSVWriter;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws InterruptedException, IOException {
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
		FileWriter scrapingData = new FileWriter("scraping.csv", true);
		CSVWriter write = new CSVWriter(scrapingData);
        String[] header = { "ID", "Name Of Product" ,"Description" ,"Image Link", "Price", "Rating" , "Merchant" };
        write.writeNext(header);
		
		try {
			HtmlPage page = webClient.getPage("https://www.tokopedia.com/p/handphone-tablet/handphone");

			webClient.getOptions().setUseInsecureSSL(true);
		    webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		    webClient.getOptions().setThrowExceptionOnScriptError(false);
//		    webClient.waitForBackgroundJavaScript(30000);
//		    webClient.waitForBackgroundJavaScriptStartingBefore(30000);
		    webClient.getOptions().setCssEnabled(false);
		    webClient.getOptions().setJavaScriptEnabled(true);
		    webClient.getCookieManager().setCookiesEnabled(true);
		    webClient.getOptions().setDownloadImages(false);
		    webClient.getOptions().setGeolocationEnabled(false);
		    webClient.getOptions().setAppletEnabled(false);
		    webClient.getOptions().setTimeout(10000);
			webClient.setRefreshHandler(new WaitingRefreshHandler());
		    webClient.getCache().setMaxSize(0);

		    List<String> imageLink = new ArrayList<>();
		    List<String> nameOfProduct = new ArrayList<>();
		    List<String> price = new ArrayList<>();
		    List<String> merchant = new ArrayList<>();

			List<HtmlImage> images = page.getByXPath("//img[@crossorigin='anonymous']");
			for (HtmlImage element : images) {
			
				imageLink.add(element.getAttributes().getNamedItem("src").getNodeValue());
				nameOfProduct.add(element.getAttributes().getNamedItem("alt").getNodeValue());
//				System.out.println("Image Link " + element.getAttributes().getNamedItem("src").getNodeValue());
//				System.out.println("Name of Product " + element.getAttributes().getNamedItem("alt").getNodeValue());

			}
			
			List<HtmlSpan> prices = page.getByXPath("//span[@class='css-o5uqvq']");

			for (HtmlSpan element : prices) {
				price.add(element.asNormalizedText());
//				System.out.println("Price " + element.asNormalizedText());
			}
			
			List<HtmlSpan> merchants = page.getByXPath("//span[@class='css-1kr22w3']");

			for (int i = 0; i < merchants.size(); i++) {
				if(i % 2 != 0) {
					merchant.add(merchants.get(i-1).asNormalizedText() + " - " + merchants.get(i).asNormalizedText());
//					System.out.println("merchant " + merchants.get(i-1).asNormalizedText() + " - " + merchants.get(i).asNormalizedText());
				}
			}
		
			// failed redirect page get more information at the other page
//			List<HtmlAnchor> contentUrl = page.getByXPath("//a[@data-testid='lnkProductContainer']");
//			for (HtmlAnchor element : contentUrl) {
//				System.out.println("contentUrl " + element.getHrefAttribute());
//				
//				String nextUrl = element.getHrefAttribute();
//				HtmlAnchor htmlAnchor = page.getAnchorByHref(nextUrl);
//				final HtmlPage newPage = htmlAnchor.click();
//				
//				webClient.getOptions().setRedirectEnabled(true);
//				System.out.println("Rating " + newPage.asNormalizedText());
//
//				webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//				HtmlSpan ratings = (HtmlSpan) newPage.getByXPath("//span[@data-testid='lblPDPDetailProductRatingNumber']").get(0);
//				System.out.println("Rating " + ratings.asNormalizedText());
//			}
			
			for (int i = 0; i < 10; i++) {

				 String[] data = { String.valueOf(i+1), nameOfProduct.get(i), " ", imageLink.get(i), price.get(i), " ", merchant.get(i) };
				 write.writeNext(data);
				 
			}
			write.close();
			webClient.close();

		} catch (IOException e) {
			System.out.println("An error occurred: " + e);
		}
	}
}
