/*
This sample FTC OpMode uses methods of the Datalogger class to specify and
collect robot data to be logged in a CSV file, ready for download and charting.

For instructions, see the tutorial at the FTC Wiki:
https://github.com/FIRST-Tech-Challenge/FtcRobotController/wiki/Datalogging


The Datalogger class is suitable for FTC OnBot Java (OBJ) programmers.
Its methods can be made available for FTC Blocks, by creating myBlocks in OBJ.

Android Studio programmers can see instructions in the Datalogger class notes.

Credit to @Windwoes (https://github.com/Windwoes).

*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Datalog Example v01", group = "Datalogging")
public class DatalogExample_v01 extends LinearOpMode
{
    Datalog datalog;

    DcMotor lifterMotor;
    Servo grabberServo;
    TouchSensor myTouchSensor;
    AnalogInput myPotSensor;
    ColorSensor myColorSensor;

    @Override
    public void runOpMode() throws InterruptedException
    {
        // Get some stuff from the hwmap
        lifterMotor = hardwareMap.get(DcMotor.class, "lifter");
        grabberServo = hardwareMap.get(Servo.class, "grabber");
        myTouchSensor = hardwareMap.get(TouchSensor.class, "sensorTouch");
        myPotSensor = hardwareMap.get(AnalogInput.class, "sensorPot");
        myColorSensor = hardwareMap.get(ColorSensor.class, "sensorColor");
    
        // Initialize our datalog
        datalog = new Datalog("datalog_02");

        // You do not need to fill every field of the datalog
        // every time you call slurp(); those fields will simply
        // contain the last value
        datalog.opModeStatus.val = "INIT";
        datalog.slurp();

        telemetry.setMsTransmissionInterval(50);

        waitForStart();

        datalog.opModeStatus.val = "RUNNING";

        for (int i = 0; opModeIsActive(); i++)
        {
            // move the motor and servo
            lifterMotor.setPower(gamepad1.left_stick_y);
            grabberServo.setPosition(gamepad1.left_trigger);
    
            // Note that the order in which we set datalog fields
            // does *not* matter! The order is configured inside
            // the Datalog class constructor.

            datalog.loopCounter.val = i;
        
            datalog.motorEncoder.val = lifterMotor.getCurrentPosition();
            datalog.servoPosition.val = grabberServo.getPosition();
            datalog.touchPress.val = (myTouchSensor.isPressed() ? "True" : "False");
            datalog.potValue.val = myPotSensor.getVoltage();
            datalog.totalLight.val = myColorSensor.alpha();


            // Note that the timestamp which goes into the log is taken
            // when slurp() is called
            datalog.slurp();

            telemetry.addData("Lifter Motor Encoder", datalog.motorEncoder.val);
            telemetry.addData("Grabber Position (commanded)", "%.2f", datalog.servoPosition.val);
            telemetry.addData("Touched", datalog.touchPress.val);
            telemetry.addData("Pot. Voltage", "%.2f", datalog.potValue.val);
            telemetry.addData("Color Sensor Alpha (total light)", datalog.totalLight.val);
            telemetry.update();

            sleep(20);
        }

        /*
         * The datalog is automatically closed and flushed to disk after the opmode ends
         * - no need to do that manually :')
         */
    }

    /*
     * This class encapsulates all the fields that will go into the datalog
     */
    public static class Datalog
    {
        // The underlying datalogger object - it only cares about an array of loggable fields
        private final Datalogger datalogger;

        // The all of the fields that we want in the datalog.
        // Note that order here is NOT important. The order is important in the setFields() call below
        public Datalogger.StringField opModeStatus = new Datalogger.StringField("OpModeStatus");
        public Datalogger.IntField loopCounter = new Datalogger.IntField("Loop Counter");
        
        public Datalogger.IntField motorEncoder = new Datalogger.IntField("Lifter Enc.");
        public Datalogger.DoubleField servoPosition = new Datalogger.DoubleField("Grabber Pos.", "0.00");
        public Datalogger.StringField touchPress = new Datalogger.StringField("Touched");
        public Datalogger.DoubleField potValue = new Datalogger.DoubleField("Pot. Value", "0.00");
        public Datalogger.IntField totalLight = new Datalogger.IntField("Total Light");

        public Datalog(String name)
        {
            // Build the underlying datalog object
            datalogger = new Datalogger.Builder()

                    // Pass through the filename
                    .setFilename(name)

                    // Request an automatic timestamp field
                    .setAutoTimestamp(Datalogger.AutoTimestamp.DECIMAL_SECONDS)

                    // Tell it about the fields we care to log
                    // Note that order *IS* important here! The order in which we list
                    // the fields is the order in which they will appear in the log
                    .setFields(
                            opModeStatus,
                            loopCounter,
                            motorEncoder,
                            servoPosition,
                            touchPress,
                            potValue,
                            totalLight
                    )
                    .build();
        }

        // Tell the datalogger to slurp up the values of the fields
        // and write a new line in the log
        public void slurp()
        {
            datalogger.slurp();
        }
    }
}
