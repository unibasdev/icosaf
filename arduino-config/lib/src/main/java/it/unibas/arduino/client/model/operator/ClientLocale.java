package it.unibas.arduino.client.model.operator;

import it.unibas.arduino.client.Constants;
import it.unibas.arduino.client.model.ClientConfiguration;
import it.unibas.arduino.client.model.Command;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientLocale implements IClient {

    private static Logger logger = LoggerFactory.getLogger(ClientLocale.class);

    private ClientConfiguration configuration;
    private BuildCommandString commandStringBuilder = new BuildCommandString();
    private ParseResponse responseParser = new ParseResponse();

    public ClientLocale(ClientConfiguration configuration) {
        this.configuration = configuration;
    }

    public void execute(Command command) throws CommandExecutionException {
        String commandString = commandStringBuilder.buildCommandString(command, configuration);
//        String urlString = "http://" + configuration.getDeviceID() + ".local" + commandString;
        String urlString = "http://" + configuration.getIpAddress() + commandString;
//        if (logger.isInfoEnabled()) logger.info("Command URL: " + urlString);
        InputStream response = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(Constants.TOTAL_TIMEOUT);
            response = new BufferedInputStream(urlConnection.getInputStream());
            handleResponse(response, command);
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage());
            throw new CommandExecutionException(ex);
        } finally {
            try {
                if (response != null) response.close();
                if (urlConnection != null) urlConnection.disconnect();
            } catch (Exception ex) {
            }
        }
    }

    private void handleResponse(InputStream response, Command command) throws IOException, CommandExecutionException {
        StringBuilder body = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            body.append(line).append("\n");
        }
        responseParser.parse(body.toString(), command);
    }

//    public void execute(Command command) throws CommandExecutionException {
//        String commandString = commandStringBuilder.buildCommandString(command, configuration);
////        String url = "http://" + configuration.getDeviceID() + ".local" + commandString;
//        String urlString = "http://" + configuration.getIpAddress() + commandString;
//        HttpGet request = new HttpGet(urlString);
//        RequestConfig requestConfig = RequestConfig.custom()
//                .setConnectionRequestTimeout(Constants.TOTAL_TIMEOUT)
//                .setConnectTimeout(Constants.TOTAL_TIMEOUT)
//                .setSocketTimeout(Constants.TOTAL_TIMEOUT)
//                .build();
//        request.setConfig(requestConfig);
//        CloseableHttpResponse response = null;
//        try {
//            response = httpClient.execute(request);
//            handleResponse(response, command);
//        } catch (Exception ex) {
//            logger.error(ex.getLocalizedMessage());
//            throw new CommandExecutionException(ex);
//        } finally {
//            try {
//                response.close();
//            } catch (Exception ex) {
//            }
//        }
//    }
//    private void handleResponse(InputStream response, Command command) throws CommandExecutionException, IOException {
//        int statusCode = response.getStatusLine().getStatusCode();
//        if (statusCode != 200) {
//            throw new CommandExecutionException("Invalid status code. " + statusCode);
//        }
//        String body = EntityUtils.toString(response.getEntity());
//        responseParser.parse(body, command);
//    }
    public void close() {
    }

}
