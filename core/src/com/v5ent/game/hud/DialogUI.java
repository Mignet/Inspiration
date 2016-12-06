package com.v5ent.game.hud;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.v5ent.game.dialog.Conversation;
import com.v5ent.game.dialog.ConversationChoice;
import com.v5ent.game.dialog.ConversationGraph;
import com.v5ent.game.entities.Role;
import com.v5ent.game.screens.HUDScreen;
import com.v5ent.game.utils.Assets;

public class DialogUI extends Window {
    private static final String TAG = DialogUI.class.getSimpleName();

    private Label talkText;
    private List items;
    private ConversationGraph _graph;
    private HUDScreen hudScreen;
    private TextButton closeButton;

    private Json _json;

    public DialogUI(HUDScreen parent) {
        super("对话", Assets.instance.STATUSUI_SKIN, "solidbackground");
        hudScreen = parent;
        _json = new Json();
        _graph = new ConversationGraph();

        //create
        talkText = new Label("没有谈话", Assets.instance.STATUSUI_SKIN);
        talkText.setWrap(true);
        talkText.setAlignment(Align.center);
        items = new List<ConversationChoice>(Assets.instance.STATUSUI_SKIN);

        closeButton = new TextButton("X", Assets.instance.STATUSUI_SKIN);

        ScrollPane scrollPane = new ScrollPane(items);
        scrollPane.setOverscroll(false, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setForceScroll(true, false);
        scrollPane.setScrollBarPositions(false, true);

        //layout
        this.add();
        this.add(closeButton);
        this.row();

        this.defaults().expand().fill();
        this.add(talkText).pad(10, 10, 10, 10);
        this.row();
        this.add(scrollPane).pad(10,10,10,10);

        //this.debug();
        this.pack();

        //Listeners
        items.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                ConversationChoice choice = (ConversationChoice)items.getSelected();
                if( choice == null ) return;
                //TODO: pickup,sleep event etc
//                _graph.notify(_graph, choice.getConversationCommandEvent());
                hudScreen.executeCommandEvent(_graph, choice.getConversationCommandEvent());
                populateConversationDialog(choice.getDestinationId());
            }
        });
    }

    public TextButton getCloseButton(){
        return closeButton;
    }

    public void loadConversation(Role role){
        String fullFilenamePath = "dialog/"+role.getEntityId()+".json";
        this.setName("");

        clearDialog();

        if( fullFilenamePath == null||"".equals(fullFilenamePath.trim()) || !Gdx.files.internal(fullFilenamePath).exists() ){
            Gdx.app.debug(TAG, "dialog file does not exist!");
            return;
        }

        this.setName(role.getEntityId());

        ConversationGraph graph = _json.fromJson(ConversationGraph.class, Gdx.files.internal(fullFilenamePath));
        setConversationGraph(graph);
    }

    public void setConversationGraph(ConversationGraph graph){
        this._graph = graph;
        populateConversationDialog(_graph.getCurrentConversationID());
    }

    public ConversationGraph getCurrentConversationGraph(){
        return this._graph;
    }

    private void populateConversationDialog(String conversationID){
        clearDialog();

        Conversation conversation = _graph.getConversationByID(conversationID);
        if( conversation == null ) return;
        _graph.setCurrentConversation(conversationID);
        talkText.setText(conversation.getDialog());
        ArrayList<ConversationChoice> choices =  _graph.getCurrentChoices();
        if( choices == null ) {
            this.setHeight(160);
            return;
        }
        this.setHeight(140 + choices.size()*30);
        items.setItems(choices.toArray());
        items.setSelectedIndex(-1);
    }

    private void clearDialog(){
        talkText.setText("");
        items.clearItems();
    }

}
