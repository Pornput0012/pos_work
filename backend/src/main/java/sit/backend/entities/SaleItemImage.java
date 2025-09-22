package sit.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "sale_item_images")
public class SaleItemImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "saleitem_id", nullable = false)
    private SaleItem saleItem;

    @Column(name = "fileName", nullable = false)
    private String fileName;

    @Column(name = "imageViewOrder")
    private Integer imageViewOrder;

}