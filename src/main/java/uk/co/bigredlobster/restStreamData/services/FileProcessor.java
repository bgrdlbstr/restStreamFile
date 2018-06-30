package uk.co.bigredlobster.restStreamData.services;

import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;

@Component
class FileProcessor {

    private final String directoryPath;

    public FileProcessor() {
        try {
            final URL resource = FileProcessor.class.getClassLoader().getResource("data/data1.csv");
            if(resource == null)
                throw new RuntimeException("Failed to lookup resource via classloader");

            String path = "";
            try {
                final URI dataPath = resource.toURI();
                path = dataPath.getPath();
            } catch (URISyntaxException uri) {
                final File dataPathFile = new File(resource.getPath());
                path = dataPathFile.getPath();
            } finally {
                directoryPath = new File(path).getParent();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get resource path", e);
        }
    }

    void writeDataFromFiles(final Writer writer) {
        final Path dir = Paths.get(directoryPath);
        if (Files.notExists(dir)) {
            System.out.println("No files to process from ");
        }

        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                readFileExtensionWise(path, writer);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to writeDataFromFiles", e);
        }
    }

    private void readFileExtensionWise(final Path filePath, final Writer writer) {
        if (filePath.getFileName().toString().endsWith(".zip")) {
            try (final InputStream inputStream = readFromZipFile(filePath.toString())) {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String inpStr;
                while ((inpStr = reader.readLine()) != null) {
                    writer.write(inpStr + "\n");
                }

            } catch (Exception e) {
                throw new RuntimeException("Failed to read/write from zipFile: " + filePath.getFileName(), e);
            }
        } else {
            try (Stream<String> stream = Files.lines(filePath)) {
                stream.forEach((line) -> {
                    try {
                        writer.write(line + "\n");
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to read lines from file: " + filePath.getFileName(), e);
                    }
                });

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    private InputStream readFromZipFile(final String fileDir) {
        try {
            final ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(fileDir));
            zipInputStream.getNextEntry();
            return zipInputStream;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read from zip file: " + fileDir, e);
        }
    }
}