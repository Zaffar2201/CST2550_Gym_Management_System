import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.*;


public class Booking implements Serializable {

   /*Private attributes for Booking class*/
   private String bookingId;
   private String trainerId;
   private String trainerName;
   private String clientId;
   private String clientName;
   private String specId;
   private String focus;
   private String bookingDate;
   private String bookingTime;
   private String bookingDuration;
   
    // Thread locks
    Lock bookingLock;
    
    //Database conection
    Connection connect3;


    //Constructor
    public Booking(String bookingId,String trainerId,String trainerName,String clientId,String clientName,String specId,String focus,String bookingDate,String bookingTime,String bookingDuration){
        this.bookingId = bookingId;
        this.trainerId=trainerId;
        this.trainerName = trainerName;
        this.clientId = clientId;
        this.clientName = clientName;
        this.specId=specId;
        this.focus= focus;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.bookingDuration = bookingDuration;
        

    }

    public Booking(){
        //Reentrant lock - no fairness
        this.bookingLock = new ReentrantLock();
    }
    
                /*Setters*/
     public void setBookingId(String bookId){
        this.bookingId = bookId;
    }

    public void setTrainerId(String trainerId){
        this.trainerId = trainerId;
    }

    public void setTrainerName(String trainerName){
        this.trainerName = trainerName;
    }

    public void setClientId(String clientId){
        this.clientId = clientId;
    }

    public void setClientName(String clientName){
        this.clientName = clientName;
    }

    public void setSpecId(String specId){
        this.specId = specId;
    }

    public void setFocus(String focus){
        this.focus = focus;
    }

    public void setBookingDate(String bookingDate){
        this.bookingDate = bookingDate;
    }

    public void setBookingTime(String bookingTime){
        this.bookingTime = bookingTime;
    }

    public void setBookingDuration(String bookingDuration){
        this.bookingDuration = bookingDuration;
    }

    
            /*Getters*/
    public String getBookingId(){
        return bookingId;
    }

    public String getTrainerId(){
        return trainerId;
    }
    public String getTrainerName(){
        return trainerName;
    }

    public String getClientId(){
        return clientId;
    }
    public String getClientName(){
        return clientName;
    }
    public String getSpecId(){
        return specId;
    }
    public String getFocus(){
        return focus;
    }

    public String getBookingDate(){
        return bookingDate;
    }

    public String getBookingTime(){
        return bookingTime;
    }

    public String getBookingDuration(){
        return bookingDuration;
    }

    
    /*Print data*/
    public void print() {

        // Beautify table for console view
        System.out.format("|%17s %17s %17s %17s %17s %17s %17s %17s %17s %17s",
                getBookingId() + "  |",
                getTrainerId() + "  |",
                getTrainerName() + "  |",
                getClientId() + "  |",
                getClientName() + "  |",
                getSpecId() + "  |",
                getFocus() + "  |",
                getBookingDate() + "  |",
                getBookingTime() + "  |",
                getBookingDuration() + " |");
        
        System.out.println(" ");
    }

