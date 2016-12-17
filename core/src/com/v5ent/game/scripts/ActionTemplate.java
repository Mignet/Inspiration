package com.v5ent.game.scripts;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static com.v5ent.game.scripts.Types.BOOL;


/**
 * Created by Mignet on 2016/12/17.
 */

public class ActionTemplate {
    private int NumActions; // # of actions in template
    private ScriptAction m_ActionParent; // list of template actions

    // Functions for reading text (mainly used in actions)
    private boolean GetNextQuotedLine(char[] Data, File fp, long MaxSize){
            int c;
            int Pos = 0;
        FileReader fr = null;
        try {
            fr = new FileReader(fp);
            if((c = fr.read()) == -1){
                return false;
            }
            // Read until a quote is reached (or EOF)
            while(true) {
                if((char)c == '"') {
// Read until next quote (or EOF)
                    while(true) {
                        if((c = fr.read()) == -1)
                            return false;
// Return text when 2nd quote found
                        if((char)c == '"') {
                            Data[Pos] = 0;
                            return true;
                        }
// Add acceptable text to line
                        if(c != 0x0a && c != 0x0d) {
                            if(Pos < MaxSize-1)
                                Data[Pos++] = (char)c;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            if (fr != null)
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    };
    private boolean GetNextWord(char[] Data, File fp, long MaxSize){
        int c;
        int Pos = 0;
// Reset word to empty
        Data[0] = 0;
        FileReader fr = null;
        try {
            fr = new FileReader(fp);
// Read until an acceptable character found
            while (true) {
                if ((c = fr.read()) == -1) {
                    Data[0] = 0;
                    return false;
                }
// Check for start of word
                if (c != 32 && c != 0x0a && c != 0x0d) {
                    Data[Pos++] = (char) c;
// Loop until end of word (or EOF)
                    while ((c = fr.read()) != -1) {
// Break on acceptable word separators
                        if (c == 32 || c == 0x0a || c == 0x0d)
                            break;
// Add if enough room left
                        if (Pos < MaxSize - 1)
                            Data[Pos++] = (char) c;
                    }
// Add end of line to text
                    Data[Pos] = 0;
                    return true;
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            if (fr != null)
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    };

    public ActionTemplate(){};
//    ~cActionTemplate();
    // Load and free an action template
    public boolean Load(String filename){
        File fp = new File(filename);
        char[] Text = new char[2048];
        ScriptAction Action, ActionPtr = null;
        Entry Entry;
        int i, j;
// Free previous action structures
        Free();
// Open the action file
        if(!fp.exists()){
            return false;
        }
//        if((fp=fopen(Filename, “rb”))==null)
//            return false;
// Keep looping until end of file found
        while(true) {
// Get next quoted action
            if(GetNextQuotedLine(Text, fp, 2048) == false)
                break;
// Quit if no action text
            if(Text[0]==0)
                break;
// Allocate an action structure and append it to list
            Action = new ScriptAction();
            Action.Next = null;
            if(ActionPtr == null)
                m_ActionParent = Action;
            else
            ActionPtr.Next = Action;
            ActionPtr = Action;
// Copy action text
            Action.Text = String.valueOf(Text);
//            strcpy(Action.Text, Text);
// Store action ID
            Action.ID = NumActions;
// Increase the number of actions loaded
            NumActions++;
// Count the number of entries in the action
            for(i=0;i<Text.length;i++) {
                if(Text[i] == '~')
                Action.NumEntries++;
            }
// Allocate and read in entries (if any)
            if(Action.NumEntries!=0) {
                Action.Entries = new Entry[Action.NumEntries];
                for(i=0;i<Action.NumEntries;i++) {
                    Entry = Action.Entries[i];
// Get type of entry
                    GetNextWord(Text, fp, 2048);
// TEXT type, no data follows
                    if("TEXT".equals(Text)) {
// Set to text type
                        Entry.Type = Types.TEXT.getValue();
                    } else
// INT type, get min and max values
                        if("INT".equals(Text)) {
// Set to INT type and allocate INT entry
                            Entry.Type = Types.INT.getValue();
// Get min value
                            GetNextWord(Text, fp, 2048);
                            Entry.lMin = Long.valueOf(Text.toString());
// Get max value
                            GetNextWord(Text, fp, 2048);
                            Entry.lMax = Long.valueOf(Text.toString());
                        } else
// FLOAT type, get min and max values
                            if("FLOAT".equals(Text)) {
// Set to FLOAT type and allocate FLOAT entry
                                Entry.Type = Types.FLOAT.getValue();
// Get min value
                                GetNextWord(Text, fp, 2048);
                                Entry.fMin = Float.valueOf(Text.toString());
// Get max value
                                GetNextWord(Text, fp, 2048);
                                Entry.fMax = Float.valueOf(Text.toString());
                            } else
// BOOL type, no options
                                if("BOOL".equals(Text)) {
// Set to BOOL type and allocate BOOL entry
                                    Entry.Type = BOOL.getValue();
                                } else
// CHOICE type, get number of entries and entry’s texts
                                    if("CHOICE".equals(Text)) {
// Set to CHOICE type and allocate CHOICE entry
                                        Entry.Type = Types.CHOICE.getValue();
// Get the number of choices
                                        GetNextWord(Text, fp, 1024);
                                        Entry.NumChoices = Integer.valueOf(Text.toString());
                                        Entry.Choices = new String[Entry.NumChoices];
// Get each entry text
                                        for(j=0;j<Entry.NumChoices;j++) {
                                            GetNextQuotedLine(Text, fp, 2048);
                                            Entry.Choices[j] = String.valueOf(Text);
                                        }
                                    }
                }
            }
        }
        return true;
    };
    public boolean Free(){return true;};
    // Get # actions in template, action parent,
// and specific action structure.
    public long GetNumActions(){return NumActions;};
    public ScriptAction GetActionParent(){return m_ActionParent;};
    public ScriptAction GetAction(long Num){return null;};
// Get a specific type of sScript structure
    public Script CreateScriptAction(int Type){
        int i;
        Script Script;
        ScriptAction ActionPtr;
// Make sure it’s a valid action - Type is really the
// action ID (from the list of actions already loaded).
        if(Type >= NumActions)
            return null;
// Get pointer to action
        if((ActionPtr = GetAction(Type)) == null)
            return null;
// Create new sScript structure
        Script = new Script();
        // Set type and number of entries (allocating a list)
        Script.Type = Type;
        Script.NumEntries = ActionPtr.NumEntries;
        Script.Entries = new ScriptEntry[Script.NumEntries];
// Set up each entry
        for(i=0;i<Script.NumEntries;i++) {
// Save type
            Script.Entries[i].Type = ActionPtr.Entries[i].Type;
// Set up entry data based on type
            switch(Types.valueOf(Script.Entries[i].Type)) {
                case TEXT:
                    Script.Entries[i].Text = null;
                    break;
                case INT:
                    Script.Entries[i].lValue = ActionPtr.Entries[i].lMin;
                    break;
                case FLOAT:
                    Script.Entries[i].fValue = ActionPtr.Entries[i].fMin;
                    break;
                case BOOL:
                    Script.Entries[i].bValue = true;
                    break;
                case CHOICE:
                    Script.Entries[i].Selection = 0;
                    break;
            }
        }
        return Script;
    };
    // Get info about actions and entries
//    public long GetNumEntries(long ActionNum);
//    public Entry GetEntry(long ActionNum, long EntryNum);
//    // Expand action text using min/first/true choice values
//    public boolean ExpandDefaultActionText(String Buffer, ScriptAction Action);
//    // Expand action text using selections
//    public boolean ExpandActionText(String Buffer, ScriptAction Script);
}
