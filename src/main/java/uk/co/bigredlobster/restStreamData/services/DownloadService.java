package uk.co.bigredlobster.restStreamData.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class DownloadService {

    @Autowired
    FileProcessor fileProcessor;

    public StreamingResponseBody getSpringStream(String internalFileName) {
        return output -> writeToStream(output, internalFileName);
    }

    public void writeToStream(final OutputStream os, final String fileName) throws IOException {
        final ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(os));
        final ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);

        try (final Writer writer = new BufferedWriter(new OutputStreamWriter(zipOut, Charset.forName("UTF-8").newEncoder()))) {
            fileProcessor.writeDataFromFiles(writer);
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException("Failed to writeDataFromFiles", e);
        } finally {
            os.close();
        }
    }

}