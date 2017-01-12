package org.fl.noodlenotify.core.status;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;

public abstract class AbstractStatusChecker implements StatusChecker, MethodInterceptor {

	//private final static Logger logger = LoggerFactory.getLogger(AbstractStatusChecker.class);
	
	protected long connectId;

	protected String ip;
	protected int port;
	protected String url;
	protected String type;
	
	protected int connectTimeout;
	protected int readTimeout;
	
	protected String encoding;
	
	private Object proxy;
	
	public AbstractStatusChecker(long connectId, String ip, int port, String url, String type, int connectTimeout, int readTimeout, String encoding) {
		this.connectId = connectId;
		this.ip = ip;
		this.port = port;
		this.url = url;
		this.type = type;
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		this.encoding = encoding;
		
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.addInterface(getServiceInterfaces());
		proxyFactory.addAdvice(this);
		proxyFactory.setTarget(this);
		this.proxy = proxyFactory.getProxy();
	}
	
	protected abstract void connect() throws Exception;
	protected abstract void close();
	protected abstract Class<?> getServiceInterfaces();
	
	@Override
	public Object getProxy() {
		return proxy;
	}
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		connect();
		try {
			return invocation.proceed();
		} finally {
			close();
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (connectId ^ (connectId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractStatusChecker other = (AbstractStatusChecker) obj;
		if (connectId != other.connectId)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return new StringBuilder("AbstractConnectAgent [")
					.append("connectId:").append(connectId).append(",")
					.append("ip:").append(ip).append(",")
					.append("port:").append(port).append(",")
					.append("url:").append(url).append(",")
					.append("type:").append(type).append(",")
					.append("connectTimeout:").append(connectTimeout).append(",")
					.append("readTimeout:").append(readTimeout).append(",")
					.append("encoding:").append(encoding).append("]")
					.toString();
	}
}
