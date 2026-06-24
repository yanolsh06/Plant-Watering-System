import javax.swing.*;
import org.firmata4j.*;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ssd1306.SSD1306;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Automated Plant Watering System with Dual Graph Display
 * Features:
 * - Soil moisture monitoring
 * - Automatic watering control
 * - Moisture percentage graph
 * - Voltage-time graph
 * - OLED status display
 */
public class FINAL_PROJECT {
    // Hardware Configuration Constants
    static final int MOISTURE_SENSOR = 14; // Analog pin A0 (Firmata uses 14)
    static final int PUMP = 7;            // Digital pin D7 for pump control
    static final int LED = 4;             // Digital pin D4 for status LED
    static final String USB_PORT = "COM3"; // Change this to match your Arduino port

    // Data Collection for Graphing
    static ArrayList<Integer> moistureData = new ArrayList<>(); // Stores moisture level history
    static ArrayList<Double> voltageData = new ArrayList<>();   // Stores voltage history
    static final int MAX_DATA_POINTS = 60; // Stores up to 1 minute of data at 1 reading/sec

    // State Machine Thresholds for Watering Logic
    static final int VERY_DRY_THRESHOLD = 20;    // If moisture ≤ 20%, pump turns ON
    static final int MODERATELY_MOIST_THRESHOLD = 50; // If moisture ≤ 50%, pump stays ON
    static boolean pumpOn = false; // Tracks pump status

    // Hardware References
    private static IODevice arduino;  // Connection to Arduino via Firmata
    private static Pin pumpPin;       // Reference to the pump pin

    public static void main(String[] args) throws InterruptedException, IOException {
        /* [Initialization Phase] */
        arduino = new FirmataDevice(USB_PORT);

        // Safety Shutdown: Ensures pump turns off if the program stops unexpectedly
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (pumpPin != null) pumpPin.setValue(0);
            } catch (IOException e) {
                System.err.println("Error turning off pump: " + e.getMessage());
            }
        }));

        // Establish Firmata connection with Arduino
        arduino.start();
        arduino.ensureInitializationIsDone();

        // Initialize OLED Display for real-time status updates
        SSD1306 oled = new SSD1306(arduino.getI2CDevice((byte) 0x3C), SSD1306.Size.SSD1306_128_64);
        oled.init();

        // Configure Sensor and Output Pins
        Pin sensor = arduino.getPin(MOISTURE_SENSOR); // Moisture sensor input
        pumpPin = arduino.getPin(PUMP); // Water pump output
        Pin led = arduino.getPin(LED); // Status LED output
        sensor.setMode(Pin.Mode.ANALOG);
        pumpPin.setMode(Pin.Mode.OUTPUT);
        led.setMode(Pin.Mode.OUTPUT);
        pumpPin.setValue(0); // Ensure pump starts OFF

        // Create Moisture Graph Window
        JFrame moistureFrame = new JFrame("Moisture Level vs Time");
        GraphPanel moistureGraphPanel = new GraphPanel(moistureData);
        moistureFrame.add(moistureGraphPanel);
        moistureFrame.setSize(800, 500);
        moistureFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        moistureFrame.setVisible(true);

        // Create Voltage Graph Window
        JFrame voltageFrame = new JFrame("Voltage vs Time");
        VoltageGraphPanel voltageGraphPanel = new VoltageGraphPanel(voltageData);
        voltageFrame.add(voltageGraphPanel);
        voltageFrame.setSize(800, 500);
        voltageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        voltageFrame.setVisible(true);

        /* [Main Control Loop] */
        while (true) {
            // Read and process sensor data
            int rawValue = (int) sensor.getValue();  // Get raw sensor value (0-1023)
            int moisturePercentage = mapToPercentage(rawValue); // Convert to percentage
            double voltage = rawToVoltage(rawValue); // Convert to voltage

            /* [State Machine Implementation for Pump Control] */
            if (moisturePercentage <= VERY_DRY_THRESHOLD) {
                // If soil is very dry (≤20%), turn the pump ON
                if (!pumpOn) {
                    pumpOn = true;
                    pumpPin.setValue(1); // Activate pump
                    led.setValue(1);    // Turn on status LED
                }
            } else if (moisturePercentage <= MODERATELY_MOIST_THRESHOLD) {
                // If soil is moderately moist (≤50%), keep pump ON
                if (!pumpOn) {
                    pumpOn = true;
                    pumpPin.setValue(1);
                    led.setValue(1);
                }
            } else {
                // If soil is wet (>50%), turn pump OFF
                if (pumpOn) {
                    pumpOn = false;
                    pumpPin.setValue(0); // Deactivate pump
                    led.setValue(0);    // Turn off status LED
                }
            }

            /* [OLED Display Update] */
            oled.getCanvas().clear();
            oled.getCanvas().drawString(0, 0, "Moisture: " + moisturePercentage + "%");
            oled.getCanvas().drawString(0, 10, "Voltage: " + String.format("%.2f", voltage) + "V");
            oled.getCanvas().drawString(0, 20, "Raw: " + rawValue);
            oled.getCanvas().drawString(0, 30, "Pump: " + (pumpOn ? "ON" : "OFF"));

            // Display current soil status on OLED
            if (moisturePercentage <= VERY_DRY_THRESHOLD) {
                oled.getCanvas().drawString(0, 40, "Status: Very Dry");
            } else if (moisturePercentage <= MODERATELY_MOIST_THRESHOLD) {
                oled.getCanvas().drawString(0, 40, "Status: Moderate");
            } else {
                oled.getCanvas().drawString(0, 40, "Status: Very Wet");
            }
            oled.display(); // Refresh OLED screen

            /* [Data Collection for Graphing] */
            // Store moisture data (keeps last 60 readings)
            moistureData.add(moisturePercentage);
            if (moistureData.size() > MAX_DATA_POINTS) {
                moistureData.remove(0);
            }
            moistureGraphPanel.updateData(moistureData);

            // Store voltage data (keeps last 60 readings)
            voltageData.add(voltage);
            if (voltageData.size() > MAX_DATA_POINTS) {
                voltageData.remove(0);
            }
            voltageGraphPanel.updateData(voltageData);

            // Delay to maintain 1 sample per second
            TimeUnit.SECONDS.sleep(1);
        }
    }

    /**
     * Converts raw sensor reading (0-1023) to moisture percentage (0-100)
     * param rawValue Analog reading from moisture sensor
     * return Moisture percentage (0-100)
     */
    public static int mapToPercentage(int rawValue) {
        // Calibration values (adjust based on actual sensor readings)
        final int DRY_VALUE = 720;   // Sensor reading when soil is completely dry
        final int WET_VALUE = 520;   // Sensor reading when soil is completely wet

        // Ensure rawValue stays within valid range
        rawValue = Math.min(DRY_VALUE, Math.max(WET_VALUE, rawValue));

        // Convert raw sensor data to moisture percentage
        return 100 - ((rawValue - WET_VALUE) * 100 / (DRY_VALUE - WET_VALUE));
    }

    /**
     * Converts raw sensor reading (0-1023) to voltage (0-5V)
     * param rawValue Analog reading from moisture sensor (0-1023)
     * return Voltage in volts (0-5V)
     */
    public static double rawToVoltage(int rawValue) {
        return rawValue * (5.0 / 1023.0); // Convert ADC value to voltage
    }
}
