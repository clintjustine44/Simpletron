import java.io.*;
import java.util.*;

public class Simpletron {
    Memory memory = new Memory();
    int accumulator; // Accumulator
    int programCounter, programSize;
    String instructionRegister, operationCode, operand;

    public Simpletron(String filename){
        String[] data = new String[100];
        try {
            Scanner scanner = new Scanner(new File(filename));
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                data = line.split(" ");
                memory.addItem(Integer.parseInt(data[0]), data[1]);
                programSize++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printMemory(){
        System.out.println("\nREGISTERS");
        System.out.printf("accumulator:\t\t%+05d", accumulator);
        System.out.printf("\nprogramCounter:\t\t   %02d", programCounter);
        System.out.printf("\ninstructionRegister:\t+%4s", instructionRegister);
        System.out.printf("\noperationCode:\t\t   %2s", operationCode == null ? "00" : operationCode);
        System.out.printf("\noperand:\t\t   %2s", operand == null ? "00" : operand);
        System.out.println();
        memory.dump();
    }

    public void execute(){
        while(programCounter < programSize){
            instructionRegister = memory.getItem(programCounter);
            operationCode = instructionRegister.substring(0,2);
            operand = instructionRegister.substring(2,4);
            programCounter++;

            // Stops the program when HALT = 43 is encountered.
            if(operationCode.equals("43")){
                System.out.println("This program has completed its task.");
                break;
            }

            microcode();
        }
        this.printMemory();
    }

    private String getInput(){
        return new Scanner(System.in).nextLine();
    }

    public void microcode(){
        String data = null;
        int divisor, immediateDivisor;

        switch(operationCode){
            // Read a word from the keyboard into a specific location in memory
            case "10":
                System.out.println("Enter a value (-9999 to 9999): ");
                data = getInput();

                int value;
                try {
                    value = Integer.parseInt(data);
                } catch (NumberFormatException e){
                    System.out.println("Invalid input. Defaulting to 0000.");
                    value = 0;
                }

                if(value > 9999) value = 9999;
                if(value < -9999) value = -9999;

                String formattedData = String.format("%04d", value);
                memory.addItem(Integer.parseInt(operand.strip()), formattedData);
                break;
            // Write a word from a specific location in memory to the screen
            case "11":
                data = memory.getItem(Integer.parseInt(operand.strip()));
                System.out.println("Data: " + data);
                break;
            // Load a word from a specific location in memory into the accumulator
            case "20":
                data = memory.getItem(Integer.parseInt(operand.strip()));
                accumulator = Integer.parseInt(data);
                break;
            // Store a word from the accumulator into a specific location in memory
            case "21":
                data = String.format("%04d", accumulator);
                memory.addItem(Integer.parseInt(operand), data);
                break;
            /* Load an immediate value (00-99) into the accumulator. The 2 digit
            *  operand becomes the immediate value to be loaded in the accumulator.
            */
            case "22":
                data = operand;
                accumulator = Integer.parseInt(data);
                break;
            // Operand comes from memory:

            /* Add a word from a specific location in memory to the word in the
            *  accumulator (leave the result in the accumulator)
            */
            case "30":
                data = memory.getItem(Integer.parseInt(operand.strip()));
                accumulator += Integer.parseInt(data);
                break;
            /* Subtract a word from a specific location in memory from the 
             * word in the accumulator (leave the result in the accumulator).
            */
            case "31":
                data = memory.getItem(Integer.parseInt(operand.strip()));
                accumulator -= Integer.parseInt(data);
                break;
            /* Divide the accumulator by the word from a specific 
             * location in memory (leave the result in the accumulator).
             */
            case "32":
                data = memory.getItem(Integer.parseInt(operand.strip()));
                divisor = Integer.parseInt(data);
                if(divisor == 0){
                    System.out.printf("Error: Division by zero at instruction %02d.", programCounter - 1);
                }
                else 
                    accumulator %= divisor;
                break;
            case "33":
                data = memory.getItem(Integer.parseInt(operand.strip()));
                divisor = Integer.parseInt(data);
                
                if(divisor == 0){
                    System.out.printf("Error: Modulo by zero at instruction %02d.", programCounter - 1);
                }
                else 
                    accumulator %= divisor;

                break;
            case "34":
                data = memory.getItem(Integer.parseInt(operand.strip()));
                accumulator *= Integer.parseInt(data);
                break;
            // Operand is immediate:
            case "35":
                data = operand.strip();
                accumulator += Integer.parseInt(data);
                break;
            case "36":
                data = operand.strip();
                accumulator -= Integer.parseInt(data);
                break;
            case "37":
                data = operand.strip();
                immediateDivisor = Integer.parseInt(data);
                if(immediateDivisor == 0){
                    System.out.printf("Error: Division by zero at instruction %02d.", programCounter - 1);
                    System.exit(1);
                }
                else
                    accumulator /= immediateDivisor;
                break;
            case "38":
                data = operand.strip();
                immediateDivisor = Integer.parseInt(data);
                if(immediateDivisor == 0){
                    System.out.printf("Error: Modulo by zero at instruction %02d.", programCounter - 1);
                    System.exit(1);
                }else 
                    accumulator %= Integer.parseInt(data);
                break;
            case "39":
                data = operand.strip();
                accumulator *= Integer.parseInt(data);
                break;
            case "40":
                programCounter = Integer.parseInt(operand.strip());
                break;
            case "41":
                if(accumulator < 0)
                    programCounter = Integer.parseInt(operand.strip());
                break;
            case "42":
                if(accumulator == 0)
                    programCounter = Integer.parseInt(operand.strip());
            // Halt - this program has completed its task
            case "43":
                break;
        }
    }
    public static void main(String[] args) {
        Simpletron simpletron = new Simpletron("test.sml");
        simpletron.execute();
    }
}
