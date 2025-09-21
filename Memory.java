public class Memory {
    private final int MEMORY_SIZE = 100;
    private String[] mem;

    public Memory(){
        mem = new String[MEMORY_SIZE];
        for(int i = 0; i < 100; i++){
            mem[i] = "0000";
        }
    }
    
    public Memory(String[] data){
        this();
        int dataSize = data.length;
        System.arraycopy(data, 0, mem, 0, dataSize);
    }

    public void  addItem(int address, String data){
        this.mem[address] = data;
    }

    public String getItem(int address){
        return this.mem[address];
    }

    public void dump(){
        System.out.println("\nMEMORY:");
        System.out.print("     ");

        for (int i = 0; i < 10; i++) {
           System.out.printf("%6d", i);
        }
        System.out.println();

        for (int i = 0; i < MEMORY_SIZE; i++) {
           if (i % 10 == 0) System.out.printf("%02d   ", i);
           
           String value = mem[i];

            // Default to 0 if null
            if (value == null) value = "0000";

            // Add + sign if it's not negative
            if (!value.startsWith("-")) value = "+" + value;

           System.out.printf("%6s", value);
           
           if (i % 10 == 9) System.out.println();
         }
    }
}
