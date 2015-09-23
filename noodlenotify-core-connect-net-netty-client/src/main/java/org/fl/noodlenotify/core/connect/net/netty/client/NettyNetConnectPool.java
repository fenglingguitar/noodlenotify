package org.fl.noodlenotify.core.connect.net.netty.client;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

import org.fl.noodlenotify.core.connect.net.netty.client.exception.NettyConnectionException;
import org.fl.noodlenotify.core.connect.net.netty.client.exception.NettyException;

public class NettyNetConnectPool {
	
	protected GenericObjectPool internalPool;
    
    public NettyNetConnectPool(Config poolConfig, String ip, int port, int timeout) {
    	this.internalPool = new GenericObjectPool(new NettyNetConnectFactory(ip, port, timeout), poolConfig);
    }
    
    public NettyNetConnect getResource() {
        try {
            return (NettyNetConnect) internalPool.borrowObject();
        } catch (Exception e) {
            throw new NettyConnectionException("Could not get a resource from the pool", e);
        }
    }
        
    public void returnResource(Object resource) {
        try {
            internalPool.returnObject(resource);
        } catch (Exception e) {
            throw new NettyException("Could not return the resource to the pool", e);
        }
    }

    protected void returnBrokenResource(Object resource) {
        try {
            internalPool.invalidateObject(resource);
        } catch (Exception e) {
            throw new NettyException("Could not return the resource to the pool", e);
        }
    }

    public void destroy() {
        try {
        	internalPool.clear();
            internalPool.close();
        } catch (Exception e) {
            throw new NettyException("Could not destroy the pool", e);
        }
    }
    
    private static class NettyNetConnectFactory extends BasePoolableObjectFactory {
    	
        private final String host;
        private final int port;
        private final int timeout;

        public NettyNetConnectFactory(final String host, final int port, final int timeout) {
            super();
            this.host = host;
            this.port = port;
            this.timeout = timeout;
        }

        public Object makeObject() throws Exception {
        	
            final NettyNetConnect nettyNetConnect = new NettyNetConnect(this.host, this.port, this.timeout);
            nettyNetConnect.connect();
            return nettyNetConnect;
        }

        public void destroyObject(final Object obj) throws Exception {
            if (obj instanceof NettyNetConnect) {
                final NettyNetConnect nettyNetConnect = (NettyNetConnect) obj;
                if (nettyNetConnect.isConnected()) {
                    try {
                    	nettyNetConnect.close();
                    } catch (Exception e) {
                    	
                    }
                }
            }
        }

        public boolean validateObject(final Object obj) {
            if (obj instanceof NettyNetConnect) {
                final NettyNetConnect nettyNetConnect = (NettyNetConnect) obj;
                try {
                    return nettyNetConnect.isConnected();
                } catch (final Exception e) {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}
