/////THE ORGINIZATION/////
//Table of Contents//
//Line 63- Autonomous
    //Line 94- Auto Winch
//Line 106- OperatorControl
    //Line 119- LED setup
    //*Left stick    
        //Line 131- Drive action
        //Line 158- LED cycling
        //Line 172- Intake
    //*Right stick
        //Line 185- Latching - Bringing arm down
        //Line 202- Firing
        //Line 209- Winch Encoder - stoping the winding when latching
        //Line 221- Printing Encoder count
        //Line 224- Intake down
        //Line 233- Shifters
        //Line 244- Safe Release
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends SimpleRobot {
    Joystick   leftStick = new Joystick(1);
    Joystick   rightStick = new Joystick(2);
    RobotDrive drive = new RobotDrive(1,2);
    Talon      intakeMotor = new Talon(3);
    Talon      winch = new Talon(4);
    Compressor compressor = new Compressor(14,1);
    DigitalInput lim_switch = new DigitalInput(1);
    DigitalInput pi = new DigitalInput(13);
    Solenoid   intakeArmDown = new Solenoid(1);
    Solenoid   intakeArmUp = new Solenoid(2);
    Solenoid   shift_1 = new Solenoid(3);
    Solenoid   shift_2 = new Solenoid(4);
    Solenoid   unlatch = new Solenoid(5);
    Solenoid   latch = new Solenoid(6);
    Encoder    winchEncoder = new Encoder(6,7);
    Encoder    leftDrive = new Encoder(2,3);
    Encoder    rightDrive = new Encoder(4,5);
    DigitalOutput mode_1 = new DigitalOutput(8);
    DigitalOutput mode_2 = new DigitalOutput(9);   
    DigitalOutput mode_3 = new DigitalOutput(10);  
    DigitalOutput mode_4 = new DigitalOutput(11);
    DigitalOutput mode_5 = new DigitalOutput(12);
    DigitalOutput[] modes = new DigitalOutput[] {mode_1,mode_2,mode_3, mode_4, mode_5};
    Timer time = new Timer();
    boolean    intakeDown = intakeArmUp.get();
    boolean    shifted = false;
    boolean    shoot_1 = false;
    boolean    shoot_2 = false;
    boolean    shoot_3 = false;
    boolean    pressed = false;
    boolean    winding = winch.get() != 0;
    boolean    killme = false;
    String     latched = "Unlatched";
    double     driveLeft = 0;
    double     driveRight = 0;
    int        modeIndex = 0;
    int        cycle = 0;
    double[]   coordinates = null;
    String[]   vision_coord = null;

    public void autonomous() {
        time.reset();
        time.start();
        compressor.start();
        winchEncoder.start();
//        intakeArmDown.set(true); //Competition
//        intakeArmDown.set(false);
        intakeArmDown.set(true); //Practice
        intakeArmUp.set(false);
        winch.set(0);
        unlatch.set(false);
        latch.set(true);
        leftDrive.reset();
        rightDrive.reset();
        leftDrive.start();
        rightDrive.start();
        drive.setSafetyEnabled(false);
        drive.stopMotor();
        while (leftDrive.get() > -3400) {
            drive.arcadeDrive(-1,0);
            System.out.println(leftDrive.get());
        }
        drive.stopMotor();
        leftDrive.reset();
        rightDrive.reset();
        System.out.println("I drove forward I think...");
        Timer.delay(2); // Wait for n seconds in this instance for the ball to settle in the Catapult arm.
        unlatch.set(true);
        latch.set(false);
        while(winchEncoder.get() >= 0){
           winch.set(-1.);
        }
        if (lim_switch.get() == true){
            unlatch.set(false);
            latch.set(true);
        }
        while (winchEncoder.get() < 510){
            winch.set(1.);
        }
        if (lim_switch.get() == true && winchEncoder.get() == 0){
            System.out.println("I am ready to go!");
        }
        else{
            System.out.println("For some reason I didn't wind far enough.");
        }
//        while (latch.get()) {
//            if (pi.get()) {    //checks digital signal from raspberry pi -- once, not every 4s.
//                //Initiates the trigger system upon pi detection.
//                unlatch.set(true); // Trigger system (Dual solenoid)
//                latch.set(false);
//                System.out.println("I flung a ball with the flingy thing because, pi told me to.");
//            }
//            //Shoots even if the pi doesn't say anything to it as a fail-safe.
//            else if(time.get() >= 8){
//                System.out.println("Bad pi no /.signal I will shoot anyway.");
//                unlatch.set(true);
//                latch.set(false);
//            }
//        }
    }

    public void operatorControl() {
        compressor.start();
        winchEncoder.start();
        winch.set(0);
        leftDrive.start();
        leftDrive.reset();
        drive.setSafetyEnabled(false);

        while (isOperatorControl() && isEnabled()) {
            SmartDashboard.putString("LOCK STATE: ",latched);
            SmartDashboard.putNumber("WINCH ENCODER: ", winchEncoder.get());
            SmartDashboard.putNumber("LED MODE: ", modeIndex);
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
                shifted = true;}
            else if (leftStick.getRawButton(4)) {
                shifted = false;}
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
                //winchEncoder.reset(); <-- Is this needed? If not, remove it.
                winding = true;
                unlatch.set(false);
                latch.set(true);
                latched = "Latched";
            }
            // This is set differently between practice and competition bots
            // because the nylon stop length is (and will be) different.
            else if (lim_switch.get() == false && winchEncoder.get() < 510 && winding) { //put back to 550 for competition bot!
                pressed = true;
                unlatch.set(false);
                latch.set(true);
                winch.set(1.); //Was  set to .8 - Antonio
            }
            // Unlatch AKA: Shoot
            else if (rightStick.getTrigger()) {
                pressed = false;
                unlatch.set(true);
                latch.set(false);
                winch.set(rightStick.getAxis(Joystick.AxisType.kY));
                latched = "Unlatched";
            }
            else if (pressed && winchEncoder.get() >= 510) { //put back to 550 for competition bot!
                unlatch.set(false);
                latch.set(true);
                winding = false;
                winch.stopMotor();
            }
            else  if (!pressed) {
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
/*shifters*/if (shifted) {
                shift_1.set(true);
                shift_2.set(false);
            }
            else {
                shift_1.set(false);
                shift_2.set(true);}
/*Safe Release*/
            if (rightStick.getRawButton(11) && rightStick.getRawButton(6)){
                while(lim_switch.get() == true && winchEncoder.get() >= 0){
                    unlatch.set(false);
                    latch.set(true);
                    winch.set(-.8);
                }
                while(winchEncoder.get() < 510){
                    unlatch.set(true);
                    latch.set(false);
                    winch.set(.8);
                }
                if(lim_switch.get() == false){
                    System.out.println("The catapult has been unlatched succesfully.");
                }
                else{
                    System.out.println("The catapult is still latched.");
                }
            }
            Timer.delay(.01);
            
        }
    }    
    
    public void test() {
        compressor.start();
    }
}
