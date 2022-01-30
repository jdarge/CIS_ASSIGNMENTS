import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class pa01 {

    private static final int MAX = 512;
    private static final HashMap<Integer, Character> letterTable = LetterTable();

    public static void main(String[] args) throws IOException {

        String key = "";
        String plaintext = "";

        for (int i = 0; i < 2; i++) {

            StringBuilder current = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(args[i]));
            String line;

            // READ EVERY LINE IN THE TEXT FILE
            while ((line = br.readLine()) != null) {
                current.append(line);
            }

            // STRIP TEXT AND ADD PADDING WHEN NECESSARY
            if (i == 0) {
                key = StripText(current);
            } else {
                plaintext = StripText(current);
                plaintext = PadWithX(plaintext);
            }
        }

        // VIGENERE ENCRYPTION RESULT IS TOSSED INTO 'result'
        String result = Encrypt(key, plaintext);

        // PRINT ALL REQUIRED DATA
        PrintData(key, plaintext, result);
    }

    private static String StripText(StringBuilder input) { // CALLED IN "main" (LINE: 54, 56)

        // THIS METHOD STRIPS ALL CHARACTERS THAT ARE NOT LETTERS

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            // POSSIBLE THAT THE INPUT FILE > 'MAX'
            if (result.length() == MAX) {
                break;
            }

            // CHECK IF THE CURRENT CHARACTER IS A LETTER
            if (Character.isLetter(input.charAt(i))) {
                result.append(input.charAt(i));
            }
        }

        return result.toString().toLowerCase();
    }

    private static String PadWithX(String plaintext) { // CALLED IN "main" (LINE: 57)

        // IF 'plaintext' IS LESS THAN 'MAX' ADD PADDING USING THE LETTER 'x'

        StringBuilder result = new StringBuilder();
        result.append(plaintext);

        if (result.length() != MAX) {
            result.append("x".repeat(Math.max(0, MAX - result.length())));
        }

        return result.toString();
    }

    private static String Encrypt(String key, String plaintext) { // CALLED IN "main" (LINE: 62)

        // THIS METHOD IS THE MAIN LOGIC FOR THE VIGENERE CYPHER
        // 1. GET THE NUMERICAL REPRESENTATION OF BOTH THE 'key' AND 'plaintext' OF SOME LETTER AT 'i'
        // 2. ADD THE RESULT TOGETHER, THEN MOD THE RESULT BY 26
        // 3. FINALLY, LOOK IN THE 'letterTable' FOR THE CORRECT LETTER AND APPEND IT TO 'result'

        StringBuilder result = new StringBuilder();

        int x;
        int current_index_of_key = 0;
        for (int i = 0; i < MAX; i++) {

            // KEY CAN BE LESS THAN 'MAX' SO WE WANT TO LOOP THROUGH IT CIRCULARLY
            if (current_index_of_key == key.length()) {
                current_index_of_key = 0;
            }

            x = ((key.charAt(current_index_of_key++) - 'a') + (plaintext.charAt(i) - 'a')) % 26;

            if (letterTable.containsKey(x)) {
                result.append(letterTable.get(x));
            }
        }

        return result.toString();
    }

    private static void PrintData(String key, String plaintext, String result) { // CALLED IN "main" (LINE: 65)

        // THIS METHOD SETS UP THE EXPECTED PRINT FORMATTING

        System.out.println("\n");
        System.out.println("Vigenere Key:");
        PrintFormat(key);

        System.out.println("\n");
        System.out.println("Plaintext:");
        PrintFormat(plaintext);

        System.out.println("\n");
        System.out.println("Ciphertext:");
        PrintFormat(result);
    }

    private static void PrintFormat(String input) { // CALLED IN "PrintData" (LINE: 137, 141, 145)

        // IN THE END RESULT, WE EXPECT THERE TO BE 80 CHARACTER PER-LINE
        // THIS METHOD ENSURES THIS EXPECTATION

        // SPECIFICALLY FOR INSTANCES WHERE THE KEY IS < 'MAX' AND <= '80'
        if (input.length() <= 80) {
            System.out.println("\n" + input);
            return;
        }

        StringBuilder output = new StringBuilder();

        // ALL OTHER CASES ARE HANDLED WITH THE FORMATTING KEPT IN MIND
        for (int i = 0; i < input.length(); i++) {

            if (i % 80 != 0) {
                output.append(input.charAt(i));
            } else {
                output.append("\n").append(input.charAt(i));
            }
        }

        System.out.println(output);
    }

    private static HashMap<Integer, Character> LetterTable() { // CALLED IN THE INITIALIZATION STAGE (LINE: 34)

        // THIS TABLE IS USED TO ASSIGN AN APPROPRIATE LETTER TO A NUMERICAL RESULT
        // EVERY LATIN ALPHABET CHARACTER IS MAPPED TO A SPECIFIC INTEGER (0-25)

        HashMap<Integer, Character> table = new HashMap<>();

        table.put(0, 'a');
        table.put(1, 'b');
        table.put(2, 'c');
        table.put(3, 'd');
        table.put(4, 'e');
        table.put(5, 'f');
        table.put(6, 'g');
        table.put(7, 'h');
        table.put(8, 'i');
        table.put(9, 'j');
        table.put(10, 'k');
        table.put(11, 'l');
        table.put(12, 'm');
        table.put(13, 'n');
        table.put(14, 'o');
        table.put(15, 'p');
        table.put(16, 'q');
        table.put(17, 'r');
        table.put(18, 's');
        table.put(19, 't');
        table.put(20, 'u');
        table.put(21, 'v');
        table.put(22, 'w');
        table.put(23, 'x');
        table.put(24, 'y');
        table.put(25, 'z');

        return table;
    }

}
