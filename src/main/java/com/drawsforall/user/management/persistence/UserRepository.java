package com.drawsforall.user.management.persistence;

import com.drawsforall.user.management.business.exception.UserNotFoundException;
import com.drawsforall.user.management.persistence.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    String computerName = "ComputerNameContains";

    //Queries for getUsers
    Optional<Page<User>> findUserByComputerNameContainsAndIsActive(String computerName, String isActive, Pageable of);
    Optional<Page<User>> findUserAllByEmpNumberContainsAndIsActive(String empNumber, String isActive, Pageable of);
    Optional<Page<User>> findUserByDescriptionInContainsAndIsActive(String ticketNumber, String isActive, Pageable of);

    //Queries for report
    Optional<Page<User>> findAllByEmpNumberContainsAndCreatedDateBetween(String empNumber, LocalDateTime dateStart, LocalDateTime dateEnd, Pageable of);
    Optional<Page<User>> findUserByComputerNameContainsAndCreatedDateBetween(String computerName, LocalDateTime dateStart, LocalDateTime dateEnd, Pageable of);
    Optional<Page<User>> findUserByTicketNumberContainsAndCreatedDateBetween(String ticketNumber, LocalDateTime dateStart, LocalDateTime dateEnd, Pageable of);
    Optional<Page<User>> findUserByDescriptionInContainsAndCreatedDateBetween(String descriptionIn, LocalDateTime dateStart, LocalDateTime dateEnd, Pageable of);
    Optional<Page<User>> findUserByCreatedDateBetween(LocalDateTime dateStart, LocalDateTime dateEnd,  Pageable of);
    Optional<Page<User>> findUserByAgentNameContainsAndCreatedDateBetween(String agentName, LocalDateTime dateStart, LocalDateTime dateEnd, Pageable of);
    Optional<Page<User>> findUserByDescriptionOutContainsAndCreatedDateBetween(String DescriptionOut, LocalDateTime dateStart, LocalDateTime dateEnd, Pageable of);
    Optional<Page<User>> findUserByUpdatedDateBetween(LocalDateTime dateStart, LocalDateTime dateEnd, Pageable of);

    Optional<Page<User>> findAllByEmpNumberContainsAndComputerNameContainsAndTicketNumberContainsAndDescriptionInContainsAndCreatedDateBetweenAndAgentNameContainsAndDescriptionOutContainsAndUpdatedDateBetween(
            String empNumber,
            String computerName,
            String ticketNumber,
            String descriptionIn,
            LocalDateTime createdDateStart,
            LocalDateTime createdDateEnd,
            String agentName,
            String descriptionOut,
            LocalDateTime updatedDateStart,
            LocalDateTime updatedDateEnd,
            Pageable pageable
    );

}

