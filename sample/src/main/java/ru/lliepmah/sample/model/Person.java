package ru.lliepmah.sample.model;

/**
 * Created by Arthur Korchagin on 26.10.16
 */

public class Person {

    private int id;
    private String firstName;
    private String lastName;
    private String phone;

    public Person(int id, String firstName, String lastName, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }
}
