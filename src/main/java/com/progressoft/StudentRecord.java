package com.progressoft;
//This type of the class will help you to make the constractor and Because it implement the Student class it will override the method and implement them .
public record StudentRecord(String Sname, String Sclass, int Sgrade) implements Student {
}
