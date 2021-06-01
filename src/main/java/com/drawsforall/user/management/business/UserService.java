package com.drawsforall.user.management.business;

import com.drawsforall.user.management.business.exception.ReportNotFoundException;
import com.drawsforall.user.management.business.exception.UserNotFoundException;
import com.drawsforall.user.management.persistence.UserRepository;
import com.drawsforall.user.management.persistence.entity.User;
import com.drawsforall.user.management.web.rest.dto.PagedUserAllQueriesDTO;
import com.drawsforall.user.management.web.rest.dto.PagedUsersDTO;
import com.drawsforall.user.management.web.rest.dto.PagedUsersReportDTO;
import com.drawsforall.user.management.web.rest.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Map;

@Slf4j
@Transactional
@Service
public class UserService {

    private final String EMPLOYEE = "empNumber";
    private final String TICKET = "ticketNumber";
    private final String COMPUTER = "computerName";
    private final String DESCRIPTION_IN = "descriptionIn";
    private final String CREATED_DATE = "createdDate";
    private final String AGENT_NAME =  "agentName";
    private final String Description_Out = "descriptionOut";
    private final String UPDATED_DATE = "updatedDate";
    private final String TODAY = "today";
    private final String BETWEEN = "between";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;
    private final AuthenticationService authenticationService;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, ObjectMapper objectMapper, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.objectMapper = objectMapper;
        this.authenticationService = authenticationService;
    }

    public PagedUsersDTO getUsers(int page, int size, String sortBy, String by, String value, String isActive) {
        Page<User> users = null;

        switch (by) {
            case COMPUTER:
                users = userRepository.findUserByComputerNameContainsAndIsActive(value, isActive, PageRequest.of(page, size, orderFunction(sortBy))).orElseThrow(() -> new UserNotFoundException(value));
                log.debug("Fetching users");
                log.debug("Fetched {} users", users.getNumberOfElements());

                break;

            case DESCRIPTION_IN:
                users = userRepository.findUserByDescriptionInContainsAndIsActive(
                        value,
                        isActive,
                        PageRequest.of(page, size, orderFunction(sortBy))
                ).orElseThrow(() -> new UserNotFoundException(value));
                break;

            case EMPLOYEE:
                users = userRepository.findUserAllByEmpNumberContainsAndIsActive(value, isActive,
                        PageRequest.of(page, size, orderFunction(sortBy))).orElseThrow(() -> new UserNotFoundException(value));
                break;

            default:
                throw new IllegalArgumentException("Could not find user by " + by);
        }

        return userMapper.toPagedUsersDTO(users, by, value, isActive);

    }

    private LocalDateTime dateFormatStart(String date){
       DateTimeFormatter DATE_FORMAT_START =
       new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy[ [HH][:mm][:ss][.SSS]]")
               .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
               .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
               .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
               .toFormatter();

        return LocalDateTime.parse(date, DATE_FORMAT_START);
   }

    private LocalDateTime dateFormatEnd(String date){
        DateTimeFormatter DATE_FORMAT_END =
                new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy[ [HH][:mm][:ss][.SSS]]")
                        .parseDefaulting(ChronoField.HOUR_OF_DAY, 23)
                        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 59)
                        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 59)
                        .parseDefaulting(ChronoField.MILLI_OF_SECOND, 999)
                        .toFormatter();

        return LocalDateTime.parse(date, DATE_FORMAT_END);
    }

    public PagedUserAllQueriesDTO getPagedAllQueries(
            Integer page,
            Integer size,
            String sortBy,
            String empNumber,
            String computerName,
            String ticketNumber,
            String descriptionIn,
            String createdDateStart,
            String createdDateEnd,
            String agentName,
            String descriptionOut,
            String updatedDateStart,
            String updatedDateEnd
    ){
        Page<User> users = userRepository.findAllByEmpNumberContainsAndComputerNameContainsAndTicketNumberContainsAndDescriptionInContainsAndCreatedDateBetweenAndAgentNameContainsAndDescriptionOutContainsAndUpdatedDateBetween(
                empNumber,
                computerName,
                ticketNumber,
                descriptionIn,
                dateFormatStart(createdDateStart),
                dateFormatEnd(createdDateEnd),
                agentName,
                descriptionOut,
                dateFormatStart(updatedDateStart),
                dateFormatEnd(updatedDateEnd),
                PageRequest.of(page, size, orderFunction(sortBy))
        ).orElseThrow(() -> new UserNotFoundException("Error"));

        return userMapper.toPagedUserAllQueriesDTO(users);

    }


    public PagedUsersReportDTO getUsersReport(
            Integer page,
            Integer size,
            String by,
            String dateIn,
            String dateOut,
            String sortBy,
            String filterBy,
            String dateTypeSearch) throws ParseException {

        Page<User> users = null;

        switch (by) {

            case EMPLOYEE:
                users = userRepository.findAllByEmpNumberContainsAndCreatedDateBetween(
                        filterBy,
                        dateFormatStart(dateIn),
                        dateTypeSearch.equals(TODAY)
                                ?dateFormatEnd(dateIn)
                                :dateFormatEnd(dateOut),
                        PageRequest.of(page, size, orderFunction(sortBy))
                ).orElseThrow(() -> new UserNotFoundException(filterBy));

                break;

            case TICKET:
                users = userRepository.findUserByTicketNumberContainsAndCreatedDateBetween(
                        filterBy,
                        dateFormatStart(dateIn),
                        dateTypeSearch.equals(TODAY)
                                ?dateFormatEnd(dateIn)
                                :dateFormatEnd(dateOut),
                        PageRequest.of(page, size, orderFunction(sortBy))
                ).orElseThrow(() -> new UserNotFoundException(filterBy));
                break;

            case COMPUTER:
                users = userRepository.findUserByComputerNameContainsAndCreatedDateBetween(
                        filterBy,
                        dateFormatStart(dateIn),
                        dateTypeSearch.equals(TODAY)
                                ?dateFormatEnd(dateIn)
                                :dateFormatEnd(dateOut),
                        PageRequest.of(page, size, orderFunction(sortBy))
                ).orElseThrow(() -> new UserNotFoundException(filterBy));
                break;

            case DESCRIPTION_IN:
                users = userRepository.findUserByDescriptionInContainsAndCreatedDateBetween(
                        filterBy,
                        dateFormatStart(dateIn),
                        dateTypeSearch.equals(TODAY)
                                ?dateFormatEnd(dateIn)
                                :dateFormatEnd(dateOut),
                        PageRequest.of(page, size, orderFunction(sortBy))
                ).orElseThrow(() -> new UserNotFoundException(filterBy));
                break;

            case AGENT_NAME:
                users = userRepository.findUserByAgentNameContainsAndCreatedDateBetween(
                        filterBy,
                        dateFormatStart(dateIn),
                        dateTypeSearch.equals(TODAY)
                                ?dateFormatEnd(dateIn)
                                :dateFormatEnd(dateOut),
                        PageRequest.of(page, size, orderFunction(sortBy))
                ).orElseThrow(() -> new UserNotFoundException(filterBy));
                break;

            case Description_Out:
                users = userRepository.findUserByDescriptionOutContainsAndCreatedDateBetween(
                        filterBy,
                        dateFormatStart(dateIn),
                        dateTypeSearch.equals(TODAY)
                                ?dateFormatEnd(dateIn)
                                :dateFormatEnd(dateOut),
                        PageRequest.of(page, size, orderFunction(sortBy))
                ).orElseThrow(() -> new UserNotFoundException(filterBy));
                break;

            case CREATED_DATE:
                    users = userRepository.findUserByCreatedDateBetween(
                            dateFormatStart(dateIn),
                            dateTypeSearch.equals(TODAY)
                                    ?dateFormatEnd(dateIn)
                                    :dateFormatEnd(dateOut),
                            PageRequest.of(page, size, orderFunction(sortBy))
                    ).orElseThrow(() -> new ReportNotFoundException(dateIn));
                    //log.debug("Fetching users");
                    //log.debug("Fetched {} users", users.getNumberOfElements());
                break;

            case UPDATED_DATE:
                    users = userRepository.findUserByUpdatedDateBetween(
                            dateFormatStart(dateIn),
                            dateTypeSearch.equals(TODAY)
                                    ?dateFormatEnd(dateIn)
                                    :dateFormatEnd(dateOut),
                            PageRequest.of(page, size, orderFunction(sortBy))
                    ).orElseThrow(() -> new UserNotFoundException(dateIn));
                    //log.debug("Fetching users");
                    //log.debug("Fetched {} users", users.getNumberOfElements());
                break;
            default:
                throw new IllegalArgumentException("Could not find user by " + by);
        }

        return userMapper.toPagedUsersDTOReport(users, by, dateIn, dateOut, filterBy, dateTypeSearch);

    }



    private Sort orderFunction(String sortBy) {

        String[] strSplit = sortBy.split(",");
        String orderBy = strSplit[0];
        String orderAscOrDesc = strSplit[1];

        if (orderAscOrDesc.equals("ASC")) {
                return Sort.by(orderBy).ascending();
        } else {
                return Sort.by(orderBy).descending();
        }


    }

    public UserDTO createUser(UserDTO userDTO) {

        User user = userMapper.fromUserDTO(userDTO);
        user.setAgentName("");
        user.setDescriptionOut("");
        user.setIsActive("1");
        log.debug("Creating user {}", userDTO);
        if(user.getTicketNumber().equals("")){
            user.setTicketNumber("s/n");
        }

        try {
            User createdUser = userRepository.save(user);
            return userMapper.toUserDTO(createdUser);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

    }

    public UserDTO updateUser(Long id, Map<String, Object> fieldsToUpdate) {
        log.debug("Updating user {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        User userToUpdate = addFieldsToUpdateInExistingUser(user, fieldsToUpdate);

        try {
            User updatedUser = userRepository.save(userToUpdate);
            return userMapper.toUserDTO(updatedUser);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(ex.toString());
        }

    }

    private User addFieldsToUpdateInExistingUser(User existingUser, Map<String, Object> fieldsToUpdate) {
        log.debug("Adding fields {} to update in existing user {}", fieldsToUpdate, existingUser);
        Map<String, Object> existingUserMap = objectMapper.convertValue(existingUser, Map.class);
        existingUserMap.put("agentName", fieldsToUpdate.get("agentName"));
        existingUserMap.put("descriptionOut", fieldsToUpdate.get("descriptionOut"));
        existingUserMap.put("updatedDate", fieldsToUpdate.get("updatedDate"));
        existingUserMap.put("isActive", "0");
        log.debug("Added fields to update in existing user {}", existingUser);
        return objectMapper.convertValue(existingUserMap, User.class);
    }

    public Authentication currentUser() {
        return authenticationService.getAuthentication();
    }

    public UserDTO lookupUserOne(String value) {
        log.debug("Fetching user with {}", value);

        Long id = Long.parseLong(value);
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.toUserDTO(user);
    }

    public UserDTO removeUser(String value) {
        log.debug("Fetching user with {}", value);

        Long id = Long.parseLong(value);
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
        return userMapper.toUserDTO(user);

    }
}
