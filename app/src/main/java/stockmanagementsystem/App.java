package stockmanagementsystem;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import javafx.scene.control.TableCell;
import java.util.List;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;

public class App extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Stock Management System - Login");
        DatabaseConnection dbConnection = new DatabaseConnection();
        try {
            Connection connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // Create the login form
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        // Title
        Text sceneTitle = new Text("Welcome");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);
        
        // Username label and field
        Label userName = new Label("Username:");
        grid.add(userName, 0, 1);
        
        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);
        
        // Password label and field
        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);
        
        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);
        
        // User type selection
        Label userTypeLabel = new Label("Login as:");
        grid.add(userTypeLabel, 0, 3);
        
        ComboBox<String> userTypeComboBox = new ComboBox<>();
        userTypeComboBox.getItems().addAll("Employee", "Administrator");
        userTypeComboBox.setValue("Employee");
        grid.add(userTypeComboBox, 1, 3);
        
        // Login button
        Button loginBtn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(loginBtn);
        grid.add(hbBtn, 1, 4);
        
        // Password reset link
        Hyperlink resetPasswordLink = new Hyperlink("Forgot Password?");
        grid.add(resetPasswordLink, 1, 5);
        
        // Message text
        Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
        
        // Login button action
        loginBtn.setOnAction(e -> {
            String username = userTextField.getText();
            String password = pwBox.getText();
            String userType = userTypeComboBox.getValue();
            
            if (authenticateUser(username, password, userType)) {
                actiontarget.setText("Login successful as " + userType);
                if (userType.equalsIgnoreCase("Administrator")) {
                    // Open admin dashboard
                    openAdminDashboard(primaryStage);
                } else if (userType.equalsIgnoreCase("Employee")) {
                    // Open employee dashboard
                    openEmployeeDashboard(primaryStage);
                }
            } else {
                actiontarget.setText("Invalid username or password");
            }
        });
        
        // Password reset action
        resetPasswordLink.setOnAction(e -> {
            showPasswordResetDialog();
        });
        
        Scene scene = new Scene(grid, 400, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    // Create a class field to store the current user
    private String currentUser = "";
    
    // Update the authenticateUser method to set the current user
    private boolean authenticateUser(String username, String password, String userType) {
        UserDao userDao = new UserDao();
        User user = userDao.findByUsername(username);
        
        if (user != null && user.getUserType().equalsIgnoreCase(userType)) {
            if (userDao.verifyPassword(user, password)) {
                currentUser = username; // Store the current user
                return true;
            }
        }
        
        return false;
    }
    
    private VBox createStatBox(String title, String value) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #f2f2f2; -fx-border-color: #ccc; -fx-border-width: 1px; -fx-border-radius: 5px;");

        Label titleLabel = new Label(title);
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 24));

        box.getChildren().addAll(titleLabel, valueLabel);
        return box;
    }
    
    private void openAdminDashboard(Stage primaryStage) {
        //admin dashboard
        BorderPane borderPane = new BorderPane();

        MenuBar menuBar = new MenuBar();
        Menu productMenu = new Menu("Products");
        Menu userMenu = new Menu("Users");
        Menu reportMenu = new Menu("Reports");
        Menu restockMenu = new Menu("Restock");

        MenuItem addProductItem = new MenuItem("Add Product");
        MenuItem viewProductsItem = new MenuItem("View Products");
        MenuItem searchProductItem = new MenuItem("Search Product");
        productMenu.getItems().addAll(addProductItem, viewProductsItem, searchProductItem);

        MenuItem addUserItem = new MenuItem("Add User");
        MenuItem viewUsersItem = new MenuItem("View Users");
        userMenu.getItems().addAll(addUserItem, viewUsersItem);
        
        MenuItem viewRestockRequestsItem = new MenuItem("View Restock Requests");
        restockMenu.getItems().add(viewRestockRequestsItem);

        MenuItem salesReportItem = new MenuItem("Sales Report");
        MenuItem inventoryReportItem = new MenuItem("Inventory Report");
        reportMenu.getItems().addAll(salesReportItem, inventoryReportItem);
        
        salesReportItem.setOnAction(e -> {
            showSalesReport();
        });
        
        inventoryReportItem.setOnAction(e -> {
            showInventoryReport();
        });

        menuBar.getMenus().addAll(productMenu, userMenu, reportMenu, restockMenu);
        
        viewRestockRequestsItem.setOnAction(e -> showRestockRequestsDialog());

        addProductItem.setOnAction(e -> {
            showAddProductForm();
        });
        viewProductsItem.setOnAction(e -> {
            showProductsTable();
        });
        searchProductItem.setOnAction(e -> {
            showProductsTable(); 
        });
        
        addUserItem.setOnAction(e -> {
            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle("Add User");
            dialog.setHeaderText("Enter new user details");
            ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            TextField usernameField = new TextField();
            usernameField.setPromptText("Username");
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Password");
            TextField emailField = new TextField();
            emailField.setPromptText("Email");
            ComboBox<String> userTypeBox = new ComboBox<>();
            userTypeBox.getItems().addAll("Administrator", "Employee");
            userTypeBox.setValue("Employee");
            grid.add(new Label("Username:"), 0, 0);
            grid.add(usernameField, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(passwordField, 1, 1);
            grid.add(new Label("Email:"), 0, 2);
            grid.add(emailField, 1, 2);
            grid.add(new Label("User Type:"), 0, 3);
            grid.add(userTypeBox, 1, 3);
            dialog.getDialogPane().setContent(grid);
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    User user = new User();
                    user.setUsername(usernameField.getText().trim());
                    user.setPassword(passwordField.getText().trim());
                    user.setEmail(emailField.getText().trim());
                    user.setUserType(userTypeBox.getValue());
                    return user;
                }
                return null;
            });
            dialog.showAndWait().ifPresent(user -> {
                UserDao userDao = new UserDao();
                boolean success = userDao.addUser(user);
                Alert resultAlert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                resultAlert.setTitle(success ? "Success" : "Error");
                resultAlert.setHeaderText(null);
                resultAlert.setContentText(success ? "User added successfully!" : "Failed to add user.");
                resultAlert.showAndWait();
            });
        });

        viewUsersItem.setOnAction(e -> {
            Stage userStage = new Stage();
            userStage.setTitle("All Users");
            TableView<User> table = new TableView<>();
            TableColumn<User, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            TableColumn<User, String> usernameCol = new TableColumn<>("Username");
            usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
            TableColumn<User, String> emailCol = new TableColumn<>("Email");
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
            TableColumn<User, String> typeCol = new TableColumn<>("User Type");
            typeCol.setCellValueFactory(new PropertyValueFactory<>("userType"));
            table.getColumns().addAll(idCol, usernameCol, emailCol, typeCol);
            UserDao userDao = new UserDao();
            table.getItems().addAll(userDao.getAllUsers());
            VBox vbox = new VBox(table);
            Scene scene = new Scene(vbox, 500, 400);
            userStage.setScene(scene);
            userStage.show();
        });

        //welcome panel 
        VBox welcomePanel = new VBox(10);
        welcomePanel.setAlignment(Pos.CENTER);
        welcomePanel.setPadding(new Insets(20));
        Label welcomeLabel = new Label("Welcome to Admin Dashboard");
        welcomeLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 16));

        //quick btns
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        
        VBox totalProductsBox = createStatBox("Total Products", "");
        VBox lowStockBox = createStatBox("Low Stock", ""); // Capitalized "Low Stock"
        VBox totalUsersBox = createStatBox("Total Users", "");

        statsBox.getChildren().addAll(totalProductsBox, lowStockBox, totalUsersBox);

        // Recent sales section
        VBox recentSalesBox = new VBox(5);
        recentSalesBox.setPadding(new Insets(10));
        recentSalesBox.setStyle("-fx-background-color: #f2f2f2; -fx-border-color: #ccc; -fx-border-width: 1px; -fx-border-radius: 5px;");
        Label recentSalesLabel = new Label("Recent Sales");
        recentSalesLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
        ListView<String> recentSalesList = new ListView<>();
        recentSalesList.setPrefHeight(150);
        // Fetch recent sales from database
        SaleDAO saleDAO = new SaleDAO();
        java.util.List<Sale> sales = saleDAO.getAllSales(); // You may need to implement getAllSales()
        for (Sale sale : sales) {
            StringBuilder sb = new StringBuilder();
            sb.append("Sale #").append(sale.getId())
              .append(" - ").append(sale.getEmployeeUsername())
              .append(" - £").append(String.format("%.2f", sale.getTotalAmount()));
            recentSalesList.getItems().add(sb.toString());
        }
        recentSalesBox.getChildren().addAll(recentSalesLabel, recentSalesList);

        //logout btn
        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> 
            start(primaryStage));

        welcomePanel.getChildren().addAll(welcomeLabel, statsBox, recentSalesBox, logoutBtn);

        borderPane.setTop(menuBar);
        borderPane.setCenter(welcomePanel);
        Scene adminScene = new Scene(borderPane, 800, 600);
        primaryStage.setTitle("Stock Management System - Admin Dashboard");
        primaryStage.setScene(adminScene);
    }
    
    private void showAddProductForm() {
        Stage formStage = new Stage();
        formStage.setTitle("Add Product");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        //product type selection
        Label typeLabel = new Label("Product type:");
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("CPU", "GPU", "RAM", "SSD", "HDD", "Motherboard", "PSU");
        grid.add(typeLabel, 0, 0);
        grid.add(typeComboBox, 1, 0);

        //common fields
        Label nameLabel = new Label("name:");
        TextField nameField = new TextField();
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        
        Label brandLabel = new Label("Brand:");
        TextField brandField = new TextField();
        grid.add(brandLabel, 0, 2);
        grid.add(brandField, 1, 2);

        Label costLabel = new Label("Cost Price:");
        TextField costField = new TextField();
        grid.add(costLabel, 0, 3);
        grid.add(costField, 1, 3);
        
        Label priceLabel = new Label("Selling Price:");
        TextField priceField = new TextField();
        grid.add(priceLabel, 0, 4);
        grid.add(priceField, 1, 4);
        
        Label stockLabel = new Label("Stock Quantity:");
        TextField stockField = new TextField();
        grid.add(stockLabel, 0, 5);
        grid.add(stockField, 1, 5);

        //dynamic fields based on products i created
        VBox dynamicFieldsBox = new VBox(10);
        grid.add(dynamicFieldsBox, 0, 6, 2, 1);

        typeComboBox.setOnAction(e -> {
            dynamicFieldsBox.getChildren().clear();
            String selectedType = typeComboBox.getValue();

            if (selectedType != null) {
                switch (selectedType) {
                    case "CPU":
                        addCPUFields(dynamicFieldsBox);
                        break;
                    case "GPU":
                        addGPUFields(dynamicFieldsBox);
                        break;
                    case "RAM":
                        addRAMFields(dynamicFieldsBox);
                        break;
                    case "SSD":
                        addSSDFields(dynamicFieldsBox);
                        break;
                    case "HDD":
                        addHDDFields(dynamicFieldsBox);
                        break;
                    case "Motherboard":
                        addMotherboardFields(dynamicFieldsBox);
                        break;
                    case "PSU":
                        addPSUFields(dynamicFieldsBox);
                        break;
                    default:
                        break;
                }
            }
        });
        
        // Save button
        Button saveButton = new Button("Save Product");
        saveButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String brand = brandField.getText().trim();
            double costPrice = Double.parseDouble(costField.getText().trim());
            double sellingPrice = Double.parseDouble(priceField.getText().trim());
            int stockQuantity = Integer.parseInt(stockField.getText().trim());
            String type = typeComboBox.getValue();

            Product product = null;
            if (type.equals("CPU")) {
                product = new CPU();
                // Get CPU-specific fields
                GridPane cpuGrid = (GridPane) dynamicFieldsBox.getChildren().get(0);
                TextField socketField = (TextField) cpuGrid.getChildren().get(1);
                TextField coreField = (TextField) cpuGrid.getChildren().get(3);
                TextField speedField = (TextField) cpuGrid.getChildren().get(5);
                ((CPU)product).socketType = socketField.getText().trim();
                ((CPU)product).coreCount = Integer.parseInt(coreField.getText().trim());
                ((CPU)product).clockSpeed = Integer.parseInt(speedField.getText().trim());
            } else if (type.equals("GPU")) {
                product = new GPU();
                GridPane gpuGrid = (GridPane) dynamicFieldsBox.getChildren().get(0);
                TextField memoryField = (TextField) gpuGrid.getChildren().get(1);
                // For demonstration, only memory size is present
                ((GPU)product).memorySize = Integer.parseInt(memoryField.getText().trim());
            } else if (type.equals("RAM")) {
                product = new RAM();
                GridPane ramGrid = (GridPane) dynamicFieldsBox.getChildren().get(0);
                TextField capacityField = (TextField) ramGrid.getChildren().get(1);
                ((RAM)product).capacity = Integer.parseInt(capacityField.getText().trim());
            } else if (type.equals("SSD")) {
                product = new SSD();
                GridPane ssdGrid = (GridPane) dynamicFieldsBox.getChildren().get(0);
                TextField capacityField = (TextField) ssdGrid.getChildren().get(1);
                ((SSD)product).ConnectionType = capacityField.getText().trim();
            } else if (type.equals("HDD")) {
                product = new HDD();
                GridPane hddGrid = (GridPane) dynamicFieldsBox.getChildren().get(0);
                TextField capacityField = (TextField) hddGrid.getChildren().get(1);
                ((HDD)product).isInternal = capacityField.getText().trim();
            } else if (type.equals("Motherboard")) {
                product = new Motherboard();
                GridPane mbGrid = (GridPane) dynamicFieldsBox.getChildren().get(0);
                TextField socketTypeField = (TextField) mbGrid.getChildren().get(1);
                ((Motherboard)product).socketType = socketTypeField.getText().trim();
            } else if (type.equals("PSU")) {
                product = new PSU();
                GridPane psuGrid = (GridPane) dynamicFieldsBox.getChildren().get(0);
                TextField wattageField = (TextField) psuGrid.getChildren().get(1);
                ((PSU)product).wattage = Integer.parseInt(wattageField.getText().trim());
            }
            if (product != null) {
                product.setName(name);
                product.setBrand(brand);
                product.setCostPrice(costPrice);
                product.setSellingPrice(sellingPrice);
                product.setStockQuantity(stockQuantity);
                ProductDAO productDAO = new ProductDAO();
                productDAO.addProduct(product);
            }
            formStage.close();
        });
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(saveButton);
        grid.add(buttonBox, 1, 7);
        
        Scene scene = new Scene(grid, 500, 600);
        formStage.setScene(scene);
        formStage.show();
    }
    
    
    private void addCPUFields(VBox container) {
        // Add CPU-specific fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        Label socketLabel = new Label("Socket Type:");
        TextField socketField = new TextField();
        grid.add(socketLabel, 0, 0);
        grid.add(socketField, 1, 0);
        
        Label coreLabel = new Label("Core Count:");
        TextField coreField = new TextField();
        grid.add(coreLabel, 0, 1);
        grid.add(coreField, 1, 1);

        Label speedLabel = new Label("Speed (Ghz): ");
        TextField speedField = new TextField();
        grid.add(speedLabel,0, 2);
        grid.add(speedField, 1,2);        
        container.getChildren().add(grid);
    }
    
    private void addGPUFields(VBox container) {
        // Add GPU-specific fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        Label memoryLabel = new Label("Memory Size (GB):");
        TextField memoryField = new TextField();
        grid.add(memoryLabel, 0, 0);
        grid.add(memoryField, 1, 0);
        
        container.getChildren().add(grid);
    }
    
    private void addRAMFields(VBox container) {
        // Add RAM-specific fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        Label capacityLabel = new Label("Capacity (GB):");
        TextField capacityField = new TextField();
        grid.add(capacityLabel, 0, 0);
        grid.add(capacityField, 1, 0);
        
        container.getChildren().add(grid);
    }
    
    private void addSSDFields(VBox container) {
        // Add SSD-specific fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        Label capacityLabel = new Label("Capacity (GB):");
        TextField capacityField = new TextField();
        grid.add(capacityLabel, 0, 0);
        grid.add(capacityField, 1, 0);
        
        container.getChildren().add(grid);
    }
    
    private void addHDDFields(VBox container) {
        // Add HDD-specific fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        Label capacityLabel = new Label("Capacity (TB):");
        TextField capacityField = new TextField();
        grid.add(capacityLabel, 0, 0);
        grid.add(capacityField, 1, 0);
        
        container.getChildren().add(grid);
    }
    
    private void addMotherboardFields(VBox container) {
        // Add Motherboard-specific fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        Label socketLabel = new Label("Socket Type:");
        TextField socketField = new TextField();
        grid.add(socketLabel, 0, 0);
        grid.add(socketField, 1, 0);
        
        container.getChildren().add(grid);
    }
    
    private void addPSUFields(VBox container) {
        // Add PSU-specific fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        Label wattageLabel = new Label("Wattage:");
        TextField wattageField = new TextField();
        grid.add(wattageLabel, 0, 0);
        grid.add(wattageField, 1, 0);
        
        container.getChildren().add(grid);
    }
    
    private void showProductsTable() {
        Stage tableStage = new Stage();
        tableStage.setTitle(" All Products");

        TableView<Product> table = new TableView<>();

        TableColumn<Product, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, String> brandColumn = new TableColumn<>("Brand");
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));

        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

        TableColumn<Product, Integer> stockColumn = new TableColumn<>("Stock");
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        
        TableColumn<Product, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            String type = product.getClass().getSimpleName();
            return new SimpleStringProperty(type);
        });

        table.getColumns().addAll(idColumn, nameColumn, brandColumn, priceColumn, stockColumn, typeColumn);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        HBox searchBox = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search products");
        Button searchButton = new Button("Search");
        searchBox.getChildren().addAll(searchField, searchButton);

        // Load products into the table
        ProductDAO productDAO = new ProductDAO();
