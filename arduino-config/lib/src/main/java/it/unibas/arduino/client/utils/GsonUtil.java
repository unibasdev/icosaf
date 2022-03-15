package it.unibas.arduino.client.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class GsonUtil {

    private static String datePatternFormat = "yyyyMMdd'T'HH:mm:ss"; //20220128T16:59:22

    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new AdapterDate());
        return builder.create();
    }

    private static class AdapterDate implements JsonSerializer<Date>, JsonDeserializer<Date> {

        @Override
        public JsonElement serialize(Date date, Type tipo, JsonSerializationContext context) {
            DateFormat dateFormat = new SimpleDateFormat(datePatternFormat);
            return new JsonPrimitive(dateFormat.format(date.getTime()));
        }

        @Override
        public Date deserialize(JsonElement json, Type tipo, JsonDeserializationContext context) throws JsonParseException {
            try {
                String stringaData = json.getAsString();
                DateFormat dateFormat = new SimpleDateFormat(datePatternFormat);
                Date dataRegistrazione = dateFormat.parse(stringaData);
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(dataRegistrazione);
                return calendar.getTime();
            } catch (ParseException ex) {
                throw new JsonParseException(ex);
            }
        }
    }

}
