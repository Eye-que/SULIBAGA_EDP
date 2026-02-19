/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sulibagakent;
public class User {
    private int id;
    private String role;
    private String fullName;
    private String username;
    private String password;

    public User(int id, String role, String fullName, String username) {
        this.id = id;
        this.role = role;
        this.fullName = fullName;
        this.username = username;
    }

    public int getId() { return id; }
    public String getRole() { return role; }
    public String getFullName() { return fullName; }
    public String getUsername() { return username; }
}
