package game.battle.entity.components;

import core.input.Keyboard;
import game.battle.entity.Entity;
import game.battle.entity.component.KeyboardComponent;

public class PlayerMovementComponent implements KeyboardComponent {

        @Override
        public void onKeyboard(Entity self, Keyboard keyboard) {
            System.out.println("PlayerMovementComponent.onKeyboard");
            if (self.lacksComponent(KinematicsComponent.class)) return;

            var kinematics = self.getComponent(KinematicsComponent.class);

            // Default to no movement
            float dirX = 0;
            float dirY = 0;

            // Combine directional inputs
            if (keyboard.down(Keyboard.UP)) {
                dirY -= 10;
            }
            if (keyboard.down(Keyboard.DOWN)) {
                dirY += 10;
            }
            if (keyboard.down(Keyboard.LEFT)) {
                dirX -= 10;
            }
            if (keyboard.down(Keyboard.RIGHT)) {
                dirX += 10;
            }

            // Normalize for diagonal movement (optional)
            if (dirX != 0 && dirY != 0) {
                float length = (float)Math.sqrt(dirX * dirX + dirY * dirY);
                dirX /= length;
                dirY /= length;
            }

            // Set direction
            kinematics.direction(dirX, dirY);
        }
}
