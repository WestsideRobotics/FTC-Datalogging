/*

This sample OpMode demonstrates using the Datalogger class, which allows
FTC OpModes to log data to a CSV file, for direct import to Excel or Google Sheets.
 
This OpMode writes the data to a log file in the RC device folder FIRST/Datalogs.
Specify the filename in this OpMode code.

Further info is available here:
https://github.com/WestsideRobotics/FTC-Datalogging/wiki

The source of data here is the IMU contained in the REV Control Hub and most
REV Expansion Hubs.  Two fields are logged: Count (index number of each reading)
and Heading (Z-angle in degrees).

In the CSV/spreadsheet, these two columns are preceded by a default column:
"Time" shows # of seconds since the datalogging class was instantiated.

This v02 version logs data on a **timed interval**, greater than the average
cycle time of the unregulated while() loop -- observed to be 8-10 ms in v01.



Notes:

1. Do not run this OpMode while the RC device is connected via USB cable 
(in file transfer or MTP mode) to a Windows laptop.  The datalog file 
will be created, but no data will be logged to the file.

It's OK to run this OpMode while the RC device is connected wirelessly
via Android Debug Bridge (adb) to a Windows laptop.

2. The wiki tutorial describes v02 and v03 of the W_Datalogger_v01 class.
To use one of those, edit two lines as noted in the code below.

v03 of the Datalogger class adds a second default column:
"d ms" shows # of milliseconds since the previous line (row) of data was logged.
 
Note: ignore the first two values of "d ms"; they are not true "deltas".



Credit to Olavi Kamppari, who shared a more advanced version dated 9/9/2015.


Future feature?  Ensure data is logged to the intended field.  Don't rely on
the order of .addField() commands to correspond to the order of column headings.
 
*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import com.qualcomm.hardware.bosch.BNO055IMU;   // IMU used in REV Hubs

@Autonomous(name = "DataLog IMU v02 timed", group = "Datalogging")

public class W_DL_OpMode_IMU_v02_timed extends LinearOpMode {

    // Declare members.
    BNO055IMU imu;
    BNO055IMU.Parameters imuParameters;
    Orientation angles;
    W_Datalogger_v01 imuDL;             // can update to v02 or v03

    double myHeading;
    int readCount = 0;
    String datalogFilename = "myDatalog_001";   // modify name for each run

    ElapsedTime dataTimer;              // timer object
    int logInterval = 50;               // target interval in milliseconds
    
    @Override
    public void runOpMode() {

        // Get device from robot Configuration.
        imu = hardwareMap.get(BNO055IMU.class, "imu");

        // Initialize parameters and IMU.
        imuParameters = new BNO055IMU.Parameters();
        imuParameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imuParameters.loggingEnabled = false;
        imu.initialize(imuParameters);

        // Instantiate Datalogger class.  Can update to v02 or v03.
        imuDL = new W_Datalogger_v01(datalogFilename);
        
        // Instantiate datalog timer.
        dataTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);

        // Name the fields (column headings) generated by this OpMode.
        imuDL.addField("Count");
        imuDL.addField("IMU Heading Angle");
        imuDL.newLine();                        // end first line (row)

        telemetry.addData("Datalogging Sample -- IMU Heading", "Touch START to continue...");
        telemetry.update();
    
        waitForStart();
        
        // Reset timer for datalogging interval.
        dataTimer.reset();

        while (opModeIsActive()) {
        
            if (dataTimer.time() > logInterval)  {

                angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
                myHeading = angles.firstAngle;      // store Z-angle or heading
                readCount ++;
        
                // Populate the fields to be logged.  Must appear in the same order
                // as the column headings, to ensure data is logged to the intended
                // field name.
                imuDL.addField(readCount);
                imuDL.addField(myHeading);
                imuDL.newLine();                // end the current set of readings
            
                // Show live telemetry on the Driver Station screen.
                telemetry.addData("Count", readCount);
                telemetry.addData("myHeading", "%.1f", myHeading);

                // Show IMU system status and calibration status.
                telemetry.addLine();
                telemetry.addData("IMU status", imu.getSystemStatus().toShortString());
                telemetry.addData("IMU calibration status", imu.getCalibrationStatus().toString());

                telemetry.update();
                
                dataTimer.reset();      // start the interval timer again
              
            }   // end if(timer)
            
        }   // end main while() loop
        
        imuDL.closeDataLogger();            // close Datalogger when finished
        
    }   // end runOpMode()
    
}   // end class