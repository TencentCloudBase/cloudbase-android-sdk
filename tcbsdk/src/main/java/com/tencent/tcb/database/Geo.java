package com.tencent.tcb.database;

import com.tencent.tcb.database.Geos.LineString;
import com.tencent.tcb.database.Geos.MultiLineString;
import com.tencent.tcb.database.Geos.MultiPoint;
import com.tencent.tcb.database.Geos.MultiPolygon;
import com.tencent.tcb.database.Geos.Point;
import com.tencent.tcb.database.Geos.Polygon;
import com.tencent.tcb.utils.TcbException;

import java.util.ArrayList;

public class Geo {

    public Point point(double longitude, double latitude) throws TcbException {
        return new Point(longitude, latitude);
    }

    public MultiPoint multiPoint(ArrayList<Point> points) throws TcbException {
        return new MultiPoint(points);
    }

    public LineString lineString(ArrayList<Point> points) throws TcbException {
        return new LineString(points);
    }

    public MultiLineString multiLineString(ArrayList<LineString> lines) throws TcbException {
        return new MultiLineString(lines);
    }

    public Polygon polygon (ArrayList<LineString> lines) throws TcbException {
        return new Polygon(lines);
    }

    public MultiPolygon multiPolygon (ArrayList<Polygon> polygons) throws TcbException {
        return new MultiPolygon(polygons);
    }
}
