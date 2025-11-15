package com.portfolio.aips.project;


import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;


import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.http.client.*;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RestTemplateLoggingTest {


   /* @Test
    void testGeminiShareLinkRedirect() {
        // 리다이렉트 자동 추적 비활성화
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setInstanceFollowRedirects(false); // 중요!

        RestTemplate restTemplate = new RestTemplate(factory);

        String geminiShareUrl = "https://g.co/gemini/share/xxxxx"; // 실제 share 링크

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    geminiShareUrl, String.class);

            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Redirect URL: " + response.getHeaders().getLocation());

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("Error Status: " + e.getStatusCode());
            System.out.println("Redirect URL: " + e.getResponseHeaders().getLocation());
        }
    }
*/

    @Test
    void restTemplate_logging_test() {
        // 🔥 Response body 읽기 위해 BufferingClientHttpRequestFactory 필요
        RestTemplate rest = new RestTemplate(
                new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory())
        );

        // 🔥 Interceptor 등록: Request/Response Body Logging
        rest.getInterceptors().add((request, body, execution) -> {

            System.out.println("\n===== 📤 REQUEST =====");
            System.out.println("URI     : " + request.getURI());
            System.out.println("Method  : " + request.getMethod());
            System.out.println("Headers : " + request.getHeaders());
            System.out.println("Body    : " + new String(body));

            ClientHttpResponse response = execution.execute(request, body);

            System.out.println("\n===== 📥 RESPONSE =====");
            System.out.println("Status  : " + response.getStatusCode());
            System.out.println("Headers : " + response.getHeaders());
            String responseBody = new String(response.getBody().readAllBytes());
            System.out.println("Body    : " + responseBody + "\n");

            return response;
        });

        // 테스트용 Body
        String requestJson = """
                {
                    "message": "hello",
                    "value": 123
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        // POST 요청 테스트 (postman-echo 등 원하는 테스트 URL로 바꿔도 됨)
        String url = "https://gemini.google.com/share/c8689a38a601";

        // 🔥 실제 요청
        ResponseEntity<String> response = rest.getForEntity(url,  String.class);

        // 테스트 확인 (body가 null이면 실패)
        System.out.println(response.getBody());

        String body = response.getBody();

     /*   assertThat(body).contains("<meta property=\"og:title\" content=\"Claude\" />");*/

    }


    @Test
    void check_single_url() {
        String url = "https://claude.ai/share/ccead4c2-d3f6-461d-ac22-e768450c1a16";

        if (isValidClaudeUrl(url)) {
            System.out.println("✅ Valid Claude share URL");
        } else {
            System.out.println("❌ Invalid Claude share URL");
        }
    }

    // 🔥 Claude URL 유효성 체크 (Body 내용 확인)
    private boolean isValidClaudeUrl(String url) {
        try {
            RestTemplate rest = createRestTemplateWithBrotliSupport();

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = rest.exchange(url, HttpMethod.GET, entity, String.class);
            String body = response.getBody();



            if (body == null || body.isEmpty()) {
                System.out.println("  ⚠️  Empty response body");
                return false;
            }

            assertThat(body.contains("<title>Claude | Claude</title>")).isEqualTo(true);

            return true;

        } catch (Exception e) {
            System.out.println("  ❌ Error: " + e.getMessage());
            return false;
        }
    }

    // 🔥 Brotli 압축 해제를 지원하는 RestTemplate 생성
    private RestTemplate createRestTemplateWithBrotliSupport() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((ClientHttpRequestInterceptor) new BrotliDecompressionInterceptor());
        return restTemplate;
    }

    // 🔥 Brotli 압축 해제 인터셉터
    private static class BrotliDecompressionInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(
                org.springframework.http.HttpRequest request,
                byte[] body,
                org.springframework.http.client.ClientHttpRequestExecution execution
        ) throws IOException {

            ClientHttpResponse response = execution.execute(request, body);
            String contentEncoding = response.getHeaders().getFirst("Content-Encoding");

            if ("br".equalsIgnoreCase(contentEncoding)) {
                return new BrotliDecompressedResponse(response);
            }

            return response;
        }
    }

    // 🔥 Brotli 압축 해제된 응답
    private static class BrotliDecompressedResponse implements ClientHttpResponse {
        private final ClientHttpResponse original;
        private byte[] decompressedBody;

        public BrotliDecompressedResponse(ClientHttpResponse original) throws IOException {
            this.original = original;

            try {
                byte[] compressed = original.getBody().readAllBytes();

                // 🔥 Brotli 압축 해제 (org.brotli:dec 라이브러리 필요)
                Class<?> brotliClass = Class.forName("org.brotli.dec.BrotliInputStream");
                Object brotliStream = brotliClass
                        .getConstructor(java.io.InputStream.class)
                        .newInstance(new ByteArrayInputStream(compressed));

                java.io.InputStream stream = (java.io.InputStream) brotliStream;
                this.decompressedBody = stream.readAllBytes();
                stream.close();

            } catch (ClassNotFoundException e) {
                // Brotli 라이브러리가 없을 때
                System.err.println("\n❌ Brotli library not found!");
                System.err.println("Add to build.gradle:");
                System.err.println("  dependencies {");
                System.err.println("    implementation 'org.brotli:dec:0.1.2'");
                System.err.println("  }\n");
                throw new IOException("Brotli decompression library not available", e);

            } catch (Exception e) {
                throw new IOException("Failed to decompress Brotli response", e);
            }
        }

        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return null;
        }

        @Override
        public String getStatusText() throws IOException {
            return "";
        }

        @Override
        public void close() {

        }

        @Override
        public InputStream getBody() throws IOException {
            return null;
        }

        @Override
        public HttpHeaders getHeaders() {
            return null;
        }
    }
    }
