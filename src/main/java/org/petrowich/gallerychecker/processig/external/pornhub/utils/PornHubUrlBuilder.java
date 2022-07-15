package org.petrowich.gallerychecker.processig.external.pornhub.utils;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Component
public class PornHubUrlBuilder {
    private static final String URL = "https://www.pornhub.com/webmasters/dump_output?";
    private static final String COUNT = "count=100";
    private static final String SIZE = "size=320x240";
    private static final String RATING = "rating=All";
    private static final String DELIMITER = "delimeter=%7C";
    private static final String FIELDS = "fields=embed%2Curl%2Ccategories%2Ctitle%2Ctags%2Cduration%2Cpornstars%2Cthumbnail";
    private static final String PERIOD = "period=yesterday";
    private static final String ORDER = "order=tr";
    private static final String FORMAT = "format=csv";
    private static final String UTM_SOURCE = "utm_source=paid";
    private static final String UTM_MEDIUM = "utm_medium=hubtraffic";
    private static final String UTM_CAMPAIGN = "utm_campaign=hubtraffic_petrowich";
    private static final List<String> categoryCodes = new ArrayList<>();

    public List<String> buildUrls() {
        return categoryCodes.stream()
                .map(this::buildUrl)
                .collect(Collectors.toList());
    }

    private String buildUrl(String codes){
        StringJoiner stringJoiner = new StringJoiner("&");
        stringJoiner.add(String.format("%scategories=%s",URL,codes));
        stringJoiner.add(COUNT);
        stringJoiner.add(SIZE);
        stringJoiner.add(RATING);
        stringJoiner.add(DELIMITER);
        stringJoiner.add(FIELDS);
        stringJoiner.add(PERIOD);
        stringJoiner.add(ORDER);
        stringJoiner.add(FORMAT);
        stringJoiner.add(UTM_SOURCE);
        stringJoiner.add(UTM_MEDIUM);
        stringJoiner.add(UTM_CAMPAIGN);
        return stringJoiner.toString();
    }

    @PostConstruct
    private void fillCategoryCodes() {
        categoryCodes.add("1%2C2%2C3");
        categoryCodes.add("4%2C5%2C6");
        categoryCodes.add("7%2C8%2C9%2C10");
        categoryCodes.add("11%2C12");
        categoryCodes.add("13%2C14%2C15");
        categoryCodes.add("16%2C17%2C18%2C19%2C20");
        categoryCodes.add("21%2C22%2C23%2C24%2C25");
        categoryCodes.add("26%2C27%2C28%2C29%2C30");
        categoryCodes.add("31%2C32%2C33%2C34%2C35");
        categoryCodes.add("36%2C37%2C38%2C39%2C40%2C41");
        categoryCodes.add("42%2C43%2C44%2C45%2C46%2C47%2C48%2C49%2C50%2C51");
        categoryCodes.add("52%2C53%2C54%2C55%2C56%2C57%2C58%2C59%2C60%2C61");
        categoryCodes.add("62%2C63%2C64%2C65%2C66%2C67%2C68%2C69%2C70%2C71");
        categoryCodes.add("72%2C73%2C76%2C77%2C78%2C79%2C80%2C81%2C82%2C83");
        categoryCodes.add("84%2C85%2C86%2C88%2C89%2C90%2C91%2C92%2C93%2C94");
        categoryCodes.add("95%2C96%2C97%2C98%2C99%2C100%2C101%2C102%2C103%2C104");
        categoryCodes.add("105%2C106%2C107%2C108%2C111%2C115%2C121%2C131%2C138%2C139");
        categoryCodes.add("141%2C181%2C201%2C211%2C221%2C231%2C241%2C242%2C252%2C262");
        categoryCodes.add("272%2C312%2C322%2C332%2C342%2C352%2C362%2C372%2C382%2C392");
        categoryCodes.add("402%2C412%2C422%2C444%2C482%2C492%2C502%2C512%2C522%2C532");
        categoryCodes.add("542%2C552%2C562%2C572%2C582%2C592%2C602%2C612%2C622%2C632");
        categoryCodes.add("642%2C682%2C702%2C712%2C722%2C731%2C732%2C742%2C761%2C771");
    }
}
