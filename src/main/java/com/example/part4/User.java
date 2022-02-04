package com.example.part4;

import com.example.part5.Orders;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ussername;

    @Enumerated(EnumType.STRING)
    private Level level = Level.NORMAL;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER) // 로딩 시 orders도 불러옴
    @JoinColumn(name="user_id")
    private List<Orders> orders;

    private LocalDate updatedDate;

    @Builder
    private User(String username, List<Orders> orders){
        this.ussername = username;
        this.orders = orders;
    }

    public boolean avaliableLevelUp() {
        return Level.avaliableLevelUp(this.getLevel(),this.getTotalAmount());
    }

    public Level levelUp(){
        Level nextLevel = Level.getNextLevel(this.getTotalAmount());

        this.level = nextLevel;
        this.updatedDate = LocalDate.now();

        return nextLevel;
    }

    private int getTotalAmount() {
        return this.orders.stream()
                .mapToInt(Orders::getAmount)
                .sum();
    }

    public enum Level{
        VIP(500_000,null),
        GOLD(500_000,VIP),
        SILVER(300_000,GOLD),
        NORMAL(200_000,SILVER);

        private final int nextAmount;
        private final Level nextLevel;

        Level(int nextAmount, Level nextLevel) {
            this.nextAmount = nextAmount;
            this.nextLevel = nextLevel;
        }

        private static boolean avaliableLevelUp(Level level, int totalAmount) {
            if(Objects.isNull(level)){
                return false;
            }

            if(Objects.isNull(level.nextLevel)){
                return false;
            }

            return totalAmount >= level.nextAmount;
        }

        private static Level getNextLevel(int totalAmount) {
            if(totalAmount >= Level.VIP.nextAmount){
                return VIP;
            }

            if(totalAmount >= Level.GOLD.nextAmount){
                return GOLD.nextLevel;
            }

            if(totalAmount >= Level.SILVER.nextAmount){
                return SILVER.nextLevel;
            }

            if(totalAmount >= Level.NORMAL.nextAmount){
                return NORMAL.nextLevel;
            }

            return NORMAL;
        }
    }
}
