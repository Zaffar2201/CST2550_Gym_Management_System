import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;



/*Runnable class*/
public class ServerRunnable implements Runnable{
    
    /*Attributes*/
    private final Socket socket;
    static Connection connect = null;
    static boolean condition=true;
    static String errorReason;
    String bookingId;
    String trainerId;
    String clientId;
    String bookingDate;
    String bookingTime;
    String bookingDuration;
    String specId;
    String column;
    String newData;
    
    /*Constructor*/
    public ServerRunnable(Socket soc){
	socket = soc;
    }
    
    /*Multi threaded run method*/
    public void run() {

        try (
                /*Object stream objects*/
                ObjectOutputStream sendingObj = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream receivingObj = new ObjectInputStream(socket.getInputStream());) {

                System.out.println("Server started!");

                while (condition == true) {
                    
                    /*Read command from client*/
                    String commandReceived = (String) receivingObj.readObject();

                    /*Split command in sections*/
                    String split[] = commandReceived.split(" ", 0);
                    String sqlRequest = split[0];

                    switch (sqlRequest) {

                        case "LISTALL":

                            //Return an arraylist of booking type
                            ArrayList<Booking> booking = listAll();

                            /*Send object and confirmation message to client*/
                            sendingObj.writeObject("Success");
                            sendingObj.writeObject(booking);

                        break;

                        case "LISTPT":
                            /*Retrieve trainerId*/
                            trainerId = split[1];

                            //Checks if trainerId exist in records
                            if (!checkIfRecordExists(trainerId, "LISTPT")) {

                                sendingObj.writeObject("Failure");

                            } else {
                                sendingObj.writeObject("Success");

                                ArrayList<Booking> bookingArrayList = listPT(trainerId);

                                //Send object result to client
                                sendingObj.writeObject(bookingArrayList);

                            }

                        break;

                        case "LISTCLIENT":
                            
                            // Retrieve clientId
                            clientId = split[1];

                            if (!checkIfRecordExists(clientId, "LISTCLIENT")) {

                                sendingObj.writeObject("Failure");

                            } else {
                                sendingObj.writeObject("Success");
                                ArrayList<Booking> bookingArrayList = listClient(clientId);

                                //Send object result to client
                                sendingObj.writeObject(bookingArrayList);

                            }

                        break;
                        case "LISTDAY":

                            bookingDate = split[1];

                            // Check if bookingDate exists in records
                            if (!checkIfRecordExists(bookingDate, "LISTDAY")) {

                                sendingObj.writeObject("Failure");

                             } else {
                                sendingObj.writeObject("Success");
                                ArrayList<Booking> bookingArrayList = listDate(bookingDate);

                                sendingObj.writeObject(bookingArrayList);

                            }

                        break;

                    case "DELETE":

                        bookingId = split[1];

                        if (!checkIfRecordExists(bookingId, "DELETE")) {

                            sendingObj.writeObject("NOTEXISTS");

                        } else {

                            // Create an object of booking
                            Booking book = new Booking();
                            
                            /*Determine if deletion is a successfull one*/
                            if (book.deleteBooking(bookingId) == 1) {
                                sendingObj.writeObject("Success");
                            } else {
                                sendingObj.writeObject("Failure");
                            }

                        }

                        break;

                    case "UPDATE":

                        bookingId = split[1];
                        
                        /*Check wether bookingId exist or not*/
                        if (!checkIfRecordExists(bookingId, "BookingIdCheck")) {

                            sendingObj.writeObject(bookingId + "  does not exist in our record! Request denied!");

                        } else {
                            
                            /*
                             * Separate into sections
                             * column being fields in the database
                             * newData being the new updated data
                             */
                            column = split[2];
                            newData = split[3];

                            if (column.equals("TrainerId") || column.equals("SpecId")) {
                                
                                /*Retrieve specId incase of updating trainerId or specId fields*/
                                specId = split[4];

                            } else {
                                /*Default specId value*/
                                specId = "0";
                            }

                            /*Checks if table has been updated*/
                            if (updateBooking(bookingId, column, newData, specId) == 1) {

                                sendingObj.writeObject("Record has been updated");

                            } else {

                                /*Output error reason to client*/
                                sendingObj.writeObject(errorReason);

                            }

                        }

                        break;

                    case "ADD":

                        /*Retrieve new bookingId*/
                        bookingId = split[1];

                        /*Prevent addition in case of existing bookingId*/
                        if (checkIfRecordExists(bookingId, "BookingIdCheck")) {

                            sendingObj.writeObject(bookingId + "  already exist in our record! Addition request denied!");

                        } else {

                            // To determine whether add new client to record or not
                            int splitArrayLength = split.length;

                            String clientName = null;
                            int clientAge = 0;
                            String clientGender = null;
                            String addType = null;

                            // Arraylength is greater in case of new client
                            switch (splitArrayLength) {

                                case 8:

                                    //Retrieve new details
                                    trainerId = split[2];
                                    clientId = split[3];
                                    specId = split[4];
                                    bookingDate = split[5];
                                    bookingTime = split[6];
                                    bookingDuration = split[7];
                                    addType = "ExistingClientBooking";

                                    break;

                                default:

                                    trainerId = split[2];
                                    clientId = split[3];
                                    clientName = split[4];
                                    clientAge = Integer.parseInt(split[5]);
                                    clientGender = split[6];
                                    specId = split[7];
                                    bookingDate = split[8];
                                    bookingTime = split[9];
                                    bookingDuration = split[10];
                                    addType = "NewClient";

                                    break;

                            }
                                //Checks if new data to be entered is unique
                            if (addDuplicateBookingDetected(bookingId, trainerId, clientId, clientName, clientAge, clientGender, specId, bookingDate, bookingTime, bookingDuration, addType) == false) {

                                /*Create booking object*/
                                Booking bookingRecord = new Booking();

                                if (bookingRecord.addNewBooking(bookingId, trainerId, clientId, clientName, clientAge, clientGender, specId, bookingDate, bookingTime, bookingDuration, addType) == 1) {

                                    //Send successful message if addition is completed
                                    sendingObj.writeObject("Success");
                                }

                            } else {

                                //Write error message to client
                                sendingObj.writeObject(errorReason);

                            }

                        }

                        break;

                    case "QUIT":
                        /*Close all connections for current client*/
                        condition = false;
                        receivingObj.close();
                        sendingObj.close();
                        socket.close();
                        break;

                }

            }
           /*Exception caught*/
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Exception caught when trying to listen on port");
            
        }

    }
     /*Check if parameter entered exists in database*/
     public static boolean checkIfRecordExists(String data, String criteria) {

        boolean found = false;
        String q = null;

        try {
            /*Database connection*/
             connect = new DbConnection().getConnection();

            switch (criteria) {
                //Queries
                case "LISTCLIENT":

                    q = "SELECT * FROM Booking WHERE ClientId = ? ";
                    break;

                case "LISTPT":

                    q = "SELECT * FROM Booking WHERE TrainerId =?";
                    break;

                case "LISTDAY":
                    q = "SELECT * FROM Booking WHERE BookingDate =?";
                    break;

                case "DELETE":
                case "BookingIdCheck":
                    q = "SELECT * FROM Booking WHERE BookingId =?";

                    break;


            }

            //PreapredStatement
            PreparedStatement prepS = connect.prepareStatement(q);

            prepS.setString(1, data);

            ResultSet rset = prepS.executeQuery();


            if (rset.isBeforeFirst()) {
                found = true;
            } else {
                found = false;
            }

            prepS.close();
            connect.close();

        } catch (SQLException ex) {

            System.out.println("Error: "+ex.getMessage());
        }

        return found;


    }
     /*List all bookings: Perform query then add into Arraylist*/
    public static ArrayList<Booking> listAll() {
        ArrayList<Booking> booking = new ArrayList<>();

        try {
            connect = new DbConnection().getConnection();
            Statement stm = connect.createStatement();
            String q = "Select BookingId,Booking.TrainerId,Trainer.TrainerName,Booking.ClientId,Client.ClientName,Booking.SpecId,Specialism.Focus,BookingDate,BookingTime,BookingDuration FROM Booking,Trainer,Client,Specialism WHERE Booking.TrainerId=Trainer.TrainerId AND Booking.ClientId=Client.ClientId AND Booking.SpecId=Specialism.SpecId ORDER BY BookingId";
            ResultSet rset = stm.executeQuery(q);

            while (rset.next()) {

                /*Add into Arraylist*/
                booking.add(new Booking(rset.getString("BookingId"),
                        rset.getString("TrainerID"),
                        rset.getString("TrainerName"),
                        rset.getString("ClientId"),
                        rset.getString("ClientName"),
                        rset.getString("SpecId"),
                        rset.getString("Focus"),
                        rset.getString("BookingDate"),
                        rset.getString("BookingTime"),
                        rset.getString("BookingDuration")
                        ));


            }

            connect.close();
        } catch (SQLException ex) {

           System.out.println("Error: "+ex.getMessage());
        }


        return booking;
    }

