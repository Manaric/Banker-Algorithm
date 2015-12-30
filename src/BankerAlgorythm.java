import java.util.Scanner;
import java.util.Random;
import java.util.Arrays;

public class BankerAlgorythm
{	
    //---------------Global variable
    static int NUMBER_OF_CUSTOMERS, NUMBER_OF_RESOURCES;
    static int[] AVAILABLE;
    static int[][] MAXIMUM, ALLOCATION, NEED;

    public static void main(String args[])
    {
        //Quantity of Resource type that will be defined by the user
        NUMBER_OF_RESOURCES = args.length; //Set total of resources
        AVAILABLE = new int[NUMBER_OF_RESOURCES]; //Set the available resources to the length of the current arguments

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

        }
        algorythm();
    }

    public static void algorythm() //Method that define environment
    {
        //---------------Define variables
        NUMBER_OF_CUSTOMERS = 5; 									//Total of customers / Defined by developer
        MAXIMUM = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES];	//Maximum resources to pick for each customer
        ALLOCATION = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES]; //Currently allocated for each customer
        NEED = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES];		//Remaining of need for each customer	

        setValues();
        
                        //---------------Print start values
        System.out.println("Available resources:");
        System.out.println("" + Arrays.toString(AVAILABLE) + "\n");
        System.out.println("Maximum per client:");
        System.out.println("" + Arrays.deepToString(MAXIMUM) + "\n");
        System.out.println("Allocation start per client:");
        System.out.println("" + Arrays.deepToString(ALLOCATION) + "\n");
        System.out.println("Need per client:");
        System.out.println("" + Arrays.deepToString(NEED) + "\n");

        int result = prevention();

        System.out.println("Algorythm ended with " + result);
    }

    public static void fillArrays()
    {
        Random random = new Random(); 	//To set values

        //---------------Fill arrays
        for(int n =0; n<MAXIMUM.length;n++) //MAXIMUM Array
        {	
                for(int m =0; m<MAXIMUM[n].length;m++)
                {	
                        MAXIMUM[n][m]= random.nextInt(9);
                }
        }

        for(int n =0; n<ALLOCATION.length;n++) //ALLOCATION Array
        {	
                for(int m =0; m<ALLOCATION[n].length;m++)
                {	
                        ALLOCATION[n][m]= random.nextInt(9);
                }
        }
    }

    public static int prevention()
    {
        //---------------Declare values for algorythm
        int WORK[] = AVAILABLE;
        boolean FINISH[] = new boolean[NUMBER_OF_CUSTOMERS];

        for(int i = 0; i < NUMBER_OF_CUSTOMERS; i++)
        {
                FINISH[i] = false; //Set finish all to false
        }

        boolean aux = true; //Aux for while loop

        //---------------Prevention algorythm
        while(aux) 
        {
                aux=false; //If the algorythm doesn't do anything it will exit, it finished

                for(int n=0; n<NUMBER_OF_CUSTOMERS;n++) //To check every one of the customers
                {
                        if(!FINISH[n]) //If it haven't been proved
                        {
                                int m;

                                for(m=0;m<NUMBER_OF_RESOURCES;m++) //Check if it is lower than work
                                {
                                        if(!(NEED[n][m] <= WORK[m]))
                                        {
                                                break; //End check because is higher
                                        }
                                }

                                if(m==NUMBER_OF_RESOURCES) //If loop finished without break it result in a need less than work
                                {
                                        for(int k=0; k<NUMBER_OF_RESOURCES;k++) //Add Allocation value of that customer to the work array
                                        {
                                                WORK[k]+= ALLOCATION[n][k];
                                        }

                                        FINISH[n] = true; // So it won't enter again here
                                        aux=true; //Loop must continue
                                        System.out.println("Worked with Customer #" + n + ", is lower");
                                        n=-1;
                                }
                        }
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

        if(total==NUMBER_OF_CUSTOMERS)
        {
                return 0; //Safe state
        }
        else
        {
                return -1; //Deadlock
        }
    }

    public static int requestResources(int customer, int request)
    {
        return 0;
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
    }
}