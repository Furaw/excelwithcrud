package com.example.crud.controller;


import com.example.crud.model.Student;
import com.example.crud.repository.StudentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@RestController
public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @GetMapping("/students/{id}")
    public Student getStudentById(@PathVariable(value = "id") Long Id) throws Exception {
        return studentRepository.findById(Id)
                .orElseThrow(() -> new Exception("Id not found"));
    }

    @PostMapping("/students")
    public Student createStudent(@Valid @RequestBody Student student, BindingResult result) {
        return studentRepository.save(student);
    }

    @PutMapping("/students/{id}")
    public Student updateStudent(@PathVariable(value = "id") Long Id,
                                 @Valid @RequestBody Student student) throws Exception {

        Student studentProperties = studentRepository.findById(Id)
                .orElseThrow(() -> new Exception("Id doesnt exists"));

        studentProperties.setAge(student.getAge());
        studentProperties.setFirstName(student.getFirstName());
        studentProperties.setLastName(student.getLastName());
        studentProperties.setFaculty(student.getFaculty());
        studentProperties.setDateOfBirth(student.getDateOfBirth());

        Student updatedStudent = studentRepository.save(studentProperties);

        return updatedStudent;
    }


    @RequestMapping(value = "/delstudent/{id}", method = RequestMethod.GET)
    public ResponseEntity<Student> delStudent(@PathVariable(value = "id") Long Id) throws Exception {
        Student student = studentRepository.findById(Id)
                .orElseThrow(() -> new Exception("Id doesnt exists"));

        studentRepository.delete(student);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/import")
    public void mapReapExcelData(@RequestParam("file") MultipartFile reapExcelDataFile) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook(reapExcelDataFile.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        for (int i = 0; i < worksheet.getPhysicalNumberOfRows(); i++) {
            Student tempStudent = new Student();

            XSSFRow row = worksheet.getRow(0 + i);


            tempStudent.setFirstName(row.getCell(0).getStringCellValue());
            tempStudent.setLastName(row.getCell(1).getStringCellValue());
            tempStudent.setAge((int) row.getCell(2).getNumericCellValue());
            tempStudent.setDateOfBirth((Date) row.getCell(3).getDateCellValue());
            tempStudent.setFaculty((String) row.getCell(4).getStringCellValue());
            studentRepository.save(tempStudent);

        }


    }

    @GetMapping("/export")
    public void exportExcelData() throws IOException {

            List<Student> studentList= new LinkedList<Student>();
        studentList.addAll(studentRepository.findAll());


        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Students");

        CreationHelper createHelper = workbook.getCreationHelper();

        for (int i = 0; i < studentRepository.findAll().size(); i++) {
            sheet.autoSizeColumn(3);
            Row row = sheet.createRow(i);
            Cell cell = row.createCell(0);
            cell.setCellValue(studentList.get(i).getFirstName());

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(studentList.get(i).getLastName());

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(studentList.get(i).getAge());

            Cell cell3 = row.createCell(3);
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/mm/dd h:mm"));
            cell3.setCellValue(new Date());
            cell3.setCellStyle(cellStyle);

            cell3.setCellValue(studentList.get(i).getDateOfBirth());

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(studentList.get(i).getFaculty());

        }
        FileOutputStream out = new FileOutputStream(new File("NewFile.xlsx")); // file name with path
        workbook.write(out);
        out.close();








    }
    }


