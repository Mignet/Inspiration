package com.v5ent.game.scripts;

/**
 * Created by Mignet on 2016/12/17.
 */

public class ScriptAction {
    int ID; // Action ID (0 to # actions-1)
    String Text; // Action text[256]
    short NumEntries; // # of entries in action
    Entry[] Entries; // Array of entry structures
    ScriptAction Next; // Next action in linked list
    public ScriptAction()
    {
        ID = 0; // Set all data to defaults
        Text = null;
        NumEntries = 0;
        Entries = null;
        Next = null;
    }
    /*~sAction()
    {
        delete [] Entries; // Free entries array
        delete Next; // Delete next in list
    }*/

}
