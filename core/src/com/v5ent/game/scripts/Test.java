package com.v5ent.game.scripts;

/**
 * Created by Mignet on 2016/12/17.
 */

public class Test {
    boolean[] GameFlags = new boolean[256]; // Some game flags defined in the game
    static int NumActions = 0;
// pScript = pre-loaded script that contains the following:
// “If GameFlag (0) equals (true) then”
// “Print (It’s true!)”
// “Set GameFlag (0) to (false)”
// “Else”
// “Print (It’s false.)”
// “EndIf”
// Action processing functions

/*    Script Script_IfThen(Script Script);
    Script Script_Else(Script Script);
    Script Script_EndIf(Script Script);
    Script Script_SetFlag(Script Script);
    Script Script_Print(Script Script);*/

    // The script action execution structure
    abstract class ScriptProcess {
        abstract Script Func(Script ScriptPtr);
    } ;
    ScriptProcess[] ScriptProcesses = new ScriptProcess[5];
    // List of script action function structures
   /* ScriptProcesses ScriptProcesses[] = {
            { Script_IfThen },{ Script_Else },
            { Script_EndIf },
            { Script_SetFlag },
            { Script_Print }
    };*/
    void RunScript(Script pScript)
    {
// Clear the GameFlags array to false for this example
        for(short i=0;i<256;i++)
            GameFlags[i] = false;
// Scan through script and process functions
        while(pScript != null) {
// Call script function and break on null return value.
// Any other return type is the pointer to the next
// function, which is typically pScript.Next.
            pScript = ScriptProcesses[pScript.Type].Func(pScript);
        }
    }
    Script Script_IfThen(Script Script)
    {
        boolean Skipping; // Flag is skipping script actions
// See if a flag matches second entry
        if(GameFlags[(int)Script.Entries[0].lValue % 256] ==  Script.Entries[1].bValue)
        Skipping = false;
        else
        Skipping = true;
// At this point, Skipping states if the script actions
// need to be skipped due to a conditional if..then statement.
// Actions are further processed if skipped = false, looking
// for an else to flip the skip mode, or an endif to end
// the conditional block.
// Go to next action to process
        Script = Script.Next;
        while(Script != null) {
// if Else, flip skip mode
            if(Script.Type == 1)
                Skipping = (Skipping == true) ? false : true;
// break on EndIf
            if(Script.Type == 2)
                return Script.Next;
// Process script function in conditional block
// making sure to skip actions when condition not met.
            if(Skipping == true)
                Script = Script.Next;
            else {
                if((Script = ScriptProcesses[Script.Type].Func(Script)) == null)
                return null;
            }
        }
        return null; // end of script reached
    }
    Script Script_SetFlag(Script Script)
    {
// Set a Boolean flag
        GameFlags[(int)Script.Entries[0].lValue % 256] =  Script.Entries[1].bValue;
        return null;
    };
    Script Script_Else(Script script) { return script.Next; }
    Script Script_EndIf(Script script) { return script.Next; }
    Script Script_Print(Script Script)
    {
        MessageBox(null, Script.Entries[0].Text, "Text", "MB_OK");
        return Script.Next;
    }

    public static void main(String[] args){
        Script LoadedScript = Script.LoadScript("Script.mls", NumActions);
        Script ScriptPtr = LoadedScript; // Start at root
// Loop through all script actions in list
        while(ScriptPtr != null) {
// Is it an action 0?
            if(ScriptPtr.Type == 0) {
// This action definitely has one entry, the text.
// Display the text in a message box
                MessageBox(null, ScriptPtr.Entries[0].Text, "TEXT", "MB_OK");
            }
// Go to next action in script
            ScriptPtr = ScriptPtr.Next;
        }
    }

    private static void MessageBox(Object o, String text, String text1, String mbOk) {
        System.out.println(text1+"=>"+text);
    }
}
