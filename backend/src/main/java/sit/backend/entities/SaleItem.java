package sit.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "sale_item")
public class SaleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "model", nullable = false, length = 60)
    private String model;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private  BigDecimal price;                      // เปลี่ยนจาก Intger เป็น Bigdecimal

    @Column(name = "ram_gb")
    private Integer ramGb;

    @Column(name = "screen_size_inch", precision = 4, scale = 2)
    private BigDecimal screenSizeInch;

    @Column(name = "storage_gb")
    private Integer storageGb;

    @Column(name = "color", length = 50)
    private String color;

    @ColumnDefault("1")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @JsonIgnore
    @Column(name = "created_on", insertable = false, updatable = false)
    @org.hibernate.annotations.Generated(org.hibernate.annotations.GenerationTime.INSERT)
    private Instant createdOn;

    @JsonIgnore
    @Column(name = "updated_on", insertable = false, updatable = false)
    @org.hibernate.annotations.Generated(org.hibernate.annotations.GenerationTime.ALWAYS)
    private Instant updatedOn;

    @OneToMany(mappedBy = "saleItem",fetch = FetchType.EAGER)
    private List<SaleItemImage> saleItemImages = new ArrayList<>();

}

