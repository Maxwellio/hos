package com.example.api.controller;

import com.example.core.DTO.*;
import com.example.core.mapper.BrigadeMapper;
import com.example.core.mapper.ZehMapper;
import com.example.core.service.BrigadeService;
import com.example.core.service.NarService;
import com.example.core.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin
public class ReportController {
    private final ReportService reportService;
    private final NarService narService;
    private final ZehMapper zehMapper;
    private final BrigadeMapper brigadeMapper;
    private final BrigadeService brigadeService;
    @GetMapping("/downtime")
    public ResponseEntity<String> generateProstoiReport(
            @ModelAttribute DowntimeReportRequest value
    ) {
        String taskId = reportService.startDowntimeReport(value.getZeh(), value.getDateBegin(), value.getDateEnd());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(taskId);
    }

    @GetMapping("/downtime/status/{taskId}")
    public ResponseEntity<String> downtimeStatus(@PathVariable String taskId) {
        return ResponseEntity.ok(reportService.getDowntimeStatus(taskId));
    }

    @GetMapping("/downtime/download/{taskId}")
    public ResponseEntity<FileSystemResource> downloadDowntime(@PathVariable String taskId) {
        File file = reportService.getDowntimeFile(taskId);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"downtime_report.xls\"")
                .header("Access-Control-Expose-Headers", "Content-Disposition")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(new FileSystemResource(file));
    }

    @GetMapping("/normz")
    public ResponseEntity<StreamingResponseBody> generateHimReport(
            @ModelAttribute NormZReportRequest value
    ){
        String filename = "normz_report.xls";
        StreamingResponseBody stream = outputStream -> {
            try (Workbook workbook = reportService.generateNormZReport(value)) {
                workbook.write(outputStream);
            }
        };
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\""+ filename + "\"")
                .header("Access-Control-Expose-Headers", "Content-Disposition")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stream);
    };

    @GetMapping("/him")
    public ResponseEntity<StreamingResponseBody> generateHimReport(
            @ModelAttribute HimReportRequest value
    ){
        String filename = "him_report.xlsx";
        StreamingResponseBody stream = outputStream -> {
            try (Workbook workbook = reportService.generateHimReport(value.getZeh(), value.getType(), value.getDateBegin(), value.getDateEnd())) {
                workbook.write(outputStream);
            }
        };
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\""+ filename + "\"")
                .header("Access-Control-Expose-Headers", "Content-Disposition")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stream);
    };

    @GetMapping("/urvip")
    public ResponseEntity<StreamingResponseBody> generateVipReport(
            @ModelAttribute VipReportRequest value
    ){
        String filename = "urvip.xlsx";
        StreamingResponseBody stream = outputStream -> {
            try (Workbook workbook = reportService.generateVipReport(value.getDateStart(), value.getDateEnd())) {
                workbook.write(outputStream);
            }
        };
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\""+ filename + "\"")
                .header("Access-Control-Expose-Headers", "Content-Disposition")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stream);
    };

//    @GetMapping("/inf/start")
//    public ResponseEntity<String> startGeneration(@ModelAttribute InfReportRequest value) {
//        String taskId = UUID.randomUUID().toString();
//
//        reportService.generateReportAsync(taskId, value);
//        return ResponseEntity.status(HttpStatus.ACCEPTED).body(taskId);
//    }
//
//
//    @GetMapping("/inf/status/{taskId}")
//    public ResponseEntity<String> checkStatus(@PathVariable String taskId) {
//        String status = reportService.getStatus(taskId);
//        return ResponseEntity.ok(status);
//    }
//
//    @GetMapping("/inf/download/{taskId}")
//    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable String taskId) {
//        String status = reportService.getStatus(taskId);
//
//        if (!"SUCCESS".equals(status)) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//
//        File file = reportService.getGeneratedFile(taskId);
//        FileSystemResource resource = new FileSystemResource(file);
////        reportService.deleteGeneratedFile(taskId);
//        String filename = "inf_report.xls";
//
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + filename + "\"")
//                .header("Access-Control-Expose-Headers", "Content-Disposition")
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .contentLength(file.length())
//                .body(resource);
//    }

    @GetMapping("/inf")
    public ResponseEntity<StreamingResponseBody> generateHimReport(
            @ModelAttribute InfReportRequest value
    ){
        String filename = "inf_report.xls";
        StreamingResponseBody stream = outputStream -> {
            try (Workbook workbook = reportService.generateInfReport(value)) {
                workbook.write(outputStream);
                outputStream.flush();
            }
        };
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\""+ filename + "\"")
                .header("Access-Control-Expose-Headers", "Content-Disposition")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stream);
    };

    @GetMapping("/nar")
    public ResponseEntity<StreamingResponseBody> generateNarReport(
            @ModelAttribute NarReportRequest value
    ){
        String filename = "nar_report.xls";
        StreamingResponseBody stream = outputStream -> {
            try (Workbook workbook = reportService.generateNarReport(value)) {
                workbook.write(outputStream);
            }
        };
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\""+ filename + "\"")
                .header("Access-Control-Expose-Headers", "Content-Disposition")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stream);
    };

    @GetMapping("/users")
    public ResponseEntity<StreamingResponseBody> generateUsersReport(){
        String filename = "users.xlsx";
        StreamingResponseBody stream = outputStream -> {
            try (Workbook workbook = reportService.generateUsersReport()) {
                workbook.write(outputStream);
            }
        };
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\""+ filename + "\"")
                .header("Access-Control-Expose-Headers", "Content-Disposition")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stream);
    };

    @GetMapping("/zeh")
    public ResponseEntity<List<SelectDto>> getZehByOrg(
            @RequestParam(defaultValue = "200") Long org
    ){
        List<SelectDto> zehList =  zehMapper.toSelectDtoList(narService.getZehByOrg(org));
        zehList.add(0, new SelectDto(0L, "По Управлению"));
        return ResponseEntity.ok(zehList);
    }

    @GetMapping("/brigade")
    public ResponseEntity<List<SelectDto>> getBrigadeByOrg(
            @RequestParam(defaultValue = "200") Long org,
            @RequestParam Long zeh
    ){
        List<SelectDto> brigadeList =  brigadeMapper.toSelectDtoList(brigadeService.getBrigadesByOrgAndZeh(org, zeh));
        brigadeList.add(0, new SelectDto(0L, "Все звенья"));
        return ResponseEntity.ok(brigadeList);
    }

    @GetMapping("/equipment")
    public ResponseEntity<List<SelectDto>> getEquipment(){
        List<SelectDto> equipmentList = reportService.getEquipmentList();
        return ResponseEntity.ok(equipmentList);
    }

    @GetMapping("/equipmentExt")
    public ResponseEntity<List<SelectDto>> getEquipmentExt(){
        List<SelectDto> equipmentList = reportService.getEquipmentExtList();
        return ResponseEntity.ok(equipmentList);
    }
}
