package com.testing.example;

import com.testing.Handler;
import com.testing.constants.ConfigConstants;
import com.testing.logging.Log;
import com.testing.example.constants.ExampleConfigConstants;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.*;

import java.net.URL;
import java.util.concurrent.TimeUnit;

public class SetUp {

    @BeforeSuite
    public void Init() {
        Log.Debug("SetUp initiated");
        Handler.init();
    }

    @BeforeTest
    @Parameters({"platform", "browser", "devicename", "udid", "ip", "port"})
    public void SetUp(String platform, @Optional String browser, @Optional String devicename,
                      @Optional String udid, @Optional String ip, @Optional String port) throws Exception {

        Log.Debug("Platform: " + platform);

        if (platform.equalsIgnoreCase("android")) {
            if (Handler.GetCurrentAppiumDriver() == null) {
                if (devicename == null) devicename = ExampleConfigConstants.DEVICE_NAME;

                DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setCapability(ConfigConstants.CAPABILITIES_DEVICE_NAME, devicename);
                capabilities.setCapability(CapabilityType.BROWSER_NAME, ExampleConfigConstants.BROWSER_NAME);
                capabilities.setCapability(ConfigConstants.CAPABILITIES_PLATFORM_NAME, ExampleConfigConstants.PLATFORM_NAME);
                capabilities.setCapability(ConfigConstants.CAPABILITIES_APP_PACKAGE, ExampleConfigConstants.APP_PACKAGE);
                capabilities.setCapability(ConfigConstants.CAPABILITIES_APP_ACTIVITY, ExampleConfigConstants.APP_ACTIVITY);

                if (udid != null) capabilities.setCapability(ConfigConstants.CAPABILITIES_UDID, udid);
                if (ip == null) ip = ExampleConfigConstants.DEFAULT_IP;
                if (port == null) port = ExampleConfigConstants.DEFAULT_PORT;

                String url = "http://" + ip + ":" + port + "/wd/hub";
                Handler.SetCurrentAppiumDriver(new AndroidDriver(new URL(url), capabilities));

                Log.Debug("Appium Driver set up for device: " + Handler.GetCurrentAppiumDriver()
                        .getCapabilities().getCapability(ConfigConstants.CAPABILITIES_DEVICE_NAME));
            }
        } else if (platform.equalsIgnoreCase("web")) {
            // Path to your downloaded geckodriver
            System.setProperty("webdriver.gecko.driver",
                    "C:\\Users\\darah\\Downloads\\geckodriver-v0.36.0-win64\\geckodriver.exe");

            // Initialize Firefox WebDriver
            Handler.SetCurrentWebDriver(new FirefoxDriver());
            Handler.GetCurrentWebDriver().manage().timeouts()
                    .implicitlyWait(ConfigConstants.DEFAULT_TIMEOUT, TimeUnit.SECONDS);

            // Navigate to Google
            String url = "https://www.google.com";
            Handler.GetCurrentWebDriver().get(url);
            Log.Debug("Navigated to: " + url);
        }
    }

    @AfterTest
    public void AfterTest() {
        if (Handler.GetCurrentAppiumDriver() != null) {
            Log.Debug("Quitting Appium driver for device: " + Handler.GetCurrentAppiumDriver()
                    .getCapabilities().getCapability(ConfigConstants.CAPABILITIES_DEVICE_NAME));
            Handler.GetCurrentAppiumDriver().quit();
        }

        if (Handler.GetCurrentWebDriver() != null) {
            Log.Debug("Quitting WebDriver");
            Handler.GetCurrentWebDriver().quit();
        }
    }

    @AfterSuite
    public void AfterSuite() {
        Log.Debug("Clearing driver hashmaps");
        Handler.ClearAppiumDriverHashmap();
        Handler.ClearWebDriverHashmap();
    }
}
