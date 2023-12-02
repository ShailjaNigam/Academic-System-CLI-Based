package com.project;

import java.util.*;
import java.sql.*;
import java.io.*;

public class faculty {

    static Scanner scan = new Scanner(System.in);

    faculty() {

    }

    public boolean mainHome = true;

    public void fac_auth() {
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
            stment1 = conn.prepareStatement("select password from faculty where email = ?");
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
                            System.out.println("Enter:\n1. Re-enter the password\n2. for end");
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
                            System.out.println("Choose valid option");
                            break;
                        }
                    }

                }

            } else {
                System.out.println("faculty " + email + " does not exist");
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

        stment = conn.prepareStatement("select email,sturecord_table from student");
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

    void register_course(String email, Connection conn) throws SQLException {
        int user_can = -1;
        PreparedStatement s1 = conn.prepareStatement("select offering from admin_roles");
        ResultSet r1 = s1.executeQuery();
        if (r1.next()) {
            user_can = r1.getInt(1);
        }

        if (user_can == 1) {
            PreparedStatement stment = null;

            System.out.println("Enter the course name:");
            String ctitle = scan.nextLine();
            System.out.println("Enter the course code you want to offer:");
            String ccode = scan.nextLine();
            System.out.println("Enter the credits of course:");
            float credit = scan.nextInt();
            scan.nextLine();
            System.out.println("Enter the batches for which it is open for:");
            String batch_open = scan.nextLine();
            System.out.println("Enter the minimum cgpa required(enter 0 if no cgpa criteria id there):");
            float cgpa_req = scan.nextFloat();
            int sem = 6;
            stment = conn.prepareStatement("insert into course_offering values(?,?,?,?,?,?,?)");
            stment.setString(1, ctitle);

            stment.setString(2, ccode);
            stment.setFloat(3, credit);
            stment.setString(4, email);
            stment.setInt(5, sem);
            stment.setString(6, batch_open);
            stment.setFloat(7, cgpa_req);

            Integer rs = stment.executeUpdate();
            if (rs > 0) {
                System.out.println("Course " + ccode + " successfully offered");
                conn.commit();
            }
        } else if (user_can == 0) {
            System.out.println("Admin has not allowed to offer courses for enrollment");
        }

    }

    void deregister_course(String email, Connection conn) throws SQLException {
        int user_can = -1;
        PreparedStatement s1 = conn.prepareStatement("select withdrawl from admin_roles");
        ResultSet r1 = s1.executeQuery();
        if (r1.next()) {
            user_can = r1.getInt(1);
        }

        if (user_can == 1)

        {
            PreparedStatement stment = null;
            PreparedStatement stment1 = null;

            String course_for_deregister = null;
            String isavailable = null;
            System.out.println("Enter the course code you want to deregister:");
            course_for_deregister = scan.nextLine();

            stment = conn.prepareStatement("select * from course_offering where ccode=?");
            stment.setString(1, course_for_deregister);
            ResultSet rs = stment.executeQuery();
            if (rs.next()) {
                isavailable = rs.getString("fac_email");
            }
            if (isavailable == null) {
                System.out.println("Course " + course_for_deregister + " was not registered for offering");
            } else if (isavailable.equals(email)) {
                stment1 = conn.prepareStatement("delete from course_offering where ccode=?");
                stment1.setString(1, course_for_deregister);
                int rs1 = stment1.executeUpdate();
                if (rs1 > 0) {
                    System.out.println("Course " + course_for_deregister + " successfully deregistered from offering.");
                    conn.commit();
                }
            } else {
                System.out.println("Course " + course_for_deregister + " was offered by " + isavailable
                        + ".\n'You cannot drop this course.");
            }
        } else if (user_can == 0) {
            System.out.println("Admin has not allowed to deregister a courses from enrollment");
        }
    }

    void updateGrades(String email, Connection conn) throws SQLException, IOException {
        PreparedStatement stment2 = null;

        String path = null;

        // int done=0;

        System.out.println("Enter the csv filepath:");
        path = scan.nextLine();

        BufferedReader lineReader = new BufferedReader(new FileReader(path));
        String lineText = null;

        lineReader.readLine(); // skip header line

        while ((lineText = lineReader.readLine()) != null) {
            String[] data = lineText.split(",");
            String csvEntry_Num = data[0];
            String csvc_code = data[1];
            String csvGrade = data[2];
          //  String csv1 = " update report" + csvEntry_Num + " set grade='"+ csvGrade +"' where c_code= '"+ csvc_code +"' and grade='NA'";

            stment2 = conn.prepareStatement(
                  "update report" + csvEntry_Num + " set grade=? where c_code= ? and grade='NA'");
            stment2.setString(1, csvGrade);
            stment2.setString(2, csvc_code);
          //  System.out.println(csv1);
          //  stment2 = conn.prepareStatement(csv1);
            int rs2 = stment2.executeUpdate();


            if (rs2 > 0) {
                System.out.println("Grades are successfully upgraded.");
                conn.commit();
            }

        }

        lineReader.close();

    }

    void home(String email, Connection conn) throws SQLException, IOException {
        PreparedStatement stment = null;

        System.out.println("Hello Welcome to the Faculty Dashboard\n");

        System.out.println("Enter your choice:");
        System.out.println("1. Veiw grades of students");
        System.out.println("2. Register a course for offering");
        System.out.println("3. Deregister a course from offering");
        System.out.println("4. Update grades");
        System.out.println("5. Back to the menue Screen");

        int choice = scan.nextInt();
        scan.nextLine();
        switch (choice) {
            case 1: {
                veiwGrades(email, conn);
                break;
            }
            case 2: {
                register_course(email, conn);
                break;
            }
            case 3: {
                deregister_course(email, conn);
                break;
            }
            case 4: {
                updateGrades(email, conn);
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
