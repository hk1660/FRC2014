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
 * documentation. If you change the0 name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends IterativeRobot {

    /**
     * JOYSTICK BUTTONS * 
     */
    //DRIVER JOYSTICK
    final static int ANCHOR_UP_BUTTON = 1;  //1 is triangle
    final static int ANCHOR_DOWN_BUTTON = 3; //3 is x
    final static int GYRO_DRIVING_ON_BUTTON = 9; // 9 is start 
    final static int GYRO_DRIVING_OFF_BUTTON = 8; // 8 is select
    //OPERATOR JOYSTICK
    final static int RETRACT_LAUNCHER_BUTTON = 7; //7 = L2 
    final static int LAUNCH_BUTTON = 8; //8 = R2
    final static int WINCH_BUTTON = 6; //6 is R1
    final static int COLLECTOR_DOWN_BUTTON = 4; //4 = SQUARE
    final static int COLLECTOR_DOWN_BUTTON2 = 10; //=10 = start
    final static int COLLECTOR_UP_BUTTON = 2; //2 = CIRCLE
    final static int COLLECTOR_BUTTON = 1; //1 is triangle
    final static int COLLECTOR_BUTTON_REVERSE = 3; //3 is x

    /**
     * CHANNELS *
     */
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
    final static int PRESSURE_SWITCH_CHANNEL = 5; 

    double STARTINGTHRESHOLD = 0.0;
    final static double FULL_SPEED_FEET_PER_SECOND = 8.0;
        
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
    Timer autoTimer; 
    boolean isPancakeTimerOn = false;
    boolean isPS3Joystick = true;
    boolean isAnchorDown = false; 
    boolean gyroDriving = false;
    //boolean isdropBoolean = false; 
    DigitalInput pushitSensor; 
    DigitalInput dropitlowSensor;
    Encoder encoder;
    Gyro gyro;
    
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        driverOne = new Joystick(1);
        if (isPS3Joystick == true) {
            operatorStick = new Joystick(2);
        } else {
            driverTwo = new Joystick(2);
            operatorStick = new Joystick(3);
        }

        collector = new Talon(COLLECTOR_CHANNEL);
        frontleft = new Talon(FRONT_LEFT_CHANNEL);
        frontright = new Talon(FRONT_RIGHT_CHANNEL);
        backleft = new Talon(BACK_LEFT_CHANNEL);
        backright = new Talon(BACK_RIGHT_CHANNEL);
        winch1 = new Talon(WINCH_1_CHANNEL);
        winch2 = new Talon(WINCH_2_CHANNEL);
        drive = new RobotDrive(frontleft, backleft, frontright, backright);
        compressorRelay = new Relay(COMPRESSOR_RELAY_CHANNEL, Relay.Direction.kForward);
        compressorRelay.set(Relay.Value.kOn);
        //compressorRelaySwitchOn();
        //compressor = new Compressor(5,1);
        //compressor.start();
        pancakeRelay = new Relay(PANCAKE_CHANNEL, Relay.Direction.kBoth);
        //pancakeRelay.setDirection(Relay.Direction.kForward);
        pancakeTimer = new Timer();
        autoTimer = new Timer();
        oceanbluePistons = new Relay(ANCHOR_CHANNEL, Relay.Direction.kBoth);
        skydivePistons = new Relay(COLLECTOR_ANGLE_CHANNEL, Relay.Direction.kBoth);
        dropitlowSensor = new DigitalInput(WINCH_LIMIT_SWITCH_CHANNEL);
        pushitSensor = new DigitalInput(PRESSURE_SWITCH_CHANNEL); 
        encoder = new Encoder(2, 3);
        //int Evalue = Encoder.getRaw();
          
        gyro = new Gyro(1);
        gyro.reset();
        
        isPS3Joystick = SmartDashboard.getBoolean("usePS3Joystick", true);
        SmartDashboard.putString("Collector", "disengaged");
        SmartDashboard.putString("Pancake", "disengaged");
        SmartDashboard.putString("Winch", "OFF");
        SmartDashboard.putString("Anchor", "UP");
        SmartDashboard.putString("cArm", "RVRSE");
        SmartDashboard.putString("Compressor", "ON");
        SmartDashboard.putString("LimitSwitch", "NotTouched");
        
       // SmartDashboard.putString("displayAngle",angle);
        //   SmartDashboard.putDouble("Angle", angle);
    }
    
    public double convertDistanceInFeetToSecondsAtSpeed(double distanceInFeet, double robotSpeed)
    {
        return distanceInFeet / (FULL_SPEED_FEET_PER_SECOND * robotSpeed); 
    }
    
    public void autonomousInit()
    {
        autoTimer.reset();
        autoTimer.start();
        skydivePistons.set(Relay.Value.kReverse);
    }
    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        if (autoTimer.get() >= 10)
        {
            autoTimer.stop();
        }
        else{
            double robotSpeed = 1.0;
            double distanceInFeet = 10;
            moveForward(0, 0 + convertDistanceInFeetToSecondsAtSpeed(distanceInFeet, robotSpeed), robotSpeed);
            setCollectorDown(2, 3);
            setPancake(3, 5, false);
            setCollectorUp (5,6) ;
            setPancake(5, 7, true);
            winchDownInAuto(7,9);
        }
    }
    
    public void setCollectorDown (double startTime, double endTime)
    {
        if (autoTimer.get() > startTime && autoTimer.get() < endTime)
        {
            skydivePistons.set(Relay.Value.kForward);    
        }
    
    }
    
     public void setCollectorUp (double startTime, double endTime)
    {
        if (autoTimer.get() > startTime && autoTimer.get() < endTime)
        {
        skydivePistons.set(Relay.Value.kReverse);    
        }
    
    }
    
    public void moveForward (double startTime, double endTime, double speed) { 
       if(autoTimer.get() > startTime && autoTimer.get() < endTime) {
            drive.mecanumDrive_Cartesian(0, speed, 0, 0);
       }
       else{
           drive.mecanumDrive_Cartesian(0,0, 0, 0); 
       }
     }
    
    void setPancake(double startTime, double endTime, boolean engaged){
        if(autoTimer.get() > startTime && autoTimer.get() < endTime){
            if(engaged){
                engagePancake();
            }else{
                disengagePancake();
            }
        }
    }
    
    public void disengagePancake()
    {
        SmartDashboard.putString("Pancake", "disengaged");
        pancakeRelay.set(Relay.Value.kReverse);
    }
    public void engagePancake()
    {
         SmartDashboard.putString("Pancake", "engaged");
         pancakeRelay.set(Relay.Value.kForward);
    }
    public void winchDownInAuto(double startTime, double endTime)
    {
        if (autoTimer.get() > startTime && autoTimer.get() < endTime)
        {
            moveWinch(1.0);
        }
        else
        {
            moveWinch(0.0);
        }
    
    }
    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        checkDriveJoystick();
        checkWinchButtons();
        checkCollectorButtons();
        //checkCompressor();
        checkAnchorButtons();
        checkCollectorAngleButtons();
        checkLauncherButtons();
        checkPressure();
        checkGyroToggleButtons();
    }

    public void checkCollectorButtons() {
        if (operatorStick.getRawButton(COLLECTOR_BUTTON)) {
            SmartDashboard.putString("Collector", "FWD");
            collector.set(1.0);
        } else if (operatorStick.getRawButton(COLLECTOR_BUTTON_REVERSE)) {
            SmartDashboard.putString("Collector", "RVRSE");
            collector.set(-1.0);
        } else {
            collector.set(0.0);
            SmartDashboard.putString("Collector", "OFF");
        }

    }

    public void checkCollectorAngleButtons() {
        if (operatorStick.getRawButton(COLLECTOR_UP_BUTTON)) {
            skydivePistons.set(Relay.Value.kReverse);
            SmartDashboard.putString("cArm", "Retracted");
        }
        if (operatorStick.getRawButton(COLLECTOR_DOWN_BUTTON) || operatorStick.getRawButton(COLLECTOR_DOWN_BUTTON2)) {
            skydivePistons.set(Relay.Value.kForward);
            SmartDashboard.putString("cArm", "Extended");
        }

    }

    public void checkCompressorButtonOnDriverStick() {
        if (driverOne.getRawButton(6)) {
            SmartDashboard.putString("Compressor", "ON");
            compressorRelay.set(Relay.Value.kOn);
        }     
        if (driverOne.getRawButton(4)) {
            SmartDashboard.putString("Compressor", "OFF");
            compressorRelay.set(Relay.Value.kOff);
        }
    } 
    
    
    boolean isairBuild() { 
        return pushitSensor.get();
        
    }
    
    
    public void checkPressure() { 
       if (!isairBuild()) {
            SmartDashboard.putString("Compressor", "ON");
            compressorRelay.set(Relay.Value.kOn);
        } 
       else {
         SmartDashboard.putString("Compressor", "OFF");
            compressorRelay.set(Relay.Value.kOff);
    }      
   }
    

    
    public void checkAnchorButtons() {
        if (driverOne.getRawButton(ANCHOR_DOWN_BUTTON)) {
            isAnchorDown = true;
            SmartDashboard.putString("Anchor", "DOWN");
            oceanbluePistons.set(Relay.Value.kReverse);
            
        }
        if (driverOne.getRawButton(ANCHOR_UP_BUTTON)) {
            SmartDashboard.putString("Anchor", "UP");
            oceanbluePistons.set(Relay.Value.kForward);
            isAnchorDown = false;
        }

    }

    boolean isWinchMoving() {
        return encoder.getRate() != 0;
    }
    boolean atBottom()
    {
        return dropitlowSensor.get();
    }
    
    void moveWinch(double speed)
    {
        if (!atBottom()){
            winch1.set(speed);
            winch2.set(speed);
            SmartDashboard.putString("LimitSwitch", "Touched");
        }
        else
        {
            winch1.set(0.0);
            winch2.set(0.0);
            SmartDashboard.putString("LimitSwitch", "UnTouched");
        }
    }
    
    void checkGyroToggleButtons (){
        if (driverOne.getRawButton(GYRO_DRIVING_OFF_BUTTON )){
            gyroDriving = false;
        }
        if (driverOne.getRawButton(GYRO_DRIVING_ON_BUTTON )){
            gyroDriving = true;
        }
    }
    
    void checkWinchButtons() {
        //SmartDashboard.putNumber("Encoder", encoder.getRate());
        SmartDashboard.putBoolean("WINCH_MOVING", isWinchMoving());
        if (operatorStick.getRawButton(WINCH_BUTTON)) {
            SmartDashboard.putString("Winch", "ON");
            moveWinch(1.0);
        } else {
            SmartDashboard.putString("Winch", "OFF");
            moveWinch(0.0);
        }
    }

    public void checkLauncherButtons()
    {
        if (operatorStick.getRawButton(LAUNCH_BUTTON)) {
            disengagePancake();
        }
        if (operatorStick.getRawButton(RETRACT_LAUNCHER_BUTTON)) {
            engagePancake();
        }
    }    
    
    
    private void checkDriveJoystick() {
        if(isAnchorDown == true){
            drive.drive(0.0, 0.0);
            return;
        }
        //Set X motion based on Joystick Type
        double x = 0;

        if (isPS3Joystick) {
            x = driverOne.getRawAxis(1);
            x = x * -1;
        } else {
            x = driverOne.getRawAxis(1);
            x = x * -1;
        }
        if (x < 0.01 && x > -0.01) {
            x = 0;
        }

        if (x > 0) {
            x = (1 - STARTINGTHRESHOLD) * MathUtils.pow(x, 2) + STARTINGTHRESHOLD;
        } else if (x < 0) {
            x = -1 * ((1 - STARTINGTHRESHOLD) * MathUtils.pow(x, 2) + STARTINGTHRESHOLD);
        }

        //Set Y-motion (STRAFE) based on Joystick Type
        double y = 0;

        if (isPS3Joystick) {
            y = driverOne.getRawAxis(2);
            y = y * -1;
        } else {
            y = driverOne.getRawAxis(2);
        }

        if (y < 0.01 && y > -0.01) {
            y = 0;
        }
        if (y > 0) {
            y = (1 - STARTINGTHRESHOLD) * MathUtils.pow(y, 2) + STARTINGTHRESHOLD;
        } else if (y < 0) {
            y = -1 * ((1 - STARTINGTHRESHOLD) * MathUtils.pow(y, 2) + STARTINGTHRESHOLD);
        }

        double rotation = 0;

        //Set Rotation Angle based on Joystick Type
        if (isPS3Joystick) {
            rotation = driverOne.getRawAxis(3);
        } else {
            rotation = driverTwo.getRawAxis(1);
            rotation = rotation * -1;
        }

        if (rotation < 0.01 && rotation > -0.01) {
            rotation = 0;
        }

        if (rotation > 0) {
            rotation = (1 - STARTINGTHRESHOLD) * MathUtils.pow(rotation, 2) + STARTINGTHRESHOLD;
        } else if (rotation < 0) {
            rotation = -1 * ((1 - STARTINGTHRESHOLD) * MathUtils.pow(rotation, 2) + STARTINGTHRESHOLD);
        }
        
        double gyroAngle;
        if (gyroDriving) {
           gyroAngle = gyro.getAngle();
        }else{
            gyroAngle = 0;
        }
         
        //System.out.println("x:" + x + "y:" + y + "rotation:" + rotation);
        drive.mecanumDrive_Cartesian(x, y, rotation, gyroAngle);
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {

    }
}
