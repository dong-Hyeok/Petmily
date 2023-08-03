package com.pjt.petmily.domain.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pjt.petmily.domain.product.dto.ProductSearchDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    String clientId = "FoDAdUrsT16Nt13CAgz5";
    String clientSecret = "4_naXjLmJ3";


    // 상품검색
    public List<ProductSearchDto> productSearch(String keyword) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/shop.json")
                .queryParam("query", "반려동물" + keyword)
                .queryParam("display", 50)
                .queryParam("start", 1)
                .queryParam("sort", "sim")
                .encode()
                .build()
                .toUri();
        RequestEntity<Void> req = RequestEntity
                .get(uri)
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .build();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(req, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();

            // JSON 응답을 DTO 리스트로 파싱합니다.
            ObjectMapper mapper = new ObjectMapper();
            List<ProductSearchDto> productList = new ArrayList<>();

            try {
                JsonNode rootNode = mapper.readTree(responseBody);
                JsonNode itemsNode = rootNode.path("items");

                for (JsonNode itemNode : itemsNode) {
                    String title = itemNode.path("title").asText();
                    String lprice = itemNode.path("lprice").asText();
                    String link = itemNode.path("link").asText();
                    String category2 = itemNode.path("category2").asText();
                    String image = itemNode.path("image").asText();

                    ProductSearchDto product = new ProductSearchDto(title, lprice, link, category2, image);
                    productList.add(product);
                }
            } catch (JsonProcessingException e) {
                // JSON 파싱 오류 처리
                throw new RuntimeException("JSON 파싱 오류: " + e.getMessage());
            }

            return productList;
        } else {
            // API 호출이 실패한 경우 예외 처리를 합니다.
            throw new RuntimeException("API 호출이 실패하였습니다. 상태 코드: " + response.getStatusCodeValue());
        }


    }

}
