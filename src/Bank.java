import java.util.Arrays;


public class Bank implements Buffer//This was CircularBuffer
{
    int[] AVAILABLE, WORK; // Available resources
    int[][] MAXIMUM, ALLOCATION, NEED;
    int NUMBER_OF_CUSTOMERS, NUMBER_OF_RESOURCES;
    boolean FINISH[];   
    int occupiedCells;

    public Bank(int NUMBER_OF_RESOURCES, int[] AVAILABLE,int[][] MAXIMUM,int[][] ALLOCATION,int[][] NEED, int NUMBER_OF_CUSTOMERS)
    {
        //Set variables
        this.AVAILABLE = AVAILABLE;
	this.MAXIMUM = MAXIMUM;
	this.ALLOCATION = ALLOCATION;
	this.NEED = NEED;
	this.NUMBER_OF_RESOURCES = NUMBER_OF_RESOURCES;
        
        this.FINISH = new boolean[NUMBER_OF_CUSTOMERS];

        for(int i = 0; i < NUMBER_OF_CUSTOMERS; i++)
        {
                FINISH[i] = false; //Set finish all to false
        }
        
         //Define Available array
        for(int n=0;n<NUMBER_OF_RESOURCES;n++) //Check every kind of resources
        {
            for(int m=0; m<NUMBER_OF_CUSTOMERS;m++) //Check for every customer
                {
                    this.AVAILABLE[n] -= ALLOCATION[m][n]; //Less every resource already allocated
                }
        }
        
        WORK = AVAILABLE;
    }
    
    // display current operation and buffer state
    public synchronized void displayState(String operation)
    {
        // output operation and number of occupied buffer cells
        System.out.printf("%s%s%d)%n%s", operation, 
        "\n (Clients already attended: ", occupiedCells, "Work cells:  ");

        for (int value : WORK)
        {
            System.out.printf(" %2d  ", value); // output values in buffer
        }

        System.out.printf("%n               ");

        for (int i = 0; i < WORK.length; i++)
        {
            System.out.print("---- ");
        }

        System.out.printf("%n%n");
    } 


    @Override
    public synchronized int releaseResources(int customerNumber, int release[]) throws InterruptedException
    {
        WORK = AVAILABLE;
        //Variables
        boolean aux = true; //Aux for while loop
        int result=1;

        //---------------Prevention algorythm
        while(aux) 
        {
            aux=false; //If the algorithm doesn't do anything it will exit, it finished
//            System.out.println("Start of while");

//                System.out.println("Start of for #" + customerNumber);
                if(!FINISH[customerNumber]) //If it haven't been proved
                {
//                    System.out.println("Enter with Customer #" + customerNumber);
                    int m=0;
                    
//                    System.out.println("Thread #" + customerNumber + " in position " + m + " is " + release[m]);
                    
                    for(m=0;m<NUMBER_OF_RESOURCES;m++) //Check if it is lower than work
                    {
                        if(!(release[m] <= WORK[m]))
                        {
//                            System.out.println("End of Customer #" + customerNumber + " because is higher");
                            wait(); //End check because is higher
                            result = -1;
                            return result; //Unsafe
                        }
                    }
                    
//                    System.out.println("Number of customer is " + customerNumber);
//                    System.out.println("Value of m is " + m);
//                    System.out.println("Total of resources are " + NUMBER_OF_RESOURCES);

                    if(m==NUMBER_OF_RESOURCES) //If loop finished without break it result in a need less than work
                    {
                        System.out.println("Work before adding is " + Arrays.toString(WORK)); //Print total work
                        for(int k=0; k<NUMBER_OF_RESOURCES;k++) //Add Allocation value of that customer to the work array
                        {
                            m=0;
//                            System.out.println("Value of K is " + k);
//                            System.out.println("Total of resources are " + NUMBER_OF_RESOURCES);
                            WORK[k]+= ALLOCATION[customerNumber][k];
                        }

                        FINISH[customerNumber] = true; // So it won't enter again here
                        aux=true; //Loop must continue
                        System.out.println("Work is " + Arrays.toString(WORK)); //Print total work
                        displayState("Worked with Customer #" + customerNumber + ", is lower");
                        occupiedCells +=1;
                        notifyAll();
                        result = 0;
                        return result; //Safe state
                    }
                }
//                            System.out.println("End of for #" + customerNumber);
        }

        return result;
    }

    @Override
    public int requestResources(int customerNumber, int[] request) throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

} // end class CircularBuffer