java.util.List<Product> products = productDAO.getAllProducts();
        table.getItems().addAll(products);
        
        // Add search functionality
        searchButton.setOnAction(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                table.getItems().clear();
                table.getItems().addAll(productDAO.getAllProducts());
            } else {
                table.getItems().clear();
                table.getItems().addAll(productDAO.searchProducts(keyword));
            }
        });

        vbox.getChildren().addAll(searchBox, table);
        Scene scene = new Scene(vbox, 800, 600);
        tableStage.setScene(scene);
        tableStage.show();

      
        // Alert alert = new Alert(Alert.AlertType.INFORMATION);
        // alert.setTitle("Products");
        // alert.setHeaderText(null);
        // alert.setContentText("Products table would be displayed here");
        // alert.showAndWait();
    }
    //employee dashboard 
    private void openEmployeeDashboard(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();

        MenuBar menuBar = new MenuBar();
        Menu salesMenu = new Menu("Sales");
        Menu inventoryMenu = new Menu("Inventory");
        
        //sales menu
        MenuItem newSaleItem = new MenuItem("New Sale");
        MenuItem viewSalesItem = new MenuItem("View my Sales");
        salesMenu.getItems().addAll(newSaleItem, viewSalesItem);

        //inventory menu
        MenuItem checkStockItem = new MenuItem("Check Stock");
        MenuItem requestRestockItem = new MenuItem("Request Restock");
        inventoryMenu.getItems().addAll(checkStockItem, requestRestockItem);

        menuBar.getMenus().addAll(salesMenu, inventoryMenu);

        //actions set 
        newSaleItem.setOnAction(e -> showNewSaleForm());
        checkStockItem.setOnAction(e -> showProductsTable());
        requestRestockItem.setOnAction(e -> showRequestRestockDialog());

        //welcome panel 
        VBox welcomePanel = new VBox(15);
        welcomePanel.setAlignment(Pos.CENTER);
        welcomePanel.setPadding(new Insets(20));
        
        Label welcomeLabel = new Label("Welcome Back Employee");
        welcomeLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 16));

        //quick btns
        HBox actionBox = new HBox(20);
        actionBox.setAlignment(Pos.CENTER);

        Button newSalesButton = new Button("New Sale");
        newSalesButton.setPrefSize(150, 100);
        newSalesButton.setOnAction(e -> showNewSaleForm());

        Button checkStockButton = new Button("Check Stock");
        checkStockButton.setPrefSize(150, 100);
        checkStockButton.setOnAction(e -> showProductsTable());
        
        Button requestRestockButton = new Button("Request Restock");
        requestRestockButton.setPrefSize(150, 100);
        requestRestockButton.setOnAction(e -> showRequestRestockDialog());

        actionBox.getChildren().addAll(newSalesButton, checkStockButton, requestRestockButton);

        //recent sales
        VBox recentSalesBox = new VBox(5);
        recentSalesBox.setPadding(new Insets(10));
        recentSalesBox.setStyle("-fx-background-color: #f2f2f2; -fx-border-color: #ccc; -fx-border-width: 1px; -fx-border-radius: 5px;");

        Label recentSalesLabel = new Label("Recent Sales");
        recentSalesLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));

        ListView<String> recentSalesList = new ListView<>();
        recentSalesList.setPrefHeight(150);
        // Fetch recent sales for the current employee
        SaleDAO saleDAO = new SaleDAO();
        java.util.List<Sale> sales = saleDAO.getSalesByEmployee(currentUser);
        for (Sale sale : sales) {
            StringBuilder sb = new StringBuilder();
            sb.append("Sale #").append(sale.getId())
              .append(" - £").append(String.format("%.2f", sale.getTotalAmount()))
              .append(" - ").append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(sale.getSaleDate()));
            recentSalesList.getItems().add(sb.toString());
        }
        recentSalesBox.getChildren().addAll(recentSalesLabel, recentSalesList);

        //logout btn
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e ->
            start(primaryStage));

        welcomePanel.getChildren().addAll(welcomeLabel, actionBox, recentSalesBox, logoutButton);

        //set up borderpane 
        
        borderPane.setTop(menuBar);
        borderPane.setCenter(welcomePanel);
        Scene empScene = new Scene(borderPane, 800, 600);
        primaryStage.setTitle("Stock Management System - Employee Dashboard");
        primaryStage.setScene(empScene);

        
    }

    // Update the showNewSaleForm method
    private void showNewSaleForm() {
        Stage saleStage = new Stage(); 
        saleStage.setTitle("New Sale");
    
        BorderPane borderPane = new BorderPane();
    
        // Product selection section
        VBox productSelectionBox = new VBox(10);
        productSelectionBox.setPadding(new Insets(10));
    
        Label productLabel = new Label("Available Products in Stock:");
        productLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
    
        TextField productSearchField = new TextField();
        productSearchField.setPromptText("Search products");
    
        // Product table
        TableView<Product> productTable = new TableView<>();
        
        TableColumn<Product, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<Product, String> brandColumn = new TableColumn<>("Brand");
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        
        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        
        TableColumn<Product, Integer> stockColumn = new TableColumn<>("Stock");
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        
        TableColumn<Product, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            String type = product.getClass().getSimpleName();
            return new SimpleStringProperty(type);
        });
        
        productTable.getColumns().addAll(idColumn, nameColumn, brandColumn, priceColumn, stockColumn, typeColumn);
        
        // Load products
        ProductDAO productDAO = new ProductDAO();
        java.util.List<Product> products = productDAO.getAllProducts();
        productTable.getItems().addAll(products);
        
        // Add search functionality
        Button searchButton = new Button("Search");
        HBox searchBox = new HBox(10, productSearchField, searchButton);
        
        searchButton.setOnAction(e -> {
            String keyword = productSearchField.getText().trim();
            if (keyword.isEmpty()) {
                productTable.getItems().clear();
                productTable.getItems().addAll(productDAO.getAllProducts());
            } else {
                productTable.getItems().clear();
                productTable.getItems().addAll(productDAO.searchProducts(keyword));
            }
        });
        
        // Add to cart button
        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        productSelectionBox.getChildren().addAll(productLabel, searchBox, productTable, addToCartButton);
    
        // Cart section
        VBox cartBox = new VBox(10);
        cartBox.setPadding(new Insets(10));
        Label cartLabel = new Label("Customer Shopping Cart:");
        cartLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
    
        // Cart table
        TableView<SaleItem> cartTable = new TableView<>();
        
        TableColumn<SaleItem, String> cartProductColumn = new TableColumn<>("Product");
        cartProductColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        
        TableColumn<SaleItem, Double> cartPriceColumn = new TableColumn<>("Price");
        cartPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        TableColumn<SaleItem, Integer> cartQuantityColumn = new TableColumn<>("Quantity");
        cartQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        cartQuantityColumn.setCellFactory(col -> new TableCell<SaleItem, Integer>() {
            @Override
            protected void updateItem(Integer quantity, boolean empty) {
                super.updateItem(quantity, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(quantity.toString());
                    
                    // Add spinner for quantity adjustment
                    Spinner<Integer> spinner = new Spinner<>(1, 100, quantity);
                    spinner.setEditable(true);
                    spinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                        SaleItem item = getTableView().getItems().get(getIndex());
                        item.setQuantity(newValue);
                        getTableView().refresh();
                    });
                    setGraphic(spinner);
                }
            }
        });
        
        TableColumn<SaleItem, Double> cartSubtotalColumn = new TableColumn<>("Subtotal");
        cartSubtotalColumn.setCellValueFactory(cellData -> {
            SaleItem item = cellData.getValue();
            return new SimpleObjectProperty<>(item.getSubtotal());
        });
        
        TableColumn<SaleItem, Void> cartActionColumn = new TableColumn<>("Action");
        cartActionColumn.setCellFactory(col -> new TableCell<SaleItem, Void>() {
            private final Button deleteButton = new Button("Remove");
            
            {
                deleteButton.setOnAction(e -> {
                    SaleItem item = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(item);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
        
        cartTable.getColumns().addAll(cartProductColumn, cartPriceColumn, cartQuantityColumn, 
                                cartSubtotalColumn, cartActionColumn);
    
        // Totals section
        GridPane totalGrid = new GridPane();
        totalGrid.setHgap(10);
        totalGrid.setVgap(5);
        totalGrid.setPadding(new Insets(10));
    
        Label subtotalLabel = new Label("Subtotal:");
        Label subtotalValue = new Label("£0.00");
        subtotalValue.setStyle("-fx-font-weight: bold;");
    
        Label taxLabel = new Label("VAT (20%):");
        Label taxValue = new Label("£0.00");
    
        Label totalLabel = new Label("Total:");
        Label totalValue = new Label("£0.00");
        totalValue.setStyle("-fx-font-weight: bold;");
        totalGrid.add(subtotalLabel, 0, 0);
        totalGrid.add(subtotalValue, 1, 0);
        totalGrid.add(taxLabel, 0, 1);
        totalGrid.add(taxValue, 1, 1);
        totalGrid.add(totalLabel, 0, 2);
        totalGrid.add(totalValue, 1, 2);
    
        // Checkout button
        Button checkoutButton = new Button("Process Sale");
        checkoutButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
    
        // Add to cart functionality
        addToCartButton.setOnAction(e -> {
            Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                if (selectedProduct.getStockQuantity() > 0) {
                    // Check if product already in cart
                    boolean found = false;
                    for (SaleItem item : cartTable.getItems()) {
                        if (item.getProductId() == selectedProduct.getId()) {
                            item.setQuantity(item.getQuantity() + 1);
                            found = true;
                            cartTable.refresh();
                            break;
                        }
                    }
                    
                    if (!found) {
                        SaleItem newItem = new SaleItem(selectedProduct, 1);
                        cartTable.getItems().add(newItem);
                    }
                    
                    updateTotals(cartTable.getItems(), subtotalValue, taxValue, totalValue);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Out of Stock");
                    alert.setHeaderText(null);
                    alert.setContentText("This product is out of stock.");
                    alert.showAndWait();
                }
            }
        });
        
        // Process sale functionality
        checkoutButton.setOnAction(e -> {
            if (cartTable.getItems().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Empty Cart");
                alert.setHeaderText(null);
                alert.setContentText("Please add products to the cart before processing the sale.");
                alert.showAndWait();
                return;
            }
            
            // Create sale object
            Sale sale = new Sale();
            sale.setEmployeeUsername(currentUser); // Use the current user instead of hardcoded value
            
            for (SaleItem item : cartTable.getItems()) {
                sale.addItem(item);
            }
            
            // Save sale to database
            SaleDAO saleDAO = new SaleDAO();
            if (saleDAO.saveSale(sale)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sale Complete");
                alert.setHeaderText(null);
                alert.setContentText("Sale has been processed successfully.");
                alert.showAndWait();
                
                // Clear cart and close window
                cartTable.getItems().clear();
                updateTotals(cartTable.getItems(), subtotalValue, taxValue, totalValue);
                saleStage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Sale Failed");
                alert.setHeaderText(null);
                alert.setContentText("There was an error processing the sale.");
                alert.showAndWait();
            }
        });
    
        cartBox.getChildren().addAll(cartLabel, cartTable, totalGrid, checkoutButton);
    
        borderPane.setLeft(productSelectionBox);
        borderPane.setRight(cartBox);
    
        Scene scene = new Scene(borderPane, 900, 600);
        saleStage.setScene(scene);
        saleStage.show();
    }
    
    private void updateTotals(List<SaleItem> items, Label subtotalLabel, Label taxLabel, Label totalLabel) {
        double subtotal = 0;
        for (SaleItem item : items) {
            subtotal += item.getSubtotal();
        }
        
        double tax = subtotal * 0.2; // 20% VAT
        double total = subtotal + tax;
        
        subtotalLabel.setText(String.format("£%.2f", subtotal));
        taxLabel.setText(String.format("£%.2f", tax));
        totalLabel.setText(String.format("£%.2f", total));
    }
    
    // Method to show restock request dialog for employees
    private void showRequestRestockDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Request Product Restock");
        dialog.setHeaderText("Select a product and quantity to request");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Product selection
        ComboBox<Product> productBox = new ComboBox<>();
        productBox.setPromptText("Select Product");
        productBox.setPrefWidth(200);
        
        // Load products
        ProductDAO productDAO = new ProductDAO();
        List<Product> products = productDAO.getAllProducts();
        productBox.getItems().addAll(products);
        
        // Display product name in ComboBox
        productBox.setCellFactory(lv -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName() + " (Stock: " + item.getStockQuantity() + ")");
            }
        });
        
        productBox.setButtonCell(new ListCell<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        });
        
        // Quantity field
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        
        grid.add(new Label("Product:"), 0, 0);
        grid.add(productBox, 1, 0);
        grid.add(new Label("Quantity:"), 0, 1);
        grid.add(quantityField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    Product product = productBox.getValue();
                    if (product == null) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Please select a product");
                        return null;
                    }
                    
                    String quantityText = quantityField.getText().trim();
                    if (quantityText.isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Please enter a quantity");
                        return null;
                    }
                    
                    int quantity = Integer.parseInt(quantityText);
                    if (quantity <= 0) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Quantity must be greater than zero");
                        return null;
                    }
                    
                    // Create and save the restock request
                    RestockRequest request = new RestockRequest(
                        product.getId(),
                        product.getName(),
                        quantity,
                        currentUser
                    );
                    
                    RestockRequestDAO dao = new RestockRequestDAO();
                    boolean success = dao.addRequest(request);
                    
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Restock request submitted successfully");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to submit restock request");
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid quantity");
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    // Method to show restock requests for admin approval
    private void showRestockRequestsDialog() {
        Stage stage = new Stage();
        stage.setTitle("Restock Requests");
        
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));
        
        // Create TableView
        TableView<RestockRequest> tableView = new TableView<>();
        tableView.setPlaceholder(new Label("No pending restock requests"));
        
        // Define columns
        TableColumn<RestockRequest, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<RestockRequest, String> productCol = new TableColumn<>("Product");
        productCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productCol.setPrefWidth(150);
        
        TableColumn<RestockRequest, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("requestedQuantity"));
        
        TableColumn<RestockRequest, String> employeeCol = new TableColumn<>("Requested By");
        employeeCol.setCellValueFactory(new PropertyValueFactory<>("employeeUsername"));
        employeeCol.setPrefWidth(100);
        
        TableColumn<RestockRequest, LocalDateTime> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        dateCol.setPrefWidth(150);
        dateCol.setCellFactory(column -> new TableCell<RestockRequest, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                }
            }
        });
        
        TableColumn<RestockRequest, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        TableColumn<RestockRequest, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(200);
        actionCol.setCellFactory(param -> new TableCell<RestockRequest, Void>() {
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox pane = new HBox(5, approveBtn, rejectBtn);
            
            {
                approveBtn.setOnAction(event -> {
                    RestockRequest request = getTableView().getItems().get(getIndex());
                    handleApproveRequest(request);
                    refreshTable(tableView);
                });
                
                rejectBtn.setOnAction(event -> {
                    RestockRequest request = getTableView().getItems().get(getIndex());
                    handleRejectRequest(request);
                    refreshTable(tableView);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    RestockRequest request = getTableView().getItems().get(getIndex());
                    if ("Pending".equals(request.getStatus())) {
                        setGraphic(pane);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
        
        tableView.getColumns().addAll(idCol, productCol, quantityCol, employeeCol, dateCol, statusCol, actionCol);
        
        // Load data
        refreshTable(tableView);
        
        // Add refresh button
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> refreshTable(tableView));
        
        // Add to layout
        borderPane.setCenter(tableView);
        borderPane.setBottom(refreshBtn);
        BorderPane.setAlignment(refreshBtn, Pos.CENTER);
        BorderPane.setMargin(refreshBtn, new Insets(10, 0, 0, 0));
        
        Scene scene = new Scene(borderPane, 800, 500);
        stage.setScene(scene);
        stage.show();
    }
    
    private void refreshTable(TableView<RestockRequest> tableView) {
        RestockRequestDAO dao = new RestockRequestDAO();
        tableView.getItems().clear();
        tableView.getItems().addAll(dao.getAllRequests());
    }
    
    private void handleApproveRequest(RestockRequest request) {
        // Update request status
        RestockRequestDAO requestDAO = new RestockRequestDAO();
        boolean statusUpdated = requestDAO.updateRequestStatus(request.getId(), "Approved");
        
        if (statusUpdated) {
            // Update product stock
            ProductDAO productDAO = new ProductDAO();
            List<Product> products = productDAO.getAllProducts();
            
            for (Product product : products) {
                if (product.getId() == request.getProductId()) {
                    int newStock = product.getStockQuantity() + request.getRequestedQuantity();
                    product.setStockQuantity(newStock);
                    productDAO.updateProduct(product);
                    
                    showAlert(Alert.AlertType.INFORMATION, "Success", 
                        "Request approved and stock updated for " + product.getName());
                    return;
                }
            }
            showAlert(Alert.AlertType.WARNING, "Warning", 
                "Request approved but product not found to update stock");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to approve request");
        }
    }
    
    private void handleRejectRequest(RestockRequest request) {
        RestockRequestDAO dao = new RestockRequestDAO();
        boolean updated = dao.updateRequestStatus(request.getId(), "Rejected");
        
        if (updated) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Request rejected");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to reject request");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showPasswordResetDialog() {
        // Create a dialog for password reset
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Password Reset");
        dialog.setHeaderText("Enter your username to reset password");
        
        // Set the button types
        ButtonType resetButtonType = new ButtonType("Reset", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(resetButtonType, ButtonType.CANCEL);
        
        // Create the username label and field
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField username = new TextField();
        username.setPromptText("Username");
        
        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the username field by default
        username.requestFocus();
        
        // Convert the result to a string when the reset button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == resetButtonType) {
                return username.getText();
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(usernameValue -> {
            
            UserDao userDao = new UserDao();
            User user = userDao.findByUsername(usernameValue);

            if (user != null){
                String resetToken = generateResetToken();

                userDao.saveResetToken(user.getId(), resetToken);

                String resetLink = "http://placeholderdomain.com/reset?token="+ resetToken;
                sendResetEmail(user.getEmail(),resetLink);


                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Password Reset");
                alert.setHeaderText(null);
                alert.setContentText("A password reset link has been sent to the email associated with username: " + usernameValue);
                alert.showAndWait(); 

            }else{
                //error
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Password Reset");
                alert.setHeaderText(null);
                alert.setContentText("Username not found. Please check and try again.");
                alert.showAndWait();
            }
        
            
        });

        
    }
    
    private String generateResetToken() {
        // Generate a random token
        SecureRandom random = new SecureRandom();
        byte[] tokenBytes = new byte[24];
        random.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private void sendResetEmail(String email, String resetLink) {
        // In a real application, this would send an actual email
        
        System.out.println("Reset link sent to " + email + ": " + resetLink);
        
        // Add a notification to the user that this is a simulation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Email Simulation");
        alert.setHeaderText(null);
        alert.setContentText("A link has been sent too:" + email);
        alert.showAndWait();
    }



    public static void main(String[] args) {
        launch(args);
    }
    
    private void showSalesReport() {
        Stage reportStage = new Stage();
        reportStage.setTitle("Sales Report");
        
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Sales Report");
        titleLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 16));
        
        // Date range selection
        HBox dateRangeBox = new HBox(10);
        dateRangeBox.setAlignment(Pos.CENTER_LEFT);
        
        Label fromLabel = new Label("From:");
        DatePicker fromDatePicker = new DatePicker(LocalDate.now().minusMonths(1));
        
        Label toLabel = new Label("To:");
        DatePicker toDatePicker = new DatePicker(LocalDate.now());
        
        Button generateButton = new Button("Generate Report");
        
        dateRangeBox.getChildren().addAll(fromLabel, fromDatePicker, toLabel, toDatePicker, generateButton);
        
        // Report table
        TableView<Sale> reportTable = new TableView<>();
        reportTable.setPrefHeight(400);
        
        TableColumn<Sale, Integer> idColumn = new TableColumn<>("Sale ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Sale, java.util.Date> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        dateColumn.setCellFactory(column -> new TableCell<Sale, Date>() {
            private final java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            @Override
            protected void updateItem(java.util.Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(format.format(item));
                }
            }
        });
        
        TableColumn<Sale, String> employeeColumn = new TableColumn<>("Employee");
        employeeColumn.setCellValueFactory(new PropertyValueFactory<>("employeeUsername"));
        
        TableColumn<Sale, Double> totalColumn = new TableColumn<>("Total Amount");
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        totalColumn.setCellFactory(column -> new TableCell<Sale, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("£%.2f", item));
                }
            }
        });
        
        // Add items column to show details
        TableColumn<Sale, Void> itemsColumn = new TableColumn<>("Items");
        itemsColumn.setCellFactory(column -> {
            return new TableCell<Sale, Void>() {
                private final Button detailsButton = new Button("View Items");
                {
                    detailsButton.setOnAction(event -> {
                        Sale sale = getTableView().getItems().get(getIndex());
                        showSaleItemsDialog(sale);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(detailsButton);
                    }
                }
            };
        });
        
        reportTable.getColumns().addAll(idColumn, dateColumn, employeeColumn, totalColumn, itemsColumn);
        
        // Summary section
        GridPane summaryGrid = new GridPane();
        summaryGrid.setHgap(10);
        summaryGrid.setVgap(5);
        summaryGrid.setPadding(new Insets(10));
        summaryGrid.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-width: 1px; -fx-border-radius: 5px;");
        
        Label totalSalesLabel = new Label("Total Sales:");
        totalSalesLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
        Label totalSalesValue = new Label("0");
        
        Label totalRevenueLabel = new Label("Total Revenue:");
        totalRevenueLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
        Label totalRevenueValue = new Label("£0.00");
        
        summaryGrid.add(totalSalesLabel, 0, 0);
        summaryGrid.add(totalSalesValue, 1, 0);
        summaryGrid.add(totalRevenueLabel, 0, 1);
        summaryGrid.add(totalRevenueValue, 1, 1);
        
        // Export button
        Button exportButton = new Button("Export to CSV");
        exportButton.setDisable(true); // Initially disabled until report is generated
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(exportButton);
        
        vbox.getChildren().addAll(titleLabel, dateRangeBox, reportTable, summaryGrid, buttonBox);
        
        // Generate report functionality
        generateButton.setOnAction(e -> {
            LocalDate fromDate = fromDatePicker.getValue();
            LocalDate toDate = toDatePicker.getValue();
            
            if (fromDate == null || toDate == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid Date Range");
                alert.setHeaderText(null);
                alert.setContentText("Please select valid from and to dates.");
                alert.showAndWait();
                return;
            }
            
            if (fromDate.isAfter(toDate)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid Date Range");
                alert.setHeaderText(null);
                alert.setContentText("From date must be before or equal to To date.");
                alert.showAndWait();
                return;
            }
            
            // Convert LocalDate to java.util.Date for database query
            java.util.Date fromDateUtil = java.sql.Date.valueOf(fromDate);
            java.util.Date toDateUtil = java.sql.Date.valueOf(toDate.plusDays(1)); // Add one day to include the end date
            
            SaleDAO saleDAO = new SaleDAO();
            List<Sale> sales = saleDAO.getSalesByDateRange(fromDateUtil, toDateUtil);
            
            reportTable.getItems().clear();
            reportTable.getItems().addAll(sales);
            
            // Update summary
            int totalSales = sales.size();
            double totalRevenue = 0.0;
            for (Sale sale : sales) {
                totalRevenue += sale.getTotalAmount();
            }
            
            totalSalesValue.setText(String.valueOf(totalSales));
            totalRevenueValue.setText(String.format("£%.2f", totalRevenue));
            
            // Enable export button
            exportButton.setDisable(false);
        });
        
        // Export functionality
        exportButton.setOnAction(e -> {
            StringBuilder csv = new StringBuilder();
            csv.append("Sale ID,Date,Employee,Total Amount\n");
            
            for (Sale sale : reportTable.getItems()) {
                csv.append(sale.getId()).append(",");
                csv.append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(sale.getSaleDate())).append(",");
                csv.append(sale.getEmployeeUsername()).append(",");
                csv.append(String.format("%.2f", sale.getTotalAmount())).append("\n");
            }
            
            // Create a file chooser for saving the CSV file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Sales Report");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            
            // Set initial filename with date range
            String fromDateStr = fromDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String toDateStr = toDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            fileChooser.setInitialFileName("sales_report_" + fromDateStr + "_to_" + toDateStr + ".csv");
            
            // Show save file dialog
            File file = fileChooser.showSaveDialog(reportStage);
            
            if (file != null) {
                try {
                    // Write CSV content to the selected file
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(csv.toString());
                    fileWriter.close();
                    
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Export Successful");
                    alert.setHeaderText(null);
                    alert.setContentText("Sales report has been exported successfully to:\n" + file.getAbsolutePath());
                    alert.showAndWait();
                } catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Export Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to save the report: " + ex.getMessage());
                    alert.showAndWait();
                }
            }
        });
        
        Scene scene = new Scene(vbox, 800, 600);
        reportStage.setScene(scene);
        reportStage.show();
    }
    
    private void showSaleItemsDialog(Sale sale) {
        Stage itemsStage = new Stage();
        itemsStage.setTitle("Sale #" + sale.getId() + " Items");
        itemsStage.initModality(Modality.APPLICATION_MODAL);
        
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Items for Sale #" + sale.getId());
        titleLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
        
        TableView<SaleItem> itemsTable = new TableView<>();
        
        TableColumn<SaleItem, String> nameColumn = new TableColumn<>("Product");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));
        
        TableColumn<SaleItem, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        
        TableColumn<SaleItem, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getProduct().getSellingPrice()));
        priceColumn.setCellFactory(column -> new TableCell<SaleItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("£%.2f", item));
                }
            }
        });
        
        TableColumn<SaleItem, Double> totalColumn = new TableColumn<>("Total");
        totalColumn.setCellValueFactory(cellData -> {
            SaleItem item = cellData.getValue();
            return new SimpleObjectProperty<>(item.getQuantity() * item.getProduct().getSellingPrice());
        });
        totalColumn.setCellFactory(column -> new TableCell<SaleItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("£%.2f", item));
                }
            }
        });
        
        itemsTable.getColumns().addAll(nameColumn, quantityColumn, priceColumn, totalColumn);
        itemsTable.getItems().addAll(sale.getItems());
        
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> itemsStage.close());
        
        vbox.getChildren().addAll(titleLabel, itemsTable, closeButton);
        
        Scene scene = new Scene(vbox, 600, 400);
        itemsStage.setScene(scene);
        itemsStage.showAndWait();
    }
    
    private void showInventoryReport() {
        Stage reportStage = new Stage();
        reportStage.setTitle("Inventory Report");
        
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Inventory Report");
        titleLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 16));
        
        // Filter options
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        Label filterLabel = new Label("Filter by:");
        ComboBox<String> filterComboBox = new ComboBox<>();
        filterComboBox.getItems().addAll("All Products", "Low Stock (< 10)", "Out of Stock");
        filterComboBox.setValue("All Products");
        
        Button generateButton = new Button("Generate Report");
        
        filterBox.getChildren().addAll(filterLabel, filterComboBox, generateButton);
        
        // Report table
        TableView<Product> reportTable = new TableView<>();
        reportTable.setPrefHeight(400);
        
        TableColumn<Product, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Product, String> nameColumn = new TableColumn<>("Product Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(150);
        
        TableColumn<Product, String> brandColumn = new TableColumn<>("Brand");
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        
        TableColumn<Product, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("productType"));
        
        TableColumn<Product, Double> costColumn = new TableColumn<>("Cost Price");
        costColumn.setCellValueFactory(new PropertyValueFactory<>("costPrice"));
        costColumn.setCellFactory(column -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("£%.2f", item));
                }
            }
        });
        
        TableColumn<Product, Double> sellingColumn = new TableColumn<>("Selling Price");
        sellingColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        sellingColumn.setCellFactory(column -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("£%.2f", item));
                }
            }
        });
        
        TableColumn<Product, Integer> stockColumn = new TableColumn<>("Stock");
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        stockColumn.setCellFactory(column -> new TableCell<Product, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    if (item < 10) {
                        setStyle("-fx-text-fill: orange;");
                    }
                    if (item == 0) {
                        setStyle("-fx-text-fill: red;");
                    }
                }
            }
        });
        
        TableColumn<Product, Double> valueColumn = new TableColumn<>("Total Value");
        valueColumn.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            return new SimpleObjectProperty<>(product.getStockQuantity() * product.getCostPrice());
        });
        valueColumn.setCellFactory(column -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("£%.2f", item));
                }
            }
        });
        
        reportTable.getColumns().addAll(idColumn, nameColumn, brandColumn, typeColumn, costColumn, sellingColumn, stockColumn, valueColumn);
        
        // Summary section
        GridPane summaryGrid = new GridPane();
        summaryGrid.setHgap(10);
        summaryGrid.setVgap(5);
        summaryGrid.setPadding(new Insets(10));
        summaryGrid.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-width: 1px; -fx-border-radius: 5px;");
        
        Label totalProductsLabel = new Label("Total Products:");
        totalProductsLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
        Label totalProductsValue = new Label("0");
        
        Label totalValueLabel = new Label("Total Inventory Value:");
        totalValueLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
        Label totalValueValue = new Label("£0.00");
        
        Label lowStockLabel = new Label("Low Stock Items:");
        lowStockLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
        Label lowStockValue = new Label("0");
        
        Label outOfStockLabel = new Label("Out of Stock Items:");
        outOfStockLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
        Label outOfStockValue = new Label("0");
        
        summaryGrid.add(totalProductsLabel, 0, 0);
        summaryGrid.add(totalProductsValue, 1, 0);
        summaryGrid.add(totalValueLabel, 0, 1);
        summaryGrid.add(totalValueValue, 1, 1);
        summaryGrid.add(lowStockLabel, 0, 2);
        summaryGrid.add(lowStockValue, 1, 2);
        summaryGrid.add(outOfStockLabel, 0, 3);
        summaryGrid.add(outOfStockValue, 1, 3);
        
        // Export button
        Button exportButton = new Button("Export to CSV");
        exportButton.setDisable(true); // Initially disabled until report is generated
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(exportButton);
        
        vbox.getChildren().addAll(titleLabel, filterBox, reportTable, summaryGrid, buttonBox);
        
        // Generate report functionality
        generateButton.setOnAction(e -> {
            String filter = filterComboBox.getValue();
            
            ProductDAO productDAO = new ProductDAO();
            List<Product> products = productDAO.getAllProducts();
            List<Product> filteredProducts = new ArrayList<>();
            
            // Apply filter
            for (Product product : products) {
                if (filter.equals("All Products") || 
                    (filter.equals("Low Stock (< 10)") && product.getStockQuantity() < 10 && product.getStockQuantity() > 0) ||
                    (filter.equals("Out of Stock") && product.getStockQuantity() == 0)) {
                    filteredProducts.add(product);
                }
            }
            
            reportTable.getItems().clear();
            reportTable.getItems().addAll(filteredProducts);
            
            // Update summary
            int totalProducts = filteredProducts.size();
            double totalValue = 0.0;
            int lowStockCount = 0;
            int outOfStockCount = 0;
            
            for (Product product : products) { // Use all products for summary stats
                totalValue += product.getStockQuantity() * product.getCostPrice();
                
                if (product.getStockQuantity() < 10 && product.getStockQuantity() > 0) {
                    lowStockCount++;
                }
                
                if (product.getStockQuantity() == 0) {
                    outOfStockCount++;
                }
            }
            
            totalProductsValue.setText(String.valueOf(totalProducts));
            totalValueValue.setText(String.format("£%.2f", totalValue));
            lowStockValue.setText(String.valueOf(lowStockCount));
            outOfStockValue.setText(String.valueOf(outOfStockCount));
            
            // Enable export button
            exportButton.setDisable(false);
        });
        
        // Export functionality
        exportButton.setOnAction(e -> {
            StringBuilder csv = new StringBuilder();
            csv.append("ID,Product Name,Brand,Type,Cost Price,Selling Price,Stock,Total Value\n");
            
            for (Product product : reportTable.getItems()) {
                csv.append(product.getId()).append(",");
                csv.append(product.getName().replace(",", " ")).append(","); // Replace commas to avoid CSV issues
                csv.append(product.getBrand().replace(",", " ")).append(",");
                csv.append(((String) product.getProductType()).replace(",", " ")).append(",");
                csv.append(String.format("%.2f", product.getCostPrice())).append(",");
                csv.append(String.format("%.2f", product.getSellingPrice())).append(",");
                csv.append(product.getStockQuantity()).append(",");
                csv.append(String.format("%.2f", product.getStockQuantity() * product.getCostPrice())).append("\n");
            }
            
            // Create a file chooser for saving the CSV file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Inventory Report");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            
            // Set initial filename with filter type
            String filterType = filterComboBox.getValue().replace(" ", "_").toLowerCase();
            fileChooser.setInitialFileName("inventory_report_" + filterType + ".csv");
            
            // Show save file dialog
            File file = fileChooser.showSaveDialog(reportStage);
            
            if (file != null) {
                try {
                    // Write CSV content to the selected file
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(csv.toString());
                    fileWriter.close();
                    
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Export Successful");
                    alert.setHeaderText(null);
                    alert.setContentText("Inventory report has been exported successfully to:\n" + file.getAbsolutePath());
                    alert.showAndWait();
                } catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Export Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to save the report: " + ex.getMessage());
                    alert.showAndWait();
                }
            }
        });
        
        Scene scene = new Scene(vbox, 900, 600);
        reportStage.setScene(scene);
        reportStage.show();
    }
}