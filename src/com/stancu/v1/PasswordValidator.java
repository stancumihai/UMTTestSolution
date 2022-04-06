package com.stancu.v1;

import com.stancu.constants.PasswordConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


/*
    The base idea of my algorithm is that I construct a strong password as I calculate the minimum steps. Even if it is a much slower
    algorithm, it is a lot easier to debug, and in case I want to add the functionality of recommended password, I can return
    that password.
    The second version(refactored) of the algorithm keeps some values that are used multiple times in the upper part of the function , so I don't recalculate them.
    Not the best version, I could add dynamic programming for calculating the subsets and only calculating on them
**/
public class PasswordValidator {

    //    Helper function which checks if password has min length
    public boolean checkMinLength(String password) {
        return password.length() < PasswordConstants.PASSWORD_MIN_LENGTH;
    }

    //    Helper function which checks if password has max length
    public boolean checkMaxLength(String password) {
        return password.length() > PasswordConstants.PASSWORD_MAX_LENGTH;
    }

    //    Helper function which checks if password contains lower case character
    public boolean containsLowerCase(String password) {
        Predicate<Character> isLower = Character::isLowerCase;
        for (Character character : password.toCharArray()) {
            if (isLower.test(character)) {
                return true;
            }
        }
        return false;
    }

    // Helper function which checks if password contains upper case character
    public boolean containsUpperCase(String password) {
        Predicate<Character> isUpper = Character::isUpperCase;
        for (Character character : password.toCharArray()) {
            if (isUpper.test(character)) {
                return true;
            }
        }
        return false;
    }

    //    Helper function which checks if password contains digit character
    public boolean containsDigit(String password) {
        Predicate<Character> isDigit = Character::isDigit;
        for (Character character : password.toCharArray()) {
            if (isDigit.test(character)) {
                return true;
            }
        }
        return false;
    }

    //    Helper functions which combines the 3 functions above
    public boolean containsProperCharCases(String password) {
        return containsDigit(password) && containsLowerCase(password) && containsUpperCase(password);
    }

    /*  This function is used for adding a character. I have a dummy 'Z'. I check if my string has any lowercase
        characters. If it does not, the dummy is a 'z' because it makes the password stronger.
        I do the same with the digit . If at index 'index' I have the same dummy value, I put another character
        so that I don't raise the probability of having 3 or more repeating characters.
    */
    public Character chooseBestCharacter(String password, int index) {

        char dummy = 'Z';
        if (!containsLowerCase(password)) {
            dummy = 'z';
        } else if (!containsDigit(password)) {
            dummy = '9';
        }
        if (password.toCharArray()[index] == dummy) {
            dummy = 'Y';
        }
        return dummy;
    }

    //    This function adds at index 'index' the dummy character.
    public String addCharacter(String password, int index) {
        // I choose the best character that fits my password
        Character characterToBeAdded = chooseBestCharacter(password, index);
        return password.substring(0, index) + characterToBeAdded + password.substring(index);
    }

    /*
       This function changes the character at index 'characterIndex' with the char from chooseBestChar function
       I use a StringBuilder for appending so that I don't create another string for each append(strings are immutable)
    */
    public String changeCharacter(String password, int index) {
        char characterToBeReplacedWith = chooseBestCharacter(password, index);
        StringBuilder builder = new StringBuilder();
        for (int characterIndex = 0; characterIndex < password.length(); characterIndex++) {
            if (characterIndex == index) {
                builder.append(characterToBeReplacedWith);
            } else builder.append(password.charAt(characterIndex));
        }
        return builder.toString();
    }

