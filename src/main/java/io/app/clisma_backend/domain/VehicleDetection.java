package io.app.clisma_backend.domain;

import io.app.clisma_backend.domain.enums.VehicleType;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "VehicleDetections")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDetection {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Long id;

    @Column
    private String licensePlate;

    @Column
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType = VehicleType.OTHER ;

    @OneToOne(
            mappedBy = "vehicleDetectionId",
            fetch = FetchType.LAZY
    )
    private EmissionRecord emissionRecord;

    @OneToMany(mappedBy = "vehicleDetectionId")
    private Set<Alert> alerts = new HashSet<>();

    @ManyToOne
    private User vehicleOwner;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
