package com.project;

import java.util.Scanner;
//import java.nio.FloatBuffer;
import java.sql.*;

public class student {

    static Scanner scan = new Scanner(System.in);

    student() {

    }

    public boolean mainHome = true;

    public void s_auth() {
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
            stment1 = conn.prepareStatement("select password from student where email = ?");
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
                            System.out.println("Enter:\n1.  Re-enter the password\n2. To end the process");
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
                                end = 2;
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
                System.out.println("Student " + email + " does not exist");
            }

            conn.commit();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    void enrollment(String email, String stureport_table, Connection conn) throws SQLException {
        int user_can = -1;
        PreparedStatement s1 = conn.prepareStatement("select enrollment_start from admin_roles");
        ResultSet r1 = s1.executeQuery();
        if (r1.next()) {
            user_can = r1.getInt(1);
        }

        if (user_can == 1) {
            String batch = null;
            String course_to_enrolling = null;
            float cgpa = 0;
            boolean course_choice = false;

            PreparedStatement stment = null;
            PreparedStatement stment1 = null;
            stment = conn.prepareStatement("select * from student where email=?");
            stment.setString(1, email);
            ResultSet rs = stment.executeQuery();
            if (rs.next()) {
                // String name=rs.getString(1);
                cgpa = rs.getFloat(6);
                batch = rs.getString(8);
            }
            // System.out.println(cgpa + " " + batch);
            stment1 = conn.prepareStatement("select * from course_offering where batch_open=? and cgpa_req<=?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stment1.setString(1, batch);
            stment1.setFloat(2, cgpa);
            ResultSet rs1 = stment1.executeQuery();
            System.out.println("Enter the course code you want to enroll in:");
            System.out.println("Course Name\t\tcourse_code\t\tfaculty_email");
            while (rs1.next()) {
                System.out.println(
                        rs1.getString("ctitle") + "\t\t" + rs1.getString("ccode") + "\t\t"
                                + rs1.getString("fac_email"));
            } // 2020csb1124@iitrpr.ac.in

            course_to_enrolling = scan.nextLine();
            rs1.first();
            while (rs1.next()) {
                String cc = rs1.getString("ccode");
                if (course_to_enrolling.equals(cc)) {
                    course_choice = true;
                }
            }
            if (course_choice) {
                enrollment_status(email, stureport_table, course_to_enrolling, conn);
            } else {
                System.out.println("Enter course code");
                enrollment(email, stureport_table, conn);
            }
        } else if (user_can == 0) {
            System.out.println("Admin not allow students to enroll");
        }

    }

    void enrollment_status(String email, String stureport_table, String ccode, Connection conn) throws SQLException {
        boolean allowEnroll = true;
        String c_table_name = null;
        String grade = null;
        String pre_c = null;
        String course_title = null;
        String course_code = null;// pre-table name //pre_cs302
        Float course_credit = null;
        Integer course_semester = null;
        String exist_course_code = null;
        PreparedStatement stment = null;
        PreparedStatement stment1 = null;
        PreparedStatement stment3 = null;
        PreparedStatement stment4 = null;
        PreparedStatement stment5 = null;
        PreparedStatement stment6 = null;

        stment6 = conn.prepareStatement("select c_code from " + stureport_table + " where c_code=?");
        stment6.setString(1, ccode);
        ResultSet rs6 = stment6.executeQuery();
        if (rs6.next()) {
            exist_course_code = "not null";// record-table-name /record2020csb1124
            // System.out.println(stureport_table);
        }
        if (exist_course_code == null)

        { // getting prerequisites table name for course ccode
            stment = conn.prepareStatement("select prereq_table_name from course where code=?");
            stment.setString(1, ccode);
            ResultSet rs = stment.executeQuery();
            if (rs.next()) {
                c_table_name = rs.getString(1);// pre-table name //pre_cs106
                // System.out.println(c_table_name);
            }
            if (c_table_name != null && !c_table_name.equals("no_pre")) {

                stment1 = conn.prepareStatement("select * from " + c_table_name);
                // stment1.setString(1, c_table_name);
                ResultSet rs1 = stment1.executeQuery();// all prerequisites
                while (rs1.next()) {
                    String current_pre_code = rs1.getString(2); // cs201
                    
                    stment3 = conn.prepareStatement("select * from " + stureport_table + " where c_code=?");
                    // stment3.setString(1, stureport_table);
                    stment3.setString(1, current_pre_code);
                    ResultSet rs3 = stment3.executeQuery(); // all courses completed
                    if (rs3.next()) {
                        pre_c = rs3.getString(2);
                        grade = rs3.getString(5);

                        if (pre_c == null)
                            allowEnroll = false;
                    }
                }

                if (allowEnroll) {
                    if (!(grade.equals("E") || grade.equals("F") || grade.equals("NA"))) {

                        stment5 = conn.prepareStatement("select * from course_offering where ccode=?");
                        stment5.setString(1, ccode);
                        ResultSet rs5 = stment5.executeQuery();
                        if (rs5.next()) {
                            course_title = rs5.getString("ctitle");
                            course_code = rs5.getString("ccode");// pre-table 
                            course_credit = rs5.getFloat("credit");
                            course_semester = rs5.getInt("sem");

                        }

                        stment4 = conn.prepareStatement("insert into " + stureport_table + " values(?,?,?,?,'NA')");
                        stment4.setString(1, course_title);
                        stment4.setString(2, course_code);
                        stment4.setFloat(3, course_credit);
                        stment4.setInt(4, course_semester);
                        Integer rs4 = stment4.executeUpdate();

                        System.out.println("successfully enrolled in " + ccode);
                        if (rs4 > 0)
                            conn.commit();
                    } else {
                        System.out.println(" you Can do enrollment in " + ccode);
                        System.out.println(" prerequisites not completed.");
                    }
                } else {
                    System.out.println("you Cannot do enrollment in " + ccode);
                    System.out.println(" prerequisites not completed");
                }

            }

            else {
                System.out.println("successfully enrolled in the course " + ccode + ".");
            }
        } else {
            System.out.println("You are already enrolled in this course " + ccode + ".");
        }

    }

    void drop_course(String email, String sturecord_table, Connection conn) throws SQLException {
        int user_can = -1;
        PreparedStatement s1 = conn.prepareStatement("select drop_start from admin_roles");
        ResultSet r1 = s1.executeQuery();
        if (r1.next()) {
            user_can = r1.getInt(1);
        }

        if (user_can == 1) {
            PreparedStatement stment = null;
            PreparedStatement stment1 = null;
            PreparedStatement stment2 = null;
            PreparedStatement stment3 = null;

            int pre_sem = 0;
            String course_to_drop = null;
            boolean drop_c = false;

            stment = conn.prepareStatement("select present_semester from student where email=?");
            stment.setString(1, email);
            ResultSet rs = stment.executeQuery();
            if (rs.next()) {
                pre_sem = rs.getInt(1);
            }

            stment1 = conn.prepareStatement("select * from " + sturecord_table + "  where sem=?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            stment1.setFloat(1, pre_sem);
            ResultSet rs1 = stment1.executeQuery();
            System.out.println("These are the given courses you are currently enrolled in:");
            System.out.println("Course Name\t\tcourse code\t\tcredit");
            while (rs1.next()) {
                System.out.println(
                        rs1.getString("c_title") + "\t\t" + rs1.getString("c_code") + "\t\t"
                                + rs1.getString("c_credit"));
            } 

            System.out.println("Enter the course code you want to drop:\nEnter '-1' to end.");
            course_to_drop = scan.nextLine();
            if (course_to_drop.equals("-1")) {
                conn.commit();
            } else {
                stment3 = conn.prepareStatement("select * from " + sturecord_table + "  where sem=?",
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                stment3.setFloat(1, pre_sem);
                ResultSet rs3 = stment1.executeQuery();

                while (rs3.next()) {
                    String cd = rs3.getString("c_code");
                    System.out.println(cd);
                    if (course_to_drop.equals(cd)) {
                        drop_c = true;
                    }
                }
                if (drop_c) {
                    stment2 = conn.prepareStatement("delete from " + sturecord_table + " where c_code=?");
                    stment2.setString(1, course_to_drop);
                    Integer rs2 = stment2.executeUpdate();
                    if (rs2 > 0) {
                        System.out.println("Successfully dropped from course " + course_to_drop);
                        conn.commit();
                    }
                } else {
                    System.out.println("Enter a course code from above only");
                    drop_course(email, sturecord_table, conn);
                }

            }

        } else if (user_can == 0) {
            System.out.println("Admin not allow to drop from courses");
        }
    }

    void viewgrades(String email, String sturecord_table, Connection conn) throws SQLException {
        PreparedStatement stment = null;

        stment = conn.prepareStatement("select * from " + sturecord_table);
        ResultSet rs = stment.executeQuery();
        System.out.println("These are the courses and their grades:");
        System.out.println("course name\t\tcourse code\t\tcredit\t\tsemester\t\tgrade");

        while (rs.next()) {
            System.out.println(rs.getString(1) + "\t\t" + rs.getString(2) + "\t\t" + rs.getString(3) + "\t\t"
                    + rs.getInt(4) + "\t\t" + rs.getString(5));
        }

    }

    void compute_cgpa(String email, Connection conn) throws SQLException {
        float cgpa = 0;
        PreparedStatement stment = conn.prepareStatement("select cgpa from student where email=?");
        stment.setString(1, email);
        ResultSet r1 = stment.executeQuery();
        if (r1.next()) {
            cgpa= r1.getFloat(1);
        }
        System.out.println("Your current cgpa is " + cgpa);
    }

    public void home(String email, Connection conn) throws SQLException {
        PreparedStatement stment = null;
        PreparedStatement stment1 = null;
        String stureport_table = null;

        stment = conn.prepareStatement("select sturecord_table from student where email=?");
        stment.setString(1, email);
        ResultSet rs2 = stment.executeQuery();
        if (rs2.next()) {
            stureport_table = rs2.getString(1);
        }

        System.out.println("hello Welcome to the student dashboard\n");

        System.out.println("Enter your choice:");
        System.out.println("1. Enroll for the course");
        System.out.println("2. Drop a course");
        System.out.println("3. Veiw grades");
        System.out.println("4. Compute current CGPA");
        System.out.println("5. Back to the menue Screen");

        int choice = scan.nextInt();
        scan.nextLine();
        switch (choice) {
            case 1: {
                enrollment(email, stureport_table, conn);
                break;
            }
            case 2: {
                drop_course(email, stureport_table, conn);
                break;
            }
            case 3: {
                scan.nextLine();
                viewgrades(email, stureport_table, conn);
                break;
            }
            case 4: {
                compute_cgpa(email, conn);
                break;
            }
            case 5: {
                stment1 = conn.prepareStatement("delete from logs where email=?");
                stment1.setString(1, email);
                Integer rs1 = stment1.executeUpdate();
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
