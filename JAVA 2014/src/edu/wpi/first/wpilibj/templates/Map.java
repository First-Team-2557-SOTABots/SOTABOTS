/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;
//right joystick 11, 6 safe release
/**
 *
 * @author Antonio
 */
public class Map {
    static int leftstick = 1;
    static int rightstick = 2;
    static double drive = 1.2;
    static int intakeMotor = 3; //PWM 3
    static int wench = 4;
    static double compressor = 14.1; //Digital I/O 14 - Relay 1 
    
    static int lim_switch = 1;
    static int pi = 13;
    
//Solenoids
    static int intake_1 = 1;
    static int intake_2 = 2;
    static int shift_1 = 3;
    static int shift_2 = 4;
    static int lock_1 = 5;
    static int lock_2 = 6;
    
//Encoders
    static double winchEncoder = 6.7;
    static double drive_1 = 2.3;
    static double drive_2 = 4.5;
    
//LED's
    static int mode_1 = 8;
    static int mode_2 = 9;
    static int mode_3 = 10;
    static int mode_4 = 11;
    static int mode_5 = 12;
    
}
/*Button Mapping
//Left Joystick
    Axis = drive
    Button 3 = Shift true
    Button 4 = shift false
    Button 5 = arm up
    Button 6 = arm down
    Button 7 = Mode Index 1
    Button 8 = Mode Index 2
    Button 9 = winds down and prints encoder value
    Button 10 = winds up and prints encoder value
//Right Joystick
    Y axis = manual winch w/o latching
    Button 2 = Winding
    Buttons 3,4 = Intake in, Intake out
    Button 5 = Prints Encoder Value
    Button 
    Button 11 & 6 = Safe Release
*/