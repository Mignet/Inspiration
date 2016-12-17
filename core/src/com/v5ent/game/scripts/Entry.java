package com.v5ent.game.scripts;

/**
 * Created by Mignet on 2016/12/17.
 */

// Type of entries (for blank entries)
enum Types { NONE(0), TEXT(1), BOOL(2), INT(3), FLOAT(4), CHOICE(5);
    private int value = 0;
    Types(int value){
        this.value = value;
    }
    public int getValue(){
        return this.value;
    }
    public static Types valueOf(int value) {    //    手写的从int到enum的转换函数
        switch (value) {
            case 0:
                return NONE;
            case 1:
                return TEXT;
            case 2:
                return BOOL;
            case 3:
                return INT;
            case 4:
                return FLOAT;
            case 5:
                return CHOICE;
            default:
                return NONE;
        }
    }
};
public class Entry {

    int Type; // Type of blank entry (_TEXT, etc.)
// The following two unions contain the various
// information about a single blank entry, from
// the min/max values (for int and float values),
// and the number of choices in a multiple choice entry.
// Text and Boolean entries do not need such info.
        int NumChoices; // # of choices in list
        long lMin; // long min. value
        float fMin; // float min. value
        long lMax; // long max. value
        float fMax; // float max. value
        String[] Choices; // text array for each choice
    // Structure constructor to clear to default values
    Entry()
    {
        Type = Types.NONE.getValue();
        NumChoices = 0;
        Choices = null;
    }
// Structure destructor to clean up used resources
    /*~Entry()
    {
// Special case for choice types
        if(Type == _CHOICE) {
            if(NumChoices) {
                for(long i=0;i<NumChoices;i++)
                    delete [] Choices[i]; // Delete choice text
            }
            delete [] Choices; // Delete choice array
        }
    }*/

}
