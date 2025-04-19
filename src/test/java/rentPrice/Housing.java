package rentPrice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JOptionPane;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.nio.file.Files;

public class Housing {

	static int var = 0;
	static String rfp;
	static String lfp;

	public static void main(String[] args) throws InterruptedException, TimeoutException {

		Properties emailProps = RentMailer.loadEmailProperties();
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("profile.default_content_setting_values.notifications", 2);
		
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", prefs);
		options.addArguments("--disable-notifications");

		WebDriver driver = null;
		try {
			driver = new ChromeDriver(options);
			addLog("Launching chrome driver", "Pass", "Pass");

		} catch (Exception e) {
			addLog("Launching chrome driver", "Pass", "Fail");
			logEntry();
			sendLogs(emailProps);
			driver.quit();
			return;
		}
		try {
			driver.get("https://www.housing.com");
			addLog("opening the Url", "Pass", "Pass");
		} catch (Exception e) {
			addLog("opening the Url", "Pass", "Fail");
			logEntry();
			sendLogs(emailProps);
			driver.quit();
			return;
		}
		try {
			driver.manage().window().maximize();
			addLog("Maximizing the browser", "Pass", "Pass");
		} catch (Exception e) {
			addLog("Maximizing the browser", "Pass", "Fail");
			logEntry();
			sendLogs(emailProps);
			driver.quit();
			return;
		}

		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));


		try {
			driver.findElement(By.xpath("//*[@class = 'search-tab css-18w4akt']")).click();
			addLog("Clicking on search for Rent ", "Pass", "Pass");
		} catch (Exception e) {
			addLog("Clicking on search for rent", "Pass", "Fail");
			logEntry();
			sendLogs(emailProps);
			driver.quit();
			return;
		}

		WebElement inputContainer = driver.findElement(By.className("input-container"));
		try {
			inputContainer.click();
			addLog("Clicking on Text Box", "Pass", "Pass");
		} catch (Exception e) {
			addLog("Clicking on Text Box", "Pass", "Fail");
			logEntry();
			sendLogs(emailProps);
			driver.quit();
			return;
		}


		String inp = "";
		boolean validCity = false;
		int count = 0;
		boolean found = false;

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		while (!validCity && count < 3) {
		    try {
		        inp = JOptionPane.showInputDialog(null, "Enter the city name:", "Enter city",
		                JOptionPane.QUESTION_MESSAGE);
		        if (inp == null || inp.trim().isEmpty()) {
		            throw new Exception("Empty input or cancelled");
		        }
		    } catch (Exception e) {
		        addLog("Input of city name", "Valid non-empty string", "Fail (empty or cancelled)");
		        logEntry();
		        sendLogs(emailProps);
		        driver.quit();
		        return;
		    }

		    try {
		        WebElement searchBar = driver.findElement(By.xpath("//*[@class='search-bar css-80eldr']"));
		        searchBar.clear();
		        searchBar.sendKeys(inp);

		        // Wait for the suggestions to appear
		        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[@class='list css-aw4os4']/li")));

		        // Get the list of suggestion items
		        List<WebElement> suggestions = driver.findElements(By.xpath("//ul[@class='list css-aw4os4']/li"));

		        // Print all suggestion texts (for debug)
		        for (WebElement option : suggestions) {
		            System.out.println("Found: " + option.getText());
		        }

		        // Match input with suggestion (case-insensitive)
		        for (WebElement option : suggestions) {
		            if (option.getText().equalsIgnoreCase(inp)) {
		                option.click();
		                validCity = true;
		                found = true;
		                addLog("City input validation", "Pass", "Pass");
		                break;
		            }
		        }

		        if (!validCity) {
		            addLog("City input validation", "Pass", "Fail");
		            JOptionPane.showMessageDialog(null, "City not found. Please try again.", "Invalid Input",
		                    JOptionPane.WARNING_MESSAGE);
		        }

		    } catch (Exception e) {
		        addLog("Error while validating city input", "Pass", "Fail");
		        JOptionPane.showMessageDialog(null, "Invalid city name. Please try again.", "Invalid Input",
		                JOptionPane.WARNING_MESSAGE);
		    }

		    count++;
		}
		// If not valid after 3 tries, send log and quit
		if (!validCity) {
			System.out.println("❌ 3 invalid attempts. Exiting.");
			addLog("City selection process", "City should be valid within 3 attempts", "Fail");
			logEntry();
			sendLogs(emailProps);
			driver.quit();
			return;
		}

		String rou = "";
		if (found == true) {
			rou = JOptionPane.showInputDialog(null, "Enter the no. of data ", "Enter number",
					JOptionPane.QUESTION_MESSAGE);
		}
		int round = Integer.parseInt(rou);
		int nextMultiple = ((round / 30) + 1) * 30;
		int result = nextMultiple / 30;
		var = round;

		
		WebElement searchButton = null;
		for (int i = 0; i < 3; i++) {
			try {
				WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(2));
				searchButton = wait1.until(ExpectedConditions.elementToBeClickable(By.xpath(
						"//*[@class = 'T_searchButtonStyle _1q73exct _csbfng _2db3ly _g38jkm _5j12xx _3f1b13 _l8115q _girdoj T_btnStyle _j9qr11ef _1yfe11ef _xvuoe25i _1vt4glyw _9jtlke _7lu67f _c8dlk8 _26oii0 cta']")));
				searchButton.click();
				addLog("Trying to hit the search button", "Pass", "Pass");
				break;
			} catch (org.openqa.selenium.StaleElementReferenceException e) {
				System.out.println("Retrying due to stale element...");
				addLog("Trying to hit the search button", "Pass", "Fail");
			}
		}
		Thread.sleep(2000);
		try {
			driver.findElement(By.xpath("//*[@class = 'css-gg4vpm']")).click();
			addLog("Click on sort button", "Pass", "Pass");
		} catch (Exception e) {
			addLog("Click on sort button", "Pass", "Fail");
			logEntry();
			sendLogs(emailProps);
			driver.quit();
		}

	

		Thread.sleep(3000);
		try {
			driver.findElement(By.xpath("//*[text() = 'Price (Inc)']")).click();
			addLog("Click on low to high button", "Pass", "Pass");
		} catch (Exception e) {
			addLog("Click on low to high button", "Pass", "Fail");
			logEntry();
			sendLogs(emailProps);
			driver.quit();
		}
		Thread.sleep(2000);


		int val = 0;
		try {
			String prop = driver.findElement(By.xpath("//*[@class = 'property-count css-1v994a0']")).getText();
			String part = prop.split("of")[1].split("properties")[0].trim();
			val = Integer.parseInt(part);
			System.out.println(val);
		} catch (Exception e) {

		}

		int val1 = (val / 30) + 1;
		WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(2));

		try {
			wait2.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
					"//*[@class = 'T_infoStyle _1rft1bp4 _k84m1txw _1sfm1e54 _1yglcj1k _10yjkb7n _16wb15vq _10f91ulh _2hy4exct _1uhk1nn1 _3hqmdlk8 _93d91t02 _10m91e54 _j73acj1k _1i0zkb7n _19aa15vq _8bns1ulh _1nswgktf _czpcbfng _1epzexct _syf11t02 _leftgi _lkftgi _l8exct _cxexct _fc1yb4 _ar1bp4 _9s1txw _vy1ckf']")));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "No data is present for this location", "Thank You",
					JOptionPane.WARNING_MESSAGE);
			addLog("Data avilable", "Pass", "Fail");
			logEntry();
			sendLogs(emailProps);
			driver.quit();
			return;
		}

		for (int i = 1; i < result; i++) {
			
			if (i == val1) {
				JOptionPane.showMessageDialog(null, "All Avilable data has been extracted", "Thank You",
						JOptionPane.WARNING_MESSAGE);
				break;
			}
			Actions action = new Actions(driver);
			try {
				Thread.sleep(2000);
				action.sendKeys(Keys.END).perform();
				
			} catch (Exception e) {
				addLog("Fetch more rent data", "Pass", "Fail");
				logEntry();
				sendLogs(emailProps);
				driver.quit();
				return;
			}
			Thread.sleep(2000);
			
		}
		addLog("Fetch rent data", "Pass", "Pass");
		List<WebElement> data = driver.findElements(By.xpath(
				"//*[@class = 'T_infoStyle _1rft1bp4 _k84m1txw _1sfm1e54 _1yglcj1k _10yjkb7n _16wb15vq _10f91ulh _2hy4exct _1uhk1nn1 _3hqmdlk8 _93d91t02 _10m91e54 _j73acj1k _1i0zkb7n _19aa15vq _8bns1ulh _1nswgktf _czpcbfng _1epzexct _syf11t02 _leftgi _lkftgi _l8exct _cxexct _fc1yb4 _ar1bp4 _9s1txw _vy1ckf']"));


		try {
			for (WebElement print : data) {
				System.out.println(print.getText());
			}
			addLog("Printing data on log", "Pass", "Pass");
		} catch (Exception e) {
			addLog("Printing data on log", "Pass", "Fail");
			logEntry();
			sendLogs(emailProps);
			driver.quit();
			return;
		}



		// creating the Rent sheet
		rentSheet(data);

		// Log ENtry
		logEntry(); // Creating of log saving to file

		// Sending mail
		try {
			sendRent(emailProps);
		
		} catch (Exception e) {
			
		}

		try {
			sendLogs(emailProps);
		
		} catch (Exception e) {
		
		}

		// Log ENtry
