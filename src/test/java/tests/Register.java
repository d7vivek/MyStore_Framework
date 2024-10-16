package tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.aventstack.extentreports.Status;
import Reports.BaseTest;  // Import BaseTest which includes ExtentReport setup
import pageobjects.AccountSuccessPage;
import pageobjects.HomePage;
import pageobjects.RegisterPage;

public class Register extends BaseTest {  // Extend from BaseTest to inherit ExtentReports

    WebDriver driver = null;
    Properties prop = null;
    RegisterPage registerpage = null ;
    AccountSuccessPage accountsuccesspage = null;

    @BeforeMethod
    public void setup() {
        // Start a new test in the ExtentReport
        test = extent.createTest("Register Test - Setup");

        prop = new Properties();
        try {
            prop.load(new FileInputStream(new File("C:\\selenium-workspace\\practice-workspace\\javapractice\\pomPfProject\\src\\test\\java\\properties\\projectData.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String browserName = prop.getProperty("browser");

        if(browserName.equalsIgnoreCase("chrome")) {
            driver = new ChromeDriver();
        } else if(browserName.equalsIgnoreCase("edge")) {
            driver = new EdgeDriver();
        }

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get(prop.getProperty("url"));

        HomePage homepage = new HomePage(driver);
        homepage.clickOnMyAccount();
        registerpage = homepage.slectRegisterOption();

        // Log browser setup completion
        test.log(Status.INFO, "Browser setup and navigation to register page completed.");
    }

    @AfterMethod
    public void tearDown() {
        if(driver != null) {
            driver.quit();
        }

        // Log teardown steps
        test.log(Status.INFO, "Browser closed.");
    }

    @Test(priority = 1)
    public void registerWithAllfield() {
        test = extent.createTest("Register with All Fields Test");  // New test in report

        accountsuccesspage = registerpage.enterFirstNameToRegister(prop.getProperty("firstname"))
                .enterLastNametoRegister(prop.getProperty("lastname"))
                .enterEmailToRegister(genrateNewEmail())
                .enterTeliphone(prop.getProperty("teliphone"))
                .enterPassword(prop.getProperty("password"))
                .enterCnfPassword(prop.getProperty("password"))
                .clickOnYesNewsLetter()
                .CheckOnPrivacyPolicy()
                .clickOnSubmitButton();  // Log details of the registration process

        // Validate the expected outcome
        String expectedPageHeading = "Your Account Has Been Created!";
        String actualPageHeading = accountsuccesspage.retriveAccountSucessPageHeading();
        Assert.assertEquals(expectedPageHeading, actualPageHeading);

        // Log the test result
        test.log(Status.PASS, "Register with all fields completed successfully.");
    }

    @Test(priority = 2)
    public void registerWithMandatoryField() {
        test = extent.createTest("Register with Mandatory Fields Test");

        accountsuccesspage = registerpage.enterFirstNameToRegister(prop.getProperty("firstname"))
                .enterLastNametoRegister(prop.getProperty("lastname"))
                .enterEmailToRegister(genrateNewEmail())
                .enterTeliphone(prop.getProperty("teliphone"))
                .enterPassword(prop.getProperty("password"))
                .enterCnfPassword(prop.getProperty("password"))
                .clickOnYesNewsLetter()
                .CheckOnPrivacyPolicy().clickOnSubmitButton();

        String expectedPageHeading = "Your Account Has Been Created!";
        String actualPageHeading = accountsuccesspage.retriveAccountSucessPageHeading();
        Assert.assertEquals(actualPageHeading, expectedPageHeading);

        // Log the result in the report
        test.log(Status.PASS, "Register with mandatory fields completed successfully.");
    }

    @Test(priority = 3)
    public void registerWithoutEnteringAnyDetails() {
        test = extent.createTest("Register without Entering Any Details Test");

        registerpage.clickOnSubmitButton();

        String expectedError = "First Name must be between 1 and 32 characters!";
        String actualFirstNameError = registerpage.retrivalfirstNameError();
        Assert.assertEquals(actualFirstNameError, expectedError);

        String expectedLastNameError = "Last Name must be between 1 and 32 characters!";
        String actualLastNameError = registerpage.retriveLastNameError();
        Assert.assertEquals(actualLastNameError, expectedLastNameError);

        String expectedEmailError = "E-Mail Address does not appear to be valid!";
        String actualEmailError = registerpage.retriveEmailError();
        Assert.assertEquals(actualEmailError, expectedEmailError);

        String expectedTeliphoneError = "Telephone must be between 3 and 32 characters!";
        String actualTeliphoneError = registerpage.retriveTeliphoneError();
        Assert.assertEquals(actualTeliphoneError, expectedTeliphoneError);

        String expectedPasswordError = "Password must be between 4 and 20 characters!";
        String actualPasswordError = registerpage.retrivePasswordError();
        Assert.assertEquals(actualPasswordError, expectedPasswordError);

        String expectedPolicyError = "Warning: You must agree to the Privacy Policy!";
        String actualPolicyError = registerpage.retrivePrivacyPolicyError();
        Assert.assertEquals(actualPolicyError, expectedPolicyError);

        // Log the result in the report
        test.log(Status.PASS, "Validation of all mandatory fields passed successfully.");
    }

    public String genrateNewEmail() {
        Date date = new Date();
        return "ram" + date.toString().replaceAll(" ", "_").replace(":", "_") + "@gmail.com";
    }
}