    /*Listt bookings based on trainerId*/
    public static ArrayList<Booking> listPT(String trainerId) {
        ArrayList<Booking> booking = new ArrayList<>();

        try {

            connect = new DbConnection().getConnection();
           

            String q = "Select BookingId,Booking.TrainerId,Trainer.TrainerName,Booking.ClientId,Client.ClientName,Booking.SpecId,Specialism.Focus,BookingDate,BookingTime,BookingDuration FROM Booking,Trainer,Client,Specialism WHERE Booking.TrainerId=Trainer.TrainerId AND Booking.ClientId=Client.ClientId AND Booking.SpecId=Specialism.SpecId AND Booking.TrainerId = ? ORDER BY BookingId";
            PreparedStatement prepS = connect.prepareStatement(q);
            prepS.setString(1, trainerId);

            ResultSet rset = prepS.executeQuery();

            while (rset.next()) {

                booking.add(new Booking(rset.getString("BookingId"),
                        rset.getString("TrainerID"),
                        rset.getString("TrainerName"),
                        rset.getString("ClientId"),
                        rset.getString("ClientName"),
                        rset.getString("SpecId"),
                        rset.getString("Focus"),
                        rset.getString("BookingDate"),
                        rset.getString("BookingTime"),
                        rset.getString("BookingDuration")
                        ));

            }
            //Close connection
            prepS.close();
            connect.close();
        } catch (SQLException ex) {
            System.out.println("Error: "+ex.getMessage());

        }


        return booking;
    }

