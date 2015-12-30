import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;

public class TheBanker
{
    static int NUMBER_OF_CUSTOMERS, NUMBER_OF_RESOURCES;
    static int[] AVAILABLE;
    static int[][] MAXIMUM, ALLOCATION, NEED;
    
    public static void main(String[] args) throws InterruptedException
    {
        // create new thread pool with two threads
        ExecutorService executorService = Executors.newCachedThreadPool();

        //Set values of the arrays
        setValues(args);
        
        //Integer to check it won't fall in a deadlock
        int prevention=1;
        
        prevention = prevention();
        
        if(prevention==0)//If it can fall in a deadlock it will end the application
        {
            // create CircularBuffer to store ints
            Bank sharedLocation = new Bank(NUMBER_OF_RESOURCES,AVAILABLE,MAXIMUM,ALLOCATION,NEED,NUMBER_OF_CUSTOMERS);

            // display the initial state of the CircularBuffer
            sharedLocation.displayState("Initial State");

            for(int n=0;n<NUMBER_OF_CUSTOMERS;n++) //Create Clients based on NUMBER_OF_CUSTOMERS defined by the user
            {
                executorService.execute(new Client(sharedLocation,n,NEED[n]));
            }
        }
        else
        {
            System.out.println("The data inputs you entered will end in a deadlock. \n Application exits.");
        }    
        
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES); 
        
