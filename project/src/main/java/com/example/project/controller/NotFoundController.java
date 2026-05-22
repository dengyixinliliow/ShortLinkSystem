package com.example.project.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotFoundController {

    @GetMapping(value = "/not-found", produces = MediaType.TEXT_HTML_VALUE)
    public String notFound() {
        return """
                <!doctype html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Short Link Not Found</title>
                    <style>
                        body {
                            margin: 0;
                            min-height: 100vh;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            font-family: Arial, Helvetica, sans-serif;
                            color: #1f2937;
                            background: #f8fafc;
                        }
                        main {
                            max-width: 460px;
                            padding: 32px;
                            text-align: center;
                        }
                        h1 {
                            margin: 0 0 12px;
                            font-size: 28px;
                            font-weight: 700;
                        }
                        p {
                            margin: 0;
                            color: #64748b;
                            line-height: 1.6;
                        }
                    </style>
                </head>
                <body>
                    <main>
                        <h1>Short link not found</h1>
                        <p>The link may be expired, disabled, or unavailable.</p>
                    </main>
                </body>
                </html>
                """;
    }
}
