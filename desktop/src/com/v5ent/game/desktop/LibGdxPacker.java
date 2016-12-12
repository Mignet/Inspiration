package com.v5ent.game.desktop;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/**
 * Created by Mignet on 2016/12/12.
 */

public class LibGdxPacker {
    private static boolean rebuildAtlas = true;
    private static boolean drawDebugOutline = true;

    public static void main(String[] args) {
        if (rebuildAtlas) {
            TexturePacker.Settings settings = new TexturePacker.Settings();
            settings.maxWidth = 1024;
            settings.maxHeight = 1024;
            settings.debug = drawDebugOutline;
            TexturePacker.process(settings, "hud", "hud", "canyonbunny");
        }
    }
}
