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
    public static Options createOptionsFromYaml(String fileName) throws Exception {
        Yaml yaml = new Yaml();
        Map<String, Object> data;
        try (InputStream in = new FileInputStream(fileName)) {
            data = yaml.load(in);
        }

        String host = (String) data.get("host");
        int port = (Integer) data.get("port");
        boolean concurMode = (Boolean) data.get("concurMode");
        boolean showSendRes = (Boolean) data.get("showSendRes");
        Map<String, List<String>> clientsMap = (Map<String, List<String>>) data.get("clientsMap");

        return new Options(host, port, concurMode, showSendRes, clientsMap);
    }
}