    /*Delete booking details from database*/
    public int deleteBooking(String bookingId) {

        int deletionConfirmation = 0;

        //Lock thread
        bookingLock.lock();

        try {

            //Database connection
            connect3 = new DbConnection().getConnection();
            
            String q = "DELETE FROM Booking WHERE BookingId = ?";

            //SQLInjection prevention
            PreparedStatement prepS = connect3.prepareStatement(q);
            prepS.setString(1, bookingId);

            deletionConfirmation = prepS.executeUpdate();
            prepS.close();

            //Close connection
            connect3.close();
        } catch (SQLException ex) {
            System.out.println("Error : " + ex.getMessage());
        } finally {
            //Release lock
            bookingLock.unlock();

        }

        return deletionConfirmation;

    }
    /*Update booking details*/
    public int updateBooking(String newData, String columnField, String bookingID, String specID) {
        
        int updateConfirmation = 0;
        String query;
        PreparedStatement prepS = null;

        //Lock thread
        bookingLock.lock();
        try {

            connect3 = new DbConnection().getConnection();
            

            switch (columnField) {

                case "TrainerId":
                case "SpecId":

                    query = "UPDATE Booking SET TrainerId = ?,SpecID = ? WHERE BookingId = ?";

                    prepS = connect3.prepareStatement(query);
                    prepS.setString(1, newData);
                    prepS.setString(2, specID);
                    prepS.setString(3, bookingID);

                    updateConfirmation = prepS.executeUpdate();
                    

                    prepS.close();
                    break;

                case "ClientId":

                    query = "UPDATE Booking SET ClientId = ? WHERE BookingId = ?";

                    prepS = connect3.prepareStatement(query);
                    prepS.setString(1, newData);
                    prepS.setString(2, bookingID);

                    updateConfirmation = prepS.executeUpdate();
                    
                    prepS.close();
                    break;

                case "BookingDate":

                    query = "UPDATE Booking SET BookingDate = ? WHERE BookingId = ?";

                    prepS = connect3.prepareStatement(query);
                    prepS.setString(1, newData);
                    prepS.setString(2, bookingID);

                    updateConfirmation = prepS.executeUpdate();
                    
                    prepS.close();
                    break;

                case "BookingTime":

                    query = "UPDATE Booking SET BookingTime = ? WHERE BookingId = ?";

                    prepS = connect3.prepareStatement(query);
                    prepS.setString(1, newData);
                    prepS.setString(2, bookingID);

                    updateConfirmation = prepS.executeUpdate();
                    
                    prepS.close();
                    break;

                case "BookingDuration":

                    query = "UPDATE Booking SET BookingDuration = ? WHERE BookingId = ?";

                    prepS = connect3.prepareStatement(query);
                    prepS.setString(1, newData);
                    prepS.setString(2, bookingID);

                    updateConfirmation = prepS.executeUpdate();
                    
                    prepS.close();
                    break;

            }

            connect3.close();

        } catch (SQLException ex) {
            System.out.println("Error : " + ex.getMessage());
        } finally {

            bookingLock.unlock();
        }

        return updateConfirmation;
    }

    /*Add new bookign record*/
    public int addNewBooking(String bookingID, String trainerID, String clientID, String clientNAME, int clientAge, String clientGender, String specID, String bookingDATE, String bookingTIME, String bookingDURATION, String addType) {
        
        int addConfirmation = 0;
        String query;
        PreparedStatement prepS = null;
        bookingLock.lock();
        try {

            connect3 = new DbConnection().getConnection();
           

            switch (addType) {

                case "ExistingClientBooking":

                    query = "INSERT INTO Booking VALUES(?,?,?,?,?,?,?)";

                    prepS = connect3.prepareStatement(query);
                    prepS.setString(1, bookingID);
                    prepS.setString(2, trainerID);
                    prepS.setString(3, clientID);
                    prepS.setString(4, specID);
                    prepS.setString(5, bookingDATE);
                    prepS.setString(6, bookingTIME);
                    prepS.setString(7, bookingDURATION);

                    addConfirmation = prepS.executeUpdate();
                   
                    prepS.close();

                    break;

                default:
                    int proceedFinalAdd;

                    // Add in client table
                    query = "INSERT INTO Client VALUES(?,?,?,?)";

                    prepS = connect3.prepareStatement(query);

                    prepS.setString(1, clientID);
                    prepS.setString(2, clientNAME);
                    prepS.setInt(3, clientAge);
                    prepS.setString(4, clientGender);

                    proceedFinalAdd = prepS.executeUpdate();
                    

                    if (proceedFinalAdd == 1) {

                        /*Add in booking table as clientId is FK*/
                        query = "INSERT INTO Booking VALUES(?,?,?,?,?,?,?)";

                        prepS = connect3.prepareStatement(query);
                        prepS.setString(1, bookingID);
                        prepS.setString(2, trainerID);
                        prepS.setString(3, clientID);
                        prepS.setString(4, specID);
                        prepS.setString(5, bookingDATE);
                        prepS.setString(6, bookingTIME);
                        prepS.setString(7, bookingDURATION);

                        addConfirmation = prepS.executeUpdate();
                        

                    }

                    prepS.close();

                    break;

            }

            connect3.close();

        } catch (SQLException ex) {
            System.out.println("Error : " + ex.getMessage());
        } finally {

            //Release thread
            bookingLock.unlock();
        }

        return addConfirmation;
    }

}
