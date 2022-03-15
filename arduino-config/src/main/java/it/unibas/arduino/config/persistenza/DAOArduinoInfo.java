package it.unibas.arduino.config.persistenza;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unibas.arduino.config.modello.ArduinoInfo;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DAOArduinoInfo {

    public ArduinoInfo carica(String path) throws IOException, DAOException {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(new FileReader(path), ArduinoInfo.class);
    }

    public void salva(ArduinoInfo info, String path) throws IOException, DAOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        @Cleanup FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(path);
            gson.toJson(info, fileWriter);
        } catch (IOException ex) {
            logger.error("Impossibile salvare le informazioni {} sul file {}", info, path, ex);
            throw new DAOException(ex);
        }
    }
}
