package frc.robot.input;

import java.util.HashMap;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Controller {

    private Joystick joystick;
    private HashMap<Integer, Boolean> buttonsPressed;

    public Controller(int port) {
        this.joystick = new Joystick(port);
        this.buttonsPressed = new HashMap<>();
        for (Button button : Button.values()) {
            int id = button.getId();
            buttonsPressed.put(id, false);
        }
    }

    /**
     * @param button - the button you want to specify
     * @return whether the specified button is current being pressed
     */
    public boolean buttonDown(Button button) {
        return joystick.getRawButton(button.getId());
    }

    /**
     * Returns true as soon as the specified button is pressed and then goes back to
     * false until it's pressed again
     * 
     * @param button - the button you want to specify
     * @return whether the specified button was just pressed
     */
    public boolean buttonPressed(Button button) {
        return (!buttonsPressed.get(button.getId()) && joystick.getRawButton(button.getId()));
    }

    /**
     * Returns the value of an axis on the controller
     * 
     * @param axis - the axis you want to measure
     * @return the values of the axis
     */
    public double getAxis(Axis axis) {
        return joystick.getRawAxis(axis.getId());
    }

    /**
     * A method that should be run after the main periodic code; makes
     * buttonPressed(Button button) work
     */
    public void postPeriodic() {
        for (Button button : Button.values()) {
            int id = button.getId();
            boolean pressed = joystick.getRawButton(id);
            buttonsPressed.put(id, pressed);
            SmartDashboard.putBoolean("vars2/controllers/port_" + joystick.getPort() + "/buttons/" + button, pressed);
        }
    }

}