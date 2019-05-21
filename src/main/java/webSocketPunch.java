import spark.Request;

import static spark.Spark.*;

public class webSocketPunch {

    public static void main(String[] arg){
        final int pnum = utils.getPortFromKeyboard();
        port(pnum);
        startWebService();
        utils.openBrowser("http://localhost:"+pnum);
    }

    private static void startWebService(){

        after((req, res) -> {
            res.type("application/json");
        });

        //main page ui
        get("/", (req, res) -> {
            return "main page of websocketpunch";
        });

        //start punch, mode/socket/buffer size as query param
        get("/punch", (req, res)->{
            preprocess(req);
            return "main page of websocketpunch";
        });

        //add to queue
        post("/queue",(req, res)->{
            return "main page of websocketpunch";
        });

        // delete queue item
        delete("/queue/:index", (req,res)->{
            preprocess(req);
            return "main page of websocketpunch";
        });

        // item detail
        get("/queue/:index", (req,res)->{
            preprocess(req);
            return req.params("index");
        });

        // item status
        get("/queue/:index/status", (req,res)->{
            preprocess(req);
            return "main page of websocketpunch";
        });

        // transmission progress
        get("/queue/:index/progress", (req,res)->{
            preprocess(req);
            return "main page of websocketpunch";
        });

        //empty queue
        delete("/queue", (req,res)->{
            return "main page of websocketpunch";
        });


        //update config
        put("/config", (req,res)->{
            return "main page of websocketpunch";
        });
    }

    private static void preprocess(Request req){
        utils.printReqDetails(req);
    }



}
