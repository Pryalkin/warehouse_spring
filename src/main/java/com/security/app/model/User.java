package com.security.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @EqualsAndHashCode.Include
    private String username;
    @EqualsAndHashCode.Include
    private String password;
    @EqualsAndHashCode.Include
    private String role;
    @EqualsAndHashCode.Include
    private String[] authorities;
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Application> applications = new ArrayList<>();

    public void addApplication(Application application){
        this.applications.add(application);
        application.setUser(this);
    }

}
