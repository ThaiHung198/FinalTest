package model;

import java.io.Serializable;

public class Contact implements Serializable {
    private String phone;
    private String group;
    private String name;
    private String gender;
    private String address;
    private String birth;
    private String email;

    public Contact(String phone, String group, String name, String gender,
                   String address, String birth, String email) {
        this.phone = phone;
        this.group = group;
        this.name = name;
        this.gender = gender;
        this.address = address;
        this.birth = birth;
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("%-12s | %-8s | %-20s | %-6s | %-15s | %-10s | %s",
                phone, group, name, gender, address, birth, email);
    }

    public String toCSV() {
        return String.join(",", phone, group, name, gender, address, birth, email);
    }
}