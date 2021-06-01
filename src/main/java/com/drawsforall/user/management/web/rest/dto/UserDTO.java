package com.drawsforall.user.management.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Data
@ToString
public class UserDTO extends RepresentationModel<UserDTO> {

    private Long userId;

    @NotBlank(message = "Please provide a last name")
    private String computerName;

    @NotBlank(message = "Please provide a first name")
    private String empNumber;

    @NotBlank(message = "Please provide a last name")
    private String ticketNumber;

    @NotBlank(message = "Please provide a last name")
    private String descriptionIn;

    @NotBlank(message = "Please provide a last name")
    private String agentName;

    @NotBlank(message = "Please provide a last name")
    private String descriptionOut;

    @NotBlank(message = "Please provide a last name")
    private String isActive;

    @JsonFormat(pattern="dd-MM-yy HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(pattern="dd-MM-yy HH:mm:ss")
    private LocalDateTime updatedDate;

}
