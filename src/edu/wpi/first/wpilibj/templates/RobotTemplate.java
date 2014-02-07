/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/


//Make sure to "Commit" code on your local computer with relevant comments
//At end of session, "Push" code to the remote repository online.

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;

import com.sun.squawk.util.*;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Timer;


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
    Compressor compressor;
    Talon collector;
    Relay pancakeRelay;
    Relay oceanbluePistons;
    Relay skydivePistons;
    Timer pancakeTimer;
    boolean isPancakeTimerOn = false;
    boolean isPS2Joystick = true;
    
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        driverStick= new Joystick(1);
        operatorStick = new Joystick(2);
        collector = new Talon (5);
        frontleft = new Talon (1);
        frontright = new Talon (2);
        backleft = new Talon (3);
        backright = new Talon (4);
        winch1 = new Talon (6);
        winch2 = new Talon (7);
        drive = new RobotDrive (frontleft,backleft, frontright,backright);
        compressor = new Compressor(5,1);
        compressor.start();
        pancakeRelay = new Relay(4);
        pancakeRelay.setDirection(Relay.Direction.kForward);
        pancakeTimer = new Timer();
        oceanbluePistons = new Relay (2);
        skydivePistons = new Relay (3);
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
        checkWinch();
        checkCollector();
        checkCompressor();
        checkAnchor();
        checkCollectorAngles();
    }
    
    public void checkCollector()
    {
        if(operatorStick.getRawButton(8))
        {
            collector.set(1.0);
        }
        else
        {
            collector.set(0.0);
        }
        
        if(operatorStick.getRawButton(9))
        {
            collector.set(-1.0);
        }
    }
    public void checkCollectorAngles () 
    {
        if(driverStick.getRawButton(7)) {
             skydivePistons.setDirection(Relay.Direction.kReverse);
             
        }
        if (driverStick.getRawButton(8)) {
             skydivePistons.setDirection(Relay.Direction.kForward);
        }
    
    }
    
    public void checkCompressor()
    {
        if (driverStick.getRawButton(3)){
        compressor.start();
        }
        if(driverStick.getRawButton(4)){
        compressor.stop();
        }
        
    
    }
    
    public void checkAnchor() 
    {
        if(operatorStick.getRawButton(3)){
        oceanbluePistons.setDirection(Relay.Direction.kForward);
        }
        if (operatorStick.getRawButton(2)){
        oceanbluePistons.setDirection(Relay.Direction.kReverse);
        }
    
    }
    public void checkWinch()
    {
        if(operatorStick.getRawButton(7))
        {
            winch1.set(1.0);
            winch2.set(1.0);
        }
        else
        {
            winch1.set(0.0);
            winch2.set(0.0);
        }
    }
    
    public void checkPancake()
    {
    
        if(operatorStick.getRawButton(10) && isPancakeTimerOn == false)
        {
            pancakeTimer.start();
            isPancakeTimerOn = true;
            pancakeRelay.setDirection(Relay.Direction.kReverse);
            
        }
            // after 2 secs
        if(pancakeTimer.get() >= 2)
        {
            pancakeRelay.set(Relay.Value.kForward);        
            pancakeTimer.stop();
            pancakeTimer.reset();
            isPancakeTimerOn = false;
        }
    }
    
    private void checkDrive(){
        double x = driverStick.getRawAxis(1);
        if(x < 0.1 && x > -0.1){
            x = 0;
        }

        if(x > 0){
            x = (1-STARTINGTHRESHOLD) * MathUtils.pow(x,2) + STARTINGTHRESHOLD;
        }
        else if(x < 0){
            x = -1 * ((1-STARTINGTHRESHOLD) * MathUtils.pow(x,2) + STARTINGTHRESHOLD);
        }
        
        double y = driverStick.getRawAxis(2);
        if(y < 0.1 && y > -0.1){
            y = 0;
        }
        if(y > 0){
            y = (1-STARTINGTHRESHOLD) * MathUtils.pow(y,2) + STARTINGTHRESHOLD;
        }
        else if(y < 0){
            y = -1 * ((1-STARTINGTHRESHOLD) * MathUtils.pow(y,2) + STARTINGTHRESHOLD);
        }
        
        double rotation = 0;
        
        if(isPS2Joystick)
        {
            rotation = operatorStick.getRawAxis(4);
        }
        else
        {
            rotation = driverStick.getRawAxis(3);   
        }
        if(rotation < 0.05 && rotation > -0.05){
                rotation = 0;               
        }   
       
        if(rotation > 0){
            rotation = (1-STARTINGTHRESHOLD) * MathUtils.pow(rotation,2) + STARTINGTHRESHOLD;
        }
        else if (rotation < 0){
            rotation = -1 * ((1-STARTINGTHRESHOLD) * MathUtils.pow(rotation,2) + STARTINGTHRESHOLD);
        }
        
        double gyroAngle = 0;
        drive.mecanumDrive_Cartesian(x, y, rotation, 0.0);
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }    
}
