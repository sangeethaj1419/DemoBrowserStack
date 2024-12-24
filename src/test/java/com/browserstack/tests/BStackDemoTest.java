package com.browserstack.tests;

import com.browserstack.data.TestData;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import space.dynomake.libretranslate.Language;
import space.dynomake.libretranslate.Translator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static com.browserstack.locators.TestLocators.article;
import static com.browserstack.locators.TestLocators.opnionLink;


public class BStackDemoTest extends SeleniumTest {

  TestData testData = new TestData();
  String imageUrls;

  //  1 - Verify Website language is Spanish
  @Test(priority = 1, description = "get description of the website")
  public void getSiteLanguage() {
    WebElement htmlElement = driver.findElement(By.tagName("html"));
    String language = htmlElement.getAttribute("lang");
    String baseLang = language.split("-")[0];
    Locale locale = new Locale(baseLang);
    String langName = locale.getDisplayName();
    System.out.println("Website language is: " + langName);
    Assert.assertEquals(langName, "Spanish", "Website Language is not spanish");
  }

  //  2 - get article headers in opinion list
  @Test(priority = 2, description = "get headers of the article and save images")
  public void getHeaderSaveImage() {
    WebElement opinionLink = driver.findElement(By.xpath(opnionLink));
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("arguments[0].click()", opinionLink);
    String opinionUrl = driver.getCurrentUrl();
    Assert.assertTrue(opinionUrl.contains("opinion"));

    for (int i = 1; i <= 5; i++) {
      String articleList = driver.findElement(By.xpath(article + "[" + i + "]//h2/a")).getText();
      String articleContent = driver.findElement(By.xpath(article + "[" + i + "]//p")).getText();
      testData.spanishArtList.add(articleList);
      WebElement image = null;
      try {
        image = driver.findElement(By.xpath(article + "[" + i + "]//img"));
        if (image.isDisplayed()) {
          imageUrls = image.getAttribute("src");
          URL imageUrl = new URL(imageUrls);
          BufferedImage saveImage = ImageIO.read(imageUrl);
          String filepath = "src/test/resources/images/image" + i + ".png";
          ImageIO.write(saveImage, "png", new File(filepath));
        }
      } catch (Exception e) {
      }
      System.out.println("Article Content " + i + " " + articleList + " -> " + articleContent);
    }
    System.out.println();
  }

  //  3 - Translate the article headers and print

  @Test(priority = 3, description = "translate article headers and print the words occurrences")
  public void translateArticleHeaders() {
    HashMap<String, Integer> hm = new LinkedHashMap<>();
    for (int i = 0; i < 5; i++) {
      String translatedText = Translator.translate(Language.SPANISH, Language.ENGLISH, testData.spanishArtList.get(i));
      System.out.println("Translated Article Text" + testData.spanishArtList.get(i) + "-> " + translatedText);
      String[] str = translatedText.split(" ");
      for (String str1 : str) {
        if (!hm.containsKey(str1)) {
          hm.put(str1, 1);
        } else {
          hm.put(str1, hm.get(str1) + 1);
        }
      }
    }
    System.out.println();

    //    4 - find occurrences of word in a string

    for (Map.Entry<String, Integer> m : hm.entrySet()) {
      if (m.getValue() >= 2) {
        System.out.println(m.getKey() + " -> occurred - " + m.getValue());
      }
    }
  }
}
