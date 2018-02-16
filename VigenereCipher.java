import java.io.*;
import java.util.*;
public class VigenereCipher {
    /*private static double[] frequencies = { 8.167, 1.492, 2.782, 4.253, 12.702, 2.228,
    2.015, 6.094, 6.966, 0.153, 0.772, 4.025, 2.406, 6.749, 7.507,
    1.929, 0.095, 5.987, 6.327, 9.056, 2.758, 0.978, 2.360, 0.150,
    1.974, 0.074 }; 
     */
    private static String theKey = " ";
    //This is the driver method. It will import the files, call the functions, and write them to the required files
    public static void main(String[] args) {
        System.out.println("Importing files...");
        String toEncrypt = FiletoString("sample2_modified.txt");
        String toDecrpyt = FiletoString("sample2_cipher.txt");
        System.out.println("Cleaning files...");
        toEncrypt = cleanText(toEncrypt);
        toDecrpyt = cleanText(toDecrpyt);
        System.out.println("Encrpyting sample2_modified.txt...");
        String encryptedTxt = encrypt(toEncrypt, "leg");
        System.out.println("Analyzing the encrypted file...");
        cryptanalysis(encryptedTxt,3);
        System.out.println("Saving the the encrypted file...");
        stringtoFile("encrypted_modified.txt", encryptedTxt);
        System.out.println("Decrypting the encrypted file...");
        String decryptedTxt = decrypt(encryptedTxt, theKey );
        System.out.println("Saving the decrypted file...");
        stringtoFile("decrypted_modified.txt", decryptedTxt);
        System.out.println("Opening sample2_cipher.txt...");
        System.out.println("Analyzing sample2_cipher.txt...");
        cryptanalysis(toDecrpyt,3);
        decryptedTxt = decrypt(toDecrpyt, theKey);
        stringtoFile("decrypted_sample2.txt", decryptedTxt);
        System.out.println("Encrypted sample2_modified.txt saved as encrpyted_modified.txt...");
        System.out.println("Decrypted encrypted_file.txt saved as decrypted_modified.txt...");
        System.out.println("Decrypted sample2_cipher.txt saved as decrypted_sample2.txt...");
    }

