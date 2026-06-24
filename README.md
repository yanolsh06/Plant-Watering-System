# Plant-Watering-System

A closed-loop hardware-software automation prototype built to monitor real-time soil moisture and execute threshold-based irrigation cycles. 

[📄 Read Full Project Documentation](./Water%20Project%20Documentation.pdf)

---

## Hardware Setup & Prototyping
![image alt](https://github.com/yanolsh06/Plant-Watering-System/blob/cafe10f448e0d8ab2ecf51799713e0bfac0e32c9/AEF67ECC-E4B0-4418-812C-5C10DF59C4EE.png)
---

## System Architecture & Tech Stack
* **Microcontroller Platform:** Arduino Nano (Grove Beginner Kit matrix)
* **Core Languages & APIs:** Java (Firmata4j API), JUnit 5
* **Hardware Interfacing:** Analog Capacitive Soil Moisture Sensor, 5V Irrigation Pump, MOSFET Transistor Gates, SSD1306 OLED Screen
* **Data Visualization:** Hand-crafted Java Swing UI Components (`GraphPanel` and `VoltageGraphPanel`)

---

## How It Works (Finite State Machine)
The control loop executes a time-polling mechanism at 1-second intervals to transition through independent operational states based on sensor voltage:
1. **Critical Moisture (≤ 20%):** Triggers pump activation and toggles status LEDs.
2. **Moderate Moisture (20% – 50%):** Safely maintains the current pump duty state.
3. **Saturated Moisture (> 50%):** Instantly deactivates the pump to prevent overwatering.

*Includes an error-resilient runtime safety shutdown hook to force digital pin relaxation if communication drops.*

---

## Testing & Validation
* **Unit Testing:** Integrated a JUnit suite (`FINAL_PROJECTTest.java`) to assert accurate analog-to-digital signal conversion constraints and out-of-range value clamping logic.
* **Empirical Calibration:** Leveraged physical digital multimeter tracking to map analog voltage ranges directly against calibrated percentage parameters ($3.35\text{V}$ dry boundary to $2.90\text{V}$ wet limit).
