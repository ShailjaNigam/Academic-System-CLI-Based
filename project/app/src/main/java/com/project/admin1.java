package com.project;


import java.util.*;
import java.sql.*;
import java.io.*;

public class admin1 {

    static Scanner scan = new Scanner(System.in);

    admin1() {

    }

    public boolean mainHome = true;

    public void admin_auth() {
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
            stment1 = conn.prepareStatement("select password from admin where email = ?");
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
                            System.out.println("Enter a valid option");
                            break;
                        }
                    }

                }

            } else {
                System.out.println("Admin " + email + " does not exist");
            }

            conn.commit();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    void set_yr(String email, Connection conn) throws SQLException {
        System.out.println("Enter the current year");
        String cz = scan.nextLine();
        PreparedStatement stment = conn.prepareStatement("update admin_roles set current_yr =?");
        stment.setString(1, cz);
        int rs = stment.executeUpdate();
        if (rs > 0) {
            conn.commit();
            System.out.println("Current year successfully updated");
        }

    }

    void set_semester(String email, Connection conn) throws SQLException {
        System.out.println("Enter the current semester");
        int sem = scan.nextInt();
        scan.nextLine();
        PreparedStatement stment = conn.prepareStatement("update admin_roles set current_sem =?");
        stment.setInt(1, sem);
        int rs = stment.executeUpdate();
        if (rs > 0) {
            conn.commit();
            System.out.println("Current semester successfully updated");
        }

    }

    void course_offering_start(String email, Connection conn) throws SQLException {
        PreparedStatement stment = conn.prepareStatement("update admin_roles set offering =1");
        int rs = stment.executeUpdate();
        if (rs > 0) {
            conn.commit();
            System.out.println("Faculties can offer course for enrollment");
        }
    }

    void course_offering_end(String email, Connection conn) throws SQLException {
        PreparedStatement stment = conn.prepareStatement("update admin_roles set offering =0");
        int rs = stment.executeUpdate();
        if (rs > 0) {
            conn.commit();
            System.out.println("Faculties can no longer offer courses for enrollment");
        }
    }

    void course_withdrawl_start(String email, Connection conn) throws SQLException {
        PreparedStatement stment = conn.prepareStatement("update admin_roles set withdrawl =1");
        int rs = stment.executeUpdate();
        if (rs > 0) {
            conn.commit();
            System.out.println("Faculties can deregister courses from enrollment");
        }
    }

    void course_withdrawl_end(String email, Connection conn) throws SQLException {
        PreparedStatement stment = conn.prepareStatement("update admin_roles set withdrawl =0");
        int rs = stment.executeUpdate();
        if (rs > 0) {
            conn.commit();
            System.out.println("Faculties can no longer deregister courses from enrollment6");
        }
    }


    void enrollment_start(String email, Connection conn) throws SQLException {
        PreparedStatement stment = conn.prepareStatement("update admin_roles set enrollment_start =1");
        int rs = stment.executeUpdate();
        if (rs > 0) {
            conn.commit();
            System.out.println("Students are allowed to enroll for courses");
        }

    }

    void enrollment_end(String email, Connection conn) throws SQLException {
        PreparedStatement stment = conn.prepareStatement("update admin_roles set enrollment_start =0");
        int rs = stment.executeUpdate();
        if (rs > 0) {
            conn.commit();
            System.out.println("Students can no longer enroll for courses");
        }

    }

    void course_drop_start(String email, Connection conn) throws SQLException {
        PreparedStatement stment = conn.prepareStatement("update admin_roles set drop_start =1");
        int rs = stment.executeUpdate();
        if (rs > 0) {
            conn.commit();
            System.out.println("Students may drop from courses");
        }

    }

    void course_drop_end(String email, Connection conn) throws SQLException {
        PreparedStatement stment = conn.prepareStatement("update admin_roles set drop_start =0");
        int rs = stment.executeUpdate();
        if (rs > 0) {
            conn.commit();
            System.out.println("Students can no longer drop from courses");
        }
    }

   

    
    void home(String email, Connection conn) throws SQLException, IOException {
        PreparedStatement stment = null;

        System.out.println("Hello Welcome to the Admin Dashboard\n");

        System.out.println("Enter your choice:");
        System.out.println("1.Set the current year ");
        System.out.println("2.Set the current semester ");
        System.out.println("3.Allow Faculties to offer courses");
        System.out.println("4.stop allowing Faculties to offer courses anymore ");
        System.out.println("5.Allow Faculties to withdraw the courses");
        System.out.println("6. stop allowing Faculties to wiyhdraw the courses anymore ");
        System.out.println("7.Allow students to enroll in courses ");
        System.out.println("8.Dont allow studnets to enroll anymore ");
        System.out.println("9.Allow students to drop from courses");
        System.out.println("10.Dont allow studnets to drop anymore ");
        System.out.println("11. Back to the menue Screen");

        int choice = scan.nextInt();
        scan.nextLine();
        switch (choice) {
            case 1: {
                set_yr(email, conn);
                break;
            }
            case 2: {
                set_semester(email, conn);
                break;
            }
            case 3: {
                course_offering_start(email, conn);
                break;
            }
            case 4: {
                course_offering_end(email, conn);
                break;
            }
            case 5: {
                course_withdrawl_start(email, conn);
                break;
            }
            case 6: {
                course_withdrawl_end(email, conn);
                break;
            }

            case 7: {
                enrollment_start(email, conn);
                break;
            }
            case 8: {
                enrollment_end(email, conn);
                break;
            }
            case 9: {
                course_drop_start(email, conn);
                break;
            }
            case 10: {
                course_drop_end(email, conn);
                break;
            }
            
            case 11: {

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
