package com.project;

import java.util.*;
//import java.util.concurrent.CopyOnWriteArraySet;
import java.sql.*;
import java.io.*;

public class academic_staff1 {

    static Scanner scan = new Scanner(System.in);

    academic_staff1() {

    }

    public boolean mainHome = true;

    public void acad_auth() {
        System.out.println("Enter your email:");
        String email = scan.nextLine();
        Connection conn = null;
        PreparedStatement stment1 = null;
        PreparedStatement stment2 = null;
        PreparedStatement stment3 = null;
        String pwd = null;
        String password = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/miniproject",
                            "postgres", "17022018");
            conn.setAutoCommit(false);
            // System.out.println("Opened database successfully");
            stment1 = conn.prepareStatement("select password from academic_staff where email = ?");
            stment1.setString(1, email);

            ResultSet rs = stment1.executeQuery();
            if (rs.next()) {
                pwd = rs.getString(1);
                int end = 0;
                System.out.println("Enter your password");
                password = scan.nextLine();
                if (password.equals(pwd)) {
                    stment2 = conn.prepareStatement("insert into logs values(?,CURRENT_TIMESTAMP) ");
                    stment2.setString(1, email);
                    Integer rs2 = stment2.executeUpdate();
                    conn.commit();

                    while (mainHome) {
                        home(email, conn);
                    }
                    end = 1;
                } else {
                    System.out.println("Incorrect password");
                    end = 0;
                }

                while (end != 1) {

                    switch (end) {
                        case 0: {
                            System.out.println("Enter:\n1.  Re-enter the password\n2.  end");
                            end = scan.nextInt();
                            scan.nextLine();
                            break;
                        }
                        case 1: {
                            System.out.println("Enter your password again");
                            password = scan.nextLine();
                            if (password.equals(pwd)) {
                                stment3 = conn.prepareStatement("insert into logs values(?,CURRENT_TIMESTAMP) ");
                                stment3.setString(1, email);
                                Integer rs3 = stment3.executeUpdate();
                                conn.commit();
                                while (mainHome) {
                                    home(email, conn);
                                }
                                end = 1;
                            } else {
                                System.out.println("Incorrect password");
                                end = 0;
                            }
                            break;
                        }
                        default: {
                            System.out.println("Enter a valid option");
                            break;
                        }
                    }

                }

            } else {
                System.out.println("Academic staff " + email + " does not exist");
            }

            conn.commit();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    void veiwGrades(String email, Connection conn) throws SQLException {
        PreparedStatement stment = null;
        PreparedStatement stment1 = null;

        stment = conn.prepareStatement("select email, sturecord_table from student");
        ResultSet rs = stment.executeQuery();
        System.out.println("");
        while (rs.next()) {
            stment1 = conn.prepareStatement("select * from " + rs.getString(2));
            ResultSet rs1 = stment1.executeQuery();
            System.out.println(rs.getString(1));
            while (rs1.next()) {
                System.out.println(rs.getString(1) + "\t\t" + rs1.getString(1) + "\t\t" + rs1.getString(2) + "\t\t"
                        + rs1.getFloat(3) + "\t\t" + rs1.getInt(4) + "\t\t" + rs1.getString(5));
            }
        }

    }

    void Edit_course_catalog(String email, Connection conn) throws SQLException {

        PreparedStatement stment = null;
        int status = 0;
        String prereq_table = null;

        System.out.println("Enter name of the course:");
        String ctitle = scan.nextLine();
        System.out.println("Enter course code:");
        String ccode = scan.nextLine();
        System.out.println("Enter the course department:");
        String dept = scan.nextLine();
        System.out.println("Enter core  if course is core and elective if course is an elective course:");
        String core_and_elec = scan.nextLine();
        System.out.println("Enter the course L-T-P of the course:");
        String ltpc = scan.nextLine();
        
        System.out.println("Enter the course credit:");
       float credit = scan.nextInt();
        scan.nextLine();

        System.out.println("Enter the number of prerequisites of course (enter 0 if none):");
        int no_of_prereq  = scan.nextInt();
        scan.nextLine();
        if (no_of_prereq  == 0) {
            prereq_table = "no_pre";
        } else {
            prereq_table = "pre_" + ccode;
        }

        stment = conn.prepareStatement("insert into course values(?,?,?,?,?,?,?,?,?)");
        stment.setString(1, ctitle);
        stment.setString(2, ccode);
        stment.setString(3, dept);
        stment.setString(4, core_and_elec);
         stment.setString(5, ltpc);
        stment.setFloat(6, credit);
       
        stment.setInt(7, no_of_prereq );
        stment.setInt(8, status);
       stment.setString(9, prereq_table);

        Integer rs = stment.executeUpdate();

        for (int i = 0; i < no_of_prereq ; i++) {
            System.out.println("Enter the course name of prerequisite course:");
            String pre_course_name = scan.nextLine();
            System.out.println("Enter the course code of prerequisite course:");
            String pre_course_code = scan.nextLine();

            PreparedStatement stment1 = conn.prepareStatement("insert into " + prereq_table + " values(?,?)");
            stment1.setString(1, pre_course_name);
            stment1.setString(2, pre_course_code);

            int rs1 = stment1.executeUpdate();

        }
        if (rs > 0) {
            System.out.println("Course " + ccode + " successfully added to course catalog");
            conn.commit();
        }

    }

