# 비동기 Appender 사용법

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <appender name="ASYNC_Console" class="ch.qos.logback.classic.AsyncAppender">
        <queueSize>1000</queueSize>
        <discardingThreshold>0</discardingThreshold>
        <appender-ref ref="Console"/>
    </appender>

    <appender name="ASYNC_RollingFile" class="ch.qos.logback.classic.AsyncAppender">
        <queueSize>500</queueSize>
        <discardingThreshold>0</discardingThreshold>
        <appender-ref ref="RollingFile"/>
    </appender>

    <appender name="Console" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <charset>UTF-8</charset>
            <pattern>
                %d{yyyy-MM-dd HH:mm:ss.SSS} | %t | %highlight(%-5p) | %cyan(%logger{36}) | %m%n
            </pattern>
        </encoder>
    </appender>

    <appender name="RollingFile" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>./logs/application.log</file>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <charset>UTF-8</charset>
            <pattern>
                %d{yyyy-MM-dd HH:mm:ss.SSS} | %t | %-5p | %logger{36} | %m%n
            </pattern>
        </encoder>

        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>
                ./logs/archived/application-%d{yyyy-MM-dd}.%i.log
            </fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
    </appender>

    <root level="info">
        <appender-ref ref="ASYNC_RollingFile"/>
        <appender-ref ref="ASYNC_Console"/>
    </root>

</configuration>
```
