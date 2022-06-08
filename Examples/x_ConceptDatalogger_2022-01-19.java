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

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.Datalogger;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@TeleOp(name = "Concept Datalogger v01", group = "Datalogging")
public class ConceptDatalogger extends LinearOpMode
{
    Datalog datalog;
    BNO055IMU imu;
    VoltageSensor battery;

    @Override
    public void runOpMode() throws InterruptedException
    {
        // Get some stuff from the hwmap
        battery = hardwareMap.voltageSensor.get("Control Hub");
        imu = hardwareMap.get(BNO055IMU.class, "imu");

        // Initialize our datalog
        datalog = new Datalog("datalog_01");

        // You do not need to fill every field of the datalog
        // every time you call slurp(); those fields will simply
        // contain the last value
        datalog.opModeStatus.val = "INIT";
        datalog.battery.val = battery.getVoltage();
        datalog.slurp();

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imu.initialize(parameters);

        telemetry.setMsTransmissionInterval(50);

        waitForStart();

        datalog.opModeStatus.val = "RUNNING";

        for (int i = 0; opModeIsActive(); i++)
        {
            // Note that the order in which we set datalog fields
            // does *not* matter! The order is configured inside
            // the Datalog class constructor.

            datalog.loopCounter.val = i;
            datalog.battery.val = battery.getVoltage();

            Orientation orientation = imu.getAngularOrientation();

            datalog.yaw.val = orientation.firstAngle;
            datalog.pitch.val = orientation.secondAngle;
            datalog.roll.val = orientation.thirdAngle;

            // Note that the timestamp which goes into the log is taken
            // when slurp() is called
            datalog.slurp();

            telemetry.addData("Yaw", orientation.firstAngle);
            telemetry.addData("Pitch", orientation.secondAngle);
            telemetry.addData("Roll", orientation.thirdAngle);
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
        public Datalogger.DoubleField yaw = new Datalogger.DoubleField("Yaw", "0.00");
        public Datalogger.DoubleField pitch = new Datalogger.DoubleField("Pitch", "0.00");
        public Datalogger.DoubleField roll = new Datalogger.DoubleField("Roll", "0.00");
        public Datalogger.DoubleField battery = new Datalogger.DoubleField("Battery", "0.00");

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
                            yaw,
                            pitch,
                            roll,
                            battery
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