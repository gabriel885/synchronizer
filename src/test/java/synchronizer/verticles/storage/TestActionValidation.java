package synchronizer.verticles.storage;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import synchronizer.models.actions.*;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestActionValidation {

    private Vertx vertx;

    public TestActionValidation(){
        this.vertx = Vertx.vertx();
    }

    /**
     * test action json schemes validation
     */
    @Test
    void testFileCreationOverride() {


        // create directory
        assertTrue(Action.isValid(new JsonObject(new CreateAction(Paths.get("/opt/dir"), true, Buffer.buffer()).toJson())));
        // create file
        assertTrue(Action.isValid(new JsonObject(new CreateAction(Paths.get("/opt/dir/new.txt"), false, Buffer.buffer()).toJson())));

        // delete directory
        assertTrue(Action.isValid(new JsonObject(new DeleteAction(Paths.get("/opt/dir"), true).toJson())));
        assertTrue(Action.isValid(new JsonObject(new DeleteAction(Paths.get("/opt/dir/something"), true).toJson())));

        // delete file
        assertTrue(Action.isValid(new JsonObject(new DeleteAction(Paths.get("/opt/dir/new.txt"), false).toJson())));
        assertTrue(Action.isValid(new JsonObject(new DeleteAction(Paths.get("/opt/dir/new.txt"), false).toJson())));

        // modify directory
        assertTrue(Action.isValid(new JsonObject(new ModifyAction(Paths.get("/opt/dir/something"), true, Buffer.buffer()).toJson())));

        // modify file
        assertTrue(Action.isValid(new JsonObject(new ModifyAction(Paths.get("/opt/dir/something/new.txt"), true, Buffer.buffer().appendString("updated content")).toJson())));

        // ack message
        assertTrue(Action.isValid(new JsonObject(new Ack().toJson())));
        // nack message
        assertTrue(Action.isValid(new JsonObject(new Nack().toJson())));

        // request directory
        assertTrue(Action.isValid(new JsonObject(new RequestAction("/opt/dir/dirToRequest", true).toJson())));

        // request file
        assertTrue(Action.isValid(new JsonObject(new RequestAction("/opt/dir/fileToRequest.txt", false).toJson())));

        // response file
        assertTrue(Action.isValid(new JsonObject(new ResponseAction(Paths.get("/opt/dir/fileToResponse.txt"), false, Buffer.buffer().appendString("requested file buffer")).toJson())));

        // test false cases
        assertFalse(Action.isValid(new JsonObject().put("ttype","something")));
        assertFalse(Action.isValid(new JsonObject().put("type","UNKNOWN_AT_ALL")));
        assertFalse(Action.isValid(null));
        assertFalse(Action.isValid(new JsonObject()));



    }


}