//		logEntry(); // Creating of log saving to file
		JOptionPane.showMessageDialog(null, "Data Extraction Completed", "Thank You", JOptionPane.WARNING_MESSAGE);
		Thread.sleep(2000);
		driver.quit();
	}

	public static void logEntry() {
		XSSFWorkbook workbooksheet = new XSSFWorkbook();
		XSSFSheet sheetlog;

		try {
			sheetlog = workbooksheet.createSheet("Logs");
			addLog("Creating Log workbook", "Pass", "Pass");
		} catch (Exception e) {
			sheetlog = workbooksheet.createSheet("Logs"); // Fallback to avoid null
			addLog("Creating Log workbook", "Pass", "Fail");
		}

		XSSFRow header = sheetlog.createRow(0);
		header.createCell(0).setCellValue("Serial No");
		header.createCell(1).setCellValue("Timestamp");
		header.createCell(2).setCellValue("Steps");
		header.createCell(3).setCellValue("Expected Result");
		header.createCell(4).setCellValue("Actual Result");

		int rowNumber = 1;

		for (LogEntry entry : logs) {
			XSSFRow row = sheetlog.createRow(rowNumber++);
			row.createCell(0).setCellValue(entry.serialNo);
			row.createCell(1).setCellValue(entry.timestamp);
			row.createCell(2).setCellValue(entry.process);
			row.createCell(3).setCellValue(entry.expected);
			row.createCell(4).setCellValue(entry.actual);
		}

		for (int i = 0; i < 5; i++) {
			sheetlog.autoSizeColumn(i);
		}

		// ======= Create Path & Filename with Timestamp =======
		DateTimeFormatter folderFormatter = DateTimeFormatter.ofPattern("yyyy/MMMM/dd", Locale.ENGLISH);
		DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("HH-mm-ss");

		String folderPath = "Log Sheet/" + LocalDate.now().format(folderFormatter);
		String fileName = "log_" + LocalTime.now().format(fileFormatter) + ".xlsx";
		String fullPath = folderPath + "/" + fileName;
		lfp = fullPath;

		// ======= Create Folders If Not Exist =======
		File dir = new File(folderPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		// ======= Write to File =======
		try (FileOutputStream fileOut1 = new FileOutputStream(fullPath)) {
			workbooksheet.write(fileOut1);
			workbooksheet.close();
			System.out.println("✅ Log file created successfully at: " + fullPath);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void sendLogs(Properties emailProps) {
		LogMailer.sendEmailWithAttachments(emailProps.getProperty("from"), emailProps.getProperty("password"),
				emailProps.getProperty("toLogs"), emailProps.getProperty("ccLogs"), emailProps.getProperty("logsSubject"),
				emailProps.getProperty("logsMessage"),lfp);

	}

	public static void sendRent(Properties emailProps) {

		RentMailer.sendEmailWithAttachments(emailProps.getProperty("from"), emailProps.getProperty("password"),
				emailProps.getProperty("toRent"), emailProps.getProperty("ccRent"),emailProps.getProperty("rentSubject"),
				emailProps.getProperty("rentMessage"),rfp);

	}

	static List<LogEntry> logs = new ArrayList<>();
	static AtomicInteger logCount = new AtomicInteger(1);

	static void addLog(String process, String expected, String actual) {
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
		logs.add(new LogEntry(logCount.getAndIncrement(), timeStamp, process, expected, actual));
	}

	public static void rentSheet(List<WebElement> data) {
		// Create workbook and Sheet1
		XSSFWorkbook workbook = new XSSFWorkbook();
		
		XSSFSheet sheet1 = null;
		try {
			sheet1 = workbook.createSheet("Rent Sheet");
			addLog("Creting rent workbook", "Pass", "Pass");

		} catch (Exception e) {
			addLog("Creting rent workbook", "Pass", "Fail");
		}

		XSSFRow headerRow = sheet1.createRow(0);
		headerRow.createCell(0).setCellValue("Property Address");
		headerRow.createCell(1).setCellValue("Rent");
		headerRow.createCell(2).setCellValue("Built-up area");
		headerRow.createCell(3).setCellValue("Furnishing status");
		headerRow.createCell(4).setCellValue("Owner");

		int rowCount = 1;

		// Property class to store data
		class Property {
			String address, rent, area, furnishing, owner;
			int rentValue;

			public Property(String address, String rent, int rentValue, String area, String furnishing, String owner) {
				this.address = address;
				this.rent = rent;
				this.rentValue = rentValue;
				this.area = area;
				this.furnishing = furnishing;
				this.owner = owner;

			}

		}

		List<Property> properties = new ArrayList<>();
		int count = 0;
		for (WebElement item : data) {

			if (var == count) {
				break;
			}
			count++;
			String[] lines = item.getText().split("\n");

			String propertyAddress = "", rent = "", builtUpArea = "", furnishing = "", owner = "";
			boolean foundRent = false, foundBuiltUp = false, foundFurnishing = false;
			int rentValue = 0;

			for (int i = 0; i < lines.length; i++) {
				String line = lines[i].trim();

				if (line.equalsIgnoreCase("see price breakup") || line.equalsIgnoreCase("Builtup area")
						|| line.equalsIgnoreCase("Furnishing status") || line.equalsIgnoreCase("Contact")
						|| line.equalsIgnoreCase("+ More") || line.startsWith("Amenities:")) {
					continue;
				}

				if (propertyAddress.isEmpty() && !line.startsWith("₹")) {
					propertyAddress = line;
					continue;
				}

				if (!foundRent && line.startsWith("₹")) {
					rent = line;
					foundRent = true;
					try {
						rentValue = Integer.parseInt(line.replaceAll("[^0-9]", ""));
					} catch (Exception e) {
						rentValue = 0;
					}
					continue;
				}

				if (!foundBuiltUp && line.toLowerCase().contains("sq.ft")) {
					builtUpArea = line;
					foundBuiltUp = true;
					continue;
				}

				if (!foundFurnishing && (line.toLowerCase().contains("furnished") || line.toLowerCase().contains("semi")
						|| line.toLowerCase().contains("un-furnished"))) {
					furnishing = line;
					foundFurnishing = true;
					continue;
				}

				if (i >= lines.length - 3 && !line.equalsIgnoreCase("Contact") && owner.isEmpty()) {
					owner = line;
				}
			}

			properties.add(new Property(propertyAddress, rent, rentValue, builtUpArea, furnishing, owner));

			XSSFRow row = sheet1.createRow(rowCount++);
			row.createCell(0).setCellValue(propertyAddress);
			row.createCell(1).setCellValue(rent);
			row.createCell(2).setCellValue(builtUpArea);
			row.createCell(3).setCellValue(furnishing);
			row.createCell(4).setCellValue(owner);

		}
		System.out.println(data.size());
		if (data.size() < var) {
			JOptionPane.showMessageDialog(null, "Gathered data is less than input", "Warning",
					JOptionPane.WARNING_MESSAGE);

		}
		for (int i = 0; i < 5; i++) {
			sheet1.autoSizeColumn(i);
		}

		// Find lowest & highest rent
		Property minProp = properties.stream().min(Comparator.comparingInt(p -> p.rentValue)).orElse(null);
		Property maxProp = properties.stream().max(Comparator.comparingInt(p -> p.rentValue)).orElse(null);
		System.out.println("//10");
		// Count properties by price range
		int range_0_5000 = 0;
		int range_5000_10000 = 0;
		int range_10000_15000 = 0;
		int range_above_15000 = 0;

		for (Property p : properties) {
			if (p.rentValue <= 5000) {
				range_0_5000++;
			} else if (p.rentValue <= 10000) {
				range_5000_10000++;
			} else if (p.rentValue <= 15000) {
				range_10000_15000++;
			} else {
				range_above_15000++;
			}
		}

		// Create Sheet2: Summary
		XSSFSheet sheet2 = workbook.createSheet("Summary Sheet");
		
		sheet2.createRow(0).createCell(0).setCellValue("Summary Report");

		sheet2.createRow(2).createCell(0).setCellValue("Total properties fetched:");
		sheet2.getRow(2).createCell(1).setCellValue(properties.size());

		// Lowest priced property
		sheet2.createRow(4).createCell(0).setCellValue("Lowest Priced Property:");
		if (minProp != null) {
			sheet2.createRow(5).createCell(0).setCellValue("Rent");
			sheet2.getRow(5).createCell(1).setCellValue(minProp.rent);
			sheet2.createRow(6).createCell(0).setCellValue("Address");
			sheet2.getRow(6).createCell(1).setCellValue(minProp.address);
			sheet2.createRow(7).createCell(0).setCellValue("Area");
			sheet2.getRow(7).createCell(1).setCellValue(minProp.area);
			sheet2.createRow(8).createCell(0).setCellValue("Furnishing");
			sheet2.getRow(8).createCell(1).setCellValue(minProp.furnishing);
			sheet2.createRow(9).createCell(0).setCellValue("Owner");
			sheet2.getRow(9).createCell(1).setCellValue(minProp.owner);
		}

		// Highest priced property
		sheet2.createRow(11).createCell(0).setCellValue("Highest Priced Property:");
		if (maxProp != null) {
			sheet2.createRow(12).createCell(0).setCellValue("Rent");
			sheet2.getRow(12).createCell(1).setCellValue(maxProp.rent);
			sheet2.createRow(13).createCell(0).setCellValue("Address");
			sheet2.getRow(13).createCell(1).setCellValue(maxProp.address);
			sheet2.createRow(14).createCell(0).setCellValue("Area");
			sheet2.getRow(14).createCell(1).setCellValue(maxProp.area);
			sheet2.createRow(15).createCell(0).setCellValue("Furnishing");
			sheet2.getRow(15).createCell(1).setCellValue(maxProp.furnishing);
			sheet2.createRow(16).createCell(0).setCellValue("Owner");
			sheet2.getRow(16).createCell(1).setCellValue(maxProp.owner);
		}

		// Price Range Summary
		int startRow = 18;
		sheet2.createRow(startRow).createCell(0).setCellValue("Price Range Summary");

		sheet2.createRow(startRow + 1).createCell(0).setCellValue("0 - 5000");
		sheet2.getRow(startRow + 1).createCell(1).setCellValue(range_0_5000);

		sheet2.createRow(startRow + 2).createCell(0).setCellValue("5000 - 10000");
		sheet2.getRow(startRow + 2).createCell(1).setCellValue(range_5000_10000);

		sheet2.createRow(startRow + 3).createCell(0).setCellValue("10000 - 15000");
		sheet2.getRow(startRow + 3).createCell(1).setCellValue(range_10000_15000);

		sheet2.createRow(startRow + 4).createCell(0).setCellValue("15000+");
		sheet2.getRow(startRow + 4).createCell(1).setCellValue(range_above_15000);

		sheet2.autoSizeColumn(0);
		sheet2.autoSizeColumn(1);

//		
		try {
			
			LocalDate today = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH-mm-ss");
			String timeStamp = LocalTime.now().format(formatter);

			String year = String.valueOf(today.getYear());
			String month = today.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
			String day = String.format("%02d", today.getDayOfMonth());

			String basePath = "Rent Sheet" + File.separator + year + File.separator + month + File.separator + day;
			Files.createDirectories(Paths.get(basePath)); // Creates folders if not exist

			String fileName = "rent_" + timeStamp + ".xlsx";
			String fullPath = basePath + File.separator + fileName;

			rfp = fullPath;

			try (FileOutputStream fileout = new FileOutputStream(fullPath)) {
				workbook.write(fileout);
				workbook.close();
				System.out.println("✅ Excel file created at: " + fullPath);
				addLog("Saving rent sheet file", "Pass", "Pass");

			}
		} catch (IOException e) {
			e.printStackTrace();
			addLog("Saving rent sheet file", "Pass", "Fail");
		}

	}

}