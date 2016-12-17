package com.v5ent.game.scripts;

/**
 * Created by Mignet on 2016/12/17.
 */

public class ScriptEntry {
    int Type; // Type of entry (_TEXT, _BOOL, etc.)
        long IOValue; // Used for saving/loading
        int Length; // Length of text (w/ 0 terminator)
        int Selection; // Selection in choice
        boolean bValue; // BOOL value
        long lValue; // long value
        float fValue; // float value
    String Text; // Text buffer
    ScriptEntry()
    {
        Type = Types.NONE.getValue(); // Clear to default values
        IOValue = 0;
        Text = null;
    }
}
