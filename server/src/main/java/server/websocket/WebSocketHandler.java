package server.websocket;

import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    // TODO: have some sort of connection set up; track sessions in a map related to the logged in user (possibly through auth tokens)

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) { // TODO: fill this in
        // try {
        //     Action action = new Gson().fromJson(ctx.message(), Action.class);
        //     switch (action.type()) {
        //         case ENTER -> enter(action.visitorName(), ctx.session);
        //         case EXIT -> exit(action.visitorName(), ctx.session);
        //     }
        // } catch (IOException ex) {
        //     ex.printStackTrace();
        // }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }
}
