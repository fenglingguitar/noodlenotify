call cd D:\work\source-wowo\noodlenotify\noodlenotify
call mvn clean install -Denv=test -Dprofile.file.name=env.properties.dev
call mvn dependency:copy-dependencies -DoutputDirectory=D:\work\source-wowo\noodlenotify\noodlenotify\noodlenotify-console-web\src\main\webapp\WEB-INF\lib