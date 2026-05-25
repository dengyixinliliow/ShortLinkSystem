package com.example.project.controller;

import cn.hutool.core.util.StrUtil;
import com.example.project.common.convention.exception.ServiceException;
import com.example.project.common.convention.result.Result;
import com.example.project.common.convention.result.Results;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class PageTitleController {

    private static final Pattern TITLE_PATTERN = Pattern.compile(
            "<title[^>]*>(.*?)</title>",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    @GetMapping("/api/shortlink/v1/title")
    public Result<String> getTitleByUrl(@RequestParam("fullUrl") String fullUrl) {
        URI uri = parseHttpUri(fullUrl);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(5))
                .header("User-Agent", "Mozilla/5.0")
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return Results.success(parseTitle(response.body()));
        } catch (IOException ex) {
            throw new ServiceException("Failed to fetch page title");
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ServiceException("Failed to fetch page title");
        }
    }

    private URI parseHttpUri(String fullUrl) {
        if (StrUtil.isBlank(fullUrl)) {
            throw new ServiceException("Url cannot be null");
        }
        try {
            URI uri = new URI(fullUrl);
            String scheme = uri.getScheme();
            if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                throw new ServiceException("Only http and https urls are supported");
            }
            return uri;
        } catch (URISyntaxException ex) {
            throw new ServiceException("Invalid url");
        }
    }

    private String parseTitle(String html) {
        if (StrUtil.isBlank(html)) {
            return StrUtil.EMPTY;
        }
        Matcher matcher = TITLE_PATTERN.matcher(html);
        if (!matcher.find()) {
            return StrUtil.EMPTY;
        }
        return matcher.group(1)
                .replaceAll("\\s+", " ")
                .trim();
    }
}
