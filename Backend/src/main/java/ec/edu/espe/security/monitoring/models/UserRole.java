package ec.edu.espe.security.monitoring.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@JsonIgnoreProperties(value = {"createdAt", "updatedAt"},
        allowGetters = true)
@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private long id;
    private String name;

    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(columnDefinition = "boolean default true")
    private Boolean isActive;
}