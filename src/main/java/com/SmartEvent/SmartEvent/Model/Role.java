package com.SmartEvent.SmartEvent.Model;


import jakarta.persistence.ElementCollection;

import com.SmartEvent.SmartEvent.Enums.Permission;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document
public class Role {
    @Id
    private String _id;
    @NotNull(message = "name is requered")
    private String name;
    @ElementCollection(targetClass = Permission.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING) // store enum name (e.g., "READ_USER")
    private Set<Permission> permissions;
    private  boolean isSystemRole;
    public String get_id() {
        return _id;
    }
    public void set_id(String _id) {
        this._id = _id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Set<Permission> getPermission() {
        return permissions;
    }
    public void  addPermission(Permission permission) {
        this.permissions.add(permission);
    }
    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }
    public void setPermission(Set<Permission> permission) {
        this.permissions = permission;
    }
    public boolean getIsSystemRole() {
        return isSystemRole;
    }
    public void setIsSystemRole(boolean isSystemRole) {
        this.isSystemRole = isSystemRole;
    }
}
