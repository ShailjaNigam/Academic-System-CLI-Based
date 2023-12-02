package com.project;

import java.util.Scanner;

public class App {
    static Scanner scan = new Scanner(System.in);
    public static boolean leave = false;

    static student stu = new student();
    static faculty fac = new faculty();
    static academic_staff1 as = new academic_staff1();
    static admin1 ad = new admin1();

    static void menue() {
        System.out.println("Choose your role:");
        System.out.println("1. Admin");
        System.out.println("2. Accademic Office ");
        System.out.println("3. Faculty");
        System.out.println("4. Student");
        System.out.println("5. Quit");

        int choice = scan.nextInt();
        switch (choice) {
          
            
            case 1:{
                ad.admin_auth();
                break;
            } 
            case 2: {
                as.acad_auth();
                break;
            }
            case 3: {
                fac.fac_auth();
                break;
            }
            case 4: {
                stu.s_auth();
                break;
            }
           
            case 5: {
                leave = true;
                break;
            }
            default:
                System.out.println("Enter a valid choice");
        }
    }

    public static void main(String[] args) {

        while (!leave)
            menue();

    }
}
