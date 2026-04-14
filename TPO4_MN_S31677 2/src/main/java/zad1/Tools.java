/**
 *
 *  @author Mejza Nadia S31677
 *
 */

package zad1;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class Tools {
    public static Options createOptionsFromYaml(String fileName) throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = new FileInputStream(fileName)) {
            Map<String, Object> data = yaml.load(inputStream);
            String host = (String) data.get("host");
            int port = (int) data.get("port");
            boolean concurMode = (boolean) data.get("concurMode");
            boolean showSendRes = (boolean) data.get("showSendRes");
            Map<String, List<String>> clientsMap = (Map<String, List<String>>) data.get("clientsMap");
            return new Options(host, port, concurMode, showSendRes, clientsMap);
        }
    }
}