        //Application end message
        System.out.printf("%s%s","Application ended successfully.", "Safe state.");
    }
    
    public static void setValues(String[] args)
    {
        Scanner miScanner = new Scanner(System.in);

        //Quantity of Resource type that will be defined by the user
        NUMBER_OF_CUSTOMERS = 0; //Processes that will run
        
        //Clients
        while(NUMBER_OF_CUSTOMERS<=0)
        {
            try //Check that is not a letter the input
            {
                System.out.println("Add a value for total of clients: ");
                NUMBER_OF_CUSTOMERS = Integer.parseInt(miScanner.next()); //Set in that space the value entered
                
                if(NUMBER_OF_CUSTOMERS<=0) //If the number is less than 0 then repeat the loop
                {
                    System.out.println("Please enter a number bigger than 0");
                    NUMBER_OF_CUSTOMERS=0;
                }
            }
            catch(NumberFormatException e) //It was a letter...
            {
                System.out.println("Please enter a number");
                NUMBER_OF_CUSTOMERS=0;
            }
        }
        
        //---------------------Set variables
        NUMBER_OF_RESOURCES = args.length; //Set total of resources
        AVAILABLE = new int[NUMBER_OF_RESOURCES]; //Set the available resources to the length of the current arguments
        MAXIMUM = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES];//Maximum resources to pick for each customer
        ALLOCATION = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES]; //Currently allocated for each customer
        NEED = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES]; //Remaining of need for each customer	
        
		
	//---------------Validation process at the beginning
        for(int n =0; n<args.length;n++)
        {	
            int arg = Integer.parseInt(args[n]);
		
            if(arg<0) //Need to be bigger than 0 to be a resource
            {
                while(arg<0)
                {
                    System.out.println("Please add a value bigger than 0 where you put " + arg + ": ");
                    AVAILABLE[n] = Integer.parseInt(miScanner.next()); //Set in that space the value entered
                    arg= AVAILABLE[n];
                }
            }
            else
            {
                AVAILABLE[n] = arg; //Insert resource in the stack
            }
        }
       
        //Allocation
        for(int i = 0; i < NUMBER_OF_CUSTOMERS; i++)
        {
            for(int j = 0; j < NUMBER_OF_RESOURCES; j++)
            {
                boolean aux=true;
                while(aux) //Validation 
                {
                    try //Check that is not a letter the input
                    {
                        System.out.println("Allocation for Client #" + (i) + " for Resource Type " + (j) + ":");
                        ALLOCATION[i][j] = Integer.parseInt(miScanner.next());

                        if(ALLOCATION[i][j]<0) //If the number is less than 0 then repeat the loop
                        {
                            System.out.println("Please enter a number bigger than 0");
                            aux=true;
                        }
                        else
                        {
                            aux=false; //Exit while to continue checking allocation
                        }
                    }
                    catch(NumberFormatException e) //It was a letter...
                    {
                        System.out.println("Please enter a number");
                        ALLOCATION[i][j]=-1;
                    }
                }
            }
        }

        //Maximum
        for(int i = 0; i < NUMBER_OF_CUSTOMERS; i++)
        {
            for(int j = 0; j < NUMBER_OF_RESOURCES; j++)
            {
                boolean aux=true;
                while(aux) //Validation 
                {
                    try //Check that is not a letter the input
                    {
                        System.out.println("Maximum for Client #" + (i) + " for Resource Type " + (j) + ":");
                        MAXIMUM[i][j] = Integer.parseInt(miScanner.next());
                        NEED[i][j]= MAXIMUM[i][j] - ALLOCATION[i][j]; //Maximum of resources of each customer less the resources it already have

                        if(MAXIMUM[i][j]<0) //If the number is less than 0 then repeat the loop
                        {
                            System.out.println("Please enter a number bigger than 0");
                            aux=true;
                        }
                        else
                        {
                            aux=false; //Exit while to continue checking allocation
                        }
                    }
                    catch(NumberFormatException e) //It was a letter...
                    {
                        System.out.println("Please enter a number");
                        MAXIMUM[i][j]=-1;
                    }
                }
            }
        }

        //Need
        for(int n=0; n<NUMBER_OF_CUSTOMERS;n++) //Get need total value
        {
                for(int m=0; m<NUMBER_OF_RESOURCES;m++)
                {
                        NEED[n][m]= MAXIMUM[n][m] - ALLOCATION[n][m]; //Maximum of resources of each customer less the resources it already have
                }
        }
        
        //Define Available array
        for(int n=0;n<NUMBER_OF_RESOURCES;n++) //Check every kind of resources
        {
            for(int m=0; m<NUMBER_OF_CUSTOMERS;m++) //Check for every customer
                {
                    AVAILABLE[n] -= ALLOCATION[m][n]; //Less every resource already allocated
                }
            //System.out.println("Available in resource #" + n + " are " + AVAILABLE[n]);
        }
    }
     
    public static int prevention()
    {
        //---------------Declare values for algorythm
        int WORK[] = AVAILABLE;
        boolean FINISH[] = new boolean[NUMBER_OF_CUSTOMERS];
        // System.out.println("Start of prevention");

        for(int i = 0; i < NUMBER_OF_CUSTOMERS; i++)
        {
                FINISH[i] = false; //Set finish all to false
        }

        boolean aux = true; //Aux for while loop

        //---------------Prevention algorythm
        while(aux) 
        {
                aux=false; //If the algorythm doesn't do anything it will exit, it finished
                // System.out.println("Start of while");

                for(int n=0; n<NUMBER_OF_CUSTOMERS;n++) //To check every one of the customers
                {
                        // System.out.println("Start of for #" + n);
                        if(!FINISH[n]) //If it haven't been proved
                        {
                                // System.out.println("Enter with Customer #" + n);
                                int m;

                                for(m=0;m<NUMBER_OF_RESOURCES;m++) //Check if it is lower than work
                                {
                                        if(!(NEED[n][m] <= WORK[m]))
                                        {
                                                // System.out.println("End of Customer #" + n + " because is higher");
                                                break; //End check because is higher
                                        }
                                }

                                // System.out.println("Value of m is " + m);
                                // System.out.println("Total of resources are " + NUMBER_OF_RESOURCES);

                                if(m==NUMBER_OF_RESOURCES) //If loop finished without break it result in a need less than work
                                {
                                        for(int k=0; k<NUMBER_OF_RESOURCES;k++) //Add Allocation value of that customer to the work array
                                        {
                                                // System.out.println("Value of K is " + k);
                                                // System.out.println("Total of resources are " + NUMBER_OF_RESOURCES);
                                                WORK[k]+= ALLOCATION[n][k];
                                        }

                                        FINISH[n] = true; // So it won't enter again here
                                        aux=true; //Loop must continue
                                        n=-1;
                                }
                        }
                        // System.out.println("End of for #" + n);
                }
        }
        
        //---------------Check if algorythm was in safe state
        int total=0;

        for(int n=0; n<NUMBER_OF_CUSTOMERS; n++)
        {
                if(FINISH[n])
                {
                        total++;
                }
        }

        if(total==NUMBER_OF_CUSTOMERS)
        {
                return 0; //Safe state
        }
        else
        {
                return -1; //Deadlock
        }

        // System.out.println("End of prevention");
    }
}

