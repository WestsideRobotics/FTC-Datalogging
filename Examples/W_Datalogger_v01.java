/*
 
 This 'utility' class allows FTC OpModes to log data to a CSV file,
 for direct import to a spreadsheet like Excel or Google Sheets.
 
 The spreadsheet's default first column ("Time") shows # of seconds since
 the datalogging class was instantiated by the user's OpMode, which may
 or may not begin immediate data collection/logging.
 
 The remaining columns are determined by the user's OpMode.
 
 
 This class processes all data as text (type char or String) to create the
 comma-separated-values (CSV) file.  The spreadsheet still interprets any
 numeric data as numbers.
 
 Credit to Olavi Kamppari, who shared a more advanced version dated 9/9/2015.
 
*/

package org.firstinspires.ftc.teamcode;

import java.io.File;                    // already used in FTC SDK
import java.io.Writer;
import java.io.IOException;
import java.io.FileWriter;              // subclass of java.io.Writer

public class W_Datalogger_v01 {
    
    // Declare members.
    private Writer writer;              // stores file on RC device
    private StringBuffer lineBuffer;    // contains each line (row) of data
    private long timeBase;              // time of instantiation (milliseconds)

    // This constructor runs once, to initialize an instantiation of the class.
    public W_Datalogger_v01 (String fileName) {
        
        // Build the path with the filename provided by the calling OpMode.
        String directoryPath    = "/sdcard/FIRST/Datalogs";
        String filePath         = directoryPath + "/" + fileName + ".csv";

        new File(directoryPath).mkdir();  // create the directory if needed
        
        // Set up the file and first line (will contain column headings).
        try {
            writer = new FileWriter(filePath);
            lineBuffer = new StringBuffer(128);
        }
        catch (IOException e) {
        }
        
        timeBase = System.currentTimeMillis();
        addField("Time");                 // first/default column heading
    }   // end constructor

    // The OpMode calls this method to complete the current line (row) of data
    // and prepare for another.
    public void newLine(){
        
        long milliTime;
        try {
            lineBuffer.append('\n');                // end-of-line character
            writer.write(lineBuffer.toString());    // add line (row) to file
            lineBuffer.setLength(0);                // clear the line (row)
        }
        catch (IOException e) {
        }
        
        
        // Update and log the default first column (time of reading).
        milliTime   = System.currentTimeMillis();
        
        // Divide milliseconds by 1,000 to log seconds, in field named Time.
        // The math expression has Java type "long", hence the 1000.0 decimal.
        addField(String.format("%.3f",(milliTime - timeBase) / 1000.0));
    
    }   // end newLine() method
    
    // These two (overloaded) methods add a text field to the line (row),
    // preceded by a comma.  This creates the comma-separated values (CSV).
    
    public void addField(String s) {
        if (lineBuffer.length()>0) {
            lineBuffer.append(',');
        }
        lineBuffer.append(s);
    }

    public void addField(char c) {
        if (lineBuffer.length()>0) {
            lineBuffer.append(',');
        }
        lineBuffer.append(c);
    }

    // The following (overloaded) method converts Boolean to text 
    // (Java type char) and adds it to the current line (row).
    
    public void addField(boolean b) {
        addField(b ? '1' : '0');
    }

    // These (overloaded) methods accept various numeric types,
    // all converted to type String for the method listed above.
    // Spreadsheet programs typically interpret these correctly as numbers.

    public void addField(byte b) {
        addField(Byte.toString(b));
    }

    public void addField(short s) {
        addField(Short.toString(s));
    }

    public void addField(long l) {
        addField(Long.toString(l));
    }

    public void addField(float f) {
        addField(Float.toString(f));
    }

    public void addField(double d) {
        addField(Double.toString(d));
    }

   // The OpMode must call this method when finished logging data.
    public void closeDataLogger() {
        try {
            writer.close();             // close the file
        }
        catch (IOException e) {
        }
    }
 
}   // end class
