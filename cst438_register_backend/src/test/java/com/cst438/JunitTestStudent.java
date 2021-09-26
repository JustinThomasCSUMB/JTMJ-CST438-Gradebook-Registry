package com.cst438;

import static org.mockito.ArgumentMatchers.any;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.cst438.controller.StudentController;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = { StudentController.class })
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class JunitTestStudent {
   
   static final String URL = "http://localhost:8080";
   public static final int TEST_COURSE_ID = 40442;
   public static final String TEST_STUDENT_EMAIL = "test@csumb.edu";
   public static final String TEST_STUDENT_NAME  = "test";
   public static final int TEST_YEAR = 2021;
   public static final String TEST_SEMESTER = "Fall";
   public static final int TEST_STATUS_CODE = 1;

   @MockBean
   CourseRepository courseRepository;

   @MockBean
   StudentRepository studentRepository;

   @MockBean
   EnrollmentRepository enrollmentRepository;

   @MockBean
   GradebookService gradebookService;

   @Autowired
   private MockMvc mvc;
   
   @Test
   public void addStudent()  throws Exception {
      MockHttpServletResponse response;
      Student student = new Student();
      student.setEmail(TEST_STUDENT_EMAIL);
      student.setName(TEST_STUDENT_NAME);
      given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(null);
      given(studentRepository.save(any(Student.class))).willReturn(student);
      
      response = mvc.perform(
            MockMvcRequestBuilders
               .post("/student/add")
               .param("email", TEST_STUDENT_EMAIL)
               .param("name", TEST_STUDENT_NAME)
               .accept(MediaType.APPLICATION_JSON))
            .andReturn().getResponse();
      
      assertEquals(200, response.getStatus());
      
      Student result = fromJsonString(response.getContentAsString(), Student.class);
      assertEquals(TEST_STUDENT_EMAIL, result.getEmail());
      assertEquals(TEST_STUDENT_NAME, result.getName());
      
      verify(studentRepository).save(any(Student.class));
   }
   
   @Test
   public void setStudentReg()  throws Exception {
      MockHttpServletResponse response;
      Student student = new Student();
      student.setEmail(TEST_STUDENT_EMAIL);
      student.setName(TEST_STUDENT_NAME);
      student.setStatusCode(0);
      given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
      given(studentRepository.save(any(Student.class))).willReturn(student);
      
      response = mvc.perform(
            MockMvcRequestBuilders
               .post("/student/status")
               .param("email", TEST_STUDENT_EMAIL)
               .content(asJsonString(TEST_STATUS_CODE))
               .contentType(MediaType.APPLICATION_JSON)
               .accept(MediaType.APPLICATION_JSON))
            .andReturn().getResponse();
      
      assertEquals(200, response.getStatus());
      
      Student result = fromJsonString(response.getContentAsString(), Student.class);
      assertEquals(TEST_STUDENT_EMAIL, result.getEmail());
      assertEquals(TEST_STATUS_CODE, result.getStatusCode());
      
      verify(studentRepository).save(any(Student.class));
   }
   
   
   //Private
   private static String asJsonString(final Object obj) {
      try {

         return new ObjectMapper().writeValueAsString(obj);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   private static <T> T  fromJsonString(String str, Class<T> valueType ) {
      try {
         return new ObjectMapper().readValue(str, valueType);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
}
