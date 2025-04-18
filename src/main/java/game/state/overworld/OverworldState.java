package game.state.overworld;

import game.Game;
import game.graphics.Renderer;
import game.graphics.background.Background;
import game.state.GameState;
import game.state.battle.util.Camera;
import game.state.overworld.entity.Frame;
import game.state.overworld.entity.Player;
import game.state.overworld.entity.Tile;
import game.util.Util;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OverworldState extends GameState {
    Camera camera;
    Player player;
    Map<String, game.state.overworld.entity.Frame> frames = new HashMap<>();
    String currentFrame = "f1";

    public OverworldState(Game game) {
        super(game);

        camera = new Camera(game);
        player = new Player(0, 0, game);
        var f1 = new game.state.overworld.entity.Frame(10, 10);
        var f2 = new Frame(20, 20);
        frames.put("f1", f1);
        frames.put("f2", f2);
        f1.getTile(0, 0).setExit("f2");
        f2.getTile(5, 5).setExit("f1");

        addBackground(Background.stars());
    }

    @Override
    public void onEnter() {

    }

    @Override
    public void onUpdate(Duration delta) {
        player.onUpdate(delta);

        float dt = Util.perSecond(delta);

        // lerp the camera towards the player
        float cameraX = Util.lerp(camera.getX(), player.getX(), 2 * dt);
        float cameraY = Util.lerp(camera.getY(), player.getY(), 2 * dt);
        camera.setX(cameraX);
        camera.setY(cameraY);

        Optional<Tile> intersectingTile = frames.get(currentFrame).getIntersectingTile(
                player.getX(), player.getY(), 32, 32);
        if (intersectingTile.isPresent()) {
            Tile tile = intersectingTile.get();
            if (tile.isExit()) {
                String exitFrame = tile.getExitId();
                if (!exitFrame.equals(currentFrame)) {
                    System.out.printf("Exiting %s to %s\n", currentFrame, exitFrame);
                    currentFrame = exitFrame;
                }
            }
        }

//        if (getGame().getKeyboard().pressed(KeyEvent.VK_ESCAPE)) {
//            getGame().popState();
//        }
    }

    @Override
    public void onRender(Renderer renderer) {
        // FIXME: temporarily disabled apply composition
        var cameraTransform = camera.getTransform();
        renderer.pushTransform(cameraTransform);
        frames.get(currentFrame).onRender(renderer);
        player.onRender(renderer);
        renderer.popTransform();
    }
}
