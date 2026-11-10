package com.example.core.service;

import com.example.core.DTO.*;
import com.example.core.exception.ReportGenerationException;
import com.example.core.repository.BrigadeRepository;
import com.example.core.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final ReportRepository exelReportRepository;
    private final BrigadeRepository brigadeRepository;
    // Хранилище статусов: Ключ = taskId, Значение = статус (PROCESSING, SUCCESS, FAILED)
    private final Map<String, String> taskStatuses = new ConcurrentHashMap<>();
    // Папка на сервере, где будут временно лежать готовые файлы
    private final String STORAGE_DIR = System.getProperty("java.io.tmpdir") + "/excel_reports/";

    public List<SelectDto> getEquipmentList(){
        List<SelectDto> equipmentList =  new ArrayList<>();
        equipmentList.add(new SelectDto(0L, "Вся спец.техника"));
        equipmentList.add(new SelectDto(1019540L, "MAN"));
        equipmentList.add(new SelectDto(1019237L, "KENWORTH"));
        equipmentList.add(new SelectDto(1019385L, "УНП2"));
        equipmentList.add(new SelectDto(1018865L, "УНЦ"));
        equipmentList.add(new SelectDto(1018281L, "ЦА-320"));
        return equipmentList;
    }

    public List<SelectDto> getEquipmentExtList(){
        List<SelectDto> equipmentList =  new ArrayList<>();
        equipmentList.add(new SelectDto(0L, "Традиционая химизация"));
        equipmentList.add(new SelectDto(1019540L, "MAN"));
        equipmentList.add(new SelectDto(1019237L, "KENWORTH"));
        equipmentList.add(new SelectDto(1019385L, "УНП2"));
        equipmentList.add(new SelectDto(1018865L, "УНЦ"));
        equipmentList.add(new SelectDto(1018281L, "ЦА-320"));
        return equipmentList;
    }

    public Workbook generateDowntimeReport(Integer zeh, Integer type, LocalDateTime dateBegin, LocalDateTime dateEnd) throws IOException {
        List<DowntimeReportDto> report = exelReportRepository.findDowntimeReport(zeh, type, dateBegin.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), dateEnd.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        try (InputStream templateStream = getClass().getResourceAsStream("/templates/tmpHimProstoi.xls")){
            if (templateStream == null){
                throw new FileNotFoundException("Template not found");
            }
            Workbook workbook = new HSSFWorkbook(templateStream);
            Name bodyCell = workbook.getName("body");
            String bodyRef = bodyCell.getRefersToFormula().replace("'", "").split("!")[1];
            CellReference ref = new CellReference(bodyRef);
            Sheet sheet = workbook.getSheet("Him");
            int currentRow = ref.getRow();
            CellStyle baseStyle = sheet.getRow(ref.getRow()).getCell(ref.getCol()).getCellStyle();
            baseStyle.setAlignment(HorizontalAlignment.CENTER);
            baseStyle.setBorderRight(BorderStyle.THIN);
            baseStyle.setBorderLeft(BorderStyle.THIN);
            baseStyle.setBorderBottom(BorderStyle.THIN);
            baseStyle.setBorderTop(BorderStyle.THIN);
            Font baseFont = workbook.getFontAt(baseStyle.getFontIndex());
            sheet.shiftRows(currentRow + 1, sheet.getLastRowNum(), report.size(), true, false);

            CellStyle leftAlignBaseStyle = workbook.createCellStyle();
            leftAlignBaseStyle.cloneStyleFrom(baseStyle);
            leftAlignBaseStyle.setAlignment(HorizontalAlignment.LEFT);

            CellStyle boldBaseStyle  = workbook.createCellStyle();
            boldBaseStyle.cloneStyleFrom(baseStyle);
            Font boldFont = workbook.createFont();
            boldFont.setFontName(baseFont.getFontName());
            boldFont.setFontHeightInPoints(baseFont.getFontHeightInPoints());
            boldFont.setColor(baseFont.getColor());
            boldFont.setBold(true);
            boldBaseStyle.setFont(boldFont);

            CellStyle boldLeftAlignBaseStyle  = workbook.createCellStyle();
            boldLeftAlignBaseStyle.cloneStyleFrom(baseStyle);
            boldLeftAlignBaseStyle.setFont(boldFont);
            boldLeftAlignBaseStyle.setAlignment(HorizontalAlignment.LEFT);

            CellStyle boldItalicRightAlignBaseStyle  = workbook.createCellStyle();
            boldItalicRightAlignBaseStyle.cloneStyleFrom(baseStyle);
            Font boldItalicFont = workbook.createFont();
            boldItalicFont.setFontName(baseFont.getFontName());
            boldItalicFont.setFontHeightInPoints(baseFont.getFontHeightInPoints());
            boldItalicFont.setColor(baseFont.getColor());
            boldItalicFont.setBold(true);
            boldItalicFont.setItalic(true);
            boldItalicRightAlignBaseStyle.setFont(boldItalicFont);
            boldItalicRightAlignBaseStyle.setAlignment(HorizontalAlignment.RIGHT);

            for (DowntimeReportDto element : report) {
                Row row = sheet.createRow(currentRow);
                for (int i = 0; i <= 16; i++) {
                    Cell cell = row.createCell(ref.getCol() + i);
                    cell.setCellStyle(baseStyle);
                    if (Set.of(0, 1).contains(i)) {
                        row.getCell(i).setCellStyle(leftAlignBaseStyle);
                    }
                    Set<String> items = Set.of("1", "2", "3", "4", "5", "6.1", "6.2", "7");
                    String repNum = element.getRep_PP_NUM();
                    if (items.contains(element.getRep_PP_NUM())){
                        if (Set.of(0, 1).contains(i)){
                            cell.setCellStyle(boldLeftAlignBaseStyle);
                        } else {
                            cell.setCellStyle(boldBaseStyle);
                        }
                    }
                    if (Objects.equals(element.getRep_PP_NUM(), "") && i == 1){
                        row.getCell(i).setCellStyle(boldItalicRightAlignBaseStyle);
                    }
                    sheet.setActiveCell(cell.getAddress());
                }
                row.getCell(ref.getCol()).setCellValue(Objects.requireNonNullElse(element.getRep_PP_NUM(), ""));
                row.getCell(ref.getCol() + 1).setCellValue(Objects.requireNonNullElse(element.getNm(), ""));
                row.getCell(ref.getCol() + 2).setCellValue(Objects.requireNonNullElse(element.getEdizm(), ""));
                row.getCell(ref.getCol() + 3).setCellValue(Objects.requireNonNullElse(element.getVSEGO(), 0.0));
                row.getCell(ref.getCol() + 4).setCellValue(Objects.requireNonNullElse(element.getSNPH(), 0.0));
                row.getCell(ref.getCol() + 5).setCellValue(Objects.requireNonNullElse(element.getSN(), 0.0));
                row.getCell(ref.getCol() + 6).setCellValue(Objects.requireNonNullElse(element.getFN(), 0.0));
                row.getCell(ref.getCol() + 7).setCellValue(Objects.requireNonNullElse(element.getLN(), 0.0));
                row.getCell(ref.getCol() + 8).setCellValue(Objects.requireNonNullElse(element.getBN(), 0.0));
                row.getCell(ref.getCol() + 9).setCellValue(Objects.requireNonNullElse(element.getKN(), 0.0));
                row.getCell(ref.getCol() + 10).setCellValue(Objects.requireNonNullElse(element.getNSN(), 0.0));
                row.getCell(ref.getCol() + 11).setCellValue(Objects.requireNonNullElse(element.getSUBR_1(), 0.0));
                row.getCell(ref.getCol() + 12).setCellValue(Objects.requireNonNullElse(element.getSUBR_2(), 0.0));
                row.getCell(ref.getCol() + 13).setCellValue(Objects.requireNonNullElse(element.getSUBR_3(), 0.0));
                row.getCell(ref.getCol() + 14).setCellValue(Objects.requireNonNullElse(element.getSUPNP(), 0.0));
                row.getCell(ref.getCol() + 15).setCellValue(Objects.requireNonNullElse(element.getFUPNP(), 0.0));
                row.getCell(ref.getCol() + 16).setCellValue(Objects.requireNonNullElse(element.getUPNP(), 0.0));

                currentRow++;
            }
            String text = "";
            switch (type) {
                case 1018281:
                    text = "ЦА-320";
                    break;
                case 1019237:
                    text = "KENWORTH";
                    break;
                case 1018865:
                    text = "УНЦ";
                    break;
                case 1019385:
                    text = "УНП2";
                    break;
                case 1019540:
                    text = "MAN";
                    break;
            }
            String stringType;
            String title;
            if (type == 0) {
                stringType = " c применением всей спец.техники";
            } else {
                stringType = " c применением спец.техники " + text + ". ";
            }
            String stringZeh = "";
            switch (zeh) {
                case 1: stringZeh = "Цех по химизации технологических процессов №1"; break;
                case 2: stringZeh = "Цех по химизации технологических процессов №2"; break;
                case 3: stringZeh = "Цех по химизации технологических процессов №3"; break;
            }
            if (dateBegin.getYear() == dateEnd.getYear() && dateBegin.getMonth() == dateEnd.getMonth()){
                title = "за " + dateBegin.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale.Builder().setLanguage("ru").setRegion("RU").build()) + " " + dateBegin.getYear() + " г." + stringType + stringZeh;
            } else {
                title = "за период " + dateBegin.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale.Builder().setLanguage("ru").setRegion("RU").build()) + " " + dateBegin.getYear() + " г. - " + dateEnd.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale.Builder().setLanguage("ru").setRegion("RU").build()) + " " + dateEnd.getYear() + " г." + stringType + stringZeh;
            }
            getNamedCell(workbook, "RepMonth").setCellValue(title);
            workbook.setPrintArea(0,0, 16, 0, 14 + report.size());
            return workbook;
        }
    }

    public Workbook generateDowntimeReportNew(Integer zeh, LocalDateTime dateBegin, LocalDateTime dateEnd) throws IOException {
        try (InputStream templateStream = getClass().getResourceAsStream("/templates/tmpHimProstoi1.xls")){
            if (templateStream == null){
                throw new FileNotFoundException("Template not found");
            }
            Workbook workbook = new HSSFWorkbook(templateStream);

            Name bodyCell = workbook.getName("bodyALL");
            String bodyRef = bodyCell.getRefersToFormula();
            CellReference ref = new CellReference(bodyRef);
            Sheet sheet = workbook.getSheet("Вся спец.техника");
            List<DowntimeReportDto> report = exelReportRepository.findDowntimeReport(zeh, 0, dateBegin.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), dateEnd.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            sheet.shiftRows(ref.getRow() + 1, sheet.getLastRowNum(), report.size(), true, false);
            generateDowntimeReportByTech(workbook, sheet, ref, 0, zeh, dateBegin, dateEnd, report);

            bodyCell = workbook.getName("bodyMAN");
            bodyRef = bodyCell.getRefersToFormula();
            ref = new CellReference(bodyRef);
            sheet = workbook.getSheet("MAN");
            report = exelReportRepository.findDowntimeReport(zeh, 1019540, dateBegin.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), dateEnd.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            sheet.shiftRows(ref.getRow() + 1, sheet.getLastRowNum(), report.size(), true, false);
            generateDowntimeReportByTech(workbook, sheet, ref, 1019540, zeh, dateBegin, dateEnd, report);

            bodyCell = workbook.getName("bodyKW");
            bodyRef = bodyCell.getRefersToFormula();
            ref = new CellReference(bodyRef);
            sheet = workbook.getSheet("KENWORTH");
            report = exelReportRepository.findDowntimeReport(zeh, 1019237, dateBegin.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), dateEnd.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            sheet.shiftRows(ref.getRow() + 1, sheet.getLastRowNum(), report.size(), true, false);
            generateDowntimeReportByTech(workbook, sheet, ref, 1019237, zeh, dateBegin, dateEnd, report);

            bodyCell = workbook.getName("bodyUNP");
            bodyRef = bodyCell.getRefersToFormula();
            ref = new CellReference(bodyRef);
            sheet = workbook.getSheet("УНП2");
            report = exelReportRepository.findDowntimeReport(zeh, 1019385, dateBegin.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), dateEnd.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            sheet.shiftRows(ref.getRow() + 1, sheet.getLastRowNum(), report.size(), true, false);
            generateDowntimeReportByTech(workbook, sheet, ref, 1019385, zeh, dateBegin, dateEnd, report);

            bodyCell = workbook.getName("bodyUNC");
            bodyRef = bodyCell.getRefersToFormula();
            ref = new CellReference(bodyRef);
            sheet = workbook.getSheet("УНЦ");
            report = exelReportRepository.findDowntimeReport(zeh, 1018865, dateBegin.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), dateEnd.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            sheet.shiftRows(ref.getRow() + 1, sheet.getLastRowNum(), report.size(), true, false);
            generateDowntimeReportByTech(workbook, sheet, ref, 1018865, zeh, dateBegin, dateEnd, report);

            bodyCell = workbook.getName("bodyCA");
            bodyRef = bodyCell.getRefersToFormula();
            ref = new CellReference(bodyRef);
            sheet = workbook.getSheet("ЦА-320");
            report = exelReportRepository.findDowntimeReport(zeh, 1018281, dateBegin.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), dateEnd.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            sheet.shiftRows(ref.getRow() + 1, sheet.getLastRowNum(), report.size(), true, false);
            generateDowntimeReportByTech(workbook, sheet, ref, 1018281, zeh, dateBegin, dateEnd, report);

            workbook.setPrintArea(0,0, 16, 0, 14 + report.size());
            workbook.setPrintArea(1,0, 16, 0, 14 + report.size());
            workbook.setPrintArea(2,0, 16, 0, 14 + report.size());
            workbook.setPrintArea(3,0, 16, 0, 14 + report.size());
            workbook.setPrintArea(4,0, 16, 0, 14 + report.size());
            workbook.setPrintArea(5,0, 16, 0, 14 + report.size());
            return workbook;
        }
    }

    public void generateDowntimeReportByTech(Workbook workbook, Sheet sheet, CellReference ref, Integer type, Integer zeh, LocalDateTime dateBegin, LocalDateTime dateEnd, List<DowntimeReportDto> report){
        int currentRow = ref.getRow();
        CellStyle baseStyle = sheet.getRow(ref.getRow()).getCell(ref.getCol()).getCellStyle();
        baseStyle.setAlignment(HorizontalAlignment.CENTER);
        baseStyle.setBorderRight(BorderStyle.THIN);
        baseStyle.setBorderLeft(BorderStyle.THIN);
        baseStyle.setBorderBottom(BorderStyle.THIN);
        baseStyle.setBorderTop(BorderStyle.THIN);
        Font baseFont = workbook.getFontAt(baseStyle.getFontIndex());


        CellStyle leftAlignBaseStyle = workbook.createCellStyle();
        leftAlignBaseStyle.cloneStyleFrom(baseStyle);
        leftAlignBaseStyle.setAlignment(HorizontalAlignment.LEFT);

        CellStyle boldBaseStyle  = workbook.createCellStyle();
        boldBaseStyle.cloneStyleFrom(baseStyle);
        Font boldFont = workbook.createFont();
        boldFont.setFontName(baseFont.getFontName());
        boldFont.setFontHeightInPoints(baseFont.getFontHeightInPoints());
        boldFont.setColor(baseFont.getColor());
        boldFont.setBold(true);
        boldBaseStyle.setFont(boldFont);

        CellStyle boldLeftAlignBaseStyle  = workbook.createCellStyle();
        boldLeftAlignBaseStyle.cloneStyleFrom(baseStyle);
        boldLeftAlignBaseStyle.setFont(boldFont);
        boldLeftAlignBaseStyle.setAlignment(HorizontalAlignment.LEFT);

        CellStyle boldItalicRightAlignBaseStyle  = workbook.createCellStyle();
        boldItalicRightAlignBaseStyle.cloneStyleFrom(baseStyle);
        Font boldItalicFont = workbook.createFont();
        boldItalicFont.setFontName(baseFont.getFontName());
        boldItalicFont.setFontHeightInPoints(baseFont.getFontHeightInPoints());
        boldItalicFont.setColor(baseFont.getColor());
        boldItalicFont.setBold(true);
        boldItalicFont.setItalic(true);
        boldItalicRightAlignBaseStyle.setFont(boldItalicFont);
        boldItalicRightAlignBaseStyle.setAlignment(HorizontalAlignment.RIGHT);

        Set<String> items = Set.of("1", "2", "3", "4", "5", "6.1", "6.2", "7");
        for (DowntimeReportDto element : report) {
            Row row = sheet.createRow(currentRow);
            String repNum = element.getRep_PP_NUM();
            for (int i = 0; i <= 16; i++) {
                Cell cell = row.createCell(ref.getCol() + i);
                cell.setCellStyle(baseStyle);
                if (i == 0 || i == 1) {
                    row.getCell(i).setCellStyle(leftAlignBaseStyle);
                }
                if (items.contains(repNum)){
                    if (i == 0 || i == 1){
                        cell.setCellStyle(boldLeftAlignBaseStyle);
                    } else {
                        cell.setCellStyle(boldBaseStyle);
                    }
                }
                if (Objects.equals(repNum, "") && i == 1){
                    row.getCell(i).setCellStyle(boldItalicRightAlignBaseStyle);
                }
            }
            row.getCell(ref.getCol()).setCellValue(Objects.requireNonNullElse(element.getRep_PP_NUM(), ""));
            row.getCell(ref.getCol() + 1).setCellValue(Objects.requireNonNullElse(element.getNm(), ""));
            row.getCell(ref.getCol() + 2).setCellValue(Objects.requireNonNullElse(element.getEdizm(), ""));
            row.getCell(ref.getCol() + 3).setCellValue(Objects.requireNonNullElse(element.getVSEGO(), 0.0));
            row.getCell(ref.getCol() + 4).setCellValue(Objects.requireNonNullElse(element.getSNPH(), 0.0));
            row.getCell(ref.getCol() + 5).setCellValue(Objects.requireNonNullElse(element.getSN(), 0.0));
            row.getCell(ref.getCol() + 6).setCellValue(Objects.requireNonNullElse(element.getFN(), 0.0));
            row.getCell(ref.getCol() + 7).setCellValue(Objects.requireNonNullElse(element.getLN(), 0.0));
            row.getCell(ref.getCol() + 8).setCellValue(Objects.requireNonNullElse(element.getBN(), 0.0));
            row.getCell(ref.getCol() + 9).setCellValue(Objects.requireNonNullElse(element.getKN(), 0.0));
            row.getCell(ref.getCol() + 10).setCellValue(Objects.requireNonNullElse(element.getNSN(), 0.0));
            row.getCell(ref.getCol() + 11).setCellValue(Objects.requireNonNullElse(element.getSUBR_1(), 0.0));
            row.getCell(ref.getCol() + 12).setCellValue(Objects.requireNonNullElse(element.getSUBR_2(), 0.0));
            row.getCell(ref.getCol() + 13).setCellValue(Objects.requireNonNullElse(element.getSUBR_3(), 0.0));
            row.getCell(ref.getCol() + 14).setCellValue(Objects.requireNonNullElse(element.getSUPNP(), 0.0));
            row.getCell(ref.getCol() + 15).setCellValue(Objects.requireNonNullElse(element.getFUPNP(), 0.0));
            row.getCell(ref.getCol() + 16).setCellValue(Objects.requireNonNullElse(element.getUPNP(), 0.0));

            currentRow++;
        }

        String text = "";
        switch (type) {
            case 1018281:
                text = "ЦА-320";
                break;
            case 1019237:
                text = "KENWORTH";
                break;
            case 1018865:
                text = "УНЦ";
                break;
            case 1019385:
                text = "УНП2";
                break;
            case 1019540:
                text = "MAN";
                break;
        }
        String stringType;
        String title;
        if (type == 0) {
            stringType = " c применением всей спец.техники";
        } else {
            stringType = " c применением спец.техники " + text + ". ";
        }
        String stringZeh = "";
        switch (zeh) {
            case 1: stringZeh = "Цех по химизации технологических процессов №1"; break;
            case 2: stringZeh = "Цех по химизации технологических процессов №2"; break;
            case 3: stringZeh = "Цех по химизации технологических процессов №3"; break;
        }
        if (dateBegin.getYear() == dateEnd.getYear() && dateBegin.getMonth() == dateEnd.getMonth()){
            title = "за " + dateBegin.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale.Builder().setLanguage("ru").setRegion("RU").build()) + " " + dateBegin.getYear() + " г." + stringType + stringZeh;
        } else {
            title = "за период " + dateBegin.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale.Builder().setLanguage("ru").setRegion("RU").build()) + " " + dateBegin.getYear() + " г. - " + dateEnd.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale.Builder().setLanguage("ru").setRegion("RU").build()) + " " + dateEnd.getYear() + " г." + stringType + stringZeh;
        }
        switch (type) {
            case 0:
                getNamedCell(workbook, "RepMonthALL").setCellValue(title);
                break;
            case 1018281:
                getNamedCell(workbook, "RepMonthCA").setCellValue(title);
                break;
            case 1019237:
                getNamedCell(workbook, "RepMonthKW").setCellValue(title);
                break;
            case 1018865:
                getNamedCell(workbook, "RepMonthUNC").setCellValue(title);
                break;
            case 1019385:
                getNamedCell(workbook, "RepMonthUNP").setCellValue(title);
                break;
            case 1019540:
                getNamedCell(workbook, "RepMonthMAN").setCellValue(title);
                break;
        }
    }

    public Workbook generateHimReport(Integer zeh, Integer type, LocalDateTime dateBegin, LocalDateTime dateEnd) throws IOException {
        List<Object[]> report = exelReportRepository.findHimReport(zeh, type, dateBegin, dateEnd);
        List<List<Object>> data = report.stream().map(Arrays::asList).collect(Collectors.toList());
        try (InputStream templateStream = getClass().getResourceAsStream("/templates/tmpHim.xlsx")){
            if (templateStream == null){
                throw new FileNotFoundException("Template not found");
            }
            Workbook workbook = WorkbookFactory.create(templateStream);
            Name bodyCell = workbook.getName("body");
            String bodyRef = bodyCell.getRefersToFormula().replace("'", "").split("!")[1];
            CellReference ref = new CellReference(bodyRef);
            Sheet sheet = workbook.getSheetAt(0);
            CellStyle baseStyle = sheet.getRow(ref.getRow()).getCell(ref.getCol()).getCellStyle();
            baseStyle.setWrapText(true);
            baseStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 10);
            font.setFontName("arial");
            baseStyle.setFont(font);
            Font baseFont = workbook.getFontAt(baseStyle.getFontIndex());
            int currentRow = ref.getRow();
            int currentCol = ref.getCol();
            sheet.shiftRows(currentRow + 1, sheet.getLastRowNum(), report.size() - 1, true, false);
            for (List<Object> rowData : data) {
                Row row = sheet.createRow(currentRow);
                int currentColIndex = currentCol;
                for (Object cellValue : rowData) {
                    Cell cell = row.createCell(currentColIndex);
                    cell.setCellStyle(baseStyle);

                    if (cellValue instanceof Number) {
                        cell.setCellValue(((Number) cellValue).doubleValue());
                    } else if (cellValue instanceof Boolean) {
                        cell.setCellValue((Boolean) cellValue);
                    } else if (cellValue != null) {
                        cell.setCellValue(cellValue.toString());
                    }

                    currentColIndex++;
                }
                currentRow++;
            }
            int firstDataRowExcel = ref.getRow() + 1;
            int lastDataRowExcel = currentRow - 1;
            Row avgRow = sheet.getRow(currentRow);
            for (int col = 17; col <= 40; col++) {
                Cell cell = avgRow.createCell(col);
                cell.setCellStyle(baseStyle);
                String colLetter = CellReference.convertNumToColString(col);
                String formula = String.format("AVERAGE(%s%d:%s%d)", colLetter, firstDataRowExcel, colLetter, lastDataRowExcel + 1);
                cell.setCellFormula(formula);
            }
            for (int col = 78; col <= 126; col++) {
                Cell cell = avgRow.createCell(col);
                cell.setCellStyle(baseStyle);
                String colLetter = CellReference.convertNumToColString(col);
                String formula = String.format("AVERAGE(%s%d:%s%d)", colLetter, firstDataRowExcel, colLetter, lastDataRowExcel + 1);
                cell.setCellFormula(formula);
            }
            currentRow++;
            Row sumRow = sheet.getRow(currentRow);
            for (int col = 17; col <= 40; col++) {
                Cell cell = sumRow.createCell(col);
                cell.setCellStyle(baseStyle);
                String colLetter = CellReference.convertNumToColString(col);
                String formula = String.format("SUM(%s%d:%s%d)", colLetter, firstDataRowExcel, colLetter, lastDataRowExcel + 1);
                cell.setCellFormula(formula);
            }
            for (int col = 78; col <= 126; col++) {
                Cell cell = sumRow.createCell(col);
                cell.setCellStyle(baseStyle);
                String colLetter = CellReference.convertNumToColString(col);
                String formula = String.format("SUM(%s%d:%s%d)", colLetter, firstDataRowExcel, colLetter, lastDataRowExcel + 1);
                cell.setCellFormula(formula);
            }
//            workbook.setPrintArea(0,0, data.get(0).size() - 1, 0, currentRow);
            return workbook;
        } catch (FileNotFoundException e) {
            log.error("Ошибка конфигурации сервера: отсутствует шаблон отчета. {}", e.getMessage());
            throw new ReportGenerationException("Внутренняя ошибка сервера: отсутствует файл шаблона отчета");

        } catch (EncryptedDocumentException | IOException e) {
            log.error("Критическая ошибка Apache POI при чтении/сборке Excel: {}", e.getMessage(), e);
            throw new ReportGenerationException("Не удалось сформировать структуру Excel-документа");

        } catch (Exception e) {
            log.error("Непредвиденная ошибка при генерации отчета: {}", e.getMessage(), e);
            throw new ReportGenerationException("Произошла неизвестная ошибка при сборке отчета");
        }

    }

    public Workbook generateNormZReport(NormZReportRequest param) throws IOException {
        List<NormZReportDto> report = exelReportRepository.findNormReport(param.getZeh(), param.getType(), param.getBrigade(), param.getDateBegin(), param.getDateEnd());
        try (InputStream templateStream = getClass().getResourceAsStream("/templates/tmphimnormz.xls")){
            if (templateStream == null){
                throw new FileNotFoundException("Template not found");
            }
            Workbook workbook = new HSSFWorkbook(templateStream);
            Name bodyCell = workbook.getName("body");
            String bodyRef = bodyCell.getRefersToFormula().replace("'", "").split("!")[1];
            CellReference ref = new CellReference(bodyRef);
            Sheet sheet = workbook.getSheet("Лист1");
            int currentRow = ref.getRow();
            DataFormat df = workbook.createDataFormat();
            CellStyle baseStyle = sheet.getRow(ref.getRow()).getCell(ref.getCol()).getCellStyle();
            //baseStyle.setDataFormat(df.getFormat("0.00"));
            Font baseFont = workbook.getFontAt(baseStyle.getFontIndex());

            CellStyle digitalBaseStyle = workbook.createCellStyle();
            digitalBaseStyle.cloneStyleFrom(baseStyle);
            digitalBaseStyle.setDataFormat(df.getFormat("0.00"));

            CellStyle leftAlignBaseStyle = workbook.createCellStyle();
            leftAlignBaseStyle.cloneStyleFrom(baseStyle);
            leftAlignBaseStyle.setAlignment(HorizontalAlignment.LEFT);
            leftAlignBaseStyle.setWrapText(true);

            Font underLineFont = workbook.createFont();
            underLineFont.setUnderline(Font.U_SINGLE);
            underLineFont.setFontName(baseFont.getFontName());
            underLineFont.setFontHeightInPoints(baseFont.getFontHeightInPoints());
            underLineFont.setColor(baseFont.getColor());
            CellStyle underLineStyle = workbook.createCellStyle();
            underLineStyle.cloneStyleFrom(baseStyle);
            underLineStyle.setAlignment(HorizontalAlignment.CENTER);
            underLineStyle.setFont(underLineFont);
            underLineStyle.setBorderBottom(BorderStyle.NONE);
            underLineStyle.setBorderTop(BorderStyle.NONE);
            underLineStyle.setBorderLeft(BorderStyle.NONE);
            underLineStyle.setBorderRight(BorderStyle.NONE);

            List<String> customers = report.stream().map(NormZReportDto::getNm).distinct().collect(Collectors.toList());
            sheet.shiftRows(currentRow + 1, sheet.getLastRowNum(), report.size() + customers.size() + 1, true, false);
            int customerCount = 1;
            Row topRow = sheet.createRow(currentRow);
            String text = "";
            switch (param.getType()) {
                case 0:
                    text = "Химические обработки скважин с применением всей спец.техники";
                    break;
                case 1018281:
                    text = "Химические обработки скважин с применением установки ЦА-320";
                    break;
                case 1019237:
                    text = "Химические обработки скважин с применением установки KENWORTH";
                    break;
                case 1018865:
                    text = "Химические обработки скважин с применением установки УНЦ";
                    break;
                case 1019385:
                    text = "Химические обработки скважин с применением установки УНП2";
                    break;
                case 1019540:
                    text = "Химические обработки скважин с применением установки MAN";
                    break;
            }
            for (int i = 0; i <= 9; i++) {
                Cell cell = topRow.createCell(ref.getCol() + i);
                Set<Integer> items = Set.of(1,2);
                if (items.contains(i)) {
                    cell.setCellStyle(leftAlignBaseStyle);
                } else {
                    cell.setCellStyle(baseStyle);
                }
            }
            topRow.getCell(ref.getCol() + 2).setCellValue(text);
            currentRow++;
            for (String customer: customers) {
                Row customerRow = sheet.createRow(currentRow);
                List<NormZReportDto> obrab = report.stream().filter(item -> item.getNm().equals(customer)).collect(Collectors.toList());
                for (int i = 0; i <= 9; i++) {
                    Cell cell = customerRow.createCell(ref.getCol() + i);
                    Set<Integer> items = Set.of(1,2);
                    if (items.contains(i)) {
                        cell.setCellStyle(leftAlignBaseStyle);
                    } else {
                        cell.setCellStyle(baseStyle);
                    }
                }
                customerRow.getCell(ref.getCol()).setCellValue(customerCount);
                customerRow.getCell(ref.getCol() + 1).setCellValue("-");
                customerRow.getCell(ref.getCol() + 2).setCellValue(customer);
                customerRow.getCell(ref.getCol() + 3).setCellValue("-");
                customerRow.getCell(ref.getCol() + 4).setCellValue("-");
                customerRow.getCell(ref.getCol() + 5).setCellValue("-");
                customerRow.getCell(ref.getCol() + 6).setCellValue("-");
                customerRow.getCell(ref.getCol() + 7).setCellValue("-");
                customerRow.getCell(ref.getCol() + 8).setCellValue("-");
                customerRow.getCell(ref.getCol() + 9).setCellValue("-");
                currentRow++;
                int obrabCount = 0;
                for (NormZReportDto element : obrab) {
                    obrabCount++;
                    Row row = sheet.createRow(currentRow);
                    for (int i = 0; i <= 9; i++) {
                        Cell cell = row.createCell(ref.getCol() + i);
                        Set<Integer> items = Set.of(1,2);
                        Set<Integer> digit = Set.of(5,6,7,8,9);
//                        if (items.contains(i)) {
//                            cell.setCellStyle(leftAlignBaseStyle);
//                        } else {
//                            cell.setCellStyle(baseStyle);
//                        }
                        if (items.contains(i)) {
                            cell.setCellStyle(leftAlignBaseStyle);
                        } else if (digit.contains(i)) {
                            cell.setCellStyle(digitalBaseStyle);
                        } else {
                            cell.setCellStyle(baseStyle);
                        }
                    }
                    row.getCell(ref.getCol()).setCellValue(customerCount + "." + obrabCount);
                    row.getCell(ref.getCol() + 1).setCellValue(Objects.requireNonNullElse(element.getSource(), ""));
                    row.getCell(ref.getCol() + 2).setCellValue("Химическая обработка скважин " + element.getObrab() + " к." + element.getKust() + " скв." + element.getSkv());
                    row.getCell(ref.getCol() + 3).setCellValue("скв.-опер.");
                    row.getCell(ref.getCol() + 4).setCellValue(1);
                    row.getCell(ref.getCol() + 5).setCellValue(Objects.requireNonNullElse(element.getVolume(), 0.0));
                    row.getCell(ref.getCol() + 6).setCellValue(Objects.requireNonNullElse(element.getVolume(), 0.0));
                    row.getCell(ref.getCol() + 7).setCellValue(1);
                    row.getCell(ref.getCol() + 8).setCellValue(Objects.requireNonNullElse(element.getVolumeV(), 0.0));
                    row.getCell(ref.getCol() + 9).setCellValue(Objects.requireNonNullElse(element.getFactV(), 0.0));
                    currentRow++;
                }
                customerCount++;
            }
            Row SummaryRow = sheet.createRow(currentRow);
            for (int i = 0; i <= 9; i++) {
                Cell cell = SummaryRow.createCell(ref.getCol() + i);
                Set<Integer> items = Set.of(1,2);
                Set<Integer> digit = Set.of(5,6,7,8,9);
                if (items.contains(i)) {
                    cell.setCellStyle(leftAlignBaseStyle);
                } else if (digit.contains(i)) {
                    cell.setCellStyle(digitalBaseStyle);
                } else {
                    cell.setCellStyle(baseStyle);
                }
            }
            SummaryRow.getCell(ref.getCol()).setCellValue("-");
            SummaryRow.getCell(ref.getCol() + 1).setCellValue("-");
            SummaryRow.getCell(ref.getCol() + 2).setCellValue("Итого:");
            SummaryRow.getCell(ref.getCol() + 3).setCellValue("-");
            SummaryRow.getCell(ref.getCol() + 4).setCellValue(report.size());
            SummaryRow.getCell(ref.getCol() + 5).setCellValue("-");
            SummaryRow.getCell(ref.getCol() + 6).setCellValue(report.stream().mapToDouble(NormZReportDto::getVolume).sum());
            SummaryRow.getCell(ref.getCol() + 7).setCellValue(report.size());
            SummaryRow.getCell(ref.getCol() + 8).setCellValue(report.stream().mapToDouble(NormZReportDto::getVolumeV).sum());
            SummaryRow.getCell(ref.getCol() + 9).setCellValue(report.stream().mapToDouble(NormZReportDto::getFactV).sum());
            currentRow++;
            getNamedCell(workbook, "Fakt_VR").setCellValue(report.stream().mapToDouble(NormZReportDto::getFactV).sum());
            getNamedCell(workbook, "procent").setCellValue(report.stream().mapToDouble(NormZReportDto::getVolumeV).sum() / report.stream().mapToDouble(NormZReportDto::getFactV).sum() * 100);
            getNamedCell(workbook, "str_podr").setCellValue("Управление «Сургутнефтепромхим»");
            String ceh = "";
            switch (param.getZeh()) {
                case 0: ceh = "По управлению"; break;
                case 1: ceh = "Цех по химизации технологических процессов №1"; break;
                case 2: ceh = "Цех по химизации технологических процессов №2"; break;
                case 3: ceh = "Цех по химизации технологических процессов №3"; break;
            }
            getNamedCell(workbook, "ceh").setCellValue(ceh);
            getNamedCell(workbook, "vid_rabot").setCellValue("химическая обработка скважин");
            if (param.getBrigade() == 0){
                getNamedCell(workbook, "ZVENO").setCellValue("Все звенья");
            } else {
                getNamedCell(workbook, "ZVENO").setCellValue(brigadeRepository.getReferenceById(param.getBrigade().longValue()).getNm());
            }
            getNamedCell(workbook, "UtvDoljnost").setCellValue(param.getUtv());
            getNamedCell(workbook, "UtvFIO").setCellValue(param.getUtvFIO());
            getNamedCell(workbook, "Naryad").setCellValue("Нормированное задание № " + param.getNumber());
            getNamedCell(workbook, "Raboty_S").setCellValue(param.getSubmitted());
            getNamedCell(workbook, "Raboty_S_fam").setCellValue(param.getSubmittedFIO());
            getNamedCell(workbook, "Raboty_P").setCellValue(param.getAccepted());
            getNamedCell(workbook, "Raboty_P_fam").setCellValue(param.getAcceptedFIO());
            getNamedCell(workbook, "ruk_dogn").setCellValue(param.getSupervisor());
            getNamedCell(workbook, "ruk_fam").setCellValue(param.getSupervisorFIO());
            getNamedCell(workbook, "otv_isp_dolgn").setCellValue(param.getStandardSetter());
            getNamedCell(workbook, "otv_isp_fam").setCellValue(param.getStandardSetterFIO());
            getNamedCell(workbook, "r_org_tr_dolg").setCellValue(param.getGroupLeader());
            getNamedCell(workbook, "r_org_tr_fam").setCellValue(param.getGroupLeaderFIO());
            getNamedCell(workbook, "data_sost").setCellValue("Дата составления " + LocalDate.now().format(DateTimeFormatter.ofPattern("\" dd \" MMMM yyyy г.")));
            getNamedCell(workbook, "shifr").setCellValue(param.getKod());
            getNamedCell(workbook, "Chel_kolvo").setCellValue(param.getPeopleCnt());
            if (param.getPeriod() != null) {
            StringBuilder period = new StringBuilder(param.getPeriod().getMonth().getDisplayName(TextStyle.FULL_STANDALONE,   new Locale.Builder()
                                                                                                                                        .setLanguage("ru")
                                                                                                                                        .setRegion("RU")
                                                                                                                                        .build()));
            while (period.length() < 30) {
                period.insert(0, " ");
                if (period.length() < 30)
                    period.insert(period.length(), " ");
            }
                period.append(" ").append(param.getPeriod().getYear() % 100).append(" г.");
            getNamedCell(workbook, "ZaMesyac").setCellValue(period.toString());
            getNamedCell(workbook, "ZaMesyac").setCellStyle(underLineStyle);
            }
            return workbook;
        } catch (FileNotFoundException e) {
            log.error("Ошибка конфигурации сервера: отсутствует шаблон отчета. {}", e.getMessage());
            throw new ReportGenerationException("Внутренняя ошибка сервера: отсутствует файл шаблона отчета");

        } catch (EncryptedDocumentException | IOException e) {
            log.error("Критическая ошибка Apache POI при чтении/сборке Excel: {}", e.getMessage(), e);
            throw new ReportGenerationException("Не удалось сформировать структуру Excel-документа");

        } catch (Exception e) {
            log.error("Непредвиденная ошибка при генерации отчета: {}", e.getMessage(), e);
            throw new ReportGenerationException("Произошла неизвестная ошибка при сборке отчета");
        }


    }

    public Workbook generateInfReport(InfReportRequest value) throws IOException {
        try (InputStream templateStream = getClass().getResourceAsStream("/templates/tmpinformhim1.xls")) {
            if (templateStream == null) {
                throw new FileNotFoundException("Template not found");
            }
            Workbook workbook = WorkbookFactory.create(templateStream);
            Name bodyCell = workbook.getName("body");
            String bodyRef = bodyCell.getRefersToFormula().replace("'", "").split("!")[1];
            CellReference ref = new CellReference(bodyRef);
            Sheet sheet = workbook.getSheetAt(0);
            int currentRow = ref.getRow();

            CellStyle baseStyle = sheet.getRow(ref.getRow()).getCell(ref.getCol()).getCellStyle();
            baseStyle.setBorderRight(BorderStyle.THIN);
            baseStyle.setBorderLeft(BorderStyle.THIN);
            baseStyle.setBorderBottom(BorderStyle.THIN);
            baseStyle.setBorderTop(BorderStyle.THIN);
            baseStyle.setAlignment(HorizontalAlignment.CENTER);
            Font baseFont = workbook.getFontAt(baseStyle.getFontIndex());
            baseFont.setFontName("XO Oriel");

            CellStyle borderlessBaseStyle  = workbook.createCellStyle();
            borderlessBaseStyle.cloneStyleFrom(baseStyle);
            borderlessBaseStyle.setBorderRight(BorderStyle.NONE);
            borderlessBaseStyle.setBorderLeft(BorderStyle.NONE);
            borderlessBaseStyle.setBorderBottom(BorderStyle.NONE);
            borderlessBaseStyle.setBorderTop(BorderStyle.NONE);
            borderlessBaseStyle.setAlignment(HorizontalAlignment.LEFT);
            Font borderlessFont = workbook.createFont();
            borderlessFont.setFontHeightInPoints((short) 13);
            borderlessBaseStyle.setFont(borderlessFont);
            if (value.getType() == 0){
                currentRow = generateInfBlock(workbook, baseStyle, currentRow, 0, value.getDateBegin(), value.getDateEnd());
            }
            List<Integer> typeList = exelReportRepository.getTypeTech(value.getType(), value.getDateBegin(), value.getDateEnd());
            for (Integer typeTech: typeList ){
                currentRow = generateInfBlock(workbook, baseStyle, currentRow, typeTech, value.getDateBegin(), value.getDateEnd());
            }

            Map<String, Object> properties = new HashMap<>();
            short formatId = workbook.createDataFormat().getFormat("#,##0.00");
            properties.put(CellUtil.DATA_FORMAT, formatId);

            for (int r = ref.getRow(); r <= currentRow - 1; r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    row = sheet.createRow(r);
                }

                for (int c = ref.getCol() + 27; c <= ref.getCol() + 41; c++) {
                    Cell cell = row.getCell(c);
                    CellUtil.setCellStyleProperties(cell, properties);
                }

                for (int c = ref.getCol() + 65; c <= ref.getCol() + 79; c++) {
                    Cell cell = row.getCell(c);
                    CellUtil.setCellStyleProperties(cell, properties);
                }
            }

            properties = Map.of(
                    CellUtil.ALIGNMENT, HorizontalAlignment.CENTER,
                    CellUtil.VERTICAL_ALIGNMENT, VerticalAlignment.BOTTOM
            );
            if (value.getPeriod() != null){
                getNamedCell(workbook, "ZaMesyac").setCellValue("за " + value.getPeriod().getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale.Builder().setLanguage("ru").setRegion("RU").build()) + " " + value.getPeriod().getYear() + " г." );
                CellUtil.setCellStyleProperties(getNamedCell(workbook, "ZaMesyac"), properties);
            }

            shiftNamedCell(workbook, "DoljnostZam", currentRow - ref.getRow());
            shiftNamedCell(workbook, "FIOZam", currentRow - ref.getRow());
            shiftNamedCell(workbook, "DoljnostNach", currentRow - ref.getRow());
            shiftNamedCell(workbook, "FIONach", currentRow - ref.getRow());
            shiftNamedCell(workbook, "DoljnostOTIZ", currentRow - ref.getRow());
            shiftNamedCell(workbook, "FIOOTIZ", currentRow - ref.getRow());
            shiftNamedCell(workbook, "DoljnostIsp", currentRow - ref.getRow());
            shiftNamedCell(workbook, "FIOIsp", currentRow - ref.getRow());
            shiftNamedCell(workbook, "TELIsp", currentRow - ref.getRow());

            for (int i = 0; i<=13; i++){
                Row row = sheet.createRow(currentRow++);
                createRangeCells(row, (int) ref.getCol(), 47, borderlessBaseStyle);
            }

            getNamedCell(workbook, "DoljnostZam").setCellValue(value.getDepHeadOfDep());
            getNamedCell(workbook, "FIOZam").setCellValue(value.getDepHeadOfDepFIO());

            getNamedCell(workbook, "DoljnostNach").setCellValue(value.getHeadEco());
            getNamedCell(workbook, "FIONach").setCellValue(value.getHeadEcoFIO());

            getNamedCell(workbook, "DoljnostOTIZ").setCellValue(value.getHeadSel());
            getNamedCell(workbook, "FIOOTIZ").setCellValue(value.getHeadSelFIO());

            getNamedCell(workbook, "DoljnostIsp").setCellValue(value.getExecutor());
            getNamedCell(workbook, "FIOIsp").setCellValue(value.getExecutorFIO());

            workbook.setPrintArea(0,0, 79, 0, currentRow + 11);

            return workbook;

        } catch (FileNotFoundException e) {
            log.error("Ошибка конфигурации сервера: отсутствует шаблон отчета. {}", e.getMessage());
            throw new ReportGenerationException("Внутренняя ошибка сервера: отсутствует файл шаблона отчета");

        } catch (EncryptedDocumentException | IOException e) {
            log.error("Критическая ошибка Apache POI при чтении/сборке Excel: {}", e.getMessage(), e);
            throw new ReportGenerationException("Не удалось сформировать структуру Excel-документа");

        } catch (Exception e) {
            log.error("Непредвиденная ошибка при генерации отчета: {}", e.getMessage(), e);
            throw new ReportGenerationException("Произошла неизвестная ошибка при сборке отчета");
        }

    }

    public int generateInfBlock(Workbook workbook, CellStyle baseStyle, int currentRow, int type, LocalDateTime dateBegin, LocalDateTime dateEnd){
        Sheet sheet = workbook.getSheetAt(0);
        Name bodyCell = workbook.getName("body");
        HSSFPalette palette = ((HSSFWorkbook) workbook).getCustomPalette();
        String bodyRef = bodyCell.getRefersToFormula().replace("'", "").split("!")[1];
        CellReference ref = new CellReference(bodyRef);

        Font baseFont = workbook.getFontAt(baseStyle.getFontIndex());

        CellStyle boldBaseStyle  = workbook.createCellStyle();
        boldBaseStyle.cloneStyleFrom(baseStyle);
        Font boldFont = workbook.createFont();
        boldFont.setFontName(baseFont.getFontName());
        boldFont.setFontHeightInPoints(baseFont.getFontHeightInPoints());
        boldFont.setColor(baseFont.getColor());
        boldFont.setBold(true);
        boldBaseStyle.setFont(boldFont);

        CellStyle boldZehStyle = workbook.createCellStyle();
        boldZehStyle.cloneStyleFrom(boldBaseStyle);
        boldZehStyle.setAlignment(HorizontalAlignment.LEFT);
        palette.setColorAtIndex(
                IndexedColors.GREY_25_PERCENT.getIndex(),
                (byte) 216, (byte) 228, (byte) 188
        );
        boldZehStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        boldZehStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle boldMethodStyle = workbook.createCellStyle();
        boldMethodStyle.cloneStyleFrom(boldBaseStyle);
        boldMethodStyle.setAlignment(HorizontalAlignment.LEFT);
        palette.setColorAtIndex(
                IndexedColors.GREY_40_PERCENT.getIndex(),
                (byte) 255, (byte) 255, (byte) 204
        );
        boldMethodStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        boldMethodStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle boldTotalStyle = workbook.createCellStyle();
        boldTotalStyle.cloneStyleFrom(boldBaseStyle);
        palette.setColorAtIndex(
                IndexedColors.GREY_50_PERCENT.getIndex(),
                (byte) 217, (byte) 217, (byte) 217
        );
        boldTotalStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        boldTotalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        List<SelectDto> zehList;
        if (type != 0){
            String text = "";
            switch (type) {
                case 1018281:
                    text = "ЦА-320";
                    break;
                case 1019237:
                    text = "KENWORTH";
                    break;
                case 1018865:
                    text = "УНЦ";
                    break;
                case 1019385:
                    text = "УНП2";
                    break;
                case 1019540:
                    text = "MAN";
                    break;
            }
            Row row = sheet.createRow(currentRow);
            createRangeCells(row, (int) ref.getCol(), 79, boldZehStyle);
            Cell cell = row.getCell(ref.getCol());
            cell.setCellValue(text);
            currentRow++;
            zehList = exelReportRepository.getZehNew(type, dateBegin.with(TemporalAdjusters.firstDayOfYear()), dateEnd);
        } else {
            zehList = new ArrayList<>();
            zehList.add(new SelectDto(0L, "ПО УПРАВЛЕНИЮ"));
        }
        for (SelectDto zeh: zehList){
            Row row;
            Cell cell;

            if (!Objects.equals(zeh.getNm(), "")){
                row = sheet.createRow(currentRow);
                createRangeCells(row, (int) ref.getCol(), 79, boldZehStyle);
                cell = row.getCell(ref.getCol());
                cell.setCellValue(zeh.getNm());
                currentRow++;
            }
            int rowCounter = 1;
            List<MethodDto> methods = exelReportRepository.getMethodNew(type, zeh.getId(), dateBegin.with(TemporalAdjusters.firstDayOfYear()), dateEnd);
            for (MethodDto method : methods) {
                Row methodRow = sheet.createRow(currentRow);
                createRangeCells(methodRow, (int) ref.getCol(), 79, boldMethodStyle);
                cell = methodRow.getCell(ref.getCol());
                cell.setCellValue(method.getName());
                currentRow++;
                List<ObrabDto> obrabList = exelReportRepository.getObrabNew(type, method.getId(), "", zeh.getId(), dateBegin.with(TemporalAdjusters.firstDayOfYear()), dateEnd);
                for (ObrabDto obrab : obrabList) {
                    int excelRowIndex = currentRow + 1;
                    Row obrabRow = sheet.createRow(currentRow);
                    createRangeCells(obrabRow, (int) ref.getCol(), 79, baseStyle);
                    ObrabDto skvCount = exelReportRepository.getObrabNew(type, method.getId(), obrab.getKey_obrab(), zeh.getId(), dateBegin, dateEnd).stream().findFirst().orElse(null);
                    obrabRow.getCell(ref.getCol()).setCellValue(rowCounter);
                    obrabRow.getCell(ref.getCol() + 1).setCellValue(obrab.getName_obrab());
                    Map<String, Object> properties = Map.of(
                            CellUtil.ALIGNMENT, HorizontalAlignment.LEFT,
                            CellUtil.VERTICAL_ALIGNMENT, VerticalAlignment.TOP
                    );
                    CellUtil.setCellStyleProperties(obrabRow.getCell(ref.getCol() + 1), properties);
                    if (skvCount != null) {
                        Optional.ofNullable(skvCount.getKol_vo_skv()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 5).setCellValue(v), () -> {});
                        Optional.ofNullable(skvCount.getPriem_120_300()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 9).setCellValue(v), () -> {});
                        Optional.ofNullable(skvCount.getPriem_301_480()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 10).setCellValue(v), () -> {});
                        Optional.ofNullable(skvCount.getPriem_481_660()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 11).setCellValue(v), () -> {});
                        Optional.ofNullable(skvCount.getPriem_661_840()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 12).setCellValue(v), () -> {});
                        Optional.ofNullable(skvCount.getPriem_841()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 13).setCellValue(v), () -> {});
                        Optional.ofNullable(skvCount.getPriem_0_180()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 14).setCellValue(v), () -> {});
                        Optional.ofNullable(skvCount.getPriem_181_300()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 15).setCellValue(v), () -> {});
                        Optional.ofNullable(skvCount.getPriem_301_450()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 16).setCellValue(v), () -> {});
                        Optional.ofNullable(skvCount.getPriem_451_600()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 17).setCellValue(v), () -> {});
                        Optional.ofNullable(skvCount.getPriem_601_750()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 18).setCellValue(v), () -> {});
                        Optional.ofNullable(skvCount.getPriem_751()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 19).setCellValue(v), () -> {});

                        obrabRow.getCell(ref.getCol() + 20).setCellFormula(String.format("F%d-E%d", excelRowIndex, excelRowIndex));
                    }
                    PriemDto priem = exelReportRepository.getPriem(type, method.getId(), obrab.getKey_obrab(), zeh.getId(), dateBegin, dateEnd);
                    Optional.ofNullable(priem.getPlan()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 6).setCellValue(v), () -> {});
                    Optional.ofNullable(priem.getAvg()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 7).setCellValue(v), () -> {});
                    obrabRow.getCell(ref.getCol() + 8).setCellFormula(String.format("H%d-G%d", excelRowIndex, excelRowIndex));

                    PZRDto PZRMonth = exelReportRepository.getPZRNew(type, method.getId(), obrab.getKey_obrab(), zeh.getId(), dateBegin, dateEnd);
                    if (PZRMonth != null) {
                        Optional.ofNullable(PZRMonth.getBase()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 22).setCellValue(v), () -> {});
                        obrabRow.getCell(ref.getCol() + 23).setCellFormula(String.format("W%d-V%d", excelRowIndex, excelRowIndex));
                        Optional.ofNullable(PZRMonth.getSkv()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 25).setCellValue(v), () -> {});
                        obrabRow.getCell(ref.getCol() + 26).setCellFormula(String.format("Z%d-Y%d", excelRowIndex, excelRowIndex));
                    }

                    Double waterVolume = exelReportRepository.getWaterVolume(type, method.getId(), obrab.getKey_obrab(), zeh.getId(), dateBegin, dateEnd);
                    Optional.ofNullable(waterVolume).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 28).setCellValue(v), () -> {});
                    obrabRow.getCell(ref.getCol() + 29).setCellFormula(String.format("AC%d-AB%d", excelRowIndex, excelRowIndex));

                    DurationDto durationMonth = exelReportRepository.getDurationNew(type, method.getId(), obrab.getKey_obrab(), zeh.getId(), dateBegin, dateEnd);
                    if (durationMonth != null) {
                        Optional.ofNullable(durationMonth.getNormaSum()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 30).setCellValue(v), () -> {});
                        Optional.ofNullable(durationMonth.getFactSum()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 31).setCellValue(v), () -> {});
                        obrabRow.getCell(ref.getCol() + 32).setCellFormula(String.format("AF%d-AE%d", excelRowIndex, excelRowIndex));
                        Optional.ofNullable(durationMonth.getNorma()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 33).setCellValue(v), () -> {});
                        Optional.ofNullable(durationMonth.getNorma_f()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 34).setCellValue(v), () -> {});
                        obrabRow.getCell(ref.getCol() + 35).setCellFormula(String.format("IF(ISERROR(MROUND(AH%d/AI%d*100, 0.01)), 0, MROUND(AH%d/AI%d*100, 0.01))", excelRowIndex, excelRowIndex, excelRowIndex, excelRowIndex));
                        Optional.ofNullable(durationMonth.getTrudoemkCHELN()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 36).setCellValue(v), () -> {});
                        Optional.ofNullable(durationMonth.getTrudoemkCHELF()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 37).setCellValue(v), () -> {});
                        obrabRow.getCell(ref.getCol() + 38).setCellFormula(String.format("AL%d-AK%d", excelRowIndex, excelRowIndex));
                    }

                    Optional.ofNullable(obrab.getKol_vo_skv()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 43).setCellValue(v), () -> {});
                    Optional.ofNullable(obrab.getPriem_120_300()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 47).setCellValue(v), () -> {});
                    Optional.ofNullable(obrab.getPriem_301_480()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 48).setCellValue(v), () -> {});
                    Optional.ofNullable(obrab.getPriem_481_660()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 49).setCellValue(v), () -> {});
                    Optional.ofNullable(obrab.getPriem_661_840()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 50).setCellValue(v), () -> {});
                    Optional.ofNullable(obrab.getPriem_841()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 51).setCellValue(v), () -> {});
                    Optional.ofNullable(obrab.getPriem_0_180()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 52).setCellValue(v), () -> {});
                    Optional.ofNullable(obrab.getPriem_181_300()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 53).setCellValue(v), () -> {});
                    Optional.ofNullable(obrab.getPriem_301_450()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 54).setCellValue(v), () -> {});
                    Optional.ofNullable(obrab.getPriem_451_600()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 55).setCellValue(v), () -> {});
                    Optional.ofNullable(obrab.getPriem_601_750()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 56).setCellValue(v), () -> {});
                    Optional.ofNullable(obrab.getPriem_751()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 57).setCellValue(v), () -> {});
                    obrabRow.getCell(ref.getCol() + 58).setCellFormula(String.format("AR%d-AQ%d", excelRowIndex, excelRowIndex));

                    PriemDto priemYear = exelReportRepository.getPriem(type, method.getId(), obrab.getKey_obrab(), zeh.getId(), dateBegin.with(TemporalAdjusters.firstDayOfYear()), dateEnd);
                    Optional.ofNullable(priemYear.getPlan()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 44).setCellValue(v), () -> {});
                    Optional.ofNullable(priemYear.getAvg()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 45).setCellValue(v), () -> {});
                    obrabRow.getCell(ref.getCol() + 46).setCellFormula(String.format("AT%d-AS%d", excelRowIndex, excelRowIndex));

                    PZRDto PZRYear = exelReportRepository.getPZRNew(type, method.getId(), obrab.getKey_obrab(), zeh.getId(), dateBegin.with(TemporalAdjusters.firstDayOfYear()), dateEnd);
                    if (PZRYear != null){
                        Optional.ofNullable(PZRYear.getBase()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 60).setCellValue(v), () -> {});
                        obrabRow.getCell(ref.getCol() + 61).setCellFormula(String.format("BI%d-BH%d", excelRowIndex, excelRowIndex));
                        Optional.ofNullable(PZRYear.getSkv()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 63).setCellValue(v), () -> {});
                        obrabRow.getCell(ref.getCol() + 64).setCellFormula(String.format("BL%d-BK%d", excelRowIndex, excelRowIndex));
                    }

                    Double waterVolumeYear = exelReportRepository.getWaterVolume(type, method.getId(), obrab.getKey_obrab(), zeh.getId(), dateBegin.with(TemporalAdjusters.firstDayOfYear()), dateEnd);
                    Optional.ofNullable(waterVolumeYear).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 66).setCellValue(v), () -> {});
                    obrabRow.getCell(ref.getCol() + 67).setCellFormula(String.format("BO%d-BN%d", excelRowIndex, excelRowIndex));

                    DurationDto durationYear = exelReportRepository.getDurationNew(type, method.getId(), obrab.getKey_obrab(), zeh.getId(), dateBegin.with(TemporalAdjusters.firstDayOfYear()), dateEnd);
                    if (durationYear != null) {
                        Optional.ofNullable(durationYear.getNormaSum()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 68).setCellValue(v), () -> {});
                        Optional.ofNullable(durationYear.getFactSum()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 69).setCellValue(v), () -> {});
                        obrabRow.getCell(ref.getCol() + 70).setCellFormula(String.format("BR%d-BQ%d", excelRowIndex, excelRowIndex));
                        Optional.ofNullable(durationYear.getNorma()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 71).setCellValue(v), () -> {});
                        Optional.ofNullable(durationYear.getNorma_f()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 72).setCellValue(v), () -> {});
                        obrabRow.getCell(ref.getCol() + 73).setCellFormula(String.format("IF(ISERROR(MROUND(BT%d/BU%d*100, 0.01)), 0, MROUND(BT%d/BU%d*100, 0.01))", excelRowIndex, excelRowIndex, excelRowIndex, excelRowIndex));
                        Optional.ofNullable(durationYear.getTrudoemkCHELN()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 74).setCellValue(v), () -> {});
                        Optional.ofNullable(durationYear.getTrudoemkCHELF()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 75).setCellValue(v), () -> {});
                        obrabRow.getCell(ref.getCol() + 76).setCellFormula(String.format("BX%d-BW%d", excelRowIndex, excelRowIndex));
                    }
                    currentRow++;
                    rowCounter++;
                }
            }
            ObrabDto obrabTotal = exelReportRepository.getObrabTotalNew(type, zeh.getId(),  dateBegin.with(TemporalAdjusters.firstDayOfYear()), dateEnd).stream().findFirst().orElse(null);
            int excelRowIndex = currentRow + 1;
            Row obrabRow = sheet.createRow(currentRow);
            createRangeCells(obrabRow, (int) ref.getCol(), 79, boldTotalStyle);
            ObrabDto skvCountTotal = exelReportRepository.getObrabTotalNew(type, zeh.getId(), dateBegin, dateEnd).stream().findFirst().orElse(null);
            obrabRow.getCell(ref.getCol()).setCellValue("Всего");
            Map<String, Object> properties = Map.of(
                    CellUtil.ALIGNMENT, HorizontalAlignment.LEFT,
                    CellUtil.VERTICAL_ALIGNMENT, VerticalAlignment.TOP
            );
            CellUtil.setCellStyleProperties(obrabRow.getCell(ref.getCol()), properties);
            if (skvCountTotal != null) {
                Optional.ofNullable(skvCountTotal.getKol_vo_skv()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 5).setCellValue(v), () -> {});
                Optional.ofNullable(skvCountTotal.getPriem_120_300()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 9).setCellValue(v), () -> {});
                Optional.ofNullable(skvCountTotal.getPriem_301_480()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 10).setCellValue(v), () -> {});
                Optional.ofNullable(skvCountTotal.getPriem_481_660()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 11).setCellValue(v), () -> {});
                Optional.ofNullable(skvCountTotal.getPriem_661_840()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 12).setCellValue(v), () -> {});
                Optional.ofNullable(skvCountTotal.getPriem_841()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 13).setCellValue(v), () -> {});
                Optional.ofNullable(skvCountTotal.getPriem_0_180()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 14).setCellValue(v), () -> {});
                Optional.ofNullable(skvCountTotal.getPriem_181_300()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 15).setCellValue(v), () -> {});
                Optional.ofNullable(skvCountTotal.getPriem_301_450()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 16).setCellValue(v), () -> {});
                Optional.ofNullable(skvCountTotal.getPriem_451_600()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 17).setCellValue(v), () -> {});
                Optional.ofNullable(skvCountTotal.getPriem_601_750()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 18).setCellValue(v), () -> {});
                Optional.ofNullable(skvCountTotal.getPriem_751()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 19).setCellValue(v), () -> {});
                obrabRow.getCell(ref.getCol() + 20).setCellFormula(String.format("F%d-E%d", excelRowIndex, excelRowIndex));
            }

            PriemDto priemTotal = exelReportRepository.getPriemTotal(type, zeh.getId(), dateBegin, dateEnd);
            Optional.ofNullable(priemTotal.getPlan()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 6).setCellValue(v), () -> {});
            Optional.ofNullable(priemTotal.getAvg()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 7).setCellValue(v), () -> {});
            obrabRow.getCell(ref.getCol() + 8).setCellFormula(String.format("H%d-G%d", excelRowIndex, excelRowIndex));

            PZRDto PZRMonthTotal = exelReportRepository.getPZRTotalNew(type, zeh.getId(), dateBegin, dateEnd);
            if (PZRMonthTotal != null){
                Optional.ofNullable(PZRMonthTotal.getBase()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 22).setCellValue(v), () -> {});
                obrabRow.getCell(ref.getCol() + 23).setCellFormula(String.format("W%d-V%d", excelRowIndex, excelRowIndex));
                Optional.ofNullable(PZRMonthTotal.getSkv()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 25).setCellValue(v), () -> {});
                obrabRow.getCell(ref.getCol() + 26).setCellFormula(String.format("Z%d-Y%d", excelRowIndex, excelRowIndex));
            }

            Double waterVolumeTotal = exelReportRepository.getWaterVolumeTotal(type, zeh.getId(), dateBegin, dateEnd);
            Optional.ofNullable(waterVolumeTotal).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 28).setCellValue(v), () -> {});
            obrabRow.getCell(ref.getCol() + 29).setCellFormula(String.format("AC%d-AB%d", excelRowIndex, excelRowIndex));

            DurationDto durationMonthTotal = exelReportRepository.getDurationTotalNew(type, zeh.getId(), dateBegin, dateEnd);
            if (durationMonthTotal != null) {
                Optional.ofNullable(durationMonthTotal.getNormaSum()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 30).setCellValue(v), () -> {});
                Optional.ofNullable(durationMonthTotal.getFactSum()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 31).setCellValue(v), () -> {});
                obrabRow.getCell(ref.getCol() + 32).setCellFormula(String.format("AF%d-AE%d", excelRowIndex, excelRowIndex));
                Optional.ofNullable(durationMonthTotal.getNorma()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 33).setCellValue(v), () -> {});
                Optional.ofNullable(durationMonthTotal.getNorma_f()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 34).setCellValue(v), () -> {});
                obrabRow.getCell(ref.getCol() + 35).setCellFormula(String.format("IF(ISERROR(MROUND(AH%d/AI%d*100, 0.01)), 0, MROUND(AH%d/AI%d*100, 0.01))", excelRowIndex, excelRowIndex, excelRowIndex, excelRowIndex));
                Optional.ofNullable(durationMonthTotal.getTrudoemkCHELN()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 36).setCellValue(v), () -> {});
                Optional.ofNullable(durationMonthTotal.getTrudoemkCHELF()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 37).setCellValue(v), () -> {});
                obrabRow.getCell(ref.getCol() + 38).setCellFormula(String.format("AL%d-AK%d", excelRowIndex, excelRowIndex));
            }

            Optional.ofNullable(obrabTotal.getKol_vo_skv()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 43).setCellValue(v), () -> {});
            Optional.ofNullable(obrabTotal.getPriem_120_300()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 47).setCellValue(v), () -> {});
            Optional.ofNullable(obrabTotal.getPriem_301_480()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 48).setCellValue(v), () -> {});
            Optional.ofNullable(obrabTotal.getPriem_481_660()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 49).setCellValue(v), () -> {});
            Optional.ofNullable(obrabTotal.getPriem_661_840()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 50).setCellValue(v), () -> {});
            Optional.ofNullable(obrabTotal.getPriem_841()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 51).setCellValue(v), () -> {});
            Optional.ofNullable(obrabTotal.getPriem_0_180()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 52).setCellValue(v), () -> {});
            Optional.ofNullable(obrabTotal.getPriem_181_300()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 53).setCellValue(v), () -> {});
            Optional.ofNullable(obrabTotal.getPriem_301_450()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 54).setCellValue(v), () -> {});
            Optional.ofNullable(obrabTotal.getPriem_451_600()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 55).setCellValue(v), () -> {});
            Optional.ofNullable(obrabTotal.getPriem_601_750()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 56).setCellValue(v), () -> {});
            Optional.ofNullable(obrabTotal.getPriem_751()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 57).setCellValue(v), () -> {});
            obrabRow.getCell(ref.getCol() + 58).setCellFormula(String.format("AR%d-AQ%d", excelRowIndex, excelRowIndex));

            PriemDto priemTotalYear = exelReportRepository.getPriemTotal(type, zeh.getId(), dateBegin.with(TemporalAdjusters.firstDayOfYear()), dateEnd);
            Optional.ofNullable(priemTotalYear.getPlan()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 6).setCellValue(v), () -> {});
            Optional.ofNullable(priemTotalYear.getAvg()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 7).setCellValue(v), () -> {});
            obrabRow.getCell(ref.getCol() + 8).setCellFormula(String.format("AT%d-AS%d", excelRowIndex, excelRowIndex));

            PZRDto PZRYearTotal = exelReportRepository.getPZRTotalNew(type, zeh.getId(), dateBegin.with(TemporalAdjusters.firstDayOfYear()), dateEnd);
            if (PZRYearTotal != null){
                Optional.ofNullable(PZRYearTotal.getBase()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 60).setCellValue(v), () -> {});
                obrabRow.getCell(ref.getCol() + 61).setCellFormula(String.format("BI%d-BH%d", excelRowIndex, excelRowIndex));
                Optional.ofNullable(PZRYearTotal.getSkv()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 63).setCellValue(v), () -> {});
                obrabRow.getCell(ref.getCol() + 64).setCellFormula(String.format("BL%d-BK%d", excelRowIndex, excelRowIndex));
            }

            Double waterVolumeTotalYear = exelReportRepository.getWaterVolumeTotal(type, zeh.getId(), dateBegin.with(TemporalAdjusters.firstDayOfYear()), dateEnd);
            Optional.ofNullable(waterVolumeTotalYear).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 66).setCellValue(v), () -> {});
            obrabRow.getCell(ref.getCol() + 67).setCellFormula(String.format("BO%d-BN%d", excelRowIndex, excelRowIndex));

            DurationDto durationYearTotal = exelReportRepository.getDurationTotalNew(type, zeh.getId(), dateBegin.with(TemporalAdjusters.firstDayOfYear()), dateEnd);
            if (durationYearTotal != null) {
                Optional.ofNullable(durationYearTotal.getNormaSum()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 68).setCellValue(v), () -> {});
                Optional.ofNullable(durationYearTotal.getFactSum()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 69).setCellValue(v), () -> {});
                obrabRow.getCell(ref.getCol() + 70).setCellFormula(String.format("BR%d-BQ%d", excelRowIndex, excelRowIndex));
                Optional.ofNullable(durationYearTotal.getNorma()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 71).setCellValue(v), () -> {});
                Optional.ofNullable(durationYearTotal.getNorma_f()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 72).setCellValue(v), () -> {});
                obrabRow.getCell(ref.getCol() + 73).setCellFormula(String.format("IF(ISERROR(MROUND(BT%d/BU%d*100, 0.01)), 0, MROUND(BT%d/BU%d*100, 0.01))", excelRowIndex, excelRowIndex, excelRowIndex, excelRowIndex));
                Optional.ofNullable(durationYearTotal.getTrudoemkCHELN()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 74).setCellValue(v), () -> {});
                Optional.ofNullable(durationYearTotal.getTrudoemkCHELF()).filter((v) -> v != 0).ifPresentOrElse((v) -> obrabRow.getCell(ref.getCol() + 75).setCellValue(v), () -> {});
                obrabRow.getCell(ref.getCol() + 76).setCellFormula(String.format("BX%d-BW%d", excelRowIndex, excelRowIndex));
            }
            currentRow++;
            SeparateDurationDto separateDuration = exelReportRepository.getSeparateDuration(type, zeh.getId(), dateBegin, dateEnd);
            SeparateDurationDto separateDurationYear = exelReportRepository.getSeparateDuration(type, zeh.getId(), dateBegin.with(TemporalAdjusters.firstDayOfYear()), dateEnd);
            row = sheet.createRow(currentRow);
            createRangeCells(row, (int) ref.getCol(), 79, boldBaseStyle);
            cell = row.getCell(ref.getCol());
            cell.setCellValue("в том числе отработано:");
            CellUtil.setCellStyleProperties(row.getCell(ref.getCol()), properties);
            currentRow++;

            row = sheet.createRow(currentRow);
            createRangeCells(row, (int) ref.getCol(), 79, boldBaseStyle);
            cell = row.getCell(ref.getCol());
            cell.setCellValue(1);
            cell = row.getCell(ref.getCol() + 1);
            cell.setCellValue("На химизации");
            CellUtil.setCellStyleProperties(row.getCell(ref.getCol() + 1), properties);
            excelRowIndex = currentRow + 1;
            row.getCell(ref.getCol() + 30).setCellFormula(String.format("SUM(AE%d:AE%d)", excelRowIndex + 1, excelRowIndex + 5));
            row.getCell(ref.getCol() + 31).setCellFormula(String.format("SUM(AF%d:AF%d)", excelRowIndex + 1, excelRowIndex + 5));
            row.getCell(ref.getCol() + 32).setCellFormula(String.format("AF%d-AE%d", excelRowIndex, excelRowIndex));
            row.getCell(ref.getCol() + 33).setCellFormula(String.format("SUM(AH%d:AH%d)", excelRowIndex + 1, excelRowIndex + 5));
            row.getCell(ref.getCol() + 34).setCellFormula(String.format("SUM(AI%d:AI%d)", excelRowIndex + 1, excelRowIndex + 5));
