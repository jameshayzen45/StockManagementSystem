README

Prerequisites
- Java Development Kit (JDK) 8 or higher
- Gradle build tool
- MySQL Database

## Setup Instructions
1. Clone the repository to your local machine
2. Database Setup:
   
   - Install MySQL if not already installed
   - Create a new database named stock_management
   - The application will automatically create the required tables on first run

- Build the project:
./gradlew build

- Run the application:
./gradlew run

### Administrator Account
- Username: james
- Password: admin123
### Employee Account
- Username: employee
- Password: emp123

## Important Notes
- The test accounts are for development purposes only and should be changed in a production environment
- The system uses a local MySQL database for data storage
- Make sure to properly configure your database connection settings before running the application

- If you encounter database connection issues, verify your MySQL service is running
- For build issues, ensure you have the correct JDK version installed
- Check the console output for any error messages