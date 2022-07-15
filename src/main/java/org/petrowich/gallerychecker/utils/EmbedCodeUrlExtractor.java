package org.petrowich.gallerychecker.utils;

import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

@Log4j2
public class EmbedCodeUrlExtractor {
    private EmbedCodeUrlExtractor() {
    }
    public static String extractUrlFromEmbedCode(String embedCode) {
        log.trace("extracting gallery url from embed code%n{}", embedCode);
        if (embedCode != null) {
            Document document = Jsoup.parse(embedCode);
            Elements iframe = document.select("iframe");
            String embedUrl = iframe.attr("src");
            if(embedUrl.startsWith("//")){
                embedUrl = "https:".concat(embedUrl);
            }
            log.trace("extracted gallery url: {}", embedUrl);
            return embedUrl;
        } else {
            log.error("embed code is null");
            return "";
        }
    }
}
