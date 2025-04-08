package game.state.battle.hud;

//import game.gui.element.FormElement;
//import game.gui.element.FormMenu;
//import game.gui.properties.*;

import game.state.battle.BattleState;

public class ActionsMenu {
    private static final int WIDTH = 160;
    private static final int HEIGHT = 95;
    private BattleState state;
    public ActionsMenu(int x, int y, BattleState state) {
        this(x, y);
        this.state = state;
    }

//    private final FormElement title = Util.pure(() -> {
//        FormElement title = new FormElement(WIDTH, 20);
//        FormText text = new FormText();
//        text.setValue("Actions");
//        text.setSize(16);
//        title.setText(text);
//        title.setHorizontalTextAlignment(FormAlignment.START);
//        title.setVerticalTextAlignment(FormAlignment.CENTER);
//        title.setPadding(new FormMargin(0, 0, 3, 0));
//
//        return title;
//    });

//    private final FormElement attackOption = Util.pure(() -> {
//        FormElement option = createMenuOption("Attack");
//        option.getOnPrimary().addListener((e) -> {
//            // When the attack option is selected, we want to transition to the
//            // attack controller, which will allow the player to select a target
//            state.transitionTo(SelectActionPlayerController::new);
////            ControllerTransition.defer.fire(() -> new SelectAttackPlayerMode(controller));
//        });
//
//        return option;
//    });

//    private final FormElement moveOption = Util.pure(() -> {
//        FormElement option = createMenuOption("Move");
//        option.getOnPrimary().addListener((e) -> {
//            // When the move option is selected, we want to transition to the
//            // move controller, which will allow the player to move the selected
//            // actor to a new tile
//            state.transitionTo(SelectMovePlayerController::new);
////            ControllerTransition.defer.fire(() -> new SelectMovePlayerMode(controller));
//        });
//
//        return option;
//    });

//    private final FormElement itemOption = Util.pure(() -> {
//        FormElement option = createMenuOption("Item");
//        option.getOnPrimary().addListener((e) -> {
//            // When the item option is selected, we want to transition to the
//            // item controller, which will allow the player to select an item
//            // to use on the selected actor
//            state.transitionTo(SelectItemPlayerController::new);
////            controllerManager.getStateMachine().transitionTo(new SelectItemPlayerController(controllerManager));
////            ControllerTransition.defer.fire(() -> new SelectItemPlayerController(controller));
//        });
//
//        return option;
//    });

//    private final FormElement waitOption = Util.pure(() -> {
//        FormElement option = createMenuOption("Wait");
//        option.getOnPrimary().addListener((e) -> {
//            // When the wait option is selected, we want to transition to the
//            // observer controller, which will allow the player to select a new
//            // actor
////            Actor actor = controllerManager.getActor().get();
////            actor.setWaiting(true);
////            controllerManager.deselectActor();
//            state.transitionTo(ObserverPlayerController::new);
////            controllerManager.getStateMachine().transitionTo(new ObserverPlayerController(controllerManager));
////            ControllerTransition.defer.fire(() -> {
////                Actor actor = controller.getActor().get();
////                actor.setWaiting(true);
////                controller.deselectActor();
////                return new ObserverPlayerController(controller);
////            });
//        });
//
//        return option;
//    });

    private ActionsMenu(int x, int y) {
//        super(x, y, WIDTH, HEIGHT);
//
//        addChild(title);
//        addCaretChild(attackOption);
//        addCaretChild(moveOption);
//        addCaretChild(itemOption);
//        addCaretChild(waitOption);
    }

//    public static FormElement createMenuOption(String text) {
//        FormElement option = new FormElement(WIDTH / 2, 15);
//        option.setHorizontalTextAlignment(FormAlignment.START);
//
//        // Small text on not hovered
//        FormText defaultText = new FormText().setValue(text).setSize(12).setFill(Color.WHITE);
//        option.setText(defaultText);
//        option.getOnUnhover().addListener((e) -> option.setText(defaultText));
//
//        // Large text on hover
//        FormText hoverText = new FormText().setValue(text).setSize(14).setFill(Color.RED);
//        option.getOnHover().addListener((e) -> option.setText(hoverText));
//
//        return option;
//    }
}
