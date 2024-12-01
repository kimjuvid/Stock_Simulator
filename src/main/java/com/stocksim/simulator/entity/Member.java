package com.stocksim.simulator.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;
    private String displayName;
    private String roles;

//    @ToString.Exclude
//    @OneToMany(mappedBy = "member")
//    private List<Sales> sales = new ArrayList<>();

}
