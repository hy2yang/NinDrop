import spark.Request;

import static spark.Spark.*;

public class webSocketPunch {

    public static void main(String[] arg){
        before((req, res) -> {
            System.out.println(reqDetails(req));
        });

        get("/ui", (req, res) -> "Hello World!");

        get("/punch", (req, res)->{

        });

        post("/queue",(req, res)->{

        });

        delete("/queue/:index", (req,res)->{

        });

        put("config", (req,res)->{

        });
    }

    public static String reqDetails(Request req){
        req.
    }
}
