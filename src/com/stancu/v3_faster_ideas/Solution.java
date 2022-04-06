package com.stancu.v3_faster_ideas;

public class Solution {

    /*
        Another idea would to have an initial split method that I call the first time, before I enter the main loop.
        Those values are going to be in a list of strings, and I would keep track of them, and when I am moving through the
        password, I would actually have to move through the partitions. I would firstly repair the ones that have repeating
        sequences. The size of the password I would save it in a variable, and based on length validation, I would add to it, or
        I would subtract from it. I do this operation so that I don't have to permanently sum up the lengths of the partitions.
        The operations of replacement would be done per partition.

        Another idea, and the optimal one would be to only return the steps without performing operations on the password,
        but to keep track of 3k, 3k+1 and 3k+2 partitions and to only count the possible deletions and insertions.
        Therefore, for sequences like "[aaa][aaa]", i would retain a value 2, because it has 2 formations of 3 characters
        I can transform it in aaZaa9(that would with my previous code, but with this code I do not actually care about the characters
        ,but I only care about the steps that can be done, so there would be 2 steps and that's it, I do not have to think about
        what characters I can add there
        Below I have a some sort of implementation of it
    */
    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 20;

    public int passwordChecker(String password) {

        int insertCount = 0, deleteCount = 0;
        int threeKSeq = 0;
        int notThreeKSeq = 0;

        if (password.length() < MIN_LENGTH)
            insertCount = MIN_LENGTH - password.length();
        else if (password.length() > MAX_LENGTH)
            deleteCount = password.length() - MAX_LENGTH;

        char currentIndex = password.toCharArray()[0];
        int sequenceLength = 1;

        for (int currentIndexPassword = 1; currentIndexPassword < password.length(); currentIndexPassword++) {

            while (currentIndexPassword < password.length() &&
                    currentIndex == password.toCharArray()[currentIndexPassword]) {
                sequenceLength++;
                currentIndexPassword++;
            }

            if (sequenceLength % 3 == 0) {
                threeKSeq += sequenceLength / 3;
            } else if (sequenceLength != 1 && sequenceLength != 2)
                notThreeKSeq += sequenceLength / 3;

            if (currentIndexPassword < password.length())
                currentIndex = password.toCharArray()[currentIndexPassword];
            sequenceLength = 1;
        }

    //        Here I would have to add some values from above and get the final result
        return 1;
    }
}