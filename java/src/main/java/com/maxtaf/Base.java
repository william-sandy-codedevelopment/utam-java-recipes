package com.maxtaf;

import com.maxtaf.utils.ActiveNodeDeterminer;
import com.maxtaf.utils.ParamsCons;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class Base {

  public static HashMap<String, String> runAttributes;

  public static void init(String[] args) {
    HashMap<String, String> runAttributes = new HashMap<>();
    runAttributes.put("runId", args[0]);
    runAttributes.put("runnableOrCompiledFileRootFolder", args[1]);
    runAttributes.put("maxtafCloudHostUrl", args[2]);
    runAttributes.put("userProjectHostUrl", args[3]);
    runAttributes.put("urlGateway", args[4]);
    runAttributes.put("projectId", args[5]);
    runAttributes.put("firebaseToken", args[6]);
    runAttributes.put("refreshToken", args[7]);
    runAttributes.put("timeZoneId", args[8]);

    Base.runAttributes = runAttributes;
  }

  public static ApiService getApiService() {
    return new ApiService(
            runAttributes.get("runId"),
            runAttributes.get("projectId"),
            runAttributes.get("firebaseToken"),
            runAttributes.get("refreshToken"),
            runAttributes.get("maxtafCloudHostUrl"),
            runAttributes.get("userProjectHostUrl"),
            runAttributes.get("urlGateway"),
            runAttributes.get("timeZoneId")
    );
  }

  public static WebDriver createDriver() throws IOException {
    ApiService apiService = getApiService();

    String browser = apiService.getParam(ParamsCons.PARAMS_SELENIUM_BROWSER);
    if (browser == null || browser.equals("") || browser.equals("chrome"))
      return createDriver(new ChromeOptions());
    else if (browser.equals("firefox"))
      return createDriver(new FirefoxOptions());

    apiService.addLogLine("Unsupported browser: " + browser);
    apiService.addLogLine("Switching to Chrome...");

    return createDriver(new ChromeOptions());
  }

  public static WebDriver createDriver(MutableCapabilities mutableCapabilities) throws IOException {
    ApiService apiService = getApiService();

    String proxyParameter = apiService.getParam(ParamsCons.PARAMS_SELENIUM_PROXY);
    if (proxyParameter != null && !proxyParameter.equals("")) {
      Proxy proxy = new Proxy();
      proxy.setHttpProxy(proxyParameter);
      proxy.setSslProxy(proxyParameter);
      mutableCapabilities.setCapability("proxy", proxy);
    }

    mutableCapabilities.setCapability("webdriver.remote.quietExceptions", true);
    mutableCapabilities.setCapability("enableVNC", true);
    String enableVideoRecording = apiService.getParam(ParamsCons.PARAMS_SELENIUM_ENABLE_VIDEO_RECORDING);
    if (enableVideoRecording != null && enableVideoRecording.equals("true"))
      mutableCapabilities.setCapability("enableVideo", true);

    URL seleniumServerAddress = new URL(apiService.getParam(ParamsCons.PARAMS_SELENIUM_SERVER_ADDRESS));
    RemoteWebDriver driver = new RemoteWebDriver(seleniumServerAddress, mutableCapabilities);

    ActiveNodeDeterminer determiner = new ActiveNodeDeterminer(apiService.getParam(ParamsCons.PARAMS_SELENIUM_SERVER_ADDRESS));
    try {
      apiService.saveGridNodeHost(determiner.getNodeInfoForSession(driver.getSessionId()).toString());
    } catch (Exception e) {
      System.out.println("Cannot determine grid node host from driver session!");
      try {
        apiService.saveGridNodeHost(seleniumServerAddress.getHost());
      } catch (Exception e2) {
        System.out.println("Cannot determine grid node host from mx.selenium.server!");
      }
    }

    return driver;
  }

}
