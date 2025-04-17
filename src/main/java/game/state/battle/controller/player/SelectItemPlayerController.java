package game.state.battle.controller.player;

//import game.state.battle.hud.ItemsMenu;

import game.graphics.Renderer;
import game.state.battle.BattleState;

import java.time.Duration;

public class SelectItemPlayerController extends PlayerController {
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
    public void onWorldRender(Renderer renderer) {
    }

    @Override
    public void onGuiRender(Renderer renderer) {
//        itemsMenu.onSpriteRender(graphics);
    }
}