//            row.getCell(ref.getCol() + 33).setCellFormula(String.format("ROUND(AE%d/F%d,2)", excelRowIndex, excelRowIndex - 2));
//            row.getCell(ref.getCol() + 34).setCellFormula(String.format("ROUND(AF%d/F%d,2)", excelRowIndex, excelRowIndex - 2));
            row.getCell(ref.getCol() + 35).setCellFormula(String.format("IF(ISERROR(MROUND(AH%d/AI%d*100, 0.01)), 0, MROUND(AH%d/AI%d*100, 0.01))", excelRowIndex, excelRowIndex, excelRowIndex, excelRowIndex));

            row.getCell(ref.getCol() + 68).setCellFormula(String.format("SUM(BQ%d:BQ%d)", excelRowIndex + 1, excelRowIndex + 5));
            row.getCell(ref.getCol() + 69).setCellFormula(String.format("SUM(BR%d:BR%d)", excelRowIndex + 1, excelRowIndex + 5));
            row.getCell(ref.getCol() + 70).setCellFormula(String.format("BR%d-BQ%d", excelRowIndex, excelRowIndex));
            row.getCell(ref.getCol() + 71).setCellFormula(String.format("SUM(BT%d:BT%d)", excelRowIndex + 1, excelRowIndex + 5));
            row.getCell(ref.getCol() + 72).setCellFormula(String.format("SUM(BU%d:BU%d)", excelRowIndex + 1, excelRowIndex + 5));
