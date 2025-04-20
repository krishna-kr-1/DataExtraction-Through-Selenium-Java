# 🏠 Housing.com Data Scraper using Java Selenium

This project is an automation tool built using **Java** and **Selenium WebDriver** that extracts rent data from [Housing.com](https://housing.com). It prompts the user to input a **district name** and **number of data entries**, scrapes the results, saves them in an Excel file, logs every step of the process, and finally sends the output via **email**.

---

## 🌟 Features

- 🚀 Opens Chrome browser and navigates to Housing.com
- 📥 Prompts user for city/district and number of records
- 🔍 Extracts rent listing data using Selenium
- 📊 Saves extracted data into Excel using Apache POI
- 🧾 Logs each step's status (Pass/Fail)
- 📬 Emails both rent data and log file as attachments

---

## 🛠️ Tech Stack

- **Java** (JDK 17+)
- **Selenium WebDriver**
- **Apache POI** (for Excel)
- **Jakarta Mail (JavaMail)**
- **Maven**

---

## 📸 Flow Overview

1. **Open Chrome** via Selenium WebDriver
2. Navigate to [Housing.com](https://housing.com)
3. Display a dialog to enter:
   - ✅ City/District name
   - ✅ Number of entries to extract
4. Validate the input city from the suggestion list
5. Scrape rent data listings
6. Save to Excel sheet (timestamped)
7. Log every step in another Excel sheet
8. Send both files via **email**

---

## 📁 Folder Structure
```
📦 HousingScraper
├── src/
│   └── main/
│       ├── java/
│       │   └── rentPrice/               # Java classes (Main logic)
│       └── resources/
│           └── email.properties         # Email config 
├── Log Sheet/                           # Logs with timestamps
├── Rent Sheet/                          # Rent data Excel files
├── .gitignore                           # Git ignore 
├── pom.xml                              # Maven dependencies and build config
└── README.md                            # Project documentation
```


---

## 🧾 email.properties (Config File)

Create `email.properties` in `src/main/resources` (NOT uploaded to GitHub):

```properties
from=your_email@gmail.com
password=your_app_password
toRent=recipient@example.com
ccRent=cc@example.com
toLogs=recipient@example.com
ccLogs=cc@example.com
logsSubject=Automated Log Report
rentSubject=Automated Rent Report
logsMessage=Dear Team,\n\nPlease find attached the latest log report.\n\nRegards,\nKrishna
rentMessage=Dear Team,\n\nPlease find attached the rent data report.\n\nRegards,\nKrishna
```
Update this on email.properties

## 🚀 How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/HousingScraper.git
   cd HousingScraper
  
2. Add your credentials in email.properties under src/main/resources/:
```bash
  from=your_email@gmail.com
  password=your_app_password
  toRent=recipient_email@gmail.com
```
💡 Keep this file out of Git by listing it in .gitignore.

3. Run the project using Maven:
   ```bash
   mvn clean install
   mvn exec:java -Dexec.mainClass="rentPrice.YourMainClass"
  Replace YourMainClass with the actual class name containing the main method.
