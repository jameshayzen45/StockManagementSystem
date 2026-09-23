package stockmanagementsystem;

public class User {
    private int id;
    private String username;
    private String password;
    
    private String email;
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    private String userType;
    
  
    public User() {
        // Default constructor
    }
    
   
    public User(int id, String username, String password, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.userType = "user"; // Default user type
    }
    
    public User(int id, String username, String password, String email, String userType) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.userType = userType;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password){
        this.password = password;
    }


    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType){
        this.userType = userType;
    }
}

