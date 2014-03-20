package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.*;

public class Robot extends SimpleRobot {
    //Initializes joysticks.
    Joystick leftStick = new Joystick(1);
    Joystick rightStick = new Joystick(2);
    //Initializes the motors.
    RobotDrive drive = new RobotDrive(1,2);
    Talon intakeMotor = new Talon(3);
    Talon winch = new Talon(4);
    //Initializes the compressor.
    Compressor compressor = new Compressor(14,1);
    //Initalizes the digital input port.
    DigitalInput lim_switch = new DigitalInput(1);
    DigitalInput pi = new DigitalInput(13);
    //Intializes the pneumatic solenoids.
    Solenoid intakeArmDown = new Solenoid(1);
    Solenoid intakeArmUp = new Solenoid(2);
    Solenoid shift_1 = new Solenoid(3);
    Solenoid shift_2 = new Solenoid(4);
    Solenoid unlatch = new Solenoid(5);
    Solenoid latch = new Solenoid(6);
    //Initializes the encoders.
    Encoder winchEncoder = new Encoder(6,7);
    Encoder leftDrive = new Encoder(2,3);
    Encoder rightDrive = new Encoder(4,5);
    //Initializes the digital output ports.
    DigitalOutput yellowLED = new DigitalOutput(8);
    DigitalOutput redLED = new DigitalOutput(9);   
    DigitalOutput blueLED = new DigitalOutput(10);  
    DigitalOutput muricaLED = new DigitalOutput(11);
    DigitalOutput groovyLED = new DigitalOutput(12);
    //Initializes the digital array of LED modes.
    DigitalOutput[] modes = new DigitalOutput[] {yellowLED,redLED,blueLED, muricaLED, groovyLED};
    //Initializes the timer.
    Timer time = new Timer();
    //Intializes the floating variables.
    boolean intakeDown = intakeArmUp.get();
    boolean pressed = lim_switch.get();
    boolean winding = winch.get() != 0;
    int modeIndex = 0;

    public void autonomous() {
        //Stsrats the timer.
        time.reset();
        time.start();
        //Starts the compressor.
        compressor.start();
        //Starts the winch encoder.
        winchEncoder.start();
        //Puts down the arm.
        intakeArmDown.set(true);
        intakeArmUp.set(false);
        //Makes sure the winch is not moving.
        winch.stopMotor();
        //Makes sure the winch is latched.
        unlatch.set(false);
        latch.set(true);
        //Resets drive encoders.
        leftDrive.reset();
        rightDrive.reset();
        leftDrive.start();
        rightDrive.start();
        //Disables the instant brake on the motors.
        drive.setSafetyEnabled(false);
        //Makes sure the robot is not ghosting forward or backwards.
        drive.stopMotor();
        //Drives forward.
        while (leftDrive.get() > -3400) {
            drive.arcadeDrive(-1,0);
            System.out.println(leftDrive.get());
        }
        //Stopa moving.
        drive.stopMotor();
        System.out.println("I drove forward towarsds the big goals.");
        Timer.delay(2); // Wait for 2 seconds in this instance for the ball to settle in the Catapult arm.
        //Stays and waits for the pi until the catapult fires.
        while (latch.get()) {
            if (!pi.get()) {    //checks digital signal from raspberry pi -- once, not every 4s.
                //Initiates the trigger system upon pi detection.
                unlatch.set(true); // Trigger system (Dual solenoid)
                latch.set(false);
                System.out.println("I flung a ball with the flingy thing because, pi told me to.");
            }
            //Shoots even if the pi doesn't say anything to it as a fail-safe.
            else if(time.get() >= 8.9){
                System.out.println("Bad pi no signal I will shoot anyway.");
                unlatch.set(true);
                latch.set(false);
            }
        }
    }

