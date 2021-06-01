package com.drawsforall.user.management.business;

import com.drawsforall.user.management.persistence.entity.Role;
import com.drawsforall.user.management.persistence.entity.User;
import com.drawsforall.user.management.web.rest.UserController;
import com.drawsforall.user.management.web.rest.dto.PagedUserAllQueriesDTO;
import com.drawsforall.user.management.web.rest.dto.PagedUsersDTO;
import com.drawsforall.user.management.web.rest.dto.PagedUsersReportDTO;
import com.drawsforall.user.management.web.rest.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(target = "userId", source = "id")
    })
    default UserDTO toUserDTO(User user) {
        UserDTO userDTO = UserDTO.builder()
                .userId(user.getId())
                .computerName(user.getComputerName().toUpperCase())
                .empNumber(user.getEmpNumber().toUpperCase())
                .ticketNumber(user.getTicketNumber().toUpperCase())
                .descriptionIn(user.getDescriptionIn())
                .agentName(user.getAgentName())
                .createdDate(user.getCreatedDate())
                .updatedDate(user.getUpdatedDate())
                .descriptionOut(user.getDescriptionOut())
                .isActive(user.getIsActive())
                .build();

        userDTO.add(buildLinksToUserDTO(user));


        return userDTO;
    }

    List<UserDTO> toUserDTO(List<User> users);

    @Mappings({
            @Mapping(target = "id", source = "userId"),
    })
    User fromUserDTO(UserDTO userDTO);

    default PagedUsersDTO toPagedUsersDTO(Page<User> pagedUsers, String by, String value, String isActive) {
        Page<UserDTO> page = pagedUsers.map(this::toUserDTO);
        PagedUsersDTO pagedUsersDTO = PagedUsersDTO.builder()
                .elements(page.getContent())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .numberOfElements(page.getNumberOfElements())
                .currentPage(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .sort(page.getSort().toString())
                .by(by)
                .value(value)
                .isActive(isActive)
                .build();

        pagedUsersDTO.add(buildLinksToPagedUsersDTO(pagedUsers.getNumber(), pagedUsers.getSize(), pagedUsers.getSort().toString(), pagedUsers.hasNext(), pagedUsers.hasPrevious(), by, value, isActive));
        return pagedUsersDTO;
    }

    @Named("userDTOLinks")
    default List<Link> buildLinksToUserDTO(User user) {
        Link all = linkTo(methodOn(UserController.class).getUsers(null, null, null, null, null, null)).withRel("all");
        Link selfOrUpdateOrDelete = linkTo(methodOn(UserController.class).updateUser(user.getId(), null)).withRel("self");
        return Arrays.asList(all, selfOrUpdateOrDelete);
    }

    default List<Link> buildLinksToPagedUsersDTO(int number, int size, String sort, boolean hasNext, boolean hasPrevious, String by, String value, String isActive) {
        List<Link> links = new ArrayList<>();

        String fixedLink = sort.replaceAll(": DESC", ",DESC");

        if (sort.contains(": ASC")) {
            fixedLink = sort.replaceAll(": ASC", ",ASC");
        }

        Link selfRel = linkTo(methodOn(UserController.class).getUsers(number, size, fixedLink, by, value, isActive)).withSelfRel();
        links.add(selfRel);
        if (hasNext) {
            Link nextPage = linkTo(methodOn(UserController.class).getUsers(number + 1, size, fixedLink, by, value, isActive)).withRel("nextPage");
            links.add(nextPage);
        }

        if (hasPrevious) {
            Link previousPage = linkTo(methodOn(UserController.class).getUsers(number - 1, size, fixedLink, by, value, isActive)).withRel("previousPage");
            links.add(previousPage);
        }

        return links;
    }

    @Named("setRolesToUserDTO")
    default List<String> setRolesToUserDTO(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getName().toString())
                .collect(Collectors.toList());
    }

    @Named("setEncodedPassword")
    default String setEncoderPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    @Named("setDecodedPassword")
    default String setDecodedPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    default PagedUsersReportDTO toPagedUsersDTOReport(
            Page<User> pagedUsers,
            String by,
            String dateIn,
            String dateOut,
            String filterBy,
            String dateTypeSearch) throws ParseException {

        Page<UserDTO> page = pagedUsers.map(this::toUserDTO);
        PagedUsersReportDTO pagedUsersReportDTO = PagedUsersReportDTO.builder()
                .elements(page.getContent())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .numberOfElements(page.getNumberOfElements())
                .currentPage(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .sort(page.getSort().toString())
                .by(by)
                .dateIn(dateIn)
                .dateOut(dateOut)
                .filterBy(filterBy)
                .dateTypeSearch(dateTypeSearch)
                .build();

        pagedUsersReportDTO.add(buildLinksToPagedUsersReportDTO(
                pagedUsers.getNumber(),
                pagedUsers.getSize(),
                pagedUsers.getSort().toString(),
                pagedUsers.hasNext(),
                pagedUsers.hasPrevious(),
                by,
                dateIn,
                dateOut,
                filterBy,
                dateTypeSearch));
        return pagedUsersReportDTO;
    }

    default List<Link> buildLinksToUserReportDTO(User user) {
        Link selfOrUpdateOrDelete = linkTo(methodOn(UserController.class).updateUser(user.getId(), null)).withRel("self");
        return Collections.singletonList(selfOrUpdateOrDelete);
    }

    default List<Link> buildLinksToPagedUsersReportDTO(int number,
                                                       int size,
                                                       String sort,
                                                       boolean hasNext,
                                                       boolean hasPrevious,
                                                       String by,
                                                       String dateIn,
                                                       String dateOut,
                                                       String filterBy,
                                                       String dateTypeSearch) throws ParseException {
        List<Link> links = new ArrayList<>();

        String fixedLink = sort.replaceAll(": DESC", ",DESC");

        if (sort.contains(": ASC")) {
            fixedLink = sort.replaceAll(": ASC", ",ASC");
        }

        Link selfRel = linkTo(methodOn(UserController.class).report(number, size, by, dateIn, dateOut, fixedLink,  filterBy, dateTypeSearch)).withSelfRel();
        links.add(selfRel);
        if (hasNext) {
            Link nextPage = linkTo(methodOn(UserController.class).report(number + 1, size, by, dateIn, dateOut, fixedLink,  filterBy, dateTypeSearch)).withRel("nextPage");
            links.add(nextPage);
        }

        if (hasPrevious) {
            Link previousPage = linkTo(methodOn(UserController.class).report(number - 1, size, by, dateIn, dateOut, fixedLink,  filterBy, dateTypeSearch)).withRel("previousPage");
            links.add(previousPage);
        }

        return links;
    }


    default PagedUserAllQueriesDTO toPagedUserAllQueriesDTO(Page<User> pagedUsers) {

        Page<UserDTO> page = pagedUsers.map(this::toUserDTO);
        PagedUserAllQueriesDTO pagedUserAllQueriesDTO  = PagedUserAllQueriesDTO.builder()
                .elements(page.getContent())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .numberOfElements(page.getNumberOfElements())
                .currentPage(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .sort(page.getSort().toString())

                .build();

        return pagedUserAllQueriesDTO;
    }
}
