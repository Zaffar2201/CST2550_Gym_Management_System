import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;



public class Server {
    
    
    public static void main(String[] args){
    
    
    try{
	    ServerSocket serverSocket = new ServerSocket(9080);
	
	    //Repeatedly wait for connections, and process
	    while (true){

		//A 'blocking' call which waits until a connection is requested
		Socket socket = serverSocket.accept(); 

		//Create and start a new thread for the connection
		new Thread(new ServerRunnable(socket)).start();
	    }
	}catch (IOException e){
	    System.out.println("Exception caught when trying to listen on current port");
	    System.out.println(e.getMessage());
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    }
}
