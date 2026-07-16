package com.example.demo.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@Component
@Slf4j
public class FileSystemTool {
    @McpTool(
            name = "get-directory",
            title = "디렉토리 목록을 조회한다",
            description = "매개변수로 전달된 디렉토리의 파일 목록을 조회한다"
    )
    public List<String> listFiles(
            @McpToolParam(description = "디렉터리") String directory)
            throws IOException {

        try (Stream<Path> stream = Files.list(Path.of(directory))) {
            return stream
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .toList();
        }
    }

    @McpTool(
            name = "read-text-file",
            title = "텍스트 파일을 읽는다",
            description = "지정한 파일 경로의 텍스트 파일을 읽는다."
    )
    public String readTextFile(
            @McpToolParam(description = "파일 경로") String path)
            throws IOException {

        return Files.readString(Path.of(path));
    }

    @McpTool(
            name = "write-text-file",
            title = "텍스트 파일을 저장한다",
            description = "지정한 경로에 텍스트 파일을 저장하거나 기존 내용을 덮어쓴다."
    )
    public String writeTextFile(
            @McpToolParam(description = "파일 경로") String path,
            @McpToolParam(description = "저장할 텍스트 내용") String content)
            throws IOException {

        Path filePath = Path.of(path);

        // 상위 디렉터리가 없으면 생성
        if (filePath.getParent() != null) {
            Files.createDirectories(filePath.getParent());
        }

        Files.writeString(filePath, content);

        return "파일 저장 완료: " + filePath.toAbsolutePath();
    }
}
