FROM openjdk:8
ENV TZ=Asia/Shanghai
ENV ENV=dev
RUN mkdir -p /app/skywalking
WORKDIR /app
COPY target/me-server-0.0.1-SNAPSHOT.jar /app/
CMD java -javaagent:/app/skywalking/agent/skywalking-agent.jar -jar -Dskywalking.agent.service_name=me-server-${cluster} -Dapollo.cluster=${cluster} $JAVA_OPTS me-server-0.0.1-SNAPSHOT.jar
