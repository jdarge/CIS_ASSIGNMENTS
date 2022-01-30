/*=============================================================================
| Assignment: pa02 - Calculating an 8, 16, or 32 bit
| checksum on an ASCII input file
|
| Author: Jan Darge
| Language: c, c++, Java
|
| To Compile: javac pa02.java
| gcc -o pa02 pa02.c
| g++ -o pa02 pa02.cpp
|
| To Execute: java -> java pa02 inputFile.txt 8
| or c++ -> ./pa02 inputFile.txt 8
| or c -> ./pa02 inputFile.txt 8
| where inputFile.txt is an ASCII input file
| and the number 8 could also be 16 or 32
| which are the valid checksum sizes, all
| other values are rejected with an error message
| and program termination
|
| Note: All input files are simple 8 bit ASCII input
|
| Class: CIS3360 - Security in Computing - Fall 2021
| Instructor: McAlpin
| Due Date: 11/21/2021
|
+=============================================================================*/

import java.io.*;

public class pa02 {

    private static String inputFileName;
    private static int bit_size;
    private static String fileContent;

    public static void main(String[] args) {

        // INSPECTING AND CARRYING INPUT
        boolean pass = readArgs(args);

        // CHECKING IF ALL INPUT REQUIREMENTS HAVE PASSED INSPECTION
        if (!pass) {
            System.err.println("Valid checksum sizes are 8, 16, or 32\n");
            return;
        }

        // GENERATING HEX VALUE AND FORMATTING RESULT STRINGS
        String hex = startChecksum();

        // GENERATE FINAL OUTPUT STRING AND OUTPUT FILE
        String output = checkSumFinalize(hex, fileContent.length());
        output = formatOutputString(fileContent).concat(output);
        generateOutputFile(output);

        // DISPLAYING CONTENTS TO TERMINAL
        System.out.println(output);
    }

    // INITIALIZE

    private static boolean readArgs(String[] args) {

        String temp_name = args[0]; // FIRST ARG SHOULD BE A FILE NAME
        String temp_size = args[1]; // SECOND ARG SHOULD BE A (8/16/32) BIT SIZE

        // CHECKING IF THE GIVEN FILENAME EXISTS IN PATH
        if (!new File(temp_name).exists()) {
            System.out.println("Error: " + temp_name + " file does not exist. Check path.");
            return false;
        } else {
            // SAVING FILE NAME IF IT EXISTS
            inputFileName = temp_name;
        }

        // CHECKING FOR VALID BIT SIZE
        try {
            bit_size = Integer.parseInt(temp_size);// IS IT AN INT?
            return checkBitSize(); // IS IT A VALID BIT?
        } catch (Exception ignore) {
            return false;
        }

    }

    private static boolean checkBitSize() {
        // THIS VALUE SHOULD BE A (8/16/32) BIT SIZE
        return (bit_size == 8 || bit_size == 16 || bit_size == 32);
    }

