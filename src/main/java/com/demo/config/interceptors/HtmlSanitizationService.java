package com.demo.config.interceptors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class HtmlSanitizationService {

  Logger log = LoggerFactory.getLogger(HtmlSanitizationService.class);

  public String sanitizeHtml(String data) {
    if (!StringUtils.hasText(data)) {
      return data; // Return early for empty or null input
    }

    try {
      Document document = Jsoup.parse(data);
      Map<Integer, String> aTagElements = new LinkedHashMap<>();
      int position = 1;

      for (Element a : document.select("a")) {
        aTagElements.put(position++, a.outerHtml());
        a.before("<p>" + (position - 1) + "</p>"); // Use position directly
        a.remove();
      }
      // @formatter:off
      String cleanHtml = Jsoup.clean(document.html(), Safelist.relaxed()
              .addTags("html", "head", "style", "meta", "body", "title", "a")
              .addAttributes(":all", "class", "style", "media", "http-equiv",
                  "content", "type", "align", "border", "cellpadding", "cellspacing", "href")
              .addAttributes(":html", "xmlnx")
              .removeTags("script"));
      // @formatter:on
      for (Map.Entry<Integer, String> entry : aTagElements.entrySet()) {
        cleanHtml = cleanHtml.replace("<p>" + entry.getKey() + "</p>", entry.getValue());
      }

      return cleanHtml;

    } catch (Exception e) {
      log.error("Exception occurred during HTML sanitization: {}", e.getMessage(), e);
      throw new RuntimeException("HTML sanitization failed", e);
    }
  }
}
