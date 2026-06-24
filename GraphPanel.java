import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * GraphPanel - A graphical panel for displaying moisture levels over time.
 *
 * Features:
 * - Displays moisture percentage levels as a line graph.
 * - Includes grid lines for readability.
 * - Marks important thresholds (dry and moist levels).
 * - Labels the x-axis (time) and y-axis (moisture percentage).
 */
class GraphPanel extends JPanel {
    private List<Integer> data; // List to store moisture values for plotting

    // Graph Appearance Constants
    private static final Color BG_COLOR = new Color(240, 240, 240); // Background color
    private static final Color GRAPH_COLOR = Color.BLACK; // Line graph color
    private static final Color GRID_COLOR = new Color(200, 200, 200); // Grid color
    private static final Color DRY_COLOR = Color.RED; // Color for very dry threshold
    private static final Color MOIST_COLOR = Color.ORANGE; // Color for moderately moist threshold
    private static final int DOT_SIZE = 8; // Size of data points
    private static final int PADDING = 50; // Padding around the graph
    private static final int MAX_VALUE = 100; // Maximum moisture percentage (scale)

    // Threshold Levels
    private static final int VERY_DRY_THRESHOLD = 20; // Critical dry level
    private static final int MODERATELY_MOIST_THRESHOLD = 50; // Moderate moisture level

    /**
     * Constructor: Initializes the panel with moisture data.
     * param data List of moisture readings to be displayed.
     */
    public GraphPanel(List<Integer> data) {
        this.data = data;
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createTitledBorder("Moisture Level Over Time"));
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
        drawThresholds(g2d, width, height); // Draw dry and moist thresholds
        drawGraph(g2d, width, height); // Draw moisture data as a line graph
    }

    /**
     * Draws a grid with labeled moisture levels.
     */
    private void drawGrid(Graphics2D g2d, int width, int height) {
        g2d.setColor(GRID_COLOR);

        // Draw horizontal grid lines at 10% intervals
        for (int i = 0; i <= MAX_VALUE; i += 10) {
            int y = height - PADDING - (i * (height - 2 * PADDING) / MAX_VALUE);
            g2d.drawLine(PADDING, y, width - PADDING, y); // Grid lines

            // Label the moisture levels on the y-axis
            g2d.setColor(Color.BLACK);
            g2d.drawString(i + "%", PADDING - 30, y + 5);
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
     * Draws threshold lines for dry and moist conditions.
     */
    private void drawThresholds(Graphics2D g2d, int width, int height) {
        // Draw the "Very Dry" threshold in red
        int dryY = height - PADDING - (VERY_DRY_THRESHOLD * (height - 2 * PADDING) / MAX_VALUE);
        g2d.setColor(DRY_COLOR);
        g2d.drawLine(PADDING, dryY, width - PADDING, dryY);
        g2d.drawString("Dry Threshold", width - 100, dryY - 5);

        // Draw the "Moderately Moist" threshold in orange
        int moistY = height - PADDING - (MODERATELY_MOIST_THRESHOLD * (height - 2 * PADDING) / MAX_VALUE);
        g2d.setColor(MOIST_COLOR);
        g2d.drawLine(PADDING, moistY, width - PADDING, moistY);
        g2d.drawString("Moist Threshold", width - 120, moistY - 5);
    }

    /**
     * Plots the moisture data as a line graph.
     */
    private void drawGraph(Graphics2D g2d, int width, int height) {
        if (data.isEmpty()) return; // No data, nothing to draw

        // Calculate spacing between data points
        int spacing = (data.size() > 1) ? (width - 2 * PADDING) / (data.size() - 1) : 0;

        g2d.setColor(GRAPH_COLOR);

        // Draw the line connecting data points
        for (int i = 1; i < data.size(); i++) {
            int x1 = PADDING + (i - 1) * spacing;
            int y1 = height - PADDING - (data.get(i - 1) * (height - 2 * PADDING) / MAX_VALUE);
            int x2 = PADDING + i * spacing;
            int y2 = height - PADDING - (data.get(i) * (height - 2 * PADDING) / MAX_VALUE);
            g2d.drawLine(x1, y1, x2, y2);
        }

        // Draw data points as circles
        for (int i = 0; i < data.size(); i++) {
            int x = PADDING + i * spacing;
            int y = height - PADDING - (data.get(i) * (height - 2 * PADDING) / MAX_VALUE);
            g2d.fillOval(x - DOT_SIZE / 2, y - DOT_SIZE / 2, DOT_SIZE, DOT_SIZE); // Draw data point

            // Label each point with time (in seconds)
            g2d.drawString(i + "s", x - 5, height - PADDING + 15);
        }
    }

    /**
     * Updates the moisture data and repaints the graph.
     * param newData New moisture readings to display.
     */
    public void updateData(List<Integer> newData) {
        this.data = newData;
        repaint();
    }
}
