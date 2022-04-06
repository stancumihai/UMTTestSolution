package com.stancu.v2_refactored;

import com.stancu.constants.PasswordConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


/*
    The base idea of my algorithm is that I construct a strong password as I calculate the steps. Even if it is a much slower
    algorithm, it is a lot easier to debug, and in case I want to add the functionality of recommended password, I can return
    that password.
*/
public class PasswordValidator2 {

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
    private boolean containsProperCharCases(String s) {
        boolean hasUpper = false, hasLower = false, hasDigit = false;
        for (Character character : s.toCharArray()) {
            if (!hasDigit && Character.isDigit(character))
                hasDigit = true;
            if (!hasUpper && Character.isUpperCase(character))
                hasUpper = true;
            if (!hasLower && Character.isLowerCase(character))
                hasLower = true;

            if (hasLower && hasUpper && hasDigit)
                return true;
        }
        return false;
    }

    private int convertToInt(Boolean b) {
        return b ? 1 : 0;
    }

    /*
        This function is used for adding a character. I have a dummy 'Z'. I check if my string has any lowercase
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
        Character toBeAdded = chooseBestCharacter(password, index);
        return password.substring(0, index) + toBeAdded + password.substring(index);
    }

    /* This function changes the character at index 'index' with the char from chooseBestChar function
       I use a StringBuilder for appending so that I don't create another string for each append(strings are immutable)
    */
    public String changeCharacter(String password, int index) {
        char toBeAdded = chooseBestCharacter(password, index);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < password.length(); i++) {
            if (i == index) {
                builder.append(toBeAdded);
            } else builder.append(password.charAt(i));
        }
        return builder.toString();
    }

    /*
        Splits password into partitions. (Their reunion is the password and all the subsets are disjoint)
        Here I used a bit of math. 3 repeating characters mean number 3 , which means regarding modulo
        only 3 values 0 , 1 , 2
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
        int index = 0;
        for (String partition : partitions) {
            int length = partition.length();
            if (length < 3) {
                index += length;
                continue;
            }
            if (length % 3 == 0) {
                return index;
            }
            if (length % 3 == 1 && mod1Index == -1) {
                mod1Index = index;
            }
            if (length % 3 == 2 && mod2Index == -1) {
                mod2Index = index;
            }
            index += length;
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

    public boolean isStrongPasswordEnough(String password) {

        if (repeatingIndex(password) > -1) {
            return false;
        }
        return containsProperCharCases(password);
    }

    public int strongPasswordChecker(String password) {
        int steps = 0;
        while (true) {
            // Here I find the first repeating index.
            int repeatIndex = repeatingIndex(password);
            int safeIndex = findSafeIndex(password);
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
                if (repeatIndex > -1) {
                    safeIndex = repeatIndex + 2;
                }
                // I return the password without the find safe index.
                password = password.substring(0, safeIndex) + password.substring(safeIndex + 1);
            } else {
                // If none of the above map to my password, it can be the case that my password is a string one, so I check that
                if (isStrongPasswordEnough(password)) {
                    break;
                }
                // finds safe index
                if (repeatIndex > -1) {
                    safeIndex = repeatIndex + 2;
                }
                password = changeCharacter(password, safeIndex);
                if (isStrongPasswordEnough(password)) {
                    steps += 1;
                    break;
                }
            }
            steps += 1;
        }
        return steps;
    }
}
