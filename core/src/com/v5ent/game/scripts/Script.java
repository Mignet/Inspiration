package com.v5ent.game.scripts;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by Mignet on 2016/12/17.
 */

public class Script {
    int Type; // 0 to (number of actions-1)
    int NumEntries; // # entries in this script action
    ScriptEntry[] Entries; // Array of entries
    Script Prev; // Prev in linked list
    Script Next; // Next in linked list
    Script()
    {
        Type = 0; // Clear to defaults
        NumEntries = 0;
        Entries = null;
        Prev = Next = null;
//        Script ScriptRoot = new Script();
//        Script ScriptPtr = new Script();
//        ScriptPtr.Prev = ScriptRoot; // Point back to root
//        ScriptRoot.Next = ScriptPtr; // Point to second action
    }
    void TraverseScript(Script pScript)
    {
        while(pScript != null) { // loop until no more script actions
// Do something with pScript
// pScript.Type holds the script action ID
            pScript = pScript.Next; // Go to next script action
        }
    }
    boolean SaveScript(String filename, Script ScriptRoot)
    {
        File fp = new File(filename);
        int i, j, NumActions;
        Script ScriptPtr;
// Make sure there’s some script actions
        if((ScriptPtr = ScriptRoot) == null)
            return false;
// Count the number of actions
        NumActions = 0;
        while(ScriptPtr != null) {
            NumActions++; // Increase count
            ScriptPtr = ScriptPtr.Next; // Next action
        }
// Open the file for output
        if(!fp.exists())
            return false; // return a failure
// Output # of script actions
//        fwrite(&NumActions, 1, sizeof(long), fp);
        FileWriter fw = null;
        try {
            fw = new FileWriter(fp);
            fw.write(String.valueOf(NumActions));
// Loop through each script action
            ScriptPtr = ScriptRoot;
            for(i=0;i<NumActions;i++) {
// Output type of action and # of entries
                fw.write(String.valueOf(ScriptPtr.Type));
//                fwrite(&ScriptPtr.Type, 1, sizeof(long), fp);
                fw.write(String.valueOf(ScriptPtr.NumEntries));
//                fwrite(&ScriptPtr.NumEntries, 1, sizeof(long), fp);
// Output entry data (if any)
                if(ScriptPtr.NumEntries > 0) {
                    for(j=0;j<ScriptPtr.NumEntries;j++) {
// Write entry type and data
                        fw.write(String.valueOf(ScriptPtr.Entries[j].Type));
//                        fwrite(&ScriptPtr.Entries[j].Type, 1,sizeof(long), fp);
                        fw.write(String.valueOf(ScriptPtr.Entries[j].IOValue));
//                        fwrite(&ScriptPtr.Entries[j].IOValue,1,sizeof(long),fp);
// Write text entry (if any)
                        if(ScriptPtr.Entries[j].Type == Types.TEXT.getValue() &&  ScriptPtr.Entries[j].Text != null){
                            fw.write(ScriptPtr.Entries[j].Text);
//                            fwrite(ScriptPtr.Entries[j].Text, 1, ScriptPtr.Entries[j].Length, fp);
                        }
                    }
                }
// Go to next script structure in linked list
                ScriptPtr = ScriptPtr.Next;
            }
//            fclose(fp);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            if (fw != null)
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
        }
        return true; // return a success!
    }

static Script LoadScript(String filename, int NumActions)
        {
        File fp = new File(filename);
        int i, j, Num;
        Script ScriptRoot = null, Script, ScriptPtr = null;
// Open the file for input
            if(!fp.exists()){
                return null;
            }
//        if((fp=fopen(Filename, “rb”))==null)
//        return null;
// Get # of script actions from file
            FileReader fr = null;
            try {
                fr = new FileReader(fp);
                char ch = (char)fr.read();
                Num = Integer.valueOf(String.valueOf(ch));
//                fread(&Num, 1, sizeof(long), fp);
// Store number of actions in user supplied variable
                if(NumActions != 0) {
                    NumActions = Num;
                }
// Loop through each script action
                for(i=0;i<Num;i++) {
// Allocate a script structure and link in
                    Script = new Script();
                    if(ScriptPtr == null) {
                        ScriptRoot = Script; // Assign root
                    }else {
                        ScriptPtr.Next = Script;
                    }
                    ScriptPtr = Script;
// Get type of action and # of entries
                    ch = (char)fr.read();
                    Script.Type = Integer.valueOf(String.valueOf(ch));
//                    fread(&Script.Type, 1, sizeof(long), fp);
                    ch = (char)fr.read();
                    Script.NumEntries = Integer.valueOf(String.valueOf(ch));
//                    fread(&Script.NumEntries, 1, sizeof(long), fp);
// Get entry data (if any)
                    if(Script.NumEntries!=0) {
// Allocate entry array
                        Script.Entries = new ScriptEntry[Script.NumEntries];
// Load in each entry
                        for(j=0;j<Script.NumEntries;j++) {
// Get entry type and data
                            ch = (char)fr.read();
                            Script.Entries[j].Type = Integer.valueOf(String.valueOf(ch));
//                            fread(&Script.Entries[j].Type, 1, sizeof(long), fp);
                            ch = (char)fr.read();
                            Script.Entries[j].IOValue = Integer.valueOf(String.valueOf(ch));
//                            fread(&Script.Entries[j].IOValue, 1, sizeof(long), fp);
// Get text (if any)
                            if(Script.Entries[j].Type == Types.TEXT.getValue() &&  Script.Entries[j].Length!=0) {
// Allocate a buffer and get string
                                char[] temp =  new char[Script.Entries[j].Length];
                                fr.read(temp);
                                Script.Entries[j].Text = String.valueOf(temp);
//                                fread(Script.Entries[j].Text, 1,   Script.Entries[j].Length, fp);
                            }
                        }
                    }
                }
                fr.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (fr != null)
                    try {
                        fr.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

        return ScriptRoot;
        }
}
