import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;



public class Client {
    
           // Static variables
           static String status=null;
           static String date=null;
           static String time=null;
           static String trainerId=null;
           static String clientId=null;
           static String bookingId=null;
           static String duration=null;
           static String specId=null;
           static String clientName=null;
           static int clientAge=0;
           static String clientGender=null;
           static Scanner userInput;
    
    
    
    
    public static void main(String[] args) throws ClassNotFoundException, IOException{
    
      //Connect to server  
     try (Socket soc = new Socket("localhost",9080);

             //ObjectStream to send/receive data
             ObjectInputStream receivingObject = new ObjectInputStream(soc.getInputStream());
             ObjectOutputStream sendingObject = new ObjectOutputStream(soc.getOutputStream());
            
            
             ) {
         
           userInput = new Scanner(System.in);
           System.out.println("Client Started!");
           boolean condition = true;
    
           
     
            while(condition==true){
            
                    System.out.println();
                    System.out.println("Please enter desired query first, then enter appropriate booking detail when prompted : ");
                    
                    System.out.printf("______________\n");
                    System.out.printf("| QUERIES    |\n");
                    System.out.printf("|____________|\n");
                    System.out.printf("| LISTALL    |\n");
                    System.out.printf("| LISTPT     |\n");
                    System.out.printf("| LISTCLIENT |\n");
                    System.out.printf("| LISTDAY    |\n");
                    System.out.printf("| ADD        |\n");
                    System.out.printf("| UPDATE     |\n");
                    System.out.printf("| DELETE     |\n");
                    System.out.printf("| QUIT       |\n");
                    System.out.printf("--------------\n");
                    
                    
                    System.out.print("Query : ");
                    
                    String queryDesired = userInput.nextLine().toUpperCase();
                   

                  //Switch case depending on queryDesired  
                  switch (queryDesired) {

                        case "LISTALL":
                            //Send query to server
                            sendingObject.writeObject(queryDesired);

                            //Response received from server    
                            status = (String) receivingObject.readObject();

                                switch (status) {

                                    case "Success":
                                        
                                        //Insert receiving object into an arraylist
                                        ArrayList<Booking> bookingArrayList = (ArrayList<Booking>) receivingObject.readObject();
                                         
                                        System.out.printf("___________________________________________________________________________________________________________________________________________________________________________________\n");
                                        System.out.printf("|%17s %17s %17s %17s %17s %17s %17s %17s %17s %16s", "  BookingId     |", "TrainerId    |","TrainerName    |", "ClientId    |","ClientName    |", "SpecId     |", "Focus     |", " BookingDate  |", " BookingTime   |", " BookingDuration|\n");
                                        
                                        //Loop through arraylist
                                         bookingArrayList.forEach((data) -> {
                                             data.print();
                                             });
                                  
                                        System.out.printf("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                                        System.out.println("\n");
                                        
                                        
                                    break;

                                    //Execute default case
                                    default:
                                        System.out.println(status);
                                        break;

                                }

                            break;

                            
                        case "LISTPT":
                             System.out.println();
                             System.out.print("Enter trainerId: ");
                             trainerId = userInput.nextLine();
                             System.out.println();
                             
                             // Error message if failed validity of data
                            if (!passedValidityOfData(trainerId, 4, 4,"PT")) {
                                System.out.println("Invalid data entered for trainerId");


                            } else {


                                sendingObject.writeObject(queryDesired+" "+trainerId);
                                   
                                // Response from server
                                 status = (String) receivingObject.readObject();

                                    switch (status) {

                                        case "Success":

                                            ArrayList<Booking> bookingArrayList = (ArrayList<Booking>) receivingObject.readObject();
                                       
                                            System.out.printf("___________________________________________________________________________________________________________________________________________________________________________________\n");
                                            System.out.printf("|%17s %17s %17s %17s %17s %17s %17s %17s %17s %16s", "  BookingId     |", "TrainerId    |", "TrainerName    |", "ClientId    |", "ClientName    |", "SpecId     |", "Focus     |", " BookingDate  |", " BookingTime   |", " BookingDuration|\n");

                                            
                                            bookingArrayList.forEach((data) -> {
                                                data.print();
                                            });

                                            System.out.printf("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                                            System.out.println("\n");
                                            break;

                                        // Error message in case of invalid Trainer Id
                                        case "Failure":
                                            System.out.println("Trainer Id does not exist in our records");
                                            break;

                                        default:
                                            System.out.println(status);
                                            break;

                                    }



                            }
                            
                            break;

                        case "LISTCLIENT":

                            System.out.println();
                            //Prompt user for input
                            System.out.print("Enter ClientId: ");
                            clientId = userInput.nextLine();
                            System.out.println();

                            //Validation method
                            if (!passedValidityOfData(clientId, 4, 4,"C0")) {
                                System.out.println("Invalid data entered for ClientId");


                            } else {


                                    // Send command to server
                                    sendingObject.writeObject(queryDesired+" "+clientId);
                                    
                                    // Status received from server
                                    status = (String) receivingObject.readObject();

                                    switch (status) {

                                        //Display data in case of a successfull response
                                        case "Success":

                                            ArrayList<Booking> bookingArrayList = (ArrayList<Booking>) receivingObject.readObject();
                                         
                                            System.out.printf("___________________________________________________________________________________________________________________________________________________________________________________\n");
                                            System.out.printf("|%17s %17s %17s %17s %17s %17s %17s %17s %17s %16s", "  BookingId     |", "TrainerId    |", "TrainerName    |", "ClientId    |", "ClientName    |", "SpecId     |", "Focus     |", " BookingDate  |", " BookingTime   |", " BookingDuration|\n");

                                            
                                            bookingArrayList.forEach((data) -> {
                                                data.print();
                                            });

                                            System.out.printf("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                                            System.out.println("\n");
                                         break;

                                        // Invalid client Id
                                        case "Failure":
                                            System.out.println("ClientId  does not exist in our records");
                                            break;

                                        // Any errors thrown by Java
                                        default:
                                            System.out.println(status);
                                            break;

                                    }

                                }


                             break;

                        case "LISTDAY":
                            System.out.println();
                            // User prompted for date
                            System.out.print("Enter date yyyy-mm-dd : ");
                            date = userInput.nextLine();
                            System.out.println();

                            // Regex to determine date format
                            if (!date.matches("[0-9]{4}[-]{1}[0-1]{1}[0-2]{1}[-]{1}[0-3]{1}[0-9]{1}")) {
                                System.out.println("Invalid date format!");


                            } else {

                                    sendingObject.writeObject(queryDesired+" "+date);
                                    

                                    status = (String) receivingObject.readObject();

                                    switch (status) {

                                        case "Success":

                                            //Convert receiving object into ArrayList of type Booking
                                            ArrayList<Booking> bookingArrayList = (ArrayList<Booking>) receivingObject.readObject();
                                         
                                            System.out.printf("___________________________________________________________________________________________________________________________________________________________________________________\n");
                                            System.out.printf("|%17s %17s %17s %17s %17s %17s %17s %17s %17s %16s", "  BookingId     |", "TrainerId    |", "TrainerName    |", "ClientId    |", "ClientName    |", "SpecId     |", "Focus     |", " BookingDate  |", " BookingTime   |", " BookingDuration|\n");

                                            
                                            bookingArrayList.forEach((data) -> {
                                                data.print();
                                            });

                                            System.out.printf("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                                            System.out.println("\n");
                                         break;


                                         //Error message
                                        case "Failure":
                                            System.out.println("No booking exist on this date in our records");
                                            break;

                                        default:
                                            System.out.println(status);
                                            break;
                                        }



                                }       


                            break;
                        case "DELETE":

                            System.out.print("Enter Booking id: ");
                            bookingId = userInput.nextLine();
                            System.out.println();

                            //Booking Id validation test
                            if (!passedValidityOfData(bookingId, 4, 4,"B0")) {
                                System.out.println("Invalid Booking id entered!");


                            } else {



                                    //Send command together with bookingId
                                    sendingObject.writeObject(queryDesired+" "+bookingId);
                                    
                                    /*
                                    * Response from server
                                    */
                                     status = (String) receivingObject.readObject();

                                    switch (status) {

                                        // Inform user in case of a successful deletion
                                        case "Success":

                                            System.out.println("Deletion completed");
                                            break;

                                         // Error messages faced by the server
                                        case "NOTEXISTS":
                                            System.out.println("DELETION FAILURE! No record exists with this BookingId");
                                            break;

                                        default:
                                            System.out.println(status);
                                            break;
                                    }


                            }
                            break;

                        case "UPDATE":

                            System.out.println();
                            System.out.print("Enter Booking id: ");
                            bookingId = userInput.nextLine();
                            System.out.println();

                            //Validity test
                            if (!passedValidityOfData(bookingId, 4, 4,"B0")) {
                                System.out.println("Invalid Booking id entered!");

                            } else {

                               /*
                                * Table to indicate fields to user
                                */
                               
                                System.out.printf("_______________________\n");
                                System.out.printf("| FIELD TO UPDATE     |\n");
                                System.out.printf("|_____________________|\n");
                                System.out.printf("| TrainerId           |\n");
                                System.out.printf("| ClientId            |\n");
                                System.out.printf("| SpecId              |\n");
                                System.out.printf("| BookingDate         |\n");
                                System.out.printf("| BookingTime         |\n");
                                System.out.printf("| BookingDuration     |\n");                           
                                System.out.printf("-----------------------\n");
                               
                         
                                 // User field input choice
                                 System.out.print("Field: ");
                                 String fieldCriteria = userInput.nextLine();
                                 System.out.println();

                                
                                switch (fieldCriteria) {

                                    // Switch statement to execute according user choice
                                    case "TrainerId":
                                    case "SpecId":

                                        System.out.print("Enter new TrainerId value:");
                                        trainerId = userInput.nextLine();
                                        System.out.println();
                                        
                                        System.out.print("Enter new Specialism Id: ");
                                        specId = userInput.nextLine();
                                        System.out.println();

                                        // Make sure valid data has been emtered for both trainerId and specId
                                        if (!passedValidityOfData(trainerId, 4, 4,"PT") && !passedValidityOfData(specId, 4, 4,"S0")) {
                                            System.out.println("Invalid data entered! Please double check Trainer id and SpecId!");

                                        } else {
                                            /*
                                            * Send queries and booking details to server
                                            */
                                            sendingObject.writeObject(queryDesired+" "+bookingId+" "+fieldCriteria+" "+trainerId+" "+specId);
                                            System.out.println();
                                           
                                            // Print response received from Server
                                            System.out.println((String) receivingObject.readObject());
                                        }

                                        break;

                                   
                                    case "ClientId":
                                        //Prompt user for client Id
                                        System.out.println();
                                        System.out.print("Enter new Client Id:");
                                        clientId = userInput.nextLine();
                                         System.out.println();

                                         //Make sure a valid client Id has been entered before proceeding
                                        if (!passedValidityOfData(clientId, 4, 4,"C0")) {
                                            System.out.println("Invalid Client id entered!");

                                        } else {
                                            sendingObject.writeObject(queryDesired+" "+bookingId+" "+fieldCriteria+" "+clientId);
                                            System.out.println();
                                            
                                            // Print server response
                                            System.out.println((String) receivingObject.readObject());
                                        }

                                          break;
                                          
                                          
                                    case "BookingDate":
                                        
                                        System.out.println();
                                        
                                        // Specify date format
                                        System.out.print("Enter date yyyy-mm-dd : ");
                                        date = userInput.nextLine();
                                        System.out.println();

                                        // Regex to check date format
                                        if (!date.matches("[0-9]{4}[-]{1}[0-1]{1}[0-2]{1}[-]{1}[0-3]{1}[0-9]{1}")) {
                                            System.out.println("Invalid date format!");


                                        }else{
                                            
                                            sendingObject.writeObject(queryDesired+" "+bookingId+" "+fieldCriteria+" "+date);
                                            System.out.println();
                                            
                                            System.out.println((String) receivingObject.readObject());


                                        }

                                        break;
                                    case "BookingTime":

                                        System.out.print("Enter time HH:MM : ");
                                         time = userInput.nextLine();
                                         
                                         // time format check
                                        if(!time.matches("[0-1]{1}[0-9]{1}[:]{1}[0-5]{1}[0-9]{1}")){
                                            System.out.println("Invalid time format!");

                                        }else{
                                            sendingObject.writeObject(queryDesired+" "+bookingId+" "+fieldCriteria+" "+time);
                                            System.out.println();
                                            
                                            System.out.println((String) receivingObject.readObject());


                                        }

                                        break;

                                    case "BookingDuration":

                                        System.out.print("Enter duration HH:MM : ");
                                        duration = userInput.nextLine();

                                        if(!duration.matches("[0-1]{1}[0-9]{1}[:]{1}[0-5]{1}[0-9]{1}")){
                                            System.out.println("Invalid duration format!");

                                        }else{
                                            sendingObject.writeObject(queryDesired+" "+bookingId+" "+fieldCriteria+" "+duration);
                                            System.out.println();
                                            
                                            //Response form server
                                            System.out.println((String) receivingObject.readObject());


                                        }


                                        break;

                                       
                                       /*
                                        * Default error message in case of invalid field entered for update process
                                        */
                                    default:
                                       System.out.println("Invalid field entered! Please double-check!");
                                        break;
                                }




                            }


                            break;
                            
                            
                        case "ADD":
                            
                            //Boolean variable to read server response if sent data
                            boolean expectServerReturnMessage=false;
                            System.out.println();
                            System.out.print("New Customer Booking [Y/N]: ");
                            String newCustomerBooking = (userInput.nextLine().toUpperCase());
                            System.out.println();
                            
                            // Check to determine whether to add details into client table
                            if(!newCustomerBooking.equals("Y") && !newCustomerBooking.equals("N")){
                           
                                System.out.println("Invalid query answer! Failure to add new booking!");
                            
                            }else if(newCustomerBooking.equals("N")){
                            
                                    // Call passedAddBookingValidation
                                if (passedAddBookingValidation("N") == true) {
                                    expectServerReturnMessage=true;
                                    // Send command to server
                                    sendingObject.writeObject(queryDesired + " " + bookingId + " " + trainerId + " " + clientId + " " + specId + " " + date + " " + time + " " + duration);
                                    
                                }

                            }else{
                            
                                if (passedAddBookingValidation("Y") == true) {
                                    expectServerReturnMessage=true;
                                   sendingObject.writeObject(queryDesired + " " + bookingId + " " + trainerId + " " + clientId + " " + clientName + " " + clientAge + " " + clientGender + " " + specId + " " + date + " " + time + " " + duration);

                                }

                            }

                                System.out.println();
                               
                                if(expectServerReturnMessage==true){
                                
                                 status = (String) receivingObject.readObject();

                                 if(status.equals("Success")){
                                 
                                     // Display confirmation message
                                     System.out.println("Addition completed");
                                 }else{
                                 
                                     System.out.println(status);
                                 }
                                 
                                }
                                
                            break;

                        case "QUIT":
                            // Exit while loop
                            condition = false;
                            sendingObject.writeObject("QUIT");
                            
                            //Close Object stream
                            sendingObject.close();
                            receivingObject.close();
                            System.out.println("Exiting program!");
                            
                            // Close socket
                            soc.close();
                            break;

                            
                        default:
                            /*Error message in case of invalid command*/
                            System.out.println("Invalid command!");
                            break;
                    }


            
            }
            
     //Catch IO exception
    }catch (IOException e) {
       System.err.println("Couldn't get I/O for the connection to ");
       System.exit(1);
    }
    
    }
    
    /*Test if booking detail entered is a valid one*/
    public static boolean passedValidityOfData(String data,int minLength,int maxlength,String startingPrefix){
        boolean validData = false;

        if(data.isEmpty() || data.length()>maxlength || data.length()<minLength || !data.startsWith(startingPrefix)){
            validData=false;
        }else{
            validData=true;
        }

        return validData;

    }
    
    /*Check validity of data for addition of nooking*/
    public  static boolean passedAddBookingValidation(String newCustomerBookingCriteria){
    
    boolean validData=false;
    
    switch(newCustomerBookingCriteria){
    
        case "N":

            while (true) {

                System.out.print("Enter booking Id to be assigned: ");
                bookingId = userInput.nextLine();
                System.out.println();

                if (!passedValidityOfData(bookingId, 4, 4, "B0")) {
                    System.out.println("Invalid booking id type entered! Please follow our protocols for bookingId, B0xx");
                    break;
                }

                System.out.print("Enter personal trainer id for the new booking session: ");
                trainerId = userInput.nextLine();
                System.out.println();

                if (!passedValidityOfData(trainerId, 4, 4, "PT")) {
                    System.out.println("Invalid trainer id type entered! Please follow our protocols for trainerId, PTxx");
                    break;
                }

                System.out.print("Enter client id for the new booking session: ");
                clientId = userInput.nextLine();
                System.out.println();

                if (!passedValidityOfData(clientId, 4, 4, "C0")) {
                    System.out.println("Invalid client id type entered! Please follow our protocols for clientId, C0xx");
                    break;
                }

                System.out.print("Enter specialism id for the new booking session: ");
                specId = userInput.nextLine();
                System.out.println();

                if (!passedValidityOfData(specId, 4, 4, "S0")) {
                    System.out.println("Invalid specialism id type entered! Please follow our protocols for specialismId, S0xx");
                    break;
                }

                System.out.print("Enter booking date for the new booking session [yyyy-mm-dd]: ");
                date = userInput.nextLine();
                System.out.println();

                if (!date.matches("[0-9]{4}[-]{1}[0-1]{1}[0-2]{1}[-]{1}[0-3]{1}[0-9]{1}")) {
                    System.out.println("Invalid date format entered for the new booking session!");
                    break;
                }

                System.out.print("Enter booking time for the new booking session time [HH:MM]: ");
                time = userInput.nextLine();
                System.out.println();

                if (!time.matches("[0-1]{1}[0-9]{1}[:]{1}[0-5]{1}[0-9]{1}")) {
                    System.out.println("Invalid time format entered for the new booking session!");
                    break;
                }

                System.out.print("Enter duration HH:MM : ");
                duration = userInput.nextLine();
                System.out.println();

                if (!duration.matches("[0-1]{1}[0-9]{1}[:]{1}[0-5]{1}[0-9]{1}")) {
                    System.out.println("Invalid duration format entered for the new booking session!");
                    break;

                }

                // Below statement will be reached only if all criteria has been met
                validData=true;
               

                break;
            }

            break;

        case "Y":

            while (true) {

                System.out.print("Enter booking Id to be assigned: ");
                bookingId = userInput.nextLine();
                System.out.println();

                if (!passedValidityOfData(bookingId, 4, 4, "B0")) {
                    System.out.println("Invalid booking id type entered! Please follow our protocols for bookingId, B0xx");
                    break;
                }

                System.out.print("Enter personal trainer id for the new booking session: ");
                trainerId = userInput.nextLine();
                System.out.println();

                if (!passedValidityOfData(trainerId, 4, 4, "PT")) {
                    System.out.println("Invalid trainer id type entered! Please follow our protocols for trainerId, PTxx");
                    break;
                }

                System.out.print("Enter client id for the new booking session: ");
                clientId = userInput.nextLine();
                System.out.println();

                if (!passedValidityOfData(clientId, 4, 4, "C0")) {
                    System.out.println("Invalid client id type entered! Please follow our protocols for clientId, C0xx");
                    break;
                }

                System.out.print("Enter new client name: ");
                clientName = userInput.nextLine();
                System.out.println();

                /*Make sure client name contains only non-numeric characters*/
                if (!clientName.matches("^[a-zA-Z]+$")) {
                    System.out.println("Invalid client name type entered! Please try again");
                    break;
                }

                System.out.print("Enter new client age: ");
                

                try {

                    clientAge = Integer.parseInt(userInput.nextLine());

                    if (clientAge < 0 || clientAge > 110) {

                        System.out.println("Invalid client age entered! Please try again");
                        break;
                    }

                } catch (NumberFormatException e) {

                    System.out.println("Invalid client age entered! Please try again");
                    break;
                }
                System.out.println();
                System.out.print("Enter new client gender [M/F]: ");
                clientGender = userInput.nextLine().toUpperCase();
                System.out.println();

                if (!clientGender.equals("M") && !clientGender.equals("F")) {

                    System.out.println("Invalid client gender entered! Please try again");
                    break;

                }

                System.out.print("Enter specialism id for the new booking session: ");
                specId = userInput.nextLine();
                System.out.println();

                if (!passedValidityOfData(specId, 4, 4, "S0")) {
                    System.out.println("Invalid specialism id type entered! Please follow our protocols for specialismId, S0xx");
                    break;
                }

                System.out.print("Enter booking date for the new booking session [yyyy-mm-dd]: ");
                date = userInput.nextLine();
                System.out.println();

                if (!date.matches("[0-9]{4}[-]{1}[0-1]{1}[0-2]{1}[-]{1}[0-3]{1}[0-9]{1}")) {
                    System.out.println("Invalid date format entered for the new booking session!");
                    break;
                }

                System.out.print("Enter booking time for the new booking session time [HH:MM]: ");
                time = userInput.nextLine();
                System.out.println();

                if (!time.matches("[0-1]{1}[0-9]{1}[:]{1}[0-5]{1}[0-9]{1}")) {
                    System.out.println("Invalid time format entered for the new booking session!");
                    break;
                }

                System.out.print("Enter duration HH:MM : ");
                duration = userInput.nextLine();
                System.out.println();

                if (!duration.matches("[0-1]{1}[0-9]{1}[:]{1}[0-5]{1}[0-9]{1}")) {
                    System.out.println("Invalid duration format entered for the new booking session!");
                    break;

                }

                validData=true;

                break;

            }


    
            break;
    
    }

    return validData;
    
    
    
}
}
