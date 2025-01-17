package com.estsoft13.matdori.domain;

import com.estsoft13.matdori.dto.restaurant.AddRestaurantRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "avg_rating", nullable = false)
    private Double avgRating;

    public Restaurant(AddRestaurantRequestDto requestDto) {
        this.name = requestDto.getName();
        this.address = requestDto.getAddress();
        this.category = requestDto.getCategory();
        this.avgRating = requestDto.getAvgRating();
    }

    public void update(String name, String address, String category) {
        this.name = name;
        this.address = address;
        this.category = category;
    }

    public Restaurant(String name, String address, String category) {
        this.name = name;
        this.address = address;
        this.category = category;
        this.avgRating = 0.0;
    }
}
