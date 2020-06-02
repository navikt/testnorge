package testutil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileReader {

    public static String fileToString(String filepath) throws IOException {
        InputStream fileStream = FileReader.class.getClassLoader().getResourceAsStream(filepath);
        assert fileStream != null;
        InputStreamReader streamReader = new InputStreamReader(fileStream);
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder buffer = new StringBuilder();
        String str;

        while((str = reader.readLine()) != null) {
            buffer.append(str);
        }

        return buffer.toString();
    }
}
