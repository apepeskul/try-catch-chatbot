package blog.trycatch.chatbot;


import com.google.gson.GsonBuilder;
import io.swagger.client.ApiException;
import io.swagger.client.model.Activity;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EchoController {

    private static final Logger logger = LoggerFactory.getLogger(EchoController.class);

    @Autowired
    private MessageProcessorComponent messageProcessorComponent;


    @RequestMapping(value = "/messages", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseEntity processMessage(@RequestBody String requestString) throws ApiException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter()).create();
        Activity activity = gsonBuilder.create().fromJson(requestString, Activity.class);
        if (activity.getType().equals("message")) {
            logger.info("Received message:\n" + requestString);
            messageProcessorComponent.enqueueMessage(activity);
        } else {
            logger.warn("I don't understand messages:\n" + requestString);
        }
        return ResponseEntity.ok().build();
    }
}