    /*List client booking based on parameter(clientId)*/
    public static ArrayList<Booking> listClient(String clientId) {
        ArrayList<Booking> booking = new ArrayList<>();

        try {

            connect = new DbConnection().getConnection();
            

            String q = "Select BookingId,Booking.TrainerId,Trainer.TrainerName,Booking.ClientId,Client.ClientName,Booking.SpecId,Specialism.Focus,BookingDate,BookingTime,BookingDuration FROM Booking,Trainer,Client,Specialism WHERE Booking.TrainerId=Trainer.TrainerId AND Booking.ClientId=Client.ClientId AND Booking.SpecId=Specialism.SpecId AND Booking.ClientId = ? ORDER BY BookingId";
            PreparedStatement prepS = connect.prepareStatement(q);
            prepS.setString(1, clientId);

            ResultSet rset = prepS.executeQuery();

            //Loop until result set has data
            while (rset.next()) {

                booking.add(new Booking(rset.getString("BookingId"),
                        rset.getString("TrainerID"),
                        rset.getString("TrainerName"),
                        rset.getString("ClientId"),
                        rset.getString("ClientName"),
                        rset.getString("SpecId"),
                        rset.getString("Focus"),
                        rset.getString("BookingDate"),
                        rset.getString("BookingTime"),
                        rset.getString("BookingDuration")
                ));

            }
            prepS.close();
            connect.close();
        } catch (SQLException ex) {
	    System.out.println("Error:" +ex.getMessage());

        }

        return booking;
    }

