package uk.co.bigredlobster.restStreamData.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.co.bigredlobster.restStreamData.services.DownloadService;

import javax.servlet.http.HttpServletResponse;

@RestController
public class StreamDataController {

    @Autowired
    DownloadService service;

    @RequestMapping(value = "/getDataFromFile", method = RequestMethod.GET)
    public StreamingResponseBody getStream(HttpServletResponse response) {
        response.addHeader("Content-disposition", "attachment;filename=data.zip");
        response.setContentType("application/octet-stream");
        response.setStatus(HttpServletResponse.SC_OK);
        return service.getSpringStream("data.csv");
    }

}