    //This method will read the file and convert it to a string
    public static String FiletoString(String filePath)
    {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
        {

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null)
            {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return contentBuilder.toString();

    }

    //This method will use the string and write it to a file
    public static void stringtoFile(String fileName, String strContent) {

        BufferedWriter bufferedWriter = null;
        try {
            File myFile = new File(fileName);
            // check if file exist, otherwise create the file before writing
            if (!myFile.exists()) {
                myFile.createNewFile();
            }
            Writer writer = new FileWriter(myFile);
            bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(strContent);
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try{
                if(bufferedWriter != null) bufferedWriter.close();
            } catch(Exception ex){

            }
        }
    }

    //this method will encrypt a given text with the given key
    public static String encrypt(String text, String key) {
        String result = "";
        text = text.toUpperCase();
        key = key.toUpperCase();
        int rawChar;
        int keyChar;
        char encryptedChar;
        //loops through the raw text file, adds the raw character (rawChar) to the corresponding key character (keyChar)
        //it is then & by 26 to get the char numeric val, then 65 is added to get the ascii code. Result is then char casted to give the actual char
        for (int i = 0; i < text.length(); i++){
            rawChar = (int)text.charAt(i);
            keyChar = (int)key.charAt(i % key.length());
            encryptedChar = (char)((rawChar + keyChar) % 26 + 65);
            result += encryptedChar;
        }
        return result;
    }

    //this method will decrypt a given text with the given key
    static String decrypt(String text, String key) {
        String result = "";
        int temp;
        text=text.toUpperCase();
        key=key.toUpperCase();
        int cipherChar,keyChar;
        //loops through each char in the cipherText and checks if it matches the character in the corresponding key string
        for(int i = 0; i < text.length(); i++)
        {
            cipherChar = (int)text.charAt(i);
            keyChar = (int)key.charAt(i % key.length());
            temp = cipherChar-keyChar;
            if(temp < 0){
                temp=26-Math.abs(temp);
            }
            result += (char)(temp + 65);
        }
        return result;
    }

    public static void cryptanalysis(String text, int kSize){
        
        text=text.toUpperCase();
        
        //create 2 arrays one to store the common indexs and one to store the common values of the characters
        double [] cVal = new double[kSize];
        int [] cIndex = new int[kSize];
        int possibleMatch = 0;

        //create stringbuilder in order to construct the key
        StringBuilder sb = new StringBuilder();

        //create 2d array in order to track the occurences of each characters corresponding value
        int[][] occurences = new int[kSize][26];

        //create array to hold the size of each split of the ciphertext
        int[] groupSize = new int[kSize];
        //int groupSize = 0;

        //each iteration will increement the group size accordingly meaning it will store the amount of characters of each split of the cipher text
        //each iteration will produce ksize partitions, which will increment each time the value 0-25 is found
        //this means it will track the frequency of occurences for each char value for each partition (ksize partitions)
        
        for(int i = 0; i < text.length(); i++)
        {
            occurences[i % kSize][text.charAt(i)-65]++;
            groupSize[i % kSize]++;
        }
        
        double perc;
        char currentLetter;
        int freq;
        
        //this loop will execute ksize amount of times, meaning if the key size is 3, it wil execute 3 times
        for(int i = 0; i < kSize; i++)
        {
            
            System.out.print("-------------------------------\n");
            System.out.print("Frequency table at position: " + i);
            System.out.print("\n-------------------------------\n\n");
            System.out.print("Alph Freq  Perc  Bar\n");
            
            //this will execute 26 times in order to run frequeny
            for(int j = 0; j < 26; j++)
            {
                //determins which value has occured the most times, stores them in the most common index array
                freq = occurences[i][j];
                perc = (1.0 * freq) / (1.0 * groupSize[i]);
                currentLetter = (char)(j + 65);
                if(cVal[i] < (perc))
                {
                    cVal[i] = (perc);
                    cIndex[i] = j;
                }
                
                //print out each letter and its freq and % of occurence
                System.out.printf("%c: %5d  (%4.2f) ",currentLetter,freq, perc);
                
                //this simply prints the bar representing the freq % with *
                for(int k = 0; k < (int)(100 * (1.0 * freq)) / (1.0 * groupSize[i]); k++)
                {
                    System.out.print("*");
                }

                System.out.println();
            } 
            
            System.out.println();
        }

        //this loop will contstruct a string containing the key using the 
        for(int i = 0; i < kSize; i++)
        {
            possibleMatch=(cIndex[i] - 4);

            if(possibleMatch < 0)
            {
                possibleMatch = 26 - Math.abs(possibleMatch);
            }

            sb.append(((char)(possibleMatch+65)));
        }

        theKey = toString(sb);
        System.out.println("The most likely key is " + theKey + " with a length of " + kSize);
        System.out.println("");
    }

    public static String toString(StringBuilder sb){
        return sb.toString();
    }

   
    //this method will check to ensure tht all characters are readable to the porgram
    //for example, the first character in the modified file is " and ghe last is /n... this method will remove them
    //int a, and int b are there for testing. they show the difference in file size between the raw file and the clean file
    //this method is to enable the program to be run without modified files for testing
    //THIS IS NEEDED OR PROGRAM WILL NOT FUNCTION!!!!!
    public static String cleanText(String rawFile)
    {

        String clean = "";
        rawFile = rawFile.toUpperCase();
        //int a shows the file size of the raw file
        //int a = rawFile.length();
        for(int i = 0; i < rawFile.length();i++)
        {

            if(rawFile.charAt(i) - 65 >= 0 && rawFile.charAt(i) - 65 <= 26)
            {
                clean += rawFile.charAt(i);
            }

        }
        
        //int b shows the file size of the cleaned file
        //int b = clean.length();
        return clean;
    }
}
