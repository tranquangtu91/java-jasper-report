package com.example.demo.model;

import java.io.Serializable;
import java.util.List;

public class Business implements Serializable {
    private String name;
    private String address;
    private List<Employee> employees;

    // Constructor
    public Business(String name, String address, List<Employee> employees) {
        this.name = name;
        this.address = address;
        this.employees = employees;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    // toString() method for debugging
    @Override
    public String toString() {
        return "Business{name='" + name + "', address='" + address + "', employees=" + employees + '}';
    }
}