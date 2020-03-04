package com.example.crud.controller;


import com.example.crud.model.Student;
import com.example.crud.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @DeleteMapping("/students/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable(value = "id") Long Id) throws Exception {
        Student student = studentRepository.findById(Id)
                .orElseThrow(() -> new Exception("Id doesnt exists"));

        studentRepository.delete(student);

        return ResponseEntity.ok().build();
    }
    @RequestMapping(value = "/delstudent/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> delStudent(@PathVariable(value = "id") Long Id) throws Exception {
        Student student = studentRepository.findById(Id)
                .orElseThrow(() -> new Exception("Id doesnt exists"));

        studentRepository.delete(student);

        return ResponseEntity.ok().build();
    }

}
