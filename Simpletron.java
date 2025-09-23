import java.io.*;
import java.util.*;

public class Simpletron {
    Memory memory = new Memory();
    int accumulator; // Accumulator
    int programCounter, programSize;
    String instructionRegister, operationCode, operand;

    public Simpletron(String filename){
        accumulator = 0;
        programCounter = 0;
        programSize = 0;
        instructionRegister = "0000";
        operand = "00";
        operationCode = "00";
        try {
            Scanner scanner = new Scanner(new File(filename));
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                String[] data = line.split(" ");
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

    private String getInput(){
        return new Scanner(System.in).nextLine();
    }

    /** Runs until completion (no clears, no "Executing...") */
    public void run() {
        while (programCounter < programSize) {
            instructionRegister = memory.getItem(programCounter);
            operationCode = instructionRegister.substring(0,2);
            operand = instructionRegister.substring(2,4);
            programCounter++;

            if (operationCode.equals("43")) { // HALT
                System.out.println("\nThis program has completed its task.");
                break;
            }

            microcode();
        }
    }

    /** Executes one instruction (step, used only in step-by-step mode) */
    public boolean step() {
        if (programCounter >= programSize) return false;

        instructionRegister = memory.getItem(programCounter);
        operationCode = instructionRegister.substring(0,2);
        operand = instructionRegister.substring(2,4);

        // Clear terminal before showing execution
        System.out.print("\033[H\033[2J");
        System.out.flush();

        System.out.println("Executing " + instructionRegister + "...");

        programCounter++;

        if (operationCode.equals("43")) { // HALT
            System.out.println("This program has completed its task.");
            return false;
        }

        microcode();
        return true;
    }

    /** Runs step by step (prints memory after each instruction) */
    public void runStepByStep() {
        Scanner sc = new Scanner(System.in);
        printMemory();
        System.out.print("\nPress enter to start execution...");
        sc.nextLine();

        while (step()) {
            printMemory();
            System.out.print("\nPress Enter to continue: ");
            sc.nextLine();
        }
        this.printMemory(); // final dump after program ends
    }

    private int clampAccumulator() {
        if (accumulator > 9999) 
            return 9999;
        if (accumulator < -9999) 
            return -9999;
        return accumulator;
    }


    public void microcode(){
        String data = null;
        int divisor, immediateDivisor;

        switch(operationCode){
            // READ
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

                String formattedData = String.format("%+05d", value);
                memory.addItem(Integer.parseInt(operand.strip()), formattedData);
                break;
            // WRITE
            case "11":
                data = memory.getItem(Integer.parseInt(operand.strip()));
                int outvalue = Integer.parseInt(data);
                System.out.printf("%+05d\n", outvalue);
                break;
            // LoadM
            case "20":
                data = memory.getItem(Integer.parseInt(operand.strip()));
                accumulator = Integer.parseInt(data);
                break;
            // Store
            case "21":
                data = String.format("%+05d", accumulator);
                memory.addItem(Integer.parseInt(operand), data);
                break;
            // LoadI
            case "22":
                data = operand;
                accumulator = Integer.parseInt(data);
                break;
            // AddM
            case "30":
                data = memory.getItem(Integer.parseInt(operand.strip()));
                accumulator += Integer.parseInt(data);
                accumulator = clampAccumulator();
                break;
            // SubM
            case "31":
                data = memory.getItem(Integer.parseInt(operand.strip()));
                accumulator -= Integer.parseInt(data);
                accumulator = clampAccumulator();
                break;
            // DivM
            case "32":
                data = memory.getItem(Integer.parseInt(operand.strip()));
                divisor = Integer.parseInt(data);
                if(divisor == 0){
                    System.out.printf("Error: Division by zero at instruction %02d.", programCounter - 1);
                    System.exit(1);
                }
                else {
                    accumulator /= divisor;
                    accumulator = clampAccumulator();
                }
                break;
            // ModM
            case "33":
                data = memory.getItem(Integer.parseInt(operand.strip()));
                divisor = Integer.parseInt(data);
                if(divisor == 0){
                    System.out.printf("Error: Modulo by zero at instruction %02d.", programCounter - 1);
                    System.exit(1);
                }
                else {
                    accumulator %= divisor;
                    accumulator = clampAccumulator();
                }
                    
                break;
            // MulM
            case "34":
                data = memory.getItem(Integer.parseInt(operand.strip()));
                accumulator *= Integer.parseInt(data);
                accumulator = clampAccumulator();
                break;
            // AddI
            case "35":
                data = operand.strip();
                accumulator += Integer.parseInt(data);
                accumulator = clampAccumulator();
                break;
            // SubI
            case "36":
                data = operand.strip();
                accumulator -= Integer.parseInt(data);
                accumulator = clampAccumulator();
                break;
            // DivI
            case "37":
                data = operand.strip();
                immediateDivisor = Integer.parseInt(data);
                if(immediateDivisor == 0){
                    System.out.printf("Error: Division by zero at instruction %02d.", programCounter - 1);
                    System.exit(1);
                }
                else{
                    accumulator /= immediateDivisor;
                    accumulator = clampAccumulator();
                }
                    
                break;
            // ModM
            case "38":
                data = operand.strip();
                immediateDivisor = Integer.parseInt(data);
                if(immediateDivisor == 0){
                    System.out.printf("Error: Modulo by zero at instruction %02d.", programCounter - 1);
                    System.exit(1);
                }else {
                    accumulator %= Integer.parseInt(data);
                    accumulator = clampAccumulator();
                }
                    
                break;
            // MulM
            case "39":
                data = operand.strip();
                accumulator *= Integer.parseInt(data);
                accumulator = clampAccumulator();
                break;
            // JMP
            case "40": // JUMP
                programCounter = Integer.parseInt(operand.strip());
                break;
            // JN
            case "41": // JUMP IF ACC < 0
                if (accumulator < 0) {
                    programCounter = Integer.parseInt(operand.strip());
                }
                break;
            // JZ
            case "42": // JUMP IF ACC == 0
                if (accumulator == 0) {
                    programCounter = Integer.parseInt(operand.strip());
                }
                break;
            default:
                break;
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage:");
            System.out.println("  java Simpletron program.sml         # run to completion");
            System.out.println("  java Simpletron program.sml -s      # step by step");
            return;
        }

        String filename = args[0];
        boolean stepMode = args.length > 1 && args[1].equals("-s");

        Simpletron simpletron = new Simpletron(filename);
        if (stepMode) {
            simpletron.runStepByStep();
        } else {
            simpletron.run();
        }
    }
}
