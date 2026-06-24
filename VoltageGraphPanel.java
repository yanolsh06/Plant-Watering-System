import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * VoltageGraphPanel - A graphical panel for displaying voltage levels over time.
 *
 * Features:
 * - Draws a grid for easy reading of values.
 * - Plots voltage values as a line graph.
 * - Displays time (in seconds) on the x-axis and voltage (in volts) on the y-axis.
 */
class VoltageGraphPanel extends JPanel {
    private List<Double> voltageData; // List to store voltage values for plotting

    // Graph Appearance Constants
    private static final Color BG_COLOR = new Color(240, 240, 240); // Background color
    private static final Color GRAPH_COLOR = Color.BLACK; // Graph line color
    private static final Color GRID_COLOR = new Color(200, 200, 200); // Grid line color
    private static final int DOT_SIZE = 8; // Size of data points
    private static final int PADDING = 50; // Padding around the graph
    private static final double MAX_VOLTAGE = 5.0; // Maximum voltage for scaling

    /**
     * Constructor: Initializes the panel with voltage data.
     * param data List of voltage readings to be displayed.
     */
    public VoltageGraphPanel(List<Double> data) {
        this.voltageData = data;
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createTitledBorder("Voltage Over Time"));
    }

    /**
     * Overrides the paintComponent method to draw the graph.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth(), height = getHeight();

        drawGrid(g2d, width, height); // Draw background grid
        drawGraph(g2d, width, height); // Draw voltage data as a line graph
    }

    /**
     * Draws a grid with labeled voltage levels.
     */
    private void drawGrid(Graphics2D g2d, int width, int height) {
        g2d.setColor(GRID_COLOR);

        // Draw horizontal grid lines for voltage levels
        for (double v = 0; v <= MAX_VOLTAGE; v += 0.5) {
            int y = height - PADDING - (int) ((v / MAX_VOLTAGE) * (height - 2 * PADDING));
            g2d.drawLine(PADDING, y, width - PADDING, y); // Grid lines

            // Label the voltage values on the y-axis
            g2d.setColor(Color.BLACK);
            g2d.drawString(String.format("%.1fV", v), PADDING - 30, y + 5);
            g2d.setColor(GRID_COLOR);
        }

        // Draw x-axis and y-axis
        g2d.setColor(Color.BLACK);
        g2d.drawLine(PADDING, height - PADDING, width - PADDING, height - PADDING); // X-axis
        g2d.drawLine(PADDING, height - PADDING, PADDING, PADDING); // Y-axis

        // Label the x-axis as "Time (s)"
        g2d.drawString("Time (s)", width / 2 - 30, height - 10);
    }

    /**
     * Plots the voltage data as a line graph.
     */
    private void drawGraph(Graphics2D g2d, int width, int height) {
        if (voltageData.isEmpty()) return; // No data, nothing to draw

        // Calculate spacing between data points
        int spacing = (voltageData.size() > 1) ? (width - 2 * PADDING) / (voltageData.size() - 1) : 0;

        g2d.setColor(GRAPH_COLOR);

        // Draw the line connecting data points
        for (int i = 1; i < voltageData.size(); i++) {
            int x1 = PADDING + (i - 1) * spacing;
            int y1 = height - PADDING - (int) ((voltageData.get(i - 1) / MAX_VOLTAGE) * (height - 2 * PADDING));
            int x2 = PADDING + i * spacing;
            int y2 = height - PADDING - (int) ((voltageData.get(i) / MAX_VOLTAGE) * (height - 2 * PADDING));
            g2d.drawLine(x1, y1, x2, y2);
        }

        // Draw data points as circles
        for (int i = 0; i < voltageData.size(); i++) {
            int x = PADDING + i * spacing;
            int y = height - PADDING - (int) ((voltageData.get(i) / MAX_VOLTAGE) * (height - 2 * PADDING));
            g2d.fillOval(x - DOT_SIZE / 2, y - DOT_SIZE / 2, DOT_SIZE, DOT_SIZE); // Draw data point

            // Label each point with time (in seconds)
            g2d.drawString(i + "s", x - 5, height - PADDING + 15);
        }
    }

    /**
     * Updates the voltage data and repaints the graph.
     * param newData New voltage readings to display.
     */
    public void updateData(List<Double> newData) {
        this.voltageData = newData;
        repaint();
    }
}
