/*
 * Decompiled with CFR 0.152.
 */
package tools.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class PropertiesUtils {
    public static Properties load(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        BufferedReader buff = new BufferedReader(new InputStreamReader((InputStream)fis, StandardCharsets.UTF_8));
        Properties props = new Properties();
        props.load(buff);
        fis.close();
        buff.close();
        return props;
    }

    public static Properties[] load(List<File> files) throws IOException {
        Properties[] result = new Properties[files.size()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = PropertiesUtils.load(files.get(i));
        }
        return result;
    }

    public static Properties[] loadAllFromDirectory(String dir) throws IOException {
        return PropertiesUtils.loadAllFromDirectory(new File(dir));
    }

    public static Properties[] loadAllFromDirectory(File dir) throws IOException {
        return PropertiesUtils.load(PropertiesUtils.getAllPropertiesFiles(dir));
    }

    public static List<File> getAllPropertiesFiles(File dir) {
        try {
            return Files.list(dir.toPath()).map(Path::toFile).filter(it -> it.getName().endsWith(".properties")).collect(Collectors.toList());
        }
        catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}

