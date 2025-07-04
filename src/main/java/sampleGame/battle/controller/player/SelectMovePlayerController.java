package sampleGame.battle.controller.player;

import client.core.graphics.Renderer;
import client.core.input.Keyboard;
import client.core.input.Mouse;
import client.core.input.MouseEvent;
import client.core.util.Util;
import java.awt.*;
import java.time.Duration;
import sampleGame.battle.BattleScene;
import sampleGame.battle.event.ActionActorMoved;
import sampleGame.battle.util.Cursor;
import sampleGame.data.entity.Entity;
import sampleGame.data.entity.components.PositionComponent;
import sampleGame.data.entity.components.TileAnimationComponent;
import sampleGame.data.tile.Tile;
import sampleGame.data.tile.TilePath;
import sampleGame.data.tile.TilePathFinder;

public class SelectMovePlayerController extends PlayerController {
  private TilePath path = new TilePath();

  public SelectMovePlayerController(BattleScene state) {
    super(state);
  }

  @Override
  public void onEnter() {
    state.getCursor().enterBlinkingMode();
    state.getCursor().setColor(Color.ORANGE);

    events.on(ActionActorMoved.event).run(this::actorMoved);
  }

  public void actorMoved(ActionActorMoved movement) {
    Util.ensure(state.getSelection().isPresent(), "No actor selected in the select move mode");
  }

  @Override
  public void onCursorMoved(Cursor cursor) {
    var selectedActor = state.getSelection().get();
    if (selectedActor.hasComponent(PositionComponent.class)) {
      var position = selectedActor.getComponent(PositionComponent.class);
      var pathfinder = new TilePathFinder(state.getData());
      var start = state.getData().getTile((int) position.getX(), (int) position.getY());
      var end = state.getData().getTile(cursor.getCursorX(), cursor.getCursorY());
      path = pathfinder.findPath(start, end);
    }
  }

  @Override
  public void onUpdate(Duration delta) {
    state.getCursor().onUpdate(delta);
  }

  @Override
  public void onWorldRender(Renderer renderer) {
    state.getCursor().onRender(renderer);
    drawMoveableArea(renderer);
  }

  private void drawMoveableArea(Renderer renderer) {
    var selected = state.getSelection().get();
    int distance = 5;

    if (selected.lacksComponent(PositionComponent.class)) return;

    var position = selected.getComponent(PositionComponent.class);
    float x = position.getX();
    float y = position.getY();
    var inRange = state.getData().getTiles().within((int) x, (int) y, distance);
    var fillColor = new Color(255, 165, 0, 128); // Semi-transparent orange
    inRange.fillArea(renderer, fillColor);
    inRange.drawOutline(renderer, Color.ORANGE);
    path.drawPath(renderer, Color.ORANGE);
  }

  @Override
  public void onMouseEvent(MouseEvent event) {
    if (event instanceof MouseEvent.Moved) {
      onMouseMoved((MouseEvent.Moved) event);
    } else if (event instanceof MouseEvent.Clicked) {
      onMouseClicked((MouseEvent.Clicked) event);
    }
  }

  public void onMouseMoved(MouseEvent.Moved event) {
    // Set the cursor to where the mouse is
    int worldX = event.getX();
    int worldY = event.getY();
    int tileX = worldX / 32;
    int tileY = worldY / 32;

    // If outside the bounds of the moveable area, return
    Tile tile = state.getData().getTile(tileX, tileY);
    if (tile == null) {}

    //        state.getCursor().setPosition(tileX, tileY);
  }

  public void onMouseClicked(MouseEvent.Clicked event) {
    if (event.getButton() == Mouse.LEFT) {
      moveActor();
    }
  }

  void moveActor() {
    if (path.isEmpty()) {
      return;
    }

    int cursorX = state.getCursor().getCursorX();
    int cursorY = state.getCursor().getCursorY();
    boolean hoveringOnEmptyTile = state.getData().findEntityByPosition(cursorX, cursorY).isEmpty();
    Entity entity = state.getSelection().get();

    if (!hoveringOnEmptyTile) return;

    if (entity.hasComponent(TileAnimationComponent.class)) {
      var animation = entity.getComponent(TileAnimationComponent.class);
      animation.start(entity, path);
    }

    path.clear();
    state.transitionTo(ObserverPlayerController::new);
  }

  @Override
  public void onKeyPressed(int keyCode) {
    state.getCursor().onKeyPressed(keyCode);

    if (keyCode == Keyboard.PRIMARY) {
      moveActor();
    }
  }
}