    private static String readFile() {

        StringBuilder output = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFileName));
            String temp;

            // ADDING FILE CONTENTS TO A STRING
            while ((temp = br.readLine()) != null) {
                output.append(temp);
            }

            output.append((char) 0x0A);// ALL FILES END WITH 0x0A
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output.toString();
    }

    private static String generateOutputFileName() {

        StringBuilder newName = new StringBuilder();

        // GETTING RID OF UNNECESSARY INFORMATION FROM INPUT NAME
        String temp = inputFileName.replace("in", "").replace(".txt", "");

        // CREATING A PROPER/VALID OUTPUT FILE NAME
        newName.append("s").append(temp).append("-Output").append(bit_size).append(".txt");

        return newName.toString();
    }

    // MAIN CHECKSUM LOGIC

    private static String startChecksum() {

        fileContent = readFile();

        // CHECK BIT SIZE AND FOLLOW APPROPRIATE CASE
        long val;

        if(bit_size == 8) {
            val = checkSum_8(fileContent);
        } else if (bit_size == 16) {
            val = checkSum_16(fileContent);
        } else {
            val = checkSum_32(fileContent);
        }

        return lastHexBits(val, bit_size / 4 - 1); // DIVIDE BY 8 BECAUSE THE BIT SIZE NEEDS TO BE CHUNKED
    }

    private static int checkSum_8(String content) {

        int val = 0;

        for (int i = 0; i < content.length(); i++) {
            val += content.charAt(i);// ADD ALL CHARACTERS INTO A SINGLE INT VALUE
        }

        return val;
    }

    private static int checkSum_16(String input) {

        int val = 0;

        String content = input;

        while (content.length() % 2 != 0) {
            content = content.concat("X");// PADDING WITH X
        }

        fileContent = content;// UPDATING GLOBAL VARIABLE

        int temp;
        for (int i = 0; i < content.length(); i += 2) {

            // EXAMPLE OF TEMP VAR LOGIC:
            //  0000 0000 0000 0000 (start)
            //  0000 0000 1010 1010 (first character)
            //  1010 1010 0000 0000 (shift result)
            //  1010 1010 1111 0000 (adding new character to new 0's places)

            temp = content.charAt(i);
            temp = temp << 8;
            temp += content.charAt(i + 1);

            val += temp; // ADD ALL CHARACTERS INTO A SINGLE INT VALUE
        }

        return val;
    }

    private static long checkSum_32(String input) {

        int val = 0;

        String content = input;

        while (content.length() % 4 != 0) {
            content = content.concat("X");// PADDING WITH X
        }

        fileContent = content;// UPDATING GLOBAL VARIABLE

        for (int i = 0; i < content.length(); i += 4) {

            int temp = 0;

            // EXAMPLE OF TEMP VAR LOGIC:
            //  0000 0000 0000 0000 (start)
            //  0000 0000 1010 1010 (first character)
            //  1010 1010 0000 0000 (shift result)
            //  1010 1010 1111 0000 (adding new character to new 0's places)

            for (int j = i; j < i + 4; j++) {
                temp += content.charAt(j);
                if (j - i != 3) {
                    temp = temp << 8;
                }
            }

            val += temp; // ADD ALL CHARACTERS INTO A SINGLE INT VALUE
        }

        return val;
    }

    private static String lastHexBits(long n, int amount) {

        // METHOD TAKE IN AN INTEGER RESULT FROM "startChecksum()"
        // AND THE AMOUNT OF HEX VALUES WE WISH TO COLLECT FROM THE RESULT

        StringBuilder output = new StringBuilder();
        String temp = Long.toHexString(n);// INT -> HEX CONVERSION, SAVED AS STRING

        int start = temp.length() - amount - 1;// BASED ON AMOUNT, WE WILL STORE THOSE LAST CHARACTERS

        for (int i = start; i < temp.length(); i++) {
            output.append(temp.charAt(i));
        }

        return output.toString();
    }

    // OUTPUT

    private static String checkSumFinalize(String fullHex, int n) {

        String hex = formatHex(fullHex);
        String length = formatLength(n);
        String bit = formatBitLength(bit_size);

        // SETTING PROPER FORMAT FOR OUTPUT
        return "\n" + bit + " bit checksum is " + hex + " for all " + length + " chars";
    }

    private static String formatBitLength(int n) {

        String output = String.valueOf(n);

        if(n < 10) {
            output = " " + n;
        }

        return output;
    }

    private static String formatLength(int length) {

        StringBuilder output = new StringBuilder();

        // WE NEED TO HAVE 4 SPACES RESERVED WHEN DISPLAYING LENGTH OF THE STRING
        int buffer = 4 - String.valueOf(length).length();

        while (output.length() != buffer) {
            // ADD SPACED UNTIL WE'VE REACHED THE PROPER LENGTH
            output.append(" ");
        }

        // FINALLY ADD THE ACTUAL VALUE OF LENGTH
        output.append(length);

        return output.toString();
    }

    private static String formatHex(String hex) {

        StringBuilder output = new StringBuilder();

        if(hex.charAt(0) == '0') {
            hex = hex.replaceFirst("0", "");
        }

        // WE NEED TO HAVE 8 SPACES RESERVED WHEN DISPLAYING HEX VALUES OF THE STRING
        while (output.length() != (8 - hex.length())) {
            // ADD SPACED UNTIL WE'VE REACHED THE PROPER LENGTH
            output.append(" ");
        }
        output.append(hex);

        return output.toString();
    }

    private static void generateOutputFile(String output) {

        try {
            String fileName = generateOutputFileName();// SETTING UP OUTPUT FILE NAME
            FileWriter fw = new FileWriter(fileName);
            fw.write(output);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String formatOutputString(String input) {

        StringBuilder output = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            if (i % 80 != 0) {
                output.append(input.charAt(i));
            } else {
                output.append("\n").append(input.charAt(i));
            }
        }

        return output.toString();
    }
}

/*=============================================================================
| I Jan Darge (ja248964) affirm that this program is
| entirely my own work and that I have neither developed my code together with
| any another person, nor copied any code from any other person, nor permitted
| my code to be copied or otherwise used by any other person, nor have I
| copied, modified, or otherwise used programs created by others. I acknowledge
| that any violation of the above terms will be treated as academic dishonesty.
+=============================================================================*/