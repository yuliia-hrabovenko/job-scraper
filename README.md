# Techstars Job Scraper

A Spring Boot application for scraping job listings from [jobs.techstars.com](https://jobs.techstars.com) by specific job functions.  
The app allows you to choose a job function, automatically crawls the site, and extracts structured job data into a database and Google Sheets.


## Features

- Scrapes job postings filtered by **Job Function** from jobs.techstars.com
- Collects:
    - Job Page URL
    - Position Name
    - Organization URL
    - Logo URL
    - Organization Title
    - Job Function
    - Location (split into parts if possible)
    - Posted Date (Unix Timestamp)
    - Description (HTML formatting preserved)
    - Tags
- Stores data in a PostgreSQL database
- Dumps results to an SQL file (including schema)
- Multithreaded scraping for performance
- Integration with **Google Sheets API** to upload results
- Built with:
    - Spring Boot
    - Maven
    - OkHttp / Jsoup
    - ORM (Hibernate/JPA)
    - Playwright
    - Google API Client Libraries


## Overview

1. **Request Layer** – Sends HTTP requests using OkHttp or Jsoup. Playwright is used to render the page before parsing.
2. **HTML Parsing** – Extracts data from job list and job detail pages.
3. **Persistence Layer** – Saves data into SQL DB using JPA/Hibernate.
4. **Export Module** – Dumps data to SQL file.
5. **Google Sheets Integration** – Uploads parsed data to a Google Sheet.


## Example Workflow

1. User specifies **Job Function**.
2. Application fetches the filtered job list from jobs.techstars.com.
3. Each job is parsed:
    - Details extracted
    - Data saved to DB
4. Final SQL dump created with schema.
5. Data uploaded to Google Sheets.


## Links

Link to the SQL dump on the Google Drive: 
<br>https://drive.google.com/file/d/1RvtPmpY6RH0ohkqotO2t7at2HcyQoDWX/view?usp=sharing

Link to the scraped results uploaded to the Google Sheets:
<br>https://docs.google.com/spreadsheets/d/1nVDc8mvaIOchOiheJyntJnCDkEiMKMG7wbPXEuwzXao/edit?usp=sharing


## Getting Started

To install and run this project locally, please follow the instructions in the [INSTALL.md](INSTALL.md) file.
