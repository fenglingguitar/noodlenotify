java -server -Xmx${noodlenotify.exchange.netty.jvm.xmx} -Xms${noodlenotify.exchange.netty.jvm.xms} -classpath "../lib/*" org.fl.noodlenotify.core.run.Main -e -f ../conf/noodlenotify-core-exchange-netty.xml -l ../conf/logback.exchange.netty.xml