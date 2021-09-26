package com.cst438.domain;

public class StudentDTO {

   public String student_email;
   public String student_name;
   public int student_status;


   @Override
   public String toString() {
      return "StudentDTO [student_email=" + student_email + 
            ", student_name=" + student_name + 
            ", status" + student_status + "]";
   }

}
