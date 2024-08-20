package com.drivewise.smarttraffic.dto.serializer;

import com.drivewise.smarttraffic.dto.RouteDTO;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.locationtech.jts.geom.Coordinate;

import java.io.IOException;

public class RouteDTOSerializer extends StdSerializer<RouteDTO> {
    public RouteDTOSerializer() {
        this(null);
    }

    public RouteDTOSerializer(Class<RouteDTO> t) {
        super(t);
    }

    @Override
    public void serialize(RouteDTO route, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", "Feature");

        // Properties
        gen.writeObjectFieldStart("properties");
        gen.writeStringField("roadName", route.getRoadName());
        gen.writeNumberField("tci", route.getTci());
        gen.writeNumberField("tsi", route.getTsi());
        gen.writeNumberField("time", route.getTime());
        gen.writeNumberField("maxSpeed", route.getMaxSpeed());
        gen.writeNumberField("length", route.getLength());
        gen.writeEndObject();

        // Geometry
        gen.writeObjectFieldStart("geometry");
        gen.writeStringField("type", "MultiLineString");

        gen.writeArrayFieldStart("coordinates");
        for (int i = 0; i < route.getGeometry().getNumGeometries(); i++) {
            gen.writeStartArray();
            Coordinate[] coordinates = route.getGeometry().getGeometryN(i).getCoordinates();
            for (Coordinate coordinate : coordinates) {
                gen.writeArray(new double[]{coordinate.x, coordinate.y}, 0, 2);
            }
            gen.writeEndArray();
        }
        gen.writeEndArray();

        gen.writeEndObject();
        gen.writeEndObject();
    }
}