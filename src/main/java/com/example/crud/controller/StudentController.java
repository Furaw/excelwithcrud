package com.example.crud.controller;


import com.example.crud.model.Student;
import com.example.crud.repository.StudentRepository;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        for(int i=0;i<worksheet.getPhysicalNumberOfRows() ;i++) {
            Student tempStudent = new Student();

            XSSFRow row = worksheet.getRow(0 + i);


           tempStudent.setFirstName(row.getCell(0 ).getStringCellValue());
           tempStudent.setLastName(row.getCell(1 ).getStringCellValue());
           tempStudent.setAge((int) row.getCell(2 ).getNumericCellValue());
            tempStudent.setDateOfBirth( (Date) row.getCell(3).getDateCellValue());
            tempStudent.setFaculty((String)row.getCell(4).getStringCellValue());
            studentRepository.save(tempStudent);

        }


    }


}
