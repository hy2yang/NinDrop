import Service.PunchService;
import Service.PunchTask;
import Utils.AppUtils;
import Utils.BackConfig;
import spark.Request;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class WebSocketPunch {

    private static final PunchService punchService = new PunchService();


    public static void main(String[] arg){
        //final int pnum = AppUtils.AppUtils.getPortFromKeyboard();
        //port(pnum);
        port(BackConfig.DEFAULT_PORT);
        System.out.println();
        startWebService();
        AppUtils.openBrowser("http://localhost:"+ BackConfig.DEFAULT_PORT);
    }

    private static void startWebService(){

        after((req, res) -> {
            res.type("application/json");
        });

        get("/shutdown", (req, res) -> {
            punchService.shutdown();
            System.exit(0);
            return null;
        });

        //main page ui
        get("/", (req, res) -> {
            //TODO
            return "main page of websocketpunch";
        });

        //start punch, mode/socket/buffer size as query param
        get("/punch", (req, res)->{
            preprocess(req);
            Map<String, String> pMap = new HashMap<>();
            pMap.put(BackConfig.KEY_3DS_IP, req.queryParams(BackConfig.KEY_3DS_IP));
            pMap.put(BackConfig.KEY_LEGACY, req.queryParams(BackConfig.KEY_LEGACY));
            pMap.put(BackConfig.KEY_BUFFER, req.queryParams(BackConfig.KEY_BUFFER));
            punchService.setParams(pMap);
            punchService.startAllInQueue();
            return "scheduled to start all ready tasks";
        });

        //add to queue
        post("/queue",(req, res)->{
            preprocess(req);
            String romURL = req.queryParams("romURL");
            punchService.addToQueue(romURL);
            return "file added to queue";
        });

        get("/queue", (req,res)->{
            preprocess(req);
            return AppUtils.getJson(punchService.getAllTasks());
        });

        // delete queue item
        delete("/queue/:index", (req,res)->{
            preprocess(req);
            punchService.deleteAt(Integer.parseInt(req.params(":index")));
            return AppUtils.getJson(punchService.getAllTasks());
        });

        // item detail
        get("/queue/:index", (req,res)->{
            preprocess(req);
            PunchTask task = punchService.getInfoOf(Integer.parseInt(req.params(":index")));
            return AppUtils.getJson(task);
        });

        // item status
        get("/queue/:index/status", (req,res)->{
            preprocess(req);
            //TODO
            return punchService.getStatusOf(Integer.parseInt(req.params(":index")));
        });

        // transmission progress
        get("/queue/:index/progress", (req,res)->{
            preprocess(req);
            //TODO
            return "return trnsmitting progress";
        });

        //empty queue
        delete("/queue", (req,res)->{
            punchService.emptyQueue();
            return AppUtils.getJson(punchService.getAllTasks());
        });


        //update config
        put("/config", (req,res)->{
            //TODO
            return "config update success";
        });
    }

    private static void preprocess(Request req){
        AppUtils.printReqDetails(req);
    }



}
