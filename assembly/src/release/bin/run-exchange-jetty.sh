java -server -Xmx${noodlenotify.exchange.jetty.jvm.xmx} -Xms${noodlenotify.exchange.jetty.jvm.xms} -classpath "../lib/*" org.fl.noodlenotify.core.run.Main -e -f ../conf/noodlenotify-core-exchange-jetty.xml -l ../conf/logback.exchange.jetty.xml > /dev/null 2>&1 &