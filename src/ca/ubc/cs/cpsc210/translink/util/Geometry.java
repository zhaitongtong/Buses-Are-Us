package ca.ubc.cs.cpsc210.translink.util;

/**
 * Compute relationships between points, lines, and rectangles represented by LatLon objects
 */
public class Geometry {
    /**
     * Return true if the point is inside of, or on the boundary of, the rectangle formed by northWest and southeast
     * @param northWest         the coordinate of the north west corner of the rectangle
     * @param southEast         the coordinate of the south east corner of the rectangle
     * @param point             the point in question
     * @return                  true if the point is on the boundary or inside the rectangle
     */
    public static boolean rectangleContainsPoint(LatLon northWest, LatLon southEast, LatLon point) {
        if(point.getLatitude()>=southEast.getLatitude() && point.getLatitude()<= northWest.getLatitude()&&
                point.getLongitude()<=southEast.getLongitude() && point.getLongitude()>= northWest.getLongitude()){ // !
            return true;
        }
        return false;
    }

    /**
     * Return true if the rectangle intersects the line
     * @param northWest         the coordinate of the north west corner of the rectangle
     * @param southEast         the coordinate of the south east corner of the rectangle
     * @param src               one end of the line in question
     * @param dst               the other end of the line in question
     * @return                  true if any point on the line is on the boundary or inside the rectangle
     */
    public static boolean rectangleIntersectsLine(LatLon northWest, LatLon southEast, LatLon src, LatLon dst) {
        double linePointX1 = src.getLongitude();
        double linePointY1 = src.getLatitude();
        double linePointX2 = dst.getLongitude();
        double linePointY2 = dst.getLatitude();

        double rectangleLeftTopX = northWest.getLongitude();
        double rectangleLeftTopY = northWest.getLatitude();
        double rectangleRightBottomX = southEast.getLongitude();
        double rectangleRightBottomY = southEast.getLatitude();

        double slope = (linePointY2 - linePointY1) / (linePointX2 - linePointX1);
        double x = (rectangleRightBottomY - linePointY1) / slope + linePointX1;
        double y = slope * (rectangleLeftTopX - linePointX1) + linePointY1;

        if ((linePointX1 < rectangleLeftTopX && linePointX2 < rectangleLeftTopX) ||
                (linePointY1 < rectangleRightBottomY && linePointY2 < rectangleRightBottomY) ||
                (linePointX1 > rectangleRightBottomX && linePointX2 > rectangleRightBottomX) ||
                (linePointY1 > rectangleLeftTopY && linePointY2 > rectangleLeftTopY)) {
            return false;
        }

        if (y < rectangleLeftTopY && y > rectangleRightBottomY) {
            return true;
        }
        y = slope * (rectangleRightBottomX - linePointX1) + linePointY1;
        if (y < rectangleLeftTopY && y > rectangleRightBottomY) {
            return true;
        }

        if (x < rectangleRightBottomX && x > rectangleLeftTopX) {
            return true;
        }
        x = (rectangleLeftTopY - linePointY1) / slope + linePointX1;
        if (x < rectangleRightBottomX && x > rectangleLeftTopX) {
            return true;
        }

        return false;
    }

    /**
     * A utility method that you might find helpful in implementing the two previous methods
     * Return true if x is >= lwb and <= upb
     * @param lwb      the lower boundary
     * @param upb      the upper boundary
     * @param x         the value in question
     * @return          true if x is >= lwb and <= upb
     */
    private static boolean between(double lwb, double upb, double x) {
        return lwb <= x && x <= upb;
    }
}