    /*List booking on specific date base on data parameter*/
    public static ArrayList<Booking> listDate(String date) {
        ArrayList<Booking> booking = new ArrayList<>();

        try {

             connect = new DbConnection().getConnection();
            

            String q = "Select BookingId,Booking.TrainerId,Trainer.TrainerName,Booking.ClientId,Client.ClientName,Booking.SpecId,Specialism.Focus,BookingDate,BookingTime,BookingDuration FROM Booking,Trainer,Client,Specialism WHERE Booking.TrainerId=Trainer.TrainerId AND Booking.ClientId=Client.ClientId AND Booking.SpecId=Specialism.SpecId AND BookingDate = ? ORDER BY BookingId";
            PreparedStatement prepS = connect.prepareStatement(q);
            prepS.setString(1, date);

            ResultSet rset = prepS.executeQuery();

            while (rset.next()) {

               booking.add(new Booking(rset.getString("BookingId"),
                        rset.getString("TrainerID"),
                        rset.getString("TrainerName"),
                        rset.getString("ClientId"),
                        rset.getString("ClientName"),
                        rset.getString("SpecId"),
                        rset.getString("Focus"),
                        rset.getString("BookingDate"),
                        rset.getString("BookingTime"),
                        rset.getString("BookingDuration")
                        ));

            }
            prepS.close();
            connect.close();
        } catch (SQLException ex) {
            System.out.println("Error: "+ex.getMessage());

        }


        return booking;
    }

    /*Actually Uupdate booking details in database*/
    public static int updateBooking(String bookingId, String column, String newData, String specId) {

        int recordsUpdated = 0;
        String q = null;

        try {

            if (column.equals("TrainerId")) {

                if (updateDuplicateBookingDetected(newData, column, bookingId, specId) == false) {

                    //Create an object of booking and update boooking if passed all checks
                    Booking bookingRecord = new Booking();
                    recordsUpdated = bookingRecord.updateBooking(newData, column, bookingId, specId);

                }

            } else if (column.equals("ClientId")) {

                if (updateDuplicateBookingDetected(newData, column, bookingId, specId) == false) {

                    Booking bookingRecord = new Booking();
                    recordsUpdated = bookingRecord.updateBooking(newData, column, bookingId, specId);
                    
                }

            } else if (column.equals("BookingDate")) {

                if (updateDuplicateBookingDetected(newData, column, bookingId, specId) == false) {

                    Booking bookingRecord = new Booking();
                    recordsUpdated = bookingRecord.updateBooking(newData, column, bookingId, specId);
                    
                }

            } else if (column.equals("BookingTime")) {

                if (updateDuplicateBookingDetected(newData, column, bookingId, specId) == false) {

                    Booking bookingRecord = new Booking();
                    recordsUpdated = bookingRecord.updateBooking(newData, column, bookingId, specId);
                    
                }

            } else if (column.equals("BookingDuration")) {

                Booking bookingRecord = new Booking();
                recordsUpdated = bookingRecord.updateBooking(newData, column, bookingId, specId);
                System.out.println("Records updated:" + recordsUpdated);

            } else if (column.equals("SpecId")) {

                if (updateDuplicateBookingDetected(newData, column, bookingId, specId) == false) {

                    Booking bookingRecord = new Booking();
                    recordsUpdated = bookingRecord.updateBooking(newData, column, bookingId, specId);
                   

                }

            }

            // Close db connection
            connect.close();

        } catch (SQLException ex) {
            System.out.println("Error : " + ex.getMessage());
        }

        return recordsUpdated;
    }

