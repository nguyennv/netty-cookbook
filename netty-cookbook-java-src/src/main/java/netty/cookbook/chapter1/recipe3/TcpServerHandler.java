package netty.cookbook.chapter1.recipe3;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Handler implementation for the TCP server.
 */
@Sharable
public class TcpServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	System.out.println(msg);  	
        ctx.write("ok");
    }
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {    	
    	super.channelRegistered(ctx);
    	InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
    	System.out.println("channelRegistered "+ address.getAddress());
    	isCatchedException = false;
    }    
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {    	
    	super.channelUnregistered(ctx);
    	InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
    	System.out.println("channelUnregistered "+ address.getAddress());
    }    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {    	
    	super.channelActive(ctx);
    	System.out.println("channelActive "+ctx.channel());
    	ctx.channel().writeAndFlush("connected");
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {    	
    	super.channelInactive(ctx);
    	System.out.println("channelInactive "+ctx.channel().remoteAddress());
    }

    boolean isCatchedException = false;
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {    	
        ctx.flush();
        
        if( ! isCatchedException ){
        	//auto close the client connection after 500 mili-seconds
        	new Timer().schedule(new TimerTask() {			
     			@Override
     			public void run() {
     				ctx.channel().writeAndFlush("close");
     			}
     		}, 500);
        }
       
        
        //close the connection after flushing data to client
        //ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
