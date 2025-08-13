# INSTALL.md

## Prerequisites

- Java 17 or later
- Maven 3.8+
- Google Cloud Project with Sheets & Drive APIs enabled
- Docker (for running PostgreSQL db)
- Git


## Clone the Repository

```
git clone https://github.com/yuliia-hrabovenko/job-scraper.git
cd job-scraper
```


## Configure Database
Edit .env file according to .env_example:

```
POSTGRES_USER=<your_db_user>
POSTGRES_PASSWORD=<your_db_password>
POSTGRES_DB=jobs_db
```


## Set up Google Sheets API and Credentials

1. Create a Project in Google Cloud Console [console.cloud.google.com](https://console.cloud.google.com/)
2. Enable API
   - In the left sidebar, open API Library.
   - Search for Google Sheets API, select it, and enable it.
   - Repeat for Google Drive API.
3. Create OAuth Credentials
   - Go to APIs & Services > Credentials and click Create Credentials > OAuth client ID
   - Choose Desktop App as the client type
   - Save the downloaded JSON file as credentials.json in ```src/main/resources/``` directory
   - Navigate to APIs & Services > OAuth consent screen > add your email as a test user in Audience and add a scope https://www.googleapis.com/auth/spreadsheets in Data Access


## Run the Application

```
docker compose up
mvn spring-boot:run
```