//            row.getCell(ref.getCol() + 71).setCellFormula(String.format("ROUND(BQ%d/AR%d,2)", excelRowIndex, excelRowIndex - 2));
//            row.getCell(ref.getCol() + 72).setCellFormula(String.format("ROUND(BR%d/AR%d,2)", excelRowIndex, excelRowIndex - 2));
            row.getCell(ref.getCol() + 73).setCellFormula(String.format("IF(ISERROR(MROUND(BT%d/BU%d*100, 0.01)), 0, MROUND(BT%d/BU%d*100, 0.01))", excelRowIndex, excelRowIndex, excelRowIndex, excelRowIndex));
            currentRow++;

            Row pzrBase = sheet.createRow(currentRow);
            excelRowIndex = currentRow + 1;
            createRangeCells(pzrBase, (int) ref.getCol(), 79, baseStyle);
            pzrBase.getCell(ref.getCol() + 1).setCellValue("ПЗР на базе");
            CellUtil.setCellStyleProperties(pzrBase.getCell(ref.getCol() + 1), properties);
            Optional.ofNullable(separateDuration.getBase()).filter((v) -> v != 0).ifPresentOrElse((v) -> pzrBase.getCell(ref.getCol() + 30).setCellValue(v), () -> {});
            Optional.ofNullable(separateDuration.getBaseFact()).filter((v) -> v != 0).ifPresentOrElse((v) -> pzrBase.getCell(ref.getCol() + 31).setCellValue(v), () -> {});
            pzrBase.getCell(ref.getCol() + 32).setCellFormula(String.format("AF%d-AE%d", excelRowIndex, excelRowIndex));
            pzrBase.getCell(ref.getCol() + 33).setCellFormula(String.format("ROUND(AE%d/F%d,2)", excelRowIndex, excelRowIndex - 3));
            pzrBase.getCell(ref.getCol() + 34).setCellFormula(String.format("ROUND(AF%d/F%d,2)", excelRowIndex, excelRowIndex - 3));
            pzrBase.getCell(ref.getCol() + 35).setCellFormula(String.format("IF(ISERROR(MROUND(AH%d/AI%d*100, 0.01)), 0, MROUND(AH%d/AI%d*100, 0.01))", excelRowIndex, excelRowIndex, excelRowIndex, excelRowIndex));

            Optional.ofNullable(separateDurationYear.getBase()).filter((v) -> v != 0).ifPresentOrElse((v) -> pzrBase.getCell(ref.getCol() + 68).setCellValue(v), () -> {});
            Optional.ofNullable(separateDurationYear.getBaseFact()).filter((v) -> v != 0).ifPresentOrElse((v) -> pzrBase.getCell(ref.getCol() + 69).setCellValue(v), () -> {});
            pzrBase.getCell(ref.getCol() + 70).setCellFormula(String.format("BR%d-BQ%d", excelRowIndex, excelRowIndex));
            pzrBase.getCell(ref.getCol() + 71).setCellFormula(String.format("ROUND(BQ%d/AR%d,2)", excelRowIndex, excelRowIndex - 3));
            pzrBase.getCell(ref.getCol() + 72).setCellFormula(String.format("ROUND(BR%d/AR%d,2)", excelRowIndex, excelRowIndex - 3));
            pzrBase.getCell(ref.getCol() + 73).setCellFormula(String.format("IF(ISERROR(MROUND(BT%d/BU%d*100, 0.01)), 0, MROUND(BT%d/BU%d*100, 0.01))", excelRowIndex, excelRowIndex, excelRowIndex, excelRowIndex));
            currentRow++;

            Row transfer = sheet.createRow(currentRow);
            excelRowIndex = currentRow + 1;
            createRangeCells(transfer, (int) ref.getCol(), 79, baseStyle);
            transfer.getCell(ref.getCol() + 1).setCellValue("Переезд");
            CellUtil.setCellStyleProperties(transfer.getCell(ref.getCol() + 1), properties);
            Optional.ofNullable(separateDuration.getTransfer()).filter((v) -> v != 0).ifPresentOrElse((v) -> transfer.getCell(ref.getCol() + 30).setCellValue(v), () -> {});
            Optional.ofNullable(separateDuration.getTransferFact()).filter((v) -> v != 0).ifPresentOrElse((v) -> transfer.getCell(ref.getCol() + 31).setCellValue(v), () -> {});
            transfer.getCell(ref.getCol() + 32).setCellFormula(String.format("AF%d-AE%d", excelRowIndex, excelRowIndex));
            transfer.getCell(ref.getCol() + 33).setCellFormula(String.format("ROUND(AE%d/F%d,2)", excelRowIndex, excelRowIndex - 4));
            transfer.getCell(ref.getCol() + 34).setCellFormula(String.format("ROUND(AF%d/F%d,2)", excelRowIndex, excelRowIndex - 4));
            transfer.getCell(ref.getCol() + 35).setCellFormula(String.format("IF(ISERROR(MROUND(AH%d/AI%d*100, 0.01)), 0, MROUND(AH%d/AI%d*100, 0.01))", excelRowIndex, excelRowIndex, excelRowIndex, excelRowIndex));

            Optional.ofNullable(separateDurationYear.getTransfer()).filter((v) -> v != 0).ifPresentOrElse((v) -> transfer.getCell(ref.getCol() + 68).setCellValue(v), () -> {});
            Optional.ofNullable(separateDurationYear.getTransferFact()).filter((v) -> v != 0).ifPresentOrElse((v) -> transfer.getCell(ref.getCol() + 69).setCellValue(v), () -> {});
            transfer.getCell(ref.getCol() + 70).setCellFormula(String.format("BR%d-BQ%d", excelRowIndex, excelRowIndex));
            transfer.getCell(ref.getCol() + 71).setCellFormula(String.format("ROUND(BQ%d/AR%d,2)", excelRowIndex, excelRowIndex - 4));
            transfer.getCell(ref.getCol() + 72).setCellFormula(String.format("ROUND(BR%d/AR%d,2)", excelRowIndex, excelRowIndex - 4));
            transfer.getCell(ref.getCol() + 73).setCellFormula(String.format("IF(ISERROR(MROUND(BT%d/BU%d*100, 0.01)), 0, MROUND(BT%d/BU%d*100, 0.01))", excelRowIndex, excelRowIndex, excelRowIndex, excelRowIndex));
            currentRow++;

            Row pr = sheet.createRow(currentRow);
            excelRowIndex = currentRow + 1;
            createRangeCells(pr, (int) ref.getCol(), 79, baseStyle);
            pr.getCell(ref.getCol() + 1).setCellValue("ПР");
            CellUtil.setCellStyleProperties(pr.getCell(ref.getCol() + 1), properties);
            Optional.ofNullable(separateDuration.getPr()).filter((v) -> v != 0).ifPresentOrElse((v) -> pr.getCell(ref.getCol() + 30).setCellValue(v), () -> {});
            Optional.ofNullable(separateDuration.getPrFact()).filter((v) -> v != 0).ifPresentOrElse((v) -> pr.getCell(ref.getCol() + 31).setCellValue(v), () -> {});
            pr.getCell(ref.getCol() + 32).setCellFormula(String.format("AF%d-AE%d", excelRowIndex, excelRowIndex));
            pr.getCell(ref.getCol() + 33).setCellFormula(String.format("ROUND(AE%d/F%d,2)", excelRowIndex, excelRowIndex - 5));
            pr.getCell(ref.getCol() + 34).setCellFormula(String.format("ROUND(AF%d/F%d,2)", excelRowIndex, excelRowIndex - 5));
            pr.getCell(ref.getCol() + 35).setCellFormula(String.format("IF(ISERROR(MROUND(AH%d/AI%d*100, 0.01)), 0, MROUND(AH%d/AI%d*100, 0.01))", excelRowIndex, excelRowIndex, excelRowIndex, excelRowIndex));

            Optional.ofNullable(separateDurationYear.getPr()).filter((v) -> v != 0).ifPresentOrElse((v) -> pr.getCell(ref.getCol() + 68).setCellValue(v), () -> {});
            Optional.ofNullable(separateDurationYear.getPrFact()).filter((v) -> v != 0).ifPresentOrElse((v) -> pr.getCell(ref.getCol() + 69).setCellValue(v), () -> {});
            pr.getCell(ref.getCol() + 70).setCellFormula(String.format("BR%d-BQ%d", excelRowIndex, excelRowIndex));
            pr.getCell(ref.getCol() + 71).setCellFormula(String.format("ROUND(BQ%d/AR%d,2)", excelRowIndex, excelRowIndex - 5));
            pr.getCell(ref.getCol() + 72).setCellFormula(String.format("ROUND(BR%d/AR%d,2)", excelRowIndex, excelRowIndex - 5));
            pr.getCell(ref.getCol() + 73).setCellFormula(String.format("IF(ISERROR(MROUND(BT%d/BU%d*100, 0.01)), 0, MROUND(BT%d/BU%d*100, 0.01))", excelRowIndex, excelRowIndex, excelRowIndex, excelRowIndex));
            currentRow++;

            Row injection = sheet.createRow(currentRow);
            excelRowIndex = currentRow + 1;
            createRangeCells(injection, (int) ref.getCol(), 79, baseStyle);
            injection.getCell(ref.getCol() + 1).setCellValue("Закачка раствора и продавка водой");
            CellUtil.setCellStyleProperties(injection.getCell(ref.getCol() + 1), properties);
            Optional.ofNullable(separateDuration.getInjection()).filter((v) -> v != 0).ifPresentOrElse((v) -> injection.getCell(ref.getCol() + 30).setCellValue(v), () -> {});
            Optional.ofNullable(separateDuration.getInjectionFact()).filter((v) -> v != 0).ifPresentOrElse((v) -> injection.getCell(ref.getCol() + 31).setCellValue(v), () -> {});
            injection.getCell(ref.getCol() + 32).setCellFormula(String.format("AF%d-AE%d", excelRowIndex, excelRowIndex));
            injection.getCell(ref.getCol() + 33).setCellFormula(String.format("ROUND(AE%d/F%d,2)", excelRowIndex, excelRowIndex - 6));
            injection.getCell(ref.getCol() + 34).setCellFormula(String.format("ROUND(AF%d/F%d,2)", excelRowIndex, excelRowIndex - 6));
            injection.getCell(ref.getCol() + 35).setCellFormula(String.format("IF(ISERROR(MROUND(AH%d/AI%d*100, 0.01)), 0, MROUND(AH%d/AI%d*100, 0.01))", excelRowIndex, excelRowIndex, excelRowIndex, excelRowIndex));

            Optional.ofNullable(separateDurationYear.getInjection()).filter((v) -> v != 0).ifPresentOrElse((v) -> injection.getCell(ref.getCol() + 68).setCellValue(v), () -> {});
            Optional.ofNullable(separateDurationYear.getInjectionFact()).filter((v) -> v != 0).ifPresentOrElse((v) -> injection.getCell(ref.getCol() + 69).setCellValue(v), () -> {});
            injection.getCell(ref.getCol() + 70).setCellFormula(String.format("BR%d-BQ%d", excelRowIndex, excelRowIndex));
            injection.getCell(ref.getCol() + 71).setCellFormula(String.format("ROUND(BQ%d/AR%d,2)", excelRowIndex, excelRowIndex - 6));
            injection.getCell(ref.getCol() + 72).setCellFormula(String.format("ROUND(BR%d/AR%d,2)", excelRowIndex, excelRowIndex - 6));
            injection.getCell(ref.getCol() + 73).setCellFormula(String.format("IF(ISERROR(MROUND(BT%d/BU%d*100, 0.01)), 0, MROUND(BT%d/BU%d*100, 0.01))", excelRowIndex, excelRowIndex, excelRowIndex, excelRowIndex));
            currentRow++;

            Row zr = sheet.createRow(currentRow);
            excelRowIndex = currentRow + 1;
            createRangeCells(zr, (int) ref.getCol(), 79, baseStyle);
            zr.getCell(ref.getCol() + 1).setCellValue("ЗР");
            CellUtil.setCellStyleProperties(zr.getCell(ref.getCol() + 1), properties);
            Optional.ofNullable(separateDuration.getZr()).filter((v) -> v != 0).ifPresentOrElse((v) -> zr.getCell(ref.getCol() + 30).setCellValue(v), () -> {});
            Optional.ofNullable(separateDuration.getZrFact()).filter((v) -> v != 0).ifPresentOrElse((v) -> zr.getCell(ref.getCol() + 31).setCellValue(v), () -> {});
            zr.getCell(ref.getCol() + 32).setCellFormula(String.format("AF%d-AE%d", excelRowIndex, excelRowIndex));
            zr.getCell(ref.getCol() + 33).setCellFormula(String.format("ROUND(AE%d/F%d,2)", excelRowIndex, excelRowIndex - 7));
            zr.getCell(ref.getCol() + 34).setCellFormula(String.format("ROUND(AF%d/F%d,2)", excelRowIndex, excelRowIndex - 7));
            zr.getCell(ref.getCol() + 35).setCellFormula(String.format("IF(ISERROR(MROUND(AH%d/AI%d*100, 0.01)), 0, MROUND(AH%d/AI%d*100, 0.01))", excelRowIndex, excelRowIndex, excelRowIndex, excelRowIndex));

            Optional.ofNullable(separateDurationYear.getZr()).filter((v) -> v != 0).ifPresentOrElse((v) -> zr.getCell(ref.getCol() + 68).setCellValue(v), () -> {});
            Optional.ofNullable(separateDurationYear.getZrFact()).filter((v) -> v != 0).ifPresentOrElse((v) -> zr.getCell(ref.getCol() + 69).setCellValue(v), () -> {});
            zr.getCell(ref.getCol() + 70).setCellFormula(String.format("BR%d-BQ%d", excelRowIndex, excelRowIndex));
            zr.getCell(ref.getCol() + 71).setCellFormula(String.format("ROUND(BQ%d/AR%d,2)", excelRowIndex, excelRowIndex - 7));
            zr.getCell(ref.getCol() + 72).setCellFormula(String.format("ROUND(BR%d/AR%d,2)", excelRowIndex, excelRowIndex - 7));
            zr.getCell(ref.getCol() + 73).setCellFormula(String.format("IF(ISERROR(MROUND(BT%d/BU%d*100, 0.01)), 0, MROUND(BT%d/BU%d*100, 0.01))", excelRowIndex, excelRowIndex, excelRowIndex, excelRowIndex));

            sheet.groupRow(excelRowIndex - 5, excelRowIndex);
            sheet.setRowGroupCollapsed(excelRowIndex, true);
            currentRow++;

            excelRowIndex = currentRow + 1;
            row = sheet.createRow(currentRow);
            createRangeCells(row, (int) ref.getCol(), 79, boldBaseStyle);
            cell = row.getCell(ref.getCol());
            cell.setCellValue(2);
            cell = row.getCell(ref.getCol() + 1);
            cell.setCellValue("На ремонте и ТО");
            CellUtil.setCellStyleProperties(row.getCell(ref.getCol() + 1), properties);
            row.getCell(ref.getCol() + 34).setCellFormula(String.format("ROUND(AF%d/F%d,2)", excelRowIndex, excelRowIndex - 8));
            row.getCell(ref.getCol() + 72).setCellFormula(String.format("ROUND(BR%d/AR%d,2)", excelRowIndex, excelRowIndex - 8));
            currentRow++;

            excelRowIndex = currentRow + 1;
            row = sheet.createRow(currentRow);
            createRangeCells(row, (int) ref.getCol(), 79, boldBaseStyle);
            cell = row.getCell(ref.getCol());
            cell.setCellValue(3);
            cell = row.getCell(ref.getCol() + 1);
            cell.setCellValue("Стажировка");
            CellUtil.setCellStyleProperties(row.getCell(ref.getCol() + 1), properties);
            row.getCell(ref.getCol() + 34).setCellFormula(String.format("ROUND(AF%d/F%d,2)", excelRowIndex, excelRowIndex - 9));
            row.getCell(ref.getCol() + 72).setCellFormula(String.format("ROUND(BR%d/AR%d,2)", excelRowIndex, excelRowIndex - 9));
            currentRow++;

            excelRowIndex = currentRow + 1;
            row = sheet.createRow(currentRow);
            createRangeCells(row, (int) ref.getCol(), 79, boldBaseStyle);
            cell = row.getCell(ref.getCol());
            cell.setCellValue(4);
            cell = row.getCell(ref.getCol() + 1);
            cell.setCellValue("Машинистами ППУ");
            CellUtil.setCellStyleProperties(row.getCell(ref.getCol() + 1), properties);
            row.getCell(ref.getCol() + 34).setCellFormula(String.format("ROUND(AF%d/F%d,2)", excelRowIndex, excelRowIndex - 10));
            row.getCell(ref.getCol() + 72).setCellFormula(String.format("ROUND(BR%d/AR%d,2)", excelRowIndex, excelRowIndex - 10));
            currentRow++;

            List<SeparateDowntimeDto>  separateDowntimeList = exelReportRepository.getSeparateDowntime(type, zeh.getId(), dateBegin, dateEnd);
            List<SeparateDowntimeDto>  separateDowntimeYearList = exelReportRepository.getSeparateDowntime(type, zeh.getId(), dateBegin.with(TemporalAdjusters.firstDayOfYear()), dateEnd);
            excelRowIndex = currentRow + 1;
            row = sheet.createRow(currentRow);
            createRangeCells(row, (int) ref.getCol(), 79, boldBaseStyle);
            cell = row.getCell(ref.getCol());
            cell.setCellValue(6);
            cell = row.getCell(ref.getCol() + 1);
            cell.setCellValue("Простои");
            CellUtil.setCellStyleProperties(row.getCell(ref.getCol() + 1), properties);
            row.getCell(ref.getCol() + 5).setCellFormula(String.format("SUM(F%d:F%d)", excelRowIndex + 1, excelRowIndex + separateDowntimeList.size()));
            row.getCell(ref.getCol() + 31).setCellFormula(String.format("SUM(AF%d:AF%d)", excelRowIndex + 1, excelRowIndex + separateDowntimeList.size()));
            row.getCell(ref.getCol() + 34).setCellFormula(String.format("ROUND(AF%d/F%d,2)", excelRowIndex, excelRowIndex));
            row.getCell(ref.getCol() + 43).setCellFormula(String.format("SUM(AR%d:AR%d)", excelRowIndex + 1, excelRowIndex + separateDowntimeList.size()));
            row.getCell(ref.getCol() + 69).setCellFormula(String.format("SUM(BR%d:BR%d)", excelRowIndex + 1, excelRowIndex + separateDowntimeList.size()));
            row.getCell(ref.getCol() + 72).setCellFormula(String.format("ROUND(BR%d/AR%d,2)", excelRowIndex, excelRowIndex));
            currentRow++;

            for(SeparateDowntimeDto separateDowntime : separateDowntimeList){
                excelRowIndex = currentRow + 1;
                Row downtimeRow =  sheet.createRow(currentRow);
                createRangeCells(downtimeRow, (int) ref.getCol(), 79, baseStyle);
                CellUtil.setCellStyleProperties(downtimeRow.getCell(ref.getCol() + 1), properties);
                Optional.ofNullable(separateDowntime.getName()).ifPresentOrElse((v) -> downtimeRow.getCell(ref.getCol() + 1).setCellValue(v), () -> {});
                    Optional.ofNullable(separateDowntime.getCount()).ifPresentOrElse((v) -> downtimeRow.getCell(ref.getCol() + 5).setCellValue(v), () -> {});
                Optional.ofNullable(separateDowntime.getSum()).filter((v) -> v != 0).ifPresentOrElse((v) -> downtimeRow.getCell(ref.getCol() + 31).setCellValue(v), () -> {});
                downtimeRow.getCell(ref.getCol() + 34).setCellFormula(String.format("IF(ISERROR(MROUND(AF%d/F%d, 0.01)), 0, MROUND(AF%d/F%d, 0.01))", excelRowIndex, excelRowIndex, excelRowIndex, excelRowIndex));

                SeparateDowntimeDto separateDowntimeYear = separateDowntimeYearList.stream().filter(item -> item.getId().equals(separateDowntime.getId())).collect(Collectors.toList()).get(0);
                Optional.ofNullable(separateDowntimeYear.getCount()).filter((v) -> v != 0).ifPresentOrElse((v) -> downtimeRow.getCell(ref.getCol() + 43).setCellValue(v), () -> {});
                Optional.ofNullable(separateDowntimeYear.getSum()).filter((v) -> v != 0).ifPresentOrElse((v) -> downtimeRow.getCell(ref.getCol() + 69).setCellValue(v), () -> {});
                downtimeRow.getCell(ref.getCol() + 72).setCellFormula(String.format("IF(ISERROR(MROUND(BR%d/AR%d, 0.01)), 0, MROUND(BR%d/AR%d, 0.01))", excelRowIndex, excelRowIndex, excelRowIndex, excelRowIndex));
                currentRow++;
            }
            excelRowIndex = currentRow + 1;
            sheet.groupRow(excelRowIndex - separateDowntimeList.size() - 1, excelRowIndex);
            sheet.setRowGroupCollapsed(excelRowIndex, true);
        }

        return currentRow;
    }

    public Workbook generateNarReport(NarReportRequest value) {
        try (InputStream templateStream = getClass().getResourceAsStream("/templates/tmphimnar.xls")) {
            if (templateStream == null) {
                throw new FileNotFoundException("Template not found");
            }
            Workbook workbook = WorkbookFactory.create(templateStream);
            Name bodyCell = workbook.getName("body");

            String bodyRef = bodyCell.getRefersToFormula().replace("'", "").split("!")[1];
            CellReference ref = new CellReference(bodyRef);
            Sheet sheet = workbook.getSheetAt(0);
            CellStyle baseStyle = sheet.getRow(ref.getRow()).getCell(ref.getCol()).getCellStyle();
            baseStyle.setBorderRight(BorderStyle.THIN);
            baseStyle.setBorderLeft(BorderStyle.THIN);
            baseStyle.setBorderBottom(BorderStyle.THIN);
            baseStyle.setBorderTop(BorderStyle.THIN);
            baseStyle.setWrapText(true);
            Font baseFont = workbook.getFontAt(baseStyle.getFontIndex());

            CellStyle rightAlignmentBaseStyle  = workbook.createCellStyle();
            rightAlignmentBaseStyle.cloneStyleFrom(baseStyle);
            rightAlignmentBaseStyle.setAlignment(HorizontalAlignment.RIGHT);

            CellStyle centerAlignmentBaseStyle  = workbook.createCellStyle();
            centerAlignmentBaseStyle.cloneStyleFrom(baseStyle);
            centerAlignmentBaseStyle.setAlignment(HorizontalAlignment.CENTER);


            int currentRow = ref.getRow();
            List<NarBody> narBodyList = exelReportRepository.getNarBody(value.getId());
            sheet.shiftRows(currentRow + 1, sheet.getLastRowNum(), narBodyList.size(), true, false);
            for(NarBody narBody : narBodyList){
                Row row = sheet.createRow(currentRow);
                createRangeCells(row, (int) ref.getCol(), 13, baseStyle);
                for (int i=ref.getCol() + 3; i <= 13; i++){
                    row.getCell(i).setCellStyle(rightAlignmentBaseStyle);
                }
                Optional.ofNullable(narBody.getIstnorm()).ifPresentOrElse((v)   -> row.getCell(ref.getCol()).setCellValue(v), () -> {row.getCell(ref.getCol()).setCellValue("——");});
                Optional.ofNullable(narBody.getNm()).ifPresentOrElse((v)        -> row.getCell(ref.getCol() + 1).setCellValue(v), () -> {row.getCell(ref.getCol() + 1).setCellValue("——"); row.getCell(ref.getCol() + 1).setCellStyle(centerAlignmentBaseStyle);});
                Optional.ofNullable(narBody.getEd_izm()).ifPresentOrElse((v)    -> row.getCell(ref.getCol() + 2).setCellValue(v), () -> {row.getCell(ref.getCol() + 2).setCellValue("——"); row.getCell(ref.getCol() + 2).setCellStyle(centerAlignmentBaseStyle);});
                Optional.ofNullable(narBody.getNorma_str()).ifPresentOrElse((v) -> row.getCell(ref.getCol() + 3).setCellValue(v), () -> {row.getCell(ref.getCol() + 3).setCellValue("——"); row.getCell(ref.getCol() + 3).setCellStyle(centerAlignmentBaseStyle);});
                Optional.ofNullable(narBody.getOb()).ifPresentOrElse((v)        -> row.getCell(ref.getCol() + 4).setCellValue(v), () -> {row.getCell(ref.getCol() + 4).setCellValue("——"); row.getCell(ref.getCol() + 4).setCellStyle(centerAlignmentBaseStyle);});
                Optional.ofNullable(narBody.getCks()).ifPresentOrElse((v)       -> row.getCell(ref.getCol() + 5).setCellValue(v), () -> {row.getCell(ref.getCol() + 5).setCellValue("——"); row.getCell(ref.getCol() + 5).setCellStyle(centerAlignmentBaseStyle);});
                Optional.ofNullable(narBody.getNorma()).ifPresentOrElse((v)     -> row.getCell(ref.getCol() + 6).setCellValue(v), () -> {row.getCell(ref.getCol() + 6).setCellValue("——"); row.getCell(ref.getCol() + 6).setCellStyle(centerAlignmentBaseStyle);});
                Optional.ofNullable(narBody.getNormaob()).ifPresentOrElse((v)   -> row.getCell(ref.getCol() + 7).setCellValue(v), () -> {row.getCell(ref.getCol() + 7).setCellValue("——"); row.getCell(ref.getCol() + 7).setCellStyle(centerAlignmentBaseStyle);});
                Optional.ofNullable(narBody.getOb_v()).ifPresentOrElse((v)      -> row.getCell(ref.getCol() + 8).setCellValue(v), () -> {row.getCell(ref.getCol() + 8).setCellValue("——"); row.getCell(ref.getCol() + 8).setCellStyle(centerAlignmentBaseStyle);});
                Optional.ofNullable(narBody.getCks_v()).ifPresentOrElse((v)     -> row.getCell(ref.getCol() + 9).setCellValue(v), () -> {row.getCell(ref.getCol() + 9).setCellValue("——"); row.getCell(ref.getCol() + 9).setCellStyle(centerAlignmentBaseStyle);});
                Optional.ofNullable(narBody.getNorma_v()).ifPresentOrElse((v)   -> row.getCell(ref.getCol() + 10).setCellValue(v), () -> {row.getCell(ref.getCol() + 10).setCellValue("——"); row.getCell(ref.getCol() + 10).setCellStyle(centerAlignmentBaseStyle);});
                Optional.ofNullable(narBody.getNormaob_v()).ifPresentOrElse((v) -> row.getCell(ref.getCol() + 11).setCellValue(v), () -> {row.getCell(ref.getCol() + 11).setCellValue("——"); row.getCell(ref.getCol() + 11).setCellStyle(centerAlignmentBaseStyle);});
                Optional.ofNullable(narBody.getFact()).ifPresentOrElse((v)      -> row.getCell(ref.getCol() + 12).setCellValue(v), () -> {row.getCell(ref.getCol() + 12).setCellValue("——"); row.getCell(ref.getCol() + 12).setCellStyle(centerAlignmentBaseStyle);});
                Optional.ofNullable(narBody.getFactob()).ifPresentOrElse((v)    -> row.getCell(ref.getCol() + 13).setCellValue(v), () -> {row.getCell(ref.getCol() + 13).setCellValue("——"); row.getCell(ref.getCol() + 13).setCellStyle(centerAlignmentBaseStyle);});
                row.setHeight((short) -1);
                currentRow++;
            }

            Name cksBodyCell = workbook.getName("ChislenBody");
            String cksBodyRef = cksBodyCell.getRefersToFormula().replace("'", "").split("!")[1];
            CellReference cksref = new CellReference(cksBodyRef);
            currentRow = cksref.getRow();
            List<CksBody> cksBodyList = exelReportRepository.getCksBody(value.getId());
            sheet.shiftRows(currentRow + 1, sheet.getLastRowNum(), cksBodyList.size(), true, false);
            for(CksBody cksBody : cksBodyList){
                Row row = sheet.createRow(currentRow);
                copyMergedRegions(sheet, cksref.getRow(), currentRow + 1);
                createRangeCells(row, (int) cksref.getCol(), 13, baseStyle);
                for (int i=cksref.getCol(); i<=13; i++){
                    if (i >= cksref.getCol() + 1 && i <= cksref.getCol() + 4){
                        row.getCell(i).setCellStyle(baseStyle);
                    }else if (i == cksref.getCol() + 5){
                        row.getCell(i).setCellStyle(rightAlignmentBaseStyle);
                    }else {
                        row.getCell(i).setCellStyle(centerAlignmentBaseStyle);
                    }
                }
                Optional.ofNullable(cksBody.getTehnm()).ifPresentOrElse((v) -> row.getCell(cksref.getCol()).setCellValue(v), () -> {row.getCell(cksref.getCol()).setCellValue("——");});
                Optional.ofNullable(cksBody.getNm()).ifPresentOrElse((v)    -> row.getCell(cksref.getCol() + 1).setCellValue(v), () -> {row.getCell(cksref.getCol() + 1).setCellValue("——");});
                Optional.ofNullable(cksBody.getChisl()).ifPresentOrElse((v) -> row.getCell(cksref.getCol() + 5).setCellValue(v), () -> {row.getCell(cksref.getCol() + 5).setCellValue("——");});
                row.getCell(cksref.getCol() + 6).setCellValue("——");
                row.getCell(cksref.getCol() + 10).setCellValue("——");
                row.setHeight((short) -1);
                currentRow++;
            }
            //убираем лишнюю строку перед итогами
            for (int i = sheet.getNumMergedRegions() - 1; i >= 0; i--) {
                CellRangeAddress region = sheet.getMergedRegion(i);
                if (region.getFirstRow() == currentRow || region.getLastRow() == currentRow) {
                    sheet.removeMergedRegion(i);
                }
            }
            Row extraTemplateRow = sheet.getRow(currentRow);
            if (extraTemplateRow != null) {
                sheet.removeRow(extraTemplateRow);
            }
            sheet.shiftRows(currentRow + 1, sheet.getLastRowNum(), -1, true, false);

            NarInfo narInfo = exelReportRepository.getNarInfo(value.getId());

            getNamedCell(workbook, "VidObrab").setCellValue(Objects.requireNonNullElse(narInfo.getVid_HOS(), ""));
            getNamedCell(workbook, "GroupObrab").setCellValue(Objects.requireNonNullElse(narInfo.getMetodobrab(), ""));
            getNamedCell(workbook, "Ceh").setCellValue(Objects.requireNonNullElse(narInfo.getZeh(), ""));
            getNamedCell(workbook, "Priemistost").setCellValue(Objects.requireNonNullElse(narInfo.getPriem(), 0));
            getNamedCell(workbook, "DRB").setCellValue(Objects.requireNonNullElse(narInfo.getBegdate().format(DateTimeFormatter.ofPattern("\"dd\" MMMM yyyy г.")), ""));
            getNamedCell(workbook, "DRE").setCellValue(Objects.requireNonNullElse(narInfo.getEnddate().format(DateTimeFormatter.ofPattern("\"dd\" MMMM yyyy г.")), ""));

            Optional.ofNullable(narInfo.getNorma_V()).ifPresentOrElse(v -> getNamedCell(workbook, "NormativVremya").setCellValue(v), () -> {});
            Optional.ofNullable(narInfo.getNorma_F()).ifPresentOrElse(v -> getNamedCell(workbook, "ProdoljRemFact").setCellValue(v), () -> {});
            Optional.ofNullable(narInfo.getNorma_V_Chel()).ifPresentOrElse(v -> getNamedCell(workbook, "NormVremChel").setCellValue(v), () -> {});
            Optional.ofNullable(narInfo.getNorma_F_Chel()).ifPresentOrElse(v -> getNamedCell(workbook, "FaktVremChel").setCellValue(v), () -> {});
            Optional.ofNullable(narInfo.getNorma_V_Chel()).ifPresentOrElse(v -> getNamedCell(workbook, "NormVremChisl").setCellValue(v), () -> {});
            Optional.ofNullable(narInfo.getNorma_F_Chel()).ifPresentOrElse(v -> getNamedCell(workbook, "NormFaktChisl").setCellValue(v), () -> {});

            getNamedCell(workbook, "ProcVipNormDlitRem").setCellFormula("MROUND(NormVremChel/FaktVremChel*100, 0.01)");
            getNamedCell(workbook, "Kol_PZR").setCellValue(Objects.requireNonNullElse(narInfo.getKolPZR(), 0));
            getNamedCell(workbook, "SKV").setCellValue("Скважина № " + narInfo.getSkv() + " куст № " + narInfo.getKust());
            getNamedCell(workbook, "ZAKAZ").setCellValue(Objects.requireNonNullElse(narInfo.getCustomer(), ""));
            getNamedCell(workbook, "BRIGADA").setCellValue("Бригада(звено) " + narInfo.getBrg());
            getNamedCell(workbook, "MESTO").setCellValue(Objects.requireNonNullElse(narInfo.getMest(), ""));

            getNamedCell(workbook, "VsegoOtrabotano").setCellValue(Objects.requireNonNull(narInfo.getNorma_F_Chel()));
            getNamedCell(workbook, "Pereezd").setCellValue("");
            getNamedCell(workbook, "ZR").setCellValue("");
            getNamedCell(workbook, "Glushenie").setCellValue("");
            getNamedCell(workbook, "Prostoi1").setCellValue("");
            getNamedCell(workbook, "ProizvoditelnoeVremya").setCellValue(Objects.requireNonNull(narInfo.getNorma_F_Chel()));

            Optional.ofNullable(value.getPeriod()).ifPresentOrElse(v -> getNamedCell(workbook, "ZaMesyac").setCellValue("от " + v.format(DateTimeFormatter.ofPattern("\"dd\" MMMM yyyy г."))), () -> {});

            getNamedCell(workbook, "Naryad").setCellValue("Нормированное задание № " + value.getNumber());
            getNamedCell(workbook, "UtvDoljnost").setCellValue(value.getUtv());
            getNamedCell(workbook, "UtvFIO").setCellValue(value.getUtvFIO());
            Optional.ofNullable(value.getDate()).ifPresentOrElse(v -> getNamedCell(workbook, "data_sost").setCellValue("Дата составления " + v.format(DateTimeFormatter.ofPattern("\"dd\" MMMM yyyy г."))), () -> {});
//            workbook.setPrintArea(0,0, data.get(0).size() - 1, 0, currentRow);
            return workbook;

        } catch (FileNotFoundException e) {
            log.error("Ошибка конфигурации сервера: отсутствует шаблон отчета. {}", e.getMessage());
            throw new ReportGenerationException("Внутренняя ошибка сервера: отсутствует файл шаблона отчета");

        } catch (EncryptedDocumentException | IOException e) {
            log.error("Критическая ошибка Apache POI при чтении/сборке Excel: {}", e.getMessage(), e);
            throw new ReportGenerationException("Не удалось сформировать структуру Excel-документа");

        } catch (Exception e) {
            log.error("Непредвиденная ошибка при генерации отчета: {}", e.getMessage(), e);
            throw new ReportGenerationException("Произошла неизвестная ошибка при сборке отчета");
        }
    }

    public void createRangeCells(Row row, Integer from, Integer to, CellStyle style){
        Cell cell;
        row.setHeightInPoints(16.5f);
        for (int i = 0; i <= to; i++) {
            cell = row.createCell(from + i);
            cell.setCellStyle(style);
        }
    }

    public Cell getNamedCell(@NotNull Workbook workbook, String name){
        Name namedCell = workbook.getName(name);
        String reference = namedCell.getRefersToFormula();
        CellReference cellRef = new CellReference(reference);
        Sheet sheet = workbook.getSheet(cellRef.getSheetName());
        Row row = sheet.getRow(cellRef.getRow());
        return row.getCell(cellRef.getCol());
    }

    public void shiftNamedCell(@NotNull Workbook workbook, String name, Integer shift){
        Name namedCell = workbook.getName(name);
        CellReference oldRef = new CellReference(namedCell.getRefersToFormula());
        CellReference newRef = new CellReference(
                oldRef.getSheetName(),
                oldRef.getRow() + shift,
                oldRef.getCol(),
                true, true
        );
        namedCell.setRefersToFormula(newRef.formatAsString());
    }

    public void copyMergedRegions(Sheet sheet, int templateRowNum, int newRowNum) {
        // Получаем общее количество объединенных областей на всем листе
        int numMergedRegions = sheet.getNumMergedRegions();

        for (int i = 0; i < numMergedRegions; i++) {
            CellRangeAddress region = sheet.getMergedRegion(i);
            // Если область объединения находится на строке-шаблоне
            if (region.getFirstRow() == templateRowNum && region.getLastRow() == templateRowNum) {
                // Создаем точно такое же объединение, но для индекса новой строки
                CellRangeAddress newRegion = new CellRangeAddress(
                        newRowNum,               // firstRow
                        newRowNum,               // lastRow
                        region.getFirstColumn(), // firstCol
                        region.getLastColumn()   // lastCol
                );
                // Применяем новое объединение к листу
                sheet.addMergedRegion(newRegion);
            }
        }
    }

    public Workbook generateUsersReport() {
        try (InputStream templateStream = getClass().getResourceAsStream("/templates/tmpUsers.xlsx")) {
            if (templateStream == null) {
                throw new FileNotFoundException("Template not found");
            }
            Workbook workbook = WorkbookFactory.create(templateStream);
            Name bodyCell = workbook.getName("body");

            String bodyRef = bodyCell.getRefersToFormula().replace("'", "").split("!")[1];
            CellReference ref = new CellReference(bodyRef);
            Sheet sheet = workbook.getSheetAt(0);
            CellStyle baseStyle = sheet.getRow(ref.getRow()).getCell(ref.getCol()).getCellStyle();
            baseStyle.setBorderRight(BorderStyle.THIN);
            baseStyle.setBorderLeft(BorderStyle.THIN);
            baseStyle.setBorderBottom(BorderStyle.THIN);
            baseStyle.setBorderTop(BorderStyle.THIN);
            baseStyle.setWrapText(true);
            Font baseFont = workbook.getFontAt(baseStyle.getFontIndex());

            CellStyle rightAlignmentBaseStyle  = workbook.createCellStyle();
            rightAlignmentBaseStyle.cloneStyleFrom(baseStyle);
            rightAlignmentBaseStyle.setAlignment(HorizontalAlignment.RIGHT);

            CellStyle centerAlignmentBaseStyle  = workbook.createCellStyle();
            centerAlignmentBaseStyle.cloneStyleFrom(baseStyle);
            centerAlignmentBaseStyle.setAlignment(HorizontalAlignment.CENTER);

            int currentRow = ref.getRow();

            List<UserReportBody> userReportBodyList = exelReportRepository.getUsers();
            int i = 1;
            for(UserReportBody userReportBody: userReportBodyList){
                Row row = sheet.createRow(currentRow);
                createRangeCells(row, (int) ref.getCol(), 8, baseStyle);
                row.getCell(ref.getCol()).setCellValue(i);
                row.getCell(ref.getCol() + 1).setCellValue(userReportBody.getFio());
                row.getCell(ref.getCol() + 2).setCellValue(userReportBody.getDoljn());
                row.getCell(ref.getCol() + 3).setCellValue(userReportBody.getUsername());
                row.getCell(ref.getCol() + 4).setCellValue(userReportBody.getDatein().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                row.getCell(ref.getCol() + 5).setCellValue(userReportBody.getDtout().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                row.getCell(ref.getCol() + 6).setCellValue(userReportBody.getNote());
                row.getCell(ref.getCol() + 7).setCellValue(userReportBody.getActivenm());
                row.getCell(ref.getCol() + 8).setCellValue(userReportBody.getOrg_nm());
                row.setHeight((short) - 1);
                i++;
                currentRow++;
            }

//            workbook.setPrintArea(0,0, data.get(0).size() - 1, 0, currentRow);
            return workbook;

        } catch (FileNotFoundException e) {
            log.error("Ошибка конфигурации сервера: отсутствует шаблон отчета. {}", e.getMessage());
            throw new ReportGenerationException("Внутренняя ошибка сервера: отсутствует файл шаблона отчета");

        } catch (EncryptedDocumentException | IOException e) {
            log.error("Критическая ошибка Apache POI при чтении/сборке Excel: {}", e.getMessage(), e);
            throw new ReportGenerationException("Не удалось сформировать структуру Excel-документа");

        } catch (Exception e) {
            log.error("Непредвиденная ошибка при генерации отчета: {}", e.getMessage(), e);
            throw new ReportGenerationException("Произошла неизвестная ошибка при сборке отчета");
        }
    }

    public Workbook generateVipReport(LocalDateTime dateStart, LocalDateTime dateEnd) {
        try (InputStream templateStream = getClass().getResourceAsStream("/templates/tmpUrvip.xlsx")) {
            if (templateStream == null) {
                throw new FileNotFoundException("Template not found");
            }
            Workbook workbook = WorkbookFactory.create(templateStream);

            Name bodyCell = workbook.getName("bodyUpr");
            String bodyRef = bodyCell.getRefersToFormula();
            CellReference ref = new CellReference(bodyRef);
            generateVipSheet(workbook, ref, 0, dateStart, dateEnd);

            bodyCell = workbook.getName("bodyMAN");
            bodyRef = bodyCell.getRefersToFormula();
            ref = new CellReference(bodyRef);
            generateVipSheet(workbook, ref, 1019540, dateStart, dateEnd);

            bodyCell = workbook.getName("bodyUNC");
            bodyRef = bodyCell.getRefersToFormula();
            ref = new CellReference(bodyRef);
            generateVipSheet(workbook, ref, 1018865, dateStart, dateEnd);

            bodyCell = workbook.getName("bodyUNP");
            bodyRef = bodyCell.getRefersToFormula();
            ref = new CellReference(bodyRef);
            generateVipSheet(workbook, ref, 1019385, dateStart, dateEnd);

            bodyCell = workbook.getName("bodyKW");
            bodyRef = bodyCell.getRefersToFormula();
            ref = new CellReference(bodyRef);
            generateVipSheet(workbook, ref, 1019237, dateStart, dateEnd);

            bodyCell = workbook.getName("bodyUPRYear");
            bodyRef = bodyCell.getRefersToFormula();
            ref = new CellReference(bodyRef);
            generateVipSheet(workbook, ref, 0, dateStart.with(TemporalAdjusters.firstDayOfYear()), dateEnd);

            bodyCell = workbook.getName("bodyMANYear");
            bodyRef = bodyCell.getRefersToFormula();
            ref = new CellReference(bodyRef);
            generateVipSheet(workbook, ref, 1019540, dateStart.with(TemporalAdjusters.firstDayOfYear()), dateEnd);

            bodyCell = workbook.getName("bodyUNCYear");
            bodyRef = bodyCell.getRefersToFormula();
            ref = new CellReference(bodyRef);
            generateVipSheet(workbook, ref, 1018865, dateStart.with(TemporalAdjusters.firstDayOfYear()), dateEnd);

            bodyCell = workbook.getName("bodyUNPYear");
            bodyRef = bodyCell.getRefersToFormula();
            ref = new CellReference(bodyRef);
            generateVipSheet(workbook, ref, 1019385, dateStart.with(TemporalAdjusters.firstDayOfYear()), dateEnd);

            bodyCell = workbook.getName("bodyKWYear");
            bodyRef = bodyCell.getRefersToFormula();
            ref = new CellReference(bodyRef);
            generateVipSheet(workbook, ref, 1019237, dateStart.with(TemporalAdjusters.firstDayOfYear()), dateEnd);



            String title = "за " + dateStart.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale.Builder().setLanguage("ru").setRegion("RU").build()) + " " + dateStart.getYear() + " года";
            getNamedCell(workbook, "monthUpr").setCellValue(title);
            title = "за " + dateStart.with(TemporalAdjusters.firstDayOfYear()).getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale.Builder().setLanguage("ru").setRegion("RU").build()) + "-" + dateStart.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale.Builder().setLanguage("ru").setRegion("RU").build()) + " " + dateStart.getYear() + " года";
            getNamedCell(workbook, "yearUpr").setCellValue(title);
            workbook.setForceFormulaRecalculation(true);

            return workbook;

        } catch (FileNotFoundException e) {
            log.error("Ошибка конфигурации сервера: отсутствует шаблон отчета. {}", e.getMessage());
            throw new ReportGenerationException("Внутренняя ошибка сервера: отсутствует файл шаблона отчета");

        } catch (EncryptedDocumentException | IOException e) {
            log.error("Критическая ошибка Apache POI при чтении/сборке Excel: {}", e.getMessage(), e);
            throw new ReportGenerationException("Не удалось сформировать структуру Excel-документа");

        } catch (Exception e) {
            log.error("Непредвиденная ошибка при генерации отчета: {}", e.getMessage(), e);
            throw new ReportGenerationException("Произошла неизвестная ошибка при сборке отчета");
        }
    }

    public void generateVipSheet(Workbook workbook, CellReference bodyRef, Integer type, LocalDateTime dateStart, LocalDateTime dateEnd){
        Sheet sheet = workbook.getSheet(bodyRef.getSheetName());
        int refCol = bodyRef.getCol();
        List<UrVipDto> urVipList = exelReportRepository.getUrVip(type, dateStart, dateEnd);
        for (UrVipDto urVip: urVipList){
            int currentRow = bodyRef.getRow();
            int i = 0;
            if (urVip.getZakazchik() != null){
                i = urVip.getZakazchik() - 1;
            } else {
                i = 12;
            }
            int currentCol = refCol + i;
            Row row = sheet.getRow(currentRow++);
            if (urVip.getCount() != null) {
                row.getCell(currentCol).setCellValue(urVip.getCount());
            }

            row = sheet.getRow(currentRow++);
            if (urVip.getBase() != null) {
                row.getCell(currentCol).setCellValue(urVip.getBase());
            }
            row = sheet.getRow(currentRow++);
            if (urVip.getBaseFact() != null) {
                row.getCell(currentCol).setCellValue(urVip.getBaseFact());
            }

            currentRow++;
            row = sheet.getRow(currentRow++);
            if (urVip.getPrigkomp() != null) {
                row.getCell(currentCol).setCellValue(urVip.getPrigkomp());
            }
            row = sheet.getRow(currentRow++);
            if (urVip.getPrigkompFact() != null) {
                row.getCell(currentCol).setCellValue(urVip.getPrigkompFact());
            }

            currentRow++;
            row = sheet.getRow(currentRow++);
            if (urVip.getPrigRastv() != null) {
                row.getCell(currentCol).setCellValue(urVip.getPrigRastv());
            }
            row = sheet.getRow(currentRow++);
            if (urVip.getPrigRastvFact() != null) {
                row.getCell(currentCol).setCellValue(urVip.getPrigRastvFact());
            }

            currentRow++;
            row = sheet.getRow(currentRow++);
            if (urVip.getTransfer() != null) {
                row.getCell(currentCol).setCellValue(urVip.getTransfer());
            }
            row = sheet.getRow(currentRow++);
            if (urVip.getTransferFact() != null) {
                row.getCell(currentCol).setCellValue(urVip.getTransferFact());
            }

            currentRow++;
            row = sheet.getRow(currentRow++);
            if (urVip.getPzr() != null) {
                row.getCell(currentCol).setCellValue(urVip.getPzr());
            }
            row = sheet.getRow(currentRow++);
            if (urVip.getPzrFact() != null) {
                row.getCell(currentCol).setCellValue(urVip.getPzrFact());
            }

            currentRow++;
            row = sheet.getRow(currentRow++);
            if (urVip.getInjection() != null) {
                row.getCell(currentCol).setCellValue(urVip.getInjection());
            }
            row = sheet.getRow(currentRow++);
            if (urVip.getInjectionFact() != null) {
                row.getCell(currentCol).setCellValue(urVip.getInjectionFact());
            }
            currentRow++;
            row = sheet.getRow(currentRow++);
            if (urVip.getInj() != null) {
                row.getCell(currentCol).setCellValue(urVip.getInj());
            }
            row = sheet.getRow(currentRow++);
            if (urVip.getInjFact() != null) {
                row.getCell(currentCol).setCellValue(urVip.getInjFact());
            }
            currentRow++;
            row = sheet.getRow(currentRow++);
            if (urVip.getProd() != null) {
                row.getCell(currentCol).setCellValue(urVip.getProd());
            }
            row = sheet.getRow(currentRow++);
            if (urVip.getProdFact() != null) {
                row.getCell(currentCol).setCellValue(urVip.getProdFact());
            }
            currentRow++;
            row = sheet.getRow(currentRow++);
            if (urVip.getOzhReact() != null) {
                row.getCell(currentCol).setCellValue(urVip.getOzhReact());
            }
            row = sheet.getRow(currentRow++);
            if (urVip.getOzhReactFact() != null) {
                row.getCell(currentCol).setCellValue(urVip.getOzhReactFact());
            }
            currentRow += 4;
            row = sheet.getRow(currentRow++);
            if (urVip.getDowntime() != null) {
                row.getCell(currentCol).setCellValue(urVip.getDowntime());
            }
            i++;
        }
    }


}


