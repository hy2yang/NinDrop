import spark.Request;

import static spark.Spark.*;

public class WebSocketPunch {

    private static final PunchService punchService = new PunchService();

    public static void main(String[] arg){
        final int pnum = Utils.getPortFromKeyboard();
        port(pnum);
        startWebService();
        Utils.openBrowser("http://localhost:"+pnum);
    }

    private static void startWebService(){

        after((req, res) -> {
            res.type("application/json");
        });

        //main page ui
        get("/", (req, res) -> {
            //TODO
            return "main page of websocketpunch";
        });

        //start punch, mode/socket/buffer size as query param
        get("/punch", (req, res)->{
            preprocess(req);
            punchService.startAllInQueue();
            return "main page of websocketpunch";
        });

        //add to queue
        post("/queue",(req, res)->{
            preprocess(req);
            String romURL = req.queryParams("romURL");
            //Map<String, Object> bodyMap = Utils.getMapFromJSON(req.body());
            System.out.println(romURL);
            punchService.addToQueue(romURL);
            return "main page of websocketpunch";
        });

        get("/queue", (req,res)->{
            preprocess(req);

            return Utils.getJson(punchService.getAll());
        });

        // delete queue item
        delete("/queue/:index", (req,res)->{
            preprocess(req);
            punchService.deleteAt(Integer.parseInt(req.params("index")));
            return "main page of websocketpunch";
        });

        // item detail
        get("/queue/:index", (req,res)->{
            preprocess(req);
            PunchTask task = punchService.getInfoOf(Integer.parseInt(req.params("index")));
            return Utils.getJson(task);
        });

        // item status
        get("/queue/:index/status", (req,res)->{
            preprocess(req);
            //TODO
            //punchService.ge
            return "main page of websocketpunch";
        });

        // transmission progress
        get("/queue/:index/progress", (req,res)->{
            preprocess(req);
            //TODO
            return "main page of websocketpunch";
        });

        //empty queue
        delete("/queue", (req,res)->{
            punchService.emptyQueue();
            return "main page of websocketpunch";
        });


        //update config
        put("/config", (req,res)->{
            //TODO
            return "main page of websocketpunch";
        });
    }

    private static void preprocess(Request req){
        Utils.printReqDetails(req);
    }



}
