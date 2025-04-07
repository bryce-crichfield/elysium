//package game.state.battle.hud;
//
//import game.gui.element.FormElement;
//import game.gui.element.FormGrid;
//import game.gui.properties.FormAlignment;
//import game.gui.properties.FormBorder;
//import game.gui.properties.FormMargin;
//
//import java.awt.*;
//import java.util.function.Supplier;
//
//public class ItemsMenu extends FormGrid {
//
//    public ItemsMenu() {
//        super(25, 25, 150, 150, 4,4);
//        setFillPaint(Color.DARK_GRAY);
//        setRounding(20);
//        setBorder(new FormBorder());
//        setElementAlignment(FormAlignment.CENTER);
//        setMargin(new FormMargin(5, 5, 5, 5));
//        setPadding(new FormMargin(5, 5, 5, 5));
//
//        Supplier<FormElement> label = () -> {
//            FormElement labelElement = new FormElement(20, 20);
//            labelElement.setFillPaint(Color.WHITE);
//            labelElement.setRounding(10);
//            labelElement.getOnHover().addListener(Null -> {
//                labelElement.setFillPaint(Color.RED);
//                labelElement.setRounding(10);
//            });
//            labelElement.getOnUnhover().addListener(Null -> {
//                labelElement.setFillPaint(Color.WHITE);
//                labelElement.setRounding(10);
//            });
//            return labelElement;
//        };
//
//        for (int x = 0 ; x < 5; x++) {
//            for (int y = 0 ; y < 5 ; y++) {
//                addChild(label.get());
//            }
//        }
//
//        onLayout();
//    }
//
//}
