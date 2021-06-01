package com.drawsforall.user.management.persistence.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 5, max = 255, message = "Must have at least 3 characters")
    @NotBlank(message = "Please provide a  computerName")
    private String computerName;

    @Size(min = 5, max = 255, message = "Must have at least 3 characters")
    @NotBlank(message = "Please provide a  empNumber")
    private String empNumber;

    @Size(min = 5, max = 255, message = "Must have at least 3 characters")
    @NotBlank(message = "Please provide a ticketNumber")
    private String ticketNumber;

    @Size(min = 5, max = 255, message = "Must have at least 3 characters")
    @NotBlank(message = "Please provide a last descriptionIn")
    private String descriptionIn;

    @Size(min = 5, max = 255, message = "Must have at least 3 characters")
    @NotBlank(message = "Please provide a agentName")
    private String agentName;

    @Size(min = 5, max = 255, message = "Must have at least 3 characters")
    @NotBlank(message = "Please provide a descriptionOut")
    private String descriptionOut;

    private String isActive;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;


}
