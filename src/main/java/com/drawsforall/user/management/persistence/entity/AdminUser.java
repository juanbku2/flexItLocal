package com.drawsforall.user.management.persistence.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Setter
@Getter
@ToString
@Data
@Entity
@Table
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Please provide an username")
    private String username;

    @NotBlank(message = "Please provide a password")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "ADMIN_USER_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    private Set<Role> roles;


}
