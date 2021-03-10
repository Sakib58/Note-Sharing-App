package com.example.notesharingapp;

import java.util.List;

public class Group {
    String name,description,key;
    List<String> members;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Group(String key, String name, String description, List<String> members) {
        this.name = name;
        this.description = description;
        this.members = members;
        this.key = key;
    }
}
