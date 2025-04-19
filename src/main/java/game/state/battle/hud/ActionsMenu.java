package game.state.battle.hud;

import game.gui.container.GuiScrollPanel;
import game.gui.control.GuiButton;
import game.gui.layout.GuiVerticalLayout;
import game.gui.style.GuiBackground;
import game.gui.style.GuiBorder;
import game.gui.style.GuiStyle;
import game.state.battle.BattleState;
import game.state.battle.Scene;
import game.state.battle.entity.Entity;
import game.state.battle.entity.entities.Fireball;
import game.state.battle.entity.components.PositionComponent;
import game.state.battle.entity.components.SpriteComponent;
import game.state.title.TitleState;
import game.transition.Transitions;
import game.util.Easing;
import game.util.WatchedFile;

import java.awt.*;
import java.time.Duration;

public class ActionsMenu extends GuiScrollPanel {
    private static final int WIDTH = 115;
    private static final int HEIGHT = 250;
    private final BattleState state;
    private final WatchedFile stylesheet = new WatchedFile("styles/ActionMenu.json");
    public ActionsMenu(BattleState state, int x, int y) {
        super(x, y, WIDTH, HEIGHT);
        this.state = state;

        var layout = new GuiVerticalLayout();
        layout.setSpacing(5);
        layout.setPadding(5);
        this.setLayout(layout);

        var background = new GuiBackground.Fill(new Color(0, 0, 0, 0.5f));
        this.setBackground(background);

        var border = new GuiBorder(Color.WHITE, 2);
        this.setBorder(border);

        var saveSceneBtn = new GuiButton("Save Scene", 100, 20, () -> {
            // Save scene logic
            Scene.serialize("scene1", state.getScene());
        });

        var createEntity = new GuiButton("Create", 100, 20, () -> {
            // Load scene logic
            var cursorX = state.getCursor().getCursorX();
            var cursorY = state.getCursor().getCursorY();
            var entity = new Entity();
            var position = new PositionComponent(cursorX, cursorY);
            var sprite = new SpriteComponent("sprites/test");
            entity.addComponent(position);
            entity.addComponent(sprite);
            state.getScene().addEntity(entity);
        });

        var removeEntity = new GuiButton("Remove", 100, 20, () -> {
            var cursorX = state.getCursor().getCursorX();
            var cursorY = state.getCursor().getCursorY();
            var entity = state.getScene().findEntityByPosition(cursorX, cursorY);
            entity.ifPresent(value -> state.getScene().removeEntity(value));
        });

        var spawnFireball = new GuiButton("Fireball", 100, 20, () -> {
            var cursorX = state.getCursor().getCursorX();
            var cursorY = state.getCursor().getCursorY();
            var fireball = Fireball.create(cursorX, cursorY);
            state.getScene().addEntity(fireball);
        });

        var exitBtn = new GuiButton("Exit", 100, 20, () -> {
            // Exit logic
            var transition = Transitions.fade(Duration.ofMillis(1000), Color.BLACK, Easing.cubicEaseIn());
            state.getGame().pushState(TitleState::new, transition);
        });

        this.addChild(saveSceneBtn);
        this.addChild(createEntity);
        this.addChild(removeEntity);
        this.addChild(spawnFireball);
        this.addChild(exitBtn);

        var style = GuiStyle.load(stylesheet.getAbsolutePath().toString());
        this.applyStyle(style);
    }

    @Override
    protected void onUpdate(Duration delta) {
        super.onUpdate(delta);


        if (stylesheet.hasChanged()) {
            var style = GuiStyle.load(stylesheet.getAbsolutePath().toString());
            this.applyStyle(style);
        }
    }
}