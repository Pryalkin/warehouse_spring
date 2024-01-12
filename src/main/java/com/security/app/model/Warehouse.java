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
@Table(name = "warehouse")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String name;
    @OneToMany(
            mappedBy = "warehouse",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Application> applications = new ArrayList<>();
    private Double width;
    private Double length;
    private Double height;

    public void addApplication(Application application){
        this.applications.add(application);
        application.setWarehouse(this);
    }

    public void deleteApplication(Application application){
        this.applications.remove(application);
        application.setWarehouse(null);
    }

}