    /*This method checks for duplicate data before updating a booking detail*/
    public static boolean updateDuplicateBookingDetected(String data, String column, String bookingId, String specId) {

        boolean NotProceedUpdateProcess = false;
        boolean InvalidTrainerId = false;
        boolean InvalidSpecId = false;
        boolean InvalidTrainerSpec = false;

        String query = null;
        PreparedStatement prepS = null;
        ResultSet rset = null;

        try {

            connect = new DbConnection().getConnection();

            switch (column) {

                case "TrainerId":
                case "SpecId":

                    /*
                    * Checks if new Trainer Id entered exists in Trainer Table
                     */
                    query = "SELECT TrainerId FROM Trainer WHERE TrainerId = ?";

                    prepS = connect.prepareStatement(query);
                    prepS.setString(1, data);

                    rset = prepS.executeQuery();

                    if (!rset.isBeforeFirst()) {
                        InvalidTrainerId = true;
                        NotProceedUpdateProcess = true;

                    }

                    /*
                    * Checks if new Spec Id entered exists in Specialism Table
                     */
                    query = "SELECT SpecId FROM Specialism WHERE SpecId = ?";

                    prepS = connect.prepareStatement(query);
                    prepS.setString(1, specId);

                    rset = prepS.executeQuery();

                    if (!rset.isBeforeFirst()) {
                        InvalidSpecId = true;
                        NotProceedUpdateProcess = true;

                    }

                    /*
                    * Checks if new Trainer Id perform specified SpecId
                     */
                    query = "SELECT TrainerId FROM Specialism WHERE TrainerId = ? AND SpecId = ?";

                    prepS = connect.prepareStatement(query);
                    prepS.setString(1, data);
                    prepS.setString(2, specId);

                    rset = prepS.executeQuery();

                    if (!rset.isBeforeFirst()) {
                        InvalidTrainerSpec = true;
                        NotProceedUpdateProcess = true;

                    }

                    /*Error reasons to inform user*/
                    if ((InvalidTrainerId == true) && (InvalidSpecId == true) && (InvalidTrainerSpec == true)) {
                        NotProceedUpdateProcess = true;
                        errorReason = "Failure! Please make sure to enter a valid TrainerId which performs a specified valid SpecialismId";
                    } else if (InvalidTrainerId == true) {
                        NotProceedUpdateProcess = true;
                        errorReason = "Failure! Please make sure to enter a TrainerId which exists in our record";
                    } else if (InvalidSpecId == true) {
                        NotProceedUpdateProcess = true;
                        errorReason = "Failure! Please make sure to enter a SpecialismId which exists in our record";
                    } else if (InvalidTrainerSpec == true) {
                        NotProceedUpdateProcess = true;
                        errorReason = "Failure! Current trainer does not perfoms this specialismId";

                    } else {

                        /*
                       * Checks if new Trainer Id is already booked on that date and time
                         */
                        query = "SELECT * FROM Booking WHERE BookingDate=(SELECT BookingDate FROM Booking WHERE BookingId =?) AND BookingTime=(SELECT BookingTime FROM Booking WHERE BookingId = ?) AND TrainerId = ? AND SpecId = ?";

                        prepS = connect.prepareStatement(query);
                        prepS.setString(1, bookingId);
                        prepS.setString(2, bookingId);
                        prepS.setString(3, data);
                        prepS.setString(4, specId);

                        rset = prepS.executeQuery();

                        if (rset.isBeforeFirst()) {
                            NotProceedUpdateProcess = true;
                            errorReason = "Personal Trainer Id " + data + " is already booked on that date and time";
                        }

                    }

                    break;

                case "ClientId":
                    // Check if customerId exist in Client Table

                    query = "SELECT ClientId FROM Client WHERE ClientId = ?";

                    prepS = connect.prepareStatement(query);
                    prepS.setString(1, data);

                    rset = prepS.executeQuery();

                    if (!rset.isBeforeFirst()) {
                        NotProceedUpdateProcess = true;
                        errorReason = "Failure! Client with id " + data + " does not exist in our record!";

                    } else {

                        // Check if customer is already booked on that date and time
                        query = "SELECT * FROM Booking WHERE BookingDate=(SELECT BookingDate FROM Booking WHERE BookingId =?) AND BookingTime=(SELECT BookingTime FROM Booking WHERE BookingId = ?) AND ClientId = ?";

                        prepS = connect.prepareStatement(query);
                        prepS.setString(1, bookingId);
                        prepS.setString(2, bookingId);
                        prepS.setString(3, data);

                        rset = prepS.executeQuery();

                        if (rset.isBeforeFirst()) {
                            NotProceedUpdateProcess = true;
                            errorReason = "Client id " + data + " is already booked on that date and time";
                        }

                    }
                    break;

                case "BookingDate":

                    // Check if current booking Trainer is already booked on the new date at the same time
                    query = "SELECT COUNT(*), BookingTime,TrainerId FROM Booking WHERE BookingDate=? AND TrainerId=(SELECT TrainerId FROM Booking WHERE BookingId=?) AND BookingTime=(SELECT BookingTime FROM Booking WHERE BookingId = ?) GROUP BY BookingTime,TrainerId HAVING COUNT(*) > 0";
                    prepS = connect.prepareStatement(query);
                    prepS.setString(1, data);
                    prepS.setString(2, bookingId);
                    prepS.setString(3, bookingId);

                    rset = prepS.executeQuery();

                    if (rset.isBeforeFirst()) {
                        NotProceedUpdateProcess = true;
                        errorReason = "Current Personal trainer  is already booked on that date and time";
                        break;

                    } else {

                        // Checks if current booking client is already booked on the new date at the same time
                        query = "SELECT COUNT(*), BookingTime,ClientId FROM Booking WHERE BookingDate=? AND ClientId=(SELECT ClientId FROM Booking WHERE BookingId=?) AND BookingTime=(SELECT BookingTime FROM Booking WEHRE BookingId=?) GROUP BY BookingTime,ClientId HAVING COUNT(*) > 0";
                        prepS = connect.prepareStatement(query);
                        prepS.setString(1, data);
                        prepS.setString(2, bookingId);
                        prepS.setString(3,bookingId);

                        ResultSet rset2 = prepS.executeQuery();

                        if (rset2.isBeforeFirst()) {
                            NotProceedUpdateProcess = true;
                            errorReason = "Current client  is already booked on that date and time";

                        }

                    }

                    break;

                case "BookingTime":

                    // Check if current booking Trainer is already booked at the new time on the same date
                    query = "SELECT COUNT(*), BookingDate,TrainerId FROM Booking WHERE BookingTime=? AND TrainerId=(SELECT TrainerId FROM Booking WHERE BookingId=?) AND BookingDate =(SELECT BookingDate FROM Booking WHERE BookingId=?)GROUP BY BookingDate,TrainerId HAVING COUNT(*) > 0";
                    prepS = connect.prepareStatement(query);
                    prepS.setString(1, data);
                    prepS.setString(2, bookingId);
                    prepS.setString(3, bookingId);

                    rset = prepS.executeQuery();

                    if (rset.isBeforeFirst()) {
                        NotProceedUpdateProcess = true;
                        errorReason = "Current personal trainer  is already booked on that date and time";
                        break;

                    } else {

                        // Check if current booking client is already booked at the new time on the same date
                        query = "SELECT COUNT(*), BookingDate,ClientId FROM Booking WHERE BookingTime=? AND ClientId=(SELECT ClientId FROM Booking WHERE BookingId=?) AND BookingDate(SELECT BookingDate FROM Booking WHERE BookingId = ?) GROUP BY BookingDate,ClientId HAVING COUNT(*) > 0";
                        prepS = connect.prepareStatement(query);
                        prepS.setString(1, data);
                        prepS.setString(2, bookingId);
                        prepS.setString(3, bookingId);

                        ResultSet rset2 = prepS.executeQuery();

                        if (rset2.isBeforeFirst()) {
                            NotProceedUpdateProcess = true;
                            errorReason = "Current client  is already booked on that date and time";

                        }

                    }

                    break;

                case "BookingDuration":
                    NotProceedUpdateProcess = false;
                    break;

            }

            prepS.close();
            connect.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return NotProceedUpdateProcess;
    }
    
    /*This method checks if adding new booking details will result to duplicate booking*/
    public static boolean addDuplicateBookingDetected(String bookingID, String trainerID, String clientID, String clientNAME, int clientAGE, String clientGender, String specID, String bookingDATE, String bookingTIME, String bookingDURATION, String bookingType) {

        boolean NotProceedAddBookingProcess = true;

        String query = null;
        PreparedStatement prepS;
        ResultSet rset = null;

        switch (bookingType) {

            case "ExistingClientBooking":

                while (true) {
                    try {

                        connect = new DbConnection().getConnection();

                        query = "SELECT TrainerId FROM Trainer WHERE TrainerId = ?";

                        prepS = connect.prepareStatement(query);
                        prepS.setString(1, trainerID);

                        rset = prepS.executeQuery();

                        /*Exit loop in case of duplicate data*/
                        if (!rset.isBeforeFirst()) {
                            errorReason = "Failure! Please make sure to enter a TrainerId which exists in our record";
                            break;
                        }

                        query = "SELECT ClientId FROM Client WHERE ClientId = ?";

                        prepS = connect.prepareStatement(query);
                        prepS.setString(1, clientID);

                        rset = prepS.executeQuery();

                        if (!rset.isBeforeFirst()) {

                            errorReason = "Failure! Client with id " + clientID + " does not exist in our record!";
                            break;
                        }

                        query = "SELECT SpecId FROM Specialism WHERE SpecId = ?";

                        prepS = connect.prepareStatement(query);
                        prepS.setString(1, specID);

                        rset = prepS.executeQuery();

                        if (!rset.isBeforeFirst()) {
                            errorReason = "Failure! Specialism with id " + specID + " does not exist in our record!";
                            break;
                        }

                        query = "SELECT * FROM Booking WHERE BookingDate=? AND BookingTime= ? AND TrainerId = ?";

                        prepS = connect.prepareStatement(query);
                        prepS.setString(1, bookingDATE);
                        prepS.setString(2, bookingTIME);
                        prepS.setString(3, trainerID);

                        rset = prepS.executeQuery();

                        if (rset.isBeforeFirst()) {

                            errorReason = "Personal Trainer Id " + trainerID + " is already booked on that date and time";
                            break;
                        }

                        query = "SELECT * FROM Booking WHERE BookingDate=? AND BookingTime= ? AND ClientId = ?";

                        prepS = connect.prepareStatement(query);
                        prepS.setString(1, bookingDATE);
                        prepS.setString(2, bookingTIME);
                        prepS.setString(3, clientID);

                        rset = prepS.executeQuery();

                        if (rset.isBeforeFirst()) {

                            errorReason = "Client Id " + clientID + " is already booked on that date and time";
                            break;
                        }

                        query = "SELECT TrainerId FROM Specialism WHERE TrainerId = ? AND SpecId = ?";

                        prepS = connect.prepareStatement(query);
                        prepS.setString(1, trainerID);
                        prepS.setString(2, specID);

                        rset = prepS.executeQuery();

                        if (!rset.isBeforeFirst()) {
                            errorReason = "Trainer " + trainerID + " does not perform specialism " + specID;
                            break;

                        }

                        prepS.close();
                        connect.close();

                    } catch (SQLException e) {
                        System.out.println("Error: " + e.getMessage());

                    }

                    NotProceedAddBookingProcess = false;

                    break;

                }
                break;

            default:

                while (true) {

                    try {

                        connect = new DbConnection().getConnection();

                        query = "SELECT TrainerId FROM Trainer WHERE TrainerId = ?";

                        prepS = connect.prepareStatement(query);
                        prepS.setString(1, trainerID);

                        rset = prepS.executeQuery();

                        if (!rset.isBeforeFirst()) {
                            errorReason = "Failure! Please make sure to enter a TrainerId which exists in our record";
                            break;
                        }

                        query = "SELECT ClientId FROM Client WHERE ClientId = ?";

                        prepS = connect.prepareStatement(query);
                        prepS.setString(1, clientID);

                        rset = prepS.executeQuery();

                        if (rset.isBeforeFirst()) {

                            errorReason = "Failure! Client with id " + clientID + " already exist in our record!";
                            break;
                        }

                        query = "SELECT SpecId FROM Specialism WHERE SpecId = ?";

                        prepS = connect.prepareStatement(query);
                        prepS.setString(1, specID);

                        rset = prepS.executeQuery();

                        if (!rset.isBeforeFirst()) {
                            errorReason = "Failure! Specialism with id " + specID + " does not exist in our record!";
                            break;
                        }

                        query = "SELECT * FROM Booking WHERE BookingDate=? AND BookingTime= ? AND TrainerId = ?";

                        prepS = connect.prepareStatement(query);
                        prepS.setString(1, bookingDATE);
                        prepS.setString(2, bookingTIME);
                        prepS.setString(3, trainerID);

                        rset = prepS.executeQuery();

                        if (rset.isBeforeFirst()) {

                            errorReason = "Personal Trainer Id " + trainerID + " is already booked on that date and time";
                            break;
                        }

                        query = "SELECT TrainerId FROM Specialism WHERE TrainerId = ? AND SpecId = ?";

                        prepS = connect.prepareStatement(query);
                        prepS.setString(1, trainerID);
                        prepS.setString(2, specID);

                        rset = prepS.executeQuery();

                        if (!rset.isBeforeFirst()) {
                            errorReason = "Trainer " + trainerID + " does not perform specialism " + specID;
                            break;

                        }

                        prepS.close();
                        connect.close();

                    } catch (SQLException e) {

                        System.out.println("Error: " + e.getMessage());
                    }

                    NotProceedAddBookingProcess = false;
                    break;
                }

                break;
        }

        return NotProceedAddBookingProcess;

    }
    
 
    
}
