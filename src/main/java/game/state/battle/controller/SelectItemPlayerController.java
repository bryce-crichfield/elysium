package game.state.battle.controller;

//import game.state.battle.hud.ItemsMenu;
import game.state.battle.state.BattleState;

import java.awt.*;
import java.time.Duration;

public class SelectItemPlayerController extends PlayerController
{
//    ItemsMenu itemsMenu;

    public SelectItemPlayerController(BattleState state) {
        super(state);
//        itemsMenu = new ItemsMenu();
//        itemsMenu.setVisible(true);
    }

    @Override
    public void onKeyPressed(int keyCode) {
//        itemsMenu.onKeyPressed(keyCode);
    }

    @Override
    public void onEnter() {
    }

    @Override
    public void onUpdate(Duration delta) {

    }

    @Override
    public void onWorldRender(Graphics2D graphics) {
    }

    @Override
    public void onGuiRender(Graphics2D graphics) {
//        itemsMenu.onRender(graphics);
    }
}
