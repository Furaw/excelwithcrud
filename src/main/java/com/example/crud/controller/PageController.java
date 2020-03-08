package com.example.crud.controller;

import com.example.crud.model.Student;
import com.example.crud.repository.StudentRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
@Controller
public class PageController {

    StudentRepository studentRepository;

    @Autowired
    public PageController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    @PostMapping(value="/")
    public String contactSubmit(@ModelAttribute Student student, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            System.out.println("Error parsing bindingResult");
        }

        model.addAttribute("students",studentRepository.findAll());
        model.addAttribute("student", student);
        studentRepository.save(student);
        return "greeting";
    }



    @GetMapping("/")
    public String  getAllStudents(@ModelAttribute Student student, Model model , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println("Error parsing bindingResult");
        }
            model.addAttribute("students",studentRepository.findAll());
         return "greeting";
    }


    @RequestMapping(value = "/update", method = RequestMethod.GET)

    public String updateUser(@RequestParam(name="Id")long Id,@ModelAttribute Student student ,Model model , BindingResult bindingResult) throws Exception {
        model.addAttribute("students",studentRepository.findAll());
        Student studentProperties = studentRepository.findById(Id)
                .orElseThrow(() -> new Exception("Id doesnt exists"));

        studentProperties.setAge(student.getAge());
        studentProperties.setFirstName(student.getFirstName());
        studentProperties.setLastName(student.getLastName());
        studentProperties.setFaculty(student.getFaculty());
        studentProperties.setDateOfBirth(student.getDateOfBirth());

        Student updatedStudent = studentRepository.save(studentProperties);


        return "greeting";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)

    public String handleDeleteUser(@RequestParam(name="studentId")long studentId,@ModelAttribute Student student ,Model model , BindingResult bindingResult) {
            model.addAttribute("students",studentRepository.findAll());
         studentRepository.deleteById(studentId);
        return "greeting";
    }

    @PostMapping("/import")

    public String mapReapExcelData(@RequestParam("file") MultipartFile reapExcelDataFile,@ModelAttribute Student student ,Model model , BindingResult bindingResult) throws IOException {

        model.addAttribute("students",studentRepository.findAll());

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
        return "greeting";

    }

    @GetMapping("/export")

    public String exportExcelData(@ModelAttribute Student student ,Model model , BindingResult bindingResult) throws IOException {
        model.addAttribute("students",studentRepository.findAll());
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
            System.out.println(studentList.get(i).getDateOfBirth());

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(studentList.get(i).getFaculty());

        }
        FileOutputStream out = new FileOutputStream(new File("NewFile.xlsx")); // file name with path
        workbook.write(out);
        out.close();
        return "greeting";



    }
}



