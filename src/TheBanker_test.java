import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;
import java.util.Arrays;

public class TheBanker_test
{
    static int NUMBER_OF_CUSTOMERS, NUMBER_OF_RESOURCES;
    static int[] AVAILABLE;
    static int[][] MAXIMUM, ALLOCATION, NEED;
    
    public static void main(String[] args) throws InterruptedException
    {
	
       //Quantity of Resource type that will be defined by the user
        NUMBER_OF_CUSTOMERS = 5; //Processes that will run
        NUMBER_OF_RESOURCES = args.length; //Set total of resources
        AVAILABLE = new int[NUMBER_OF_RESOURCES]; //Set the available resources to the length of the current arguments
        MAXIMUM = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES];//Maximum resources to pick for each customer
        ALLOCATION = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES]; //Currently allocated for each customer
        NEED = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES]; //Remaining of need for each customer	
		
        Scanner miScanner = new Scanner(System.in);
		
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
			
	//System.out.println("Args " + args[n] + " :  Inserted " + AVAILABLE[n]);
        }
		
        // create new thread pool with two threads
        ExecutorService executorService = Executors.newCachedThreadPool();

        //Set values of the arrays
        setValues();
        
        // create CircularBuffer to store ints
        Bank sharedLocation = new Bank(NUMBER_OF_RESOURCES,AVAILABLE,MAXIMUM,ALLOCATION,NEED,NUMBER_OF_CUSTOMERS);

        // display the initial state of the CircularBuffer
        sharedLocation.displayState("Initial State");

        // execute the client tasks
        executorService.execute(new Client(sharedLocation,0,NEED[0]));
        executorService.execute(new Client(sharedLocation,1,NEED[1]));
        executorService.execute(new Client(sharedLocation,2,NEED[2]));
        executorService.execute(new Client(sharedLocation,3,NEED[3]));
        executorService.execute(new Client(sharedLocation,4,NEED[4]));


        //algorithm();
        
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES); 
    }
    
    public static void algorithm()
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
                                        System.out.println("Worked with Customer #" + n + ", is lower");
                                        n=-1;
                                }
                        }
                        // System.out.println("End of for #" + n);
                }
        }

        System.out.println("Work is " + Arrays.toString(WORK)); //Print total work

        //---------------Check if algorythm was in safe state
        int total=0;

        for(int n=0; n<NUMBER_OF_CUSTOMERS; n++)
        {
                if(FINISH[n])
                {
                        total++;
                }
        }

//        if(total==NUMBER_OF_CUSTOMERS)
//        {
//                return 0; //Safe state
//        }
//        else
//        {
//                return -1; //Deadlock
//        }

        // System.out.println("End of prevention");
    }
    
     public static void setValues()
    {
        //Allocation
        ALLOCATION[0][0] = 0; ALLOCATION[1][0] = 2; ALLOCATION[2][0] = 3; ALLOCATION[3][0] = 2; ALLOCATION[4][0] = 0;
        ALLOCATION[0][1] = 1; ALLOCATION[1][1] = 0; ALLOCATION[2][1] = 0; ALLOCATION[3][1] = 1; ALLOCATION[4][1] = 0;
        ALLOCATION[0][2] = 0; ALLOCATION[1][2] = 0; ALLOCATION[2][2] = 2; ALLOCATION[3][2] = 1; ALLOCATION[4][2] = 2;

        //Maximum
        MAXIMUM[0][0] = 7; MAXIMUM[1][0] = 3; MAXIMUM[2][0] = 9; MAXIMUM[3][0] = 2; MAXIMUM[4][0] = 4;
        MAXIMUM[0][1] = 5; MAXIMUM[1][1] = 2; MAXIMUM[2][1] = 0; MAXIMUM[3][1] = 2; MAXIMUM[4][1] = 3;
        MAXIMUM[0][2] = 3; MAXIMUM[1][2] = 2; MAXIMUM[2][2] = 2; MAXIMUM[3][2] = 2; MAXIMUM[4][2] = 3;

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
}

