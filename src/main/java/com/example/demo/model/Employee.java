package com.example.demo.model;

import java.io.Serializable;

public class Employee implements Serializable {
    private String name;
    private String position;
    private Double salary;

    // Constructor
    public Employee(String name, String position, Double salary) {
        this.name = name;
        this.position = position;
        this.salary = salary;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    // toString() method for debugging
    @Override
    public String toString() {
        return "Employee{name='" + name + "', position='" + position + "', salary=" + salary + '}';
    }
}