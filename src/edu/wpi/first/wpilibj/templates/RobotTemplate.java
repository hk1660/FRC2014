/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;

import com.sun.squawk.util.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends IterativeRobot {

    double STARTINGTHRESHOLD = 0.0;
    
    Talon frontleft;
    Talon frontright;
    Talon backleft;
    Talon backright;
    RobotDrive drive; 
    Joystick operatorStick;
    Joystick driverStick;
    Talon winch1;
    Talon winch2;
    Relay collectorRelay;
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        driverStick= new Joystick(1);
        operatorStick = new Joystick(2);
        collectorRelay = new Relay(5, Relay.Direction.kBoth);
        collectorRelay.set(Relay.Value.kOff);
        frontleft = new Talon (1);
        frontright = new Talon (2);
        backleft = new Talon (3);
        backright = new Talon (4);
        winch1 = new Talon (5);
        winch2 = new Talon (6);
        drive = new RobotDrive (frontleft,backleft, frontright,backright);

    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {

    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        checkDrive();
        if(operatorStick.getRawButton(7))
        {
        winch();
        }
        
        if(operatorStick.getRawButton(8))
        {
        collectorIn();
        }
        if(operatorStick.getRawButton(9))
        {
        collectorOut();
        }
    }
    
    public void collectorIn()
    {
    collectorRelay.set(Relay.Value.kForward);
    }
    
    public void collectorOut()
    {
    collectorRelay.set(Relay.Value.kReverse);
    }
    
    public void winch()
    {
    winch1.set(1.0);
    winch2.set(1.0);
    }
    
    private void checkDrive(){
        double x = driverStick.getRawAxis(1);
        if(x < 0.1 && x > -0.1){
            x = 0;
        }

        if(x > 0){
            x = (1-STARTINGTHRESHOLD) * MathUtils.pow(x,2-STARTINGTHRESHOLD) + STARTINGTHRESHOLD;
        }
        else if(x < 0){
            x = -1 * ((1-STARTINGTHRESHOLD) * MathUtils.pow(x,2-STARTINGTHRESHOLD) + STARTINGTHRESHOLD);
        }
        
        double y = driverStick.getRawAxis(2);
        if(y < 0.1 && y > -0.1){
            y = 0;
        }
        if(y > 0){
            y = (1-STARTINGTHRESHOLD) * MathUtils.pow(y,2-STARTINGTHRESHOLD) + STARTINGTHRESHOLD;
        }
        else if(y < 0){
            y = -1 * ((1-STARTINGTHRESHOLD) * MathUtils.pow(y,2-STARTINGTHRESHOLD) + STARTINGTHRESHOLD);
        }
        
        double rotation = driverStick.getRawAxis(3);
        if(rotation < 0.05 && rotation > -0.05){
                rotation = 0;
        }
        if(rotation > 0){
            rotation = (1-STARTINGTHRESHOLD) * MathUtils.pow(rotation,2-STARTINGTHRESHOLD) + STARTINGTHRESHOLD;
        }
        else if (rotation < 0){
            rotation = -1 * ((1-STARTINGTHRESHOLD) * MathUtils.pow(rotation,2-STARTINGTHRESHOLD) + STARTINGTHRESHOLD);
        }
        
        double gyroAngle = 0;
        
        System.out.println("x:"+x+" y:"+y+" rotation:"+rotation);
        
        drive.mecanumDrive_Cartesian(x, y, rotation, 0.0);
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
}
