package com.security.app.model;

import com.security.app.enumeration.Status;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "application")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String number;
    private String status;
    private String message;
    private String file;
    @ManyToOne(fetch = FetchType.LAZY)
    private Warehouse warehouse;
    @ManyToOne(fetch = FetchType.LAZY)
    private Subject subject;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

}