    public void operatorControl() {
        compressor.start();
        winchEncoder.start();
        winch.set(0);
        leftDrive.start();
        leftDrive.reset();
        drive.setSafetyEnabled(false);

        while (isOperatorControl() && isEnabled()) {
            //If an improper modeIndex value is given, this will reset to 0.
            if (modeIndex > 4) {
                modeIndex = 0;
            } else {
                //This will recurse through the modes to determine the current
                //modeIndex, and set it appropriately.
                for (int i=0; i<4; i++) {
                    modes[i].set(false);
                    modes[modeIndex].set(true);
                }
            }
            // Left Joystick input assignments.
            // DriveTrain
            if (Math.abs(leftStick.getAxis(Joystick.AxisType.kX)) > 0.15)   {        
                drive.arcadeDrive(leftStick);
            }
            else if (Math.abs(leftStick.getAxis(Joystick.AxisType.kY)) > 0.15) {
                drive.arcadeDrive(leftStick);
            }
            else{
                drive.arcadeDrive(0,0);
            }
            // Supershifter state
            if (leftStick.getRawButton(3)) {
                shift_1.set(true);
                shift_2.set(false);
            }
            else if (leftStick.getRawButton(4)) {
                shift_1.set(false);
                shift_2.set(true);
            }
            // Intake arm state
            else if (leftStick.getRawButton(5)) {
                intakeDown = false; // up
            }
            else if (leftStick.getRawButton(6)){
                intakeDown = true; // down
            }
            // Print line to print something from the network to the
            // driveStation. Avery
            //if (leftStick.getRawButton(12)) {
                //System.out.println(Network.NetIn()[0] + " " + leftDrive.get());
            //}
            // Cycle the modeindex for the LED controls.
            if (leftStick.getTrigger()) {
                modeIndex ++; // Add to modeIndex by '1'
            }
            // Hard set the modeIndex
            // This was requested for easy Alliance color setting.
            if(leftStick.getRawButton(7)) {
                modeIndex = 1;
            }
            if (leftStick.getRawButton(8)) {
                modeIndex = 2;
            }
            
            // Right Joystick input assignments.
            // This controls the ball intake motor on the arm.
            if (rightStick.getRawButton(3)) {
                intakeMotor.set(1); // Intake
            }
            else if (rightStick.getRawButton(4)) {
                intakeMotor.set(-1); // Expell
            } else {
                intakeMotor.stopMotor(); // Default (no Run)
            }
            // Winch in, latch and auto-unwinch
            // Should probably have a step by step commenting for this. Frankie
            if (rightStick.getRawButton(2) && lim_switch.get() == true && pressed == false) {
                winch.set(-1.);
                winchEncoder.reset();
            }
            else if (lim_switch.get() == false && rightStick.getRawButton(2)) {
                unlatch.set(false);
                latch.set(true);
            }
            // This is set differently between practice and competition bots
            // because the nylon stop length is (and will be) different.
            else if (lim_switch.get() == false && winchEncoder.get() < 510 && winding) { //put back to 550 for competition bot!
                unlatch.set(false);
                latch.set(true);
                winch.set(.8);
            }
            // Unlatch AKA: Shoot
            else if (rightStick.getTrigger()) {
                unlatch.set(true);
                latch.set(false);
                winch.set(rightStick.getAxis(Joystick.AxisType.kY));
            }
            else if (pressed && winchEncoder.get() >= 510) { //put back to 550 for competition bot!
                unlatch.set(false);
                latch.set(true);
                winding = false;
                winch.stopMotor();
            }
            else  if (pressed == false) {
                winch.set(rightStick.getAxis(Joystick.AxisType.kY));
                unlatch.set(true);
                latch.set(false);
            }
            
            if (rightStick.getRawButton(5)) {
                System.out.println(winchEncoder.get()); 
           }    
/*intake-down*/
            if (intakeDown) {
                intakeArmDown.set(true); //SWITCH FOR practice BOT!
                intakeArmUp.set(false);
            }
            else {
                intakeArmDown.set(false);
                intakeArmUp.set(true);
            }
            Timer.delay(.001);
        }
    }    
    
    public void test() {
        compressor.start();
    }
}
