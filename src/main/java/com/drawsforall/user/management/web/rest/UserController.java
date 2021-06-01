package com.drawsforall.user.management.web.rest;

import com.drawsforall.user.management.business.UserExcelExporter;
import com.drawsforall.user.management.business.UserService;
import com.drawsforall.user.management.web.rest.dto.PagedUserAllQueriesDTO;
import com.drawsforall.user.management.web.rest.dto.PagedUsersDTO;
import com.drawsforall.user.management.web.rest.dto.PagedUsersReportDTO;
import com.drawsforall.user.management.web.rest.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);

        return ResponseEntity
                .created(UriComponentsBuilder.fromPath("/users/{id}").buildAndExpand(createdUser.getUserId()).toUri())
                .body(createdUser);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedUsersDTO> getUsers(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "5") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "updatedDate,DESC") String sortBy,
            @RequestParam(name = "by", defaultValue = "computerName") String by,
            @RequestParam(name = "value", defaultValue = "") String value,
            @RequestParam(name = "isActive", defaultValue = "0") String isActive
    ) {
        return ResponseEntity.ok(userService.getUsers(page, size, sortBy, by, value,isActive));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> updateUser(
            @Valid
            @PathVariable Long id,
            @RequestBody Map<String, Object> fieldsToUpdate
    ) {
        return ResponseEntity.ok(userService.updateUser(id, fieldsToUpdate));
    }

    @GetMapping(value = "/currentUser")
    public ResponseEntity<Authentication> currentUser(){
        return ResponseEntity.ok(userService.currentUser());
    }

    @GetMapping(value = "/findUser", produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> lookupUser(
            @RequestParam(name = "value") String value
    ) {
        return ResponseEntity.ok(userService.lookupUserOne(value));
    }

    @GetMapping(value = "/delete", produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> removeUser(
            @RequestParam(name = "value") String value
    ) {
        return ResponseEntity.ok(userService.removeUser(value));
    }

    @GetMapping(value = "/report", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedUsersReportDTO> report(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "100") Integer size,
            @RequestParam(name = "by", defaultValue = "computerName") String by,
            @RequestParam(name = "dateIn", defaultValue = "01-01-2021") String dateIn,
            @RequestParam(name = "dateOut", defaultValue = "28-12-3000") String dateOut,
            @RequestParam(name = "sortBy", defaultValue = "createdDate,DESC") String sortBy,
            @RequestParam(name = "filterBy", defaultValue = "") String filterBy,
            @RequestParam(name = "dateTypeSearch", defaultValue = "") String dateTypeSearch
    ) throws ParseException {
        return ResponseEntity.ok(userService.getUsersReport(page, size, by, dateIn, dateOut, sortBy, filterBy, dateTypeSearch));
    }

    @GetMapping( "/report/excel")
    public ResponseEntity exportToExcel(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "100000") Integer size,
            @RequestParam(name = "by", defaultValue = "computerName") String by,
            @RequestParam(name = "dateIn", defaultValue = "01-01-2021") String dateIn,
            @RequestParam(name = "dateOut", defaultValue = "28-12-3000") String dateOut,
            @RequestParam(name = "sortBy", defaultValue = "updatedDate,DESC") String sortBy,
            @RequestParam(name = "filterBy", defaultValue = "") String filterBy,
            @RequestParam(name = "dateTypeSearch", defaultValue = "") String dateTypeSearch,
            HttpServletResponse response
    ) throws IOException, ParseException {

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        PagedUsersReportDTO pagedUsersReportDTO = userService.getUsersReport(page, size, by, dateIn, dateOut, sortBy, filterBy, dateTypeSearch);

        UserExcelExporter userExcelExporter = new UserExcelExporter(pagedUsersReportDTO);

        userExcelExporter.export(response);

        return  ResponseEntity.ok(response);

    }

    @GetMapping(value = "/allQueries", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedUserAllQueriesDTO> allQueries(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "100") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "createdDate,DESC") String sortBy,
            @RequestParam(name = "empNumber", defaultValue = "") String empNumber,
            @RequestParam(name = "computerName", defaultValue = "") String computerName,
            @RequestParam(name = "ticketNumber", defaultValue = "") String ticketNumber,
            @RequestParam(name = "descriptionIn", defaultValue = "") String descriptionIn,
            @RequestParam(name = "createdDateStart", defaultValue = "01-01-2021") String createdDateStart,
            @RequestParam(name = "createdDateEnd", defaultValue = "31-12-9999") String createdDateEnd,
            @RequestParam(name = "agentName", defaultValue = "") String agentName,
            @RequestParam(name = "descriptionOut", defaultValue = "") String descriptionOut,
            @RequestParam(name = "updatedDateStart", defaultValue = "01-01-2021") String updatedDateStart,
            @RequestParam(name = "updatedDateEnd", defaultValue = "31-12-3000") String updatedDateEnd
    ){
        return ResponseEntity.ok(userService.getPagedAllQueries(
                page,
                size,
                sortBy,
                empNumber,
                computerName,
                ticketNumber,
                descriptionIn,
                createdDateStart,
                createdDateEnd,
                agentName,
                descriptionOut,
                updatedDateStart,
                updatedDateEnd
        ));
    }




}
