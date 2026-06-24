import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;
class FINAL_PROJECTTest {
    private static final int DRY_RAW = 720;   // 3.4V (0% moisture)
    private static final int WET_RAW = 520;   // 2.5V (100% moisture)


    @Test
    public void testDryCondition() {
        int result = FINAL_PROJECT.mapToPercentage(DRY_RAW);
        assertEquals(0, result, "Dry condition should return 0%");
    }


    @Test
    public void testAboveDryRange() {
        int result = FINAL_PROJECT.mapToPercentage(DRY_RAW + 100);
        assertEquals(0, result, "Values above dry range should clamp to 0%");
    }


    @Test
    public void testBelowWetRange() {
        int result = FINAL_PROJECT.mapToPercentage(WET_RAW - 120);
        assertEquals(100, result, "Values below wet range should clamp to 100%");
    }
}

