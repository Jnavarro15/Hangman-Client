import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;



public class Client extends Thread{

	public Client(Consumer<Serializable> callback, int port) {
		// Your implementation for client initialization here using the provided port number
		this.callback= callback;
	}
	
	Socket socketClient;
	
	ObjectOutputStream out;
	ObjectInputStream in;
	
	private Consumer<Serializable> callback;
	
	Client(Consumer<Serializable> call){
	
		callback = call;

	}
	
	public void run() {
		
		try {
		socketClient= new Socket("127.0.0.1", ClientGUI.portNum);
	    out = new ObjectOutputStream(socketClient.getOutputStream());
	    in = new ObjectInputStream(socketClient.getInputStream());
	    socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {
			System.out.println("Could not connect");
			System.out.println(ClientGUI.portNum);
		}
		
		while(true) {
			 
			try {
			String message = in.readObject().toString();
//			System.out.println(message);
			callback.accept(message);
			}
			catch(Exception e) {}
		}
	
    }
	
	public void send(String data) {
		
		try {
			out.writeObject(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
