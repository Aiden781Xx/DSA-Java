import java.util.*;

public class IntToRoman {
    public int romanToInt(String s) {
        int number = 0;
        int prevValue = 0;

        for (int i = s.length() - 1; i >= 0; i--) {
            char currentChar = s.charAt(i);
            int currentValue = 0;

            switch (currentChar) {
                case 'I':
                    currentValue = 1;
                    break;
                case 'V':
                    currentValue = 5;
                    break;
                case 'X':
                    currentValue = 10;
                    break;
                case 'L':
                    currentValue = 50;
                    break;
                case 'C':
                    currentValue = 100;
                    break;
                case 'D':
                    currentValue = 500;
                    break;
                case 'M':
                    currentValue = 1000;
                    break;
            }

            if (currentValue < prevValue) {
                number -= currentValue;
            } else {
                number += currentValue;
            }

            prevValue = currentValue;
        }

        return number;
    }

    public static void main(String args[]) {
        Solution solution = new Solution();
        System.out.println(solution.romanToInt("MCMXV"));  
    }
}