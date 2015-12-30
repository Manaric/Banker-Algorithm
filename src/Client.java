// Fig. 23.11: Consumer.java
// Consumer with a run method that loops, reading 10 values from buffer.
import java.security.SecureRandom;

public class Client implements Runnable //This was Customer
{ 
   private static final SecureRandom generator = new SecureRandom();
   private final Buffer sharedLocation; // reference to shared object
   private int customerNumber = 0;
   private final int[] NEED;

   // constructor
   public Client(Buffer sharedLocation, int customerNumber, int[] NEED)
   {
      this.sharedLocation = sharedLocation;
      this.customerNumber = customerNumber;
      this.NEED = NEED;
   }

   // read sharedLocation's value 10 times and sum the values
   @Override
   public void run()                                           
   {      
        int request=1; //Result of the request
        
        try 
        {
            Thread.sleep(generator.nextInt(3000));
            request = requestResources(customerNumber, NEED);

        } 
        catch (InterruptedException exception) 
        {
            Thread.currentThread().interrupt(); 
        }

        System.out.println("Client "+ customerNumber + " done algorithm with result " + request + "\n");
        
        if(request==-1)
        {
            run(); //If failed try it again
        }
   }

   int requestResources(int customerNumber, int request[])
   {
        int result = 1;
        try 
        {
            //Result of algorithm
            result = sharedLocation.releaseResources(customerNumber, request);
        } 
        catch (InterruptedException interruptedException) 
        {
           System.out.println("Exception on requestResources of Client " + customerNumber);
        }
        
        //Return state 0 if safe, -1 if unsafe
        return result;
    }
} // end class Consumer