package com.pjt.petmily.domain.pet;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Entity
@Table(name = "walk")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Walk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walkId;

    @Column(name = "walk_date", nullable = false)
    private LocalDateTime walkDate;

    @Column(name = "walk_distance")
    private Integer walkDistance;

    @Column(name = "walk_spend")
    private Integer walkSpend;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;

}