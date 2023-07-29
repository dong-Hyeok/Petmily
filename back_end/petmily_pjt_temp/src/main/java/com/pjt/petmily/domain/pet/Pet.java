package com.pjt.petmily.domain.pet;

import com.pjt.petmily.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Entity
@Builder
@Table(name="pet")
public class Pet {

    @ManyToOne
    @JoinColumn(name = "userEmail")
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petId;

    @NotNull
    private String petName;

    @Column(nullable=true)
    private String petGender;

    @Column(nullable=true)
    private String petInfo;

    @Column(nullable=true)
    private Long petBirth;

    @Column(nullable=true)
    private String petImg;

    @Column(nullable=true)
    private Long speciesId;

}