    /*
        Splits password into partitions. (Their reunion is the password and all the subsets are disjoint)
        Here I used a bit of math. 3 repeating characters mean number 3 , which means regarding modulo
        only 3 values 0 , 1 , 2
        Here I waste a lot of time because it recalculates my partitions. I could only calculate them once and in the calculated value.
    */
    public int repeatingIndex(String password) {
        List<String> partitions = new ArrayList<>();
        int previousSplit = 0;
        //    here is the logic for the partition of repeating characters
        for (int i = 1; i < password.length(); i++) {
            /*
                Checks whether is the same character as previous, and if it fails the test, I add the substring
                to the partitions list, and I set previousSplit to the current index, so that the next partition will
                start at index "i".
             */
            if (password.toCharArray()[i - 1] != password.toCharArray()[i]) {
                String substring = password.substring(previousSplit, i);
                partitions.add(substring);
                previousSplit = i;
            }
        }

        /*
            I need this line because the last pair or the last element will not be added to the partitions
            because the "i" counter will not match the condition (i < password.length())
        */
        partitions.add(password.substring(previousSplit));

        // Here I see which is the max length for a partition
        int maxLength = Integer.MIN_VALUE;
        for (String partition : partitions) {
            if (partition.length() > maxLength) {
                maxLength = partition.length();
            }
        }
        /*
            If there is no partition with length >=3, then It means there are no repeating substrings,
            therefore I return -1.
         */
        if (maxLength < 3) {
            return -1;
        }
        int mod1Index = -1;
        int mod2Index = -1;
        int currentIndex = 0;
        for (String partition : partitions) {
            int length = partition.length();
            // If the length of the partition is less than 3, then it is not a repeating sequence, and I just move the index.
            if (length < 3) {
                currentIndex += length;
                continue;
            }
            //
            if (length % 3 == 0) {
                return currentIndex;
            }
            if (length % 3 == 1 && mod1Index == -1) {
                mod1Index = currentIndex;
            }
            if (length % 3 == 2 && mod2Index == -1) {
                mod2Index = currentIndex;
            }
            // Aici sar la urmatoarea partitie
            currentIndex += length;
        }
        if (mod1Index > -1) {
            return mod1Index;
        }
        if (mod2Index > -1) {
            return mod2Index;
        }
        return 0;
    }

    /*
         Because in the third validator there are only 3 aspects to take into consideration(digit,lowercase,uppercase),
         and the fact that the password must have at least 6 characters, the cases intertwine, because the minim length
         of a proper case password is 3, but I need at least 6 characters, so I will have at least 2 digits, or 2 uppercase,
         or 2 lowercase, therefore I will find a safe index that I can be based on so that it does not harm the strength of
         the password when I delete it.
     */
    public int findSafeIndex(String password) {
        for (int i = 0; i < password.length(); i++) {
            char currentPasswordCharacter = password.toCharArray()[i];
            // Here I construct the truncated password without the index i
            String truncatedPassword = password.substring(0, i) + password.substring(i + 1);
            if (Character.isDigit(currentPasswordCharacter) && containsDigit(truncatedPassword)) {
                return i;
            }
            if (Character.isLowerCase(currentPasswordCharacter) && containsLowerCase(truncatedPassword)) {
                return i;
            }
            if (Character.isUpperCase(currentPasswordCharacter) && containsUpperCase(truncatedPassword)) {
                return i;
            }
        }
        return 0;
    }

    //    This function checks whether the password is a strong one
    public boolean isStrongPassword(String password) {

        //   Firstly, I check for the min length
        if (checkMinLength(password)) {
            return false;
        }
        // Secondly, I check for the max length
        if (checkMaxLength(password)) {
            return false;
        }
        // After that, I check if the password has any substring of repeating characters
        if (repeatingIndex(password) > -1) {
            return false;
        }
        // And lastly, I check if the password has the characters with the proper casings
        return containsProperCharCases(password);
    }

    public int strongPasswordChecker(String password) {
        int steps = 0;
        while (true) {
            // Here I find the first repeating index.
            int repeatIndex = repeatingIndex(password);
            // I firstly check if my password has lower lengths that minimum
            if (checkMinLength(password)) {
                int index = 0;
                // I check whether I find repeating characters
                if (repeatIndex > -1) {
                    index = repeatIndex + 2;
                }
                // I add a character to the password to close the gap to the proper length, and the character that I add is a properly chosen one.(See in add_character function)
                password = addCharacter(password, index);
            } else if (checkMaxLength(password)) {
                // Same logic as above, but I find a safe index to delete
                int index = findSafeIndex(password);
                if (repeatIndex > -1) {
                    index = repeatIndex + 2;
                }
                // I return the password without the find safe index.
                password = password.substring(0, index) + password.substring(index + 1);
            } else {
                // If none of the above map to my password, it can be the case that my password is a string one, so I check that
                if (isStrongPassword(password)) {
                    System.out.println(password);
                    break;
                }
                // Finds safe index for character changing
                int index = findSafeIndex(password);

                // If it exists a repeating sequence, I move the index at the end of the repeating 3
                if (repeatIndex > -1) {
                    index = repeatIndex + 2;
                }
                // I change the character at index "index"
                password = changeCharacter(password, index);
            }
            // The minimum steps to be increased
            steps += 1;
            if (isStrongPassword(password)) {
                System.out.println(password);
                break;
            }
        }
        return steps;
    }
}
