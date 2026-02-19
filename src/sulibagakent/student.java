/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sulibagakent;

/**
 *
 * @author Sulibaga-Ke
 */
public class student {
    private String name;
    private int age;
    
    public void setName(String name){
    this.name = name;
    }
    public void setAge(int age){
    this.age = age;
    }
    
    public void displayDetails(){
    System.out.println("The name of the student is: " + name +  " and the age is " + age); 
    }
}
