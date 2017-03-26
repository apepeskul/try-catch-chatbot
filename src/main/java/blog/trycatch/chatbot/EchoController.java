package blog.trycatch.chatbot;


import com.google.gson.Gson;
import io.swagger.client.ApiException;
import io.swagger.client.model.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EchoController {

    @Autowired
    private MessageProcessorComponent messageProcessorComponent;


    @RequestMapping(value = "/messages", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseEntity processMessage(@RequestBody String requestString) throws ApiException {
        Gson gson = new Gson();
        Activity activity = gson.fromJson(requestString, Activity.class);
        if (activity.getType().equals("message")) {
            messageProcessorComponent.enqueueMessage(activity);
        } else {
            System.out.println("I don't understand messages of type: " + activity.getType());
        }
        return ResponseEntity.ok().build();
    }
}
