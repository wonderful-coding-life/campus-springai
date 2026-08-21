package com.example.demo.tool;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

@Component
public class SystemInfoTools {
    @McpTool(
            name = "get-os-information",
            title = "운영체제 정보 조회",
            description = "운영체제 정보를 조회한다."
    )
    public String getOsInfo() {
        return System.getProperty("os.name")
                + " "
                + System.getProperty("os.version");
    }

    @McpTool(
            name = "get-java-information",
            title = "Java 실행 환경 조회",
            description = "현재 애플리케이션이 실행 중인 Java와 JVM 정보를 조회합니다."
    )
    public JavaInfo getJavaInfo() {
        return new JavaInfo(
                System.getProperty("java.version"),
                System.getProperty("java.vendor"),
                System.getProperty("java.vm.name"),
                System.getProperty("java.vm.version")
        );
    }

    public record JavaInfo(
            String version,
            String vendor,
            String vmName,
            String vmVersion
    ) {}

    @McpTool(
            name = "get-system-resources",
            title = "시스템 리소스 조회",
            description = "사용 가능한 프로세서 수와 JVM 메모리 정보를 조회합니다."
    )
    public SystemResourceInfo getSystemResources() {
        Runtime runtime = Runtime.getRuntime();

        return new SystemResourceInfo(
                runtime.availableProcessors(),
                toMegabytes(runtime.maxMemory()),
                toMegabytes(runtime.totalMemory()),
                toMegabytes(runtime.freeMemory())
        );
    }

    private long toMegabytes(long bytes) {
        return bytes / 1024 / 1024;
    }

    public record SystemResourceInfo(
            int availableProcessors,
            long maxMemoryMb,
            long allocatedMemoryMb,
            long freeAllocatedMemoryMb
    ) {}
}
