 
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
import edu.wpi.first.wpilibj.Encoder;
import com.sun.squawk.util.*;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SensorBase;



/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends IterativeRobot {
    
    /** JOYSTICK BUTTONS **/
    //DRIVER JOYSTICK
    final static int ANCHOR_UP_BUTTON = 0;
    final static int ANCHOR_DOWN_BUTTON = 2;
    
    //OPERATOR JOYSTICK
    final static int PANCAKE_BUTTON = 7;
    final static int WINCH_BUTTON = 6;
    final static int COLLECTOR_DOWN_BUTTON = 0;
    final static int COLLECTOR_UP_BUTTON = 2;
    final static int COLLECTOR_BUTTON = 1;
    final static int COLLECTOR_BUTTON_REVERSE = 3;
    
    
    /** CHANNELS **/
    //PWM CHANNEL
    final static int FRONT_LEFT_CHANNEL = 1;
    final static int FRONT_RIGHT_CHANNEL = 2;
    final static int BACK_LEFT_CHANNEL = 3;
    final static int BACK_RIGHT_CHANNEL = 4;
    final static int COLLECTOR_CHANNEL = 5;
    final static int WINCH_2_CHANNEL = 7;
    final static int WINCH_1_CHANNEL = 6;
    
    //RELAYS
    final static int ANCHOR_CHANNEL = 4;
    final static int COLLECTOR_ANGLE_CHANNEL = 8;
    final static int COMPRESSOR_RELAY_CHANNEL = 1;
    final static int PANCAKE_CHANNEL = 2;
    
    // DIGITAL I/O
    final static int WINCH_LIMIT_SWITCH_CHANNEL = 4;
    
    
    double STARTINGTHRESHOLD = 0.0;
    
    Talon frontleft;
    Talon frontright;
    Talon backleft;
    Talon backright;
    RobotDrive drive; 
    Joystick driverOne; 
    Joystick driverTwo; 
    Joystick operatorStick; 
    Talon winch1;
    Talon winch2;
    Compressor compressor;
    Talon collector;
    Relay compressorRelay;
    Relay pancakeRelay;
    Relay oceanbluePistons;
    Relay skydivePistons;
    Timer pancakeTimer;
    boolean isPancakeTimerOn = false;
    boolean isPS3Joystick = true;
    DigitalInput dropitlowSensor;
    Encoder Encoder;
    Gyro Gyroscope;
  
    
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        driverOne = new Joystick(1);
        if(isPS3Joystick == true){
            operatorStick = new Joystick(2);
        }
        else{
            driverTwo = new Joystick(2);
            operatorStick = new Joystick(3);
        }
        
        collector = new Talon (COLLECTOR_CHANNEL);
        frontleft = new Talon (FRONT_LEFT_CHANNEL);
        frontright = new Talon (FRONT_RIGHT_CHANNEL);
        backleft = new Talon (BACK_LEFT_CHANNEL);
        backright = new Talon (BACK_RIGHT_CHANNEL);
        winch1 = new Talon (WINCH_1_CHANNEL);
        winch2 = new Talon (WINCH_2_CHANNEL);
        drive = new RobotDrive (frontleft,backleft, frontright,backright);
        compressorRelay = new Relay(COMPRESSOR_RELAY_CHANNEL, Relay.Direction.kForward);
        compressorRelay.set(Relay.Value.kOn);
        //compressorRelaySwitchOn();
        //compressor = new Compressor(5,1);
        //compressor.start();
        pancakeRelay = new Relay(PANCAKE_CHANNEL, Relay.Direction.kBoth);
        //pancakeRelay.setDirection(Relay.Direction.kForward);
        pancakeTimer = new Timer();
        oceanbluePistons = new Relay (ANCHOR_CHANNEL, Relay.Direction.kBoth);
        skydivePistons = new Relay (COLLECTOR_ANGLE_CHANNEL, Relay.Direction.kBoth);
        dropitlowSensor = new DigitalInput (WINCH_LIMIT_SWITCH_CHANNEL);
        Encoder = new Encoder(2,3);
        int Evalue = Encoder.getRaw();
        //boolean dropitlowSensor = false;
        //Gyroscope = new Gyro(5);
        
        isPS3Joystick  = SmartDashboard.getBoolean("usePS3Joystick", true);
        SmartDashboard.putString("Collector", "disengaged");
        SmartDashboard.putString("Pancake", "disengaged");
        SmartDashboard.putString("Winch", "OFF");
        SmartDashboard.putString("Anchor", "UP");
        SmartDashboard.putString("cArm", "RVRSE");
        SmartDashboard.putString("Compressor", "ON");
       // SmartDashboard.putString("displayAngle",angle);
     //   SmartDashboard.putDouble("Angle", angle);
    }
    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Timer autonomoustimer = new Timer();
        autonomoustimer.start();
        double angle = Gyroscope.getAngle();
        
        while (isAutonomous() && isEnabled())
        {
            if(autonomoustimer.get() > 0 && autonomoustimer.get() < 3)
            {
                frontleft.set(1.0);
                frontright.set(1.0);
                backleft.set(1.0);
                backright.set(1.0);
            }
            if(autonomoustimer.get() > 3 && autonomoustimer.get() < 4)
            {
                pancakeRelay.set(Relay.Value.kReverse);
            }
            if(autonomoustimer.get() > 4 && autonomoustimer.get() < 5)
            {
                pancakeRelay.set(Relay.Value.kForward);
            }
        }
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
        checkPancake();
      //  double displayAngle  =  gyro.getAngle();
    }
    
    public void checkCollector()
    {
        if(operatorStick.getRawButton(COLLECTOR_BUTTON))
        {
            SmartDashboard.putString("Collector", "FWD");
            collector.set(1.0);
        }
        else if(operatorStick.getRawButton(COLLECTOR_BUTTON_REVERSE))
        {
            SmartDashboard.putString("Collector", "RVRSE");
            collector.set(-1.0);
        }
        else
        {
            collector.set(0.0);
            SmartDashboard.putString("Collector", "OFF");
        }
        
        
    }
    /*public void compressorRelaySwitchOn() {
compressorRelay.set(Relay.Value.kOn);
//System.out.println("Compressor Relay Value Now: " + compressorRelay.get().value);

}
    */
    public void checkCollectorAngles () 
    {
        if(operatorStick.getRawButton(COLLECTOR_UP_BUTTON)) {
             skydivePistons.set(Relay.Value.kReverse);
             SmartDashboard.putString("cArm", "Retracted");
        }
        if (operatorStick.getRawButton(COLLECTOR_DOWN_BUTTON)) {
             skydivePistons.set(Relay.Value.kForward);
             SmartDashboard.putString("cArm", "Extended");
        }
    
    }
    
    public void checkCompressor()
    {
        if (driverOne.getRawButton(6))
        {
            SmartDashboard.putString("Compressor", "ON");    
            compressorRelay.set(Relay.Value.kOn);
        }
        if(driverOne.getRawButton(4))
        {
            SmartDashboard.putString("Compressor", "OFF");
            compressorRelay.set(Relay.Value.kOff);
        }
    }
    
    public void checkAnchor() 
    {
        if(driverOne.getRawButton(ANCHOR_DOWN_BUTTON)){
            SmartDashboard.putString("Anchor", "DOWN");
            oceanbluePistons.set(Relay.Value.kReverse);
        }
        if (driverOne.getRawButton(ANCHOR_UP_BUTTON)){
            SmartDashboard.putString("Anchor", "UP");
            oceanbluePistons.set(Relay.Value.kForward);
        }
    
    }
    public void checkWinch()
    {
        SmartDashboard.putDouble("Encoder", Encoder.getRate());
        if(operatorStick.getRawButton(WINCH_BUTTON) /*&& dropitlowSensor.get() == false*/)
        {
            SmartDashboard.putString("Winch", "ON");
            winch1.set(1.0);
            winch2.set(1.0);
        }
        else
        {
            SmartDashboard.putString("Winch", "OFF");
            winch1.set(0.0);
            winch2.set(0.0);
        }
    }
    
    public void checkPancake()
    {
        if(operatorStick.getRawButton(PANCAKE_BUTTON) && isPancakeTimerOn == false)
        {
            pancakeTimer.start();
            isPancakeTimerOn = true;
            SmartDashboard.putString("Pancake", "engaged");
            pancakeRelay.set(Relay.Value.kReverse);
            
        }
            // after 2 secs
        if(pancakeTimer.get() >= 2)
        {   
            SmartDashboard.putString("Pancake", "disengaged");
            pancakeRelay.set(Relay.Value.kReverse);        
            pancakeTimer.stop();
            pancakeTimer.reset();
            isPancakeTimerOn = false;
        }
    }
    
    private void checkDrive(){
        
       //Set X motion based on Joystick Type
        double x = 0; 
        
        if(isPS3Joystick) 
        { 
            x = driverOne.getRawAxis(1); 
            x = x * -1;
        }    
        else 
        { 
            x = driverOne.getRawAxis(1); 
            x = x * -1;
        }
        if(x < 0.1 && x > -0.1){
            x = 0;
        }

        if(x > 0){
            x = (1-STARTINGTHRESHOLD) * MathUtils.pow(x,2) + STARTINGTHRESHOLD;
        }
        else if(x < 0){
            x = -1 * ((1-STARTINGTHRESHOLD) * MathUtils.pow(x,2) + STARTINGTHRESHOLD);
        }
        

        //Set Y-motion (STRAFE) based on Joystick Type
        double y = 0; 
        
        if(isPS3Joystick) 
        { 
            y = driverOne.getRawAxis(2);
            y = y * -1;
        } 
        else 
        { 
            y = driverOne.getRawAxis(2);
        }
        
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
        
        //Set Rotation Angle based on Joystick Type
        if(isPS3Joystick)
        {
            rotation = driverOne.getRawAxis(3); 
            rotation = rotation * -1;
        }
        else
        {
            rotation = driverTwo.getRawAxis(1); 
            rotation = rotation * -1;
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


