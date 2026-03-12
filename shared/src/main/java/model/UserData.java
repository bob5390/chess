package model;

public class UserData {
    private String username;
    private String password;
    private String email;

    public UserData(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
    public UserData(String username, String password) {
        this(username, password, null);
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }

    @Override
    public boolean equals(Object obj) {
        UserData toTest = (UserData) obj;
        return toTest != null 
            && this.username.equals(toTest.getUsername())
            && this.password.equals(toTest.getPassword())
            && ((this.email == null && toTest.getEmail() == null) || this.email.equals(toTest.getEmail()));
    }
}