    void generate_transcript(String email, Connection conn) throws SQLException {

        System.out.println("Enter the email of the student to generate the transcript of Student: ");
        String stu_email = scan.nextLine();
        String entry_no = null;

        PreparedStatement stment = conn.prepareStatement("select * from student where email=?");
        stment.setString(1, stu_email);
        ResultSet rs = stment.executeQuery();
        if (rs.next()) {
            entry_no = rs.getString("entry_no");
          
            try {
                FileWriter myWriter = new FileWriter("result.txt");
                myWriter.write("student name: " + rs.getString(1) + "\nentry no: " + rs.getString(2) + "\nemail: "
                        + rs.getString(3) + "\ndepartment: " + rs.getString(5) + "\ncgpa: " + rs.getString(6)
                        + "\nbatch: "
                        + rs.getString(8) + "\ntotal credit till: " + rs.getString(10)
                        + "\n\nThese are the courses details of student " + stu_email
                        + "\ncourse name\t  course code\t  credit\t  semester\t  grade");

                PreparedStatement stment1 = conn.prepareStatement("select * from report" + entry_no);
                ResultSet rs1 = stment1.executeQuery();
                while (rs1.next()) {
                    myWriter.write("\n"+rs1.getString(1) + "\t  " + rs1.getString(2) + "\t  " + rs1.getFloat(3) + "\t  "
                            + rs1.getInt(4) + "\t  " + rs1.getString(5)+"\n");
                }

                myWriter.close();
                System.out.println("Transcript successfully generated.");
                conn.commit();
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

        }

    }

    void graduation_check(String email,Connection conn) throws SQLException{
        int caps=0;
        float cgpa=0;
        float credit=0;
        float req_cg=0;
        float req_credit=0;
        System.out.println("Enter email of the student for whom we have to graduation check on:");
        String stu_email=scan.nextLine();
        PreparedStatement stment=conn.prepareStatement("select * from student where email=?");
        stment.setString(1, stu_email);
        ResultSet rs=stment.executeQuery();
        if(rs.next()){
            caps=rs.getInt("capstone");
            cgpa=rs.getFloat("cgpa");
            credit=rs.getFloat("current_credit");
        }
        PreparedStatement stment1=conn.prepareStatement("select * from admin_roles");
        ResultSet rs1=stment1.executeQuery();
        if(rs1.next()){
            req_cg=rs1.getFloat("require_cgpa");
            req_credit=rs1.getFloat("totalcredit_require");

        }

        if(caps==2 && cgpa>=req_cg && credit>=req_credit){
            System.out.println("Student "+stu_email+" satisfies all the requirements so he/she can be graduated.");
        }
        else{
            System.out.println("Student "+stu_email+" does not satisfies the requirements so he/she cannot be graduated.");

        }
        
    }

    void home(String email, Connection conn) throws SQLException, IOException {
        PreparedStatement stment = null;

        System.out.println("Hello Welcome to the Academic Staff Dashboard)\n");

        System.out.println("Enter your choice:");
        System.out.println("1. Veiw grades of students");
        System.out.println("2. Edit the course catalog");
        System.out.println("3. Generate transcript");
        System.out.println("4. Graduation check");
        System.out.println("5. Back to the menue Screen");

        int choice = scan.nextInt();
        scan.nextLine();
        switch (choice) {
            case 1: {
                veiwGrades(email, conn);
                break;
            }
            case 2: {
                Edit_course_catalog(email, conn);
                break;
            }
            case 3: {
                generate_transcript(email, conn);
                break;
            }
            case 4: {
                graduation_check(email, conn);
                break;
            }
            case 5: {

                stment = conn.prepareStatement("delete from logs where email=?");
                stment.setString(1, email);
                Integer rs1 = stment.executeUpdate();
                mainHome = false;
                break;
            }
            default: {
                System.out.println("Enter a valid choice");
                break;
            }

        }
    }

}
