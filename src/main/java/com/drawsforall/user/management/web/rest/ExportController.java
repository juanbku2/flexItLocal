package com.drawsforall.user.management.web.rest;

import com.drawsforall.user.management.business.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExportController {

    private final UserService userService;

    @Autowired
    public ExportController(UserService userService) {
        this.userService = userService;
    }
/*
    @GetMapping(value = "/export/excel")
    public void exportToExcel(
            @RequestParam(name = "by", defaultValue = "today") String by,
            @RequestParam(name = "dateIn", defaultValue = "16-03-2021") String dateIn,
            @RequestParam(name = "dateOut", defaultValue = "") String dateOut,
            HttpServletResponse response
    ) throws IOException, ParseException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        PagedUsersDTOReport pagedUsersDTOReport = userService.getUsersReport(by,dateIn,dateOut);

        UserExcelExporter userExcelExporter = new UserExcelExporter(pagedUsersDTOReport);

        userExcelExporter.export(response);


    }


 */

}
