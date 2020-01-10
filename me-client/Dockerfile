FROM openjdk:8
ENV TZ=Asia/Shanghai
ENV ENV=dev
RUN mkdir -p /app/skywalking
WORKDIR /app
COPY target/me-client-0.0.1-SNAPSHOT.jar /app/
CMD java -javaagent:/app/skywalking/agent/skywalking-agent.jar -jar -Dskywalking.agent.service_name=me-client-${cluster} -Dapollo.cluster=${cluster} $JAVA_OPTS me-client-0.0.1-SNAPSHOT.jar
