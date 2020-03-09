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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
@Controller
public class MainController {

    StudentRepository studentRepository;

    @Autowired
    public MainController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping("/")
    public String  getAllStudents(@RequestParam(value = "page", required=false) Integer page,@ModelAttribute Student student, Model model , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println("Error parsing bindingResult");
        }

        if(page==null){

            Pageable pageable = PageRequest.of(0, 10);
            model.addAttribute("students",studentRepository.findAll(pageable));
        }
        else{

            Pageable pageable = PageRequest.of( page-1, 10);
            model.addAttribute("students",studentRepository.findAll(pageable));
        }
        return "greeting";
    }





    @PostMapping(value="/")
    public String contactSubmit(@RequestParam(value = "page", required=false) Integer page, @RequestParam(value = "Id", required=false) Long Id,@ModelAttribute Student student, BindingResult bindingResult, Model model) throws Exception {
        if (bindingResult.hasErrors()) {
            System.out.println("Error parsing bindingResult");
        }
        if(Id ==null){

            studentRepository.save(student);
        }
        else{
            Student studentProperties = studentRepository.findById(Id)
                    .orElseThrow(() -> new Exception("Id doesnt exists"));

            studentProperties.setAge(student.getAge());
            studentProperties.setFirstName(student.getFirstName());
            studentProperties.setLastName(student.getLastName());
            studentProperties.setFaculty(student.getFaculty());
            studentProperties.setDateOfBirth(student.getDateOfBirth());

            Student updatedStudent = studentRepository.save(studentProperties);

        }

        if(page==null){

            Pageable pageable = PageRequest.of(0, 10);
            model.addAttribute("students",studentRepository.findAll(pageable));
        }
        else{

            Pageable pageable = PageRequest.of( page-1, 10);
            model.addAttribute("students",studentRepository.findAll(pageable));
        }

        return "greeting";
    }






    @RequestMapping(value = "/delete", method = RequestMethod.GET)

    public String handleDeleteUser(@RequestParam(value = "page", required=false) Integer page,@RequestParam(name="studentId")long studentId,@ModelAttribute Student student ,Model model , BindingResult bindingResult) {
        if(page==null){

            Pageable pageable = PageRequest.of(0, 10);
            model.addAttribute("students",studentRepository.findAll(pageable));
        }
        else{

            Pageable pageable = PageRequest.of( page-1, 10);
            model.addAttribute("students",studentRepository.findAll(pageable));
        }
         studentRepository.deleteById(studentId);
        return "greeting";
    }

    @PostMapping("/import")

    public String mapReapExcelData(@RequestParam(value = "page", required=false) Integer page,@RequestParam("file") MultipartFile reapExcelDataFile,@ModelAttribute Student student ,Model model , BindingResult bindingResult) throws IOException {


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
        if(page==null){

            Pageable pageable = PageRequest.of(0, 10);
            model.addAttribute("students",studentRepository.findAll(pageable));
        }
        else{

            Pageable pageable = PageRequest.of( page-1, 10);
            model.addAttribute("students",studentRepository.findAll(pageable));
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



