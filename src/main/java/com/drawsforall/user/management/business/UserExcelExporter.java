package com.drawsforall.user.management.business;

import com.drawsforall.user.management.web.rest.dto.PagedUsersReportDTO;
import com.drawsforall.user.management.web.rest.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
public class UserExcelExporter {
    private final XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private final PagedUsersReportDTO pageUser;



    public UserExcelExporter(PagedUsersReportDTO pageUser){
        this.pageUser = pageUser;
        workbook = new XSSFWorkbook();
    }





    private void writeHeaderLine(){
        sheet = workbook.createSheet("Computers");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "No. empleado", style);
        createCell(row, 1, "Nombre Computadora", style);
        createCell(row, 2, "Motivo Entrada", style);
        createCell(row, 3, "Ticket GOT IT", style);
        createCell(row, 4, "Ingeniero Salida", style);
        createCell(row, 5, "Motivo Salida", style);
        createCell(row, 6, "Fecha Ingreso", style);
        createCell(row, 7, "Fecha Salida", style);
    }

    public void createCell(Row row, int columnCount,Object value, CellStyle style){
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if(value instanceof Integer){
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean){
            cell.setCellValue((Boolean) value);
        } else if (value instanceof LocalDateTime){
            cell.setCellValue((LocalDateTime) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines(){
        int rowCount = 1;

        CellStyle style1 = workbook.createCellStyle();




        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        style.setFont(font);

        CreationHelper creationHelper = workbook.getCreationHelper();
        style1.setFont(font);
        style1.setDataFormat(creationHelper.createDataFormat().getFormat(
                "dd-MM-yyyy"));

        List<UserDTO> listUsers = pageUser.getElements();


        for (UserDTO user: listUsers){

            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            log.debug("user:" + user);
            log.debug("Count:" + rowCount);
            createCell(row,columnCount++,user.getEmpNumber(),style);
            createCell(row,columnCount++,user.getComputerName(),style);
            createCell(row,columnCount++,user.getDescriptionIn(),style);
            createCell(row,columnCount++,user.getTicketNumber(),style);
            createCell(row,columnCount++,user.getAgentName(),style);
            createCell(row,columnCount++,user.getDescriptionOut(),style);
            createCell(row,columnCount++,user.getCreatedDate(),style1);
            createCell(row,columnCount++,user.getUpdatedDate(),style1);

        }
    }



    public void export(HttpServletResponse response) throws IOException{

        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
}
