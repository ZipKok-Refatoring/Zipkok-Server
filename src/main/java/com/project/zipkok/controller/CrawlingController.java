package com.project.zipkok.controller;

import com.project.zipkok.util.HtmlCrawling;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

//@Slf4j
//@RestController
//@RequestMapping("/crawling")
//@RequiredArgsConstructor
//@Tag(name = "Crawling API", description = "매물 크롤링 API : 크롤링을 위한 api입니다. 사용하지 말아주세요 !!")
//public class CrawlingController {
//
//    private final HtmlCrawling htmlCrawling;
//    @GetMapping("")
//    @Operation(summary = "크롤링을 위한 api 입니다. 사용하지 말아주세요!")
//    public void crawling(@RequestParam("uri") String uri) throws IOException, InterruptedException {
//        log.info("[AddressController.searchAddress]");
//
//        htmlCrawling.parsingHTML(uri);
//
//    }
//}
