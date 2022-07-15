package org.petrowich.gallerychecker.controllers.stored.checks;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.dto.stored.CheckDto;
import org.petrowich.gallerychecker.mappers.stored.CheckDtoMapper;
import org.petrowich.gallerychecker.models.checks.Check;
import org.petrowich.gallerychecker.services.checks.CheckService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.petrowich.gallerychecker.utils.HttpRequestHelper.parametersToString;
import static org.petrowich.gallerychecker.utils.HttpRequestHelper.parseNumberOrDefault;
import static org.petrowich.gallerychecker.utils.PageNumberListGenerator.generatePageNumberList;

@Log4j2
@Controller
@RequestMapping("/stored/checks")
public class CheckController {
    private static final String ATTRIBUTE_ALL_CHECKS = "allChecks";
    private static final String ATTRIBUTE_PAGE_NUMBER = "page";
    private static final String ATTRIBUTE_PAGE_SIZE = "size";
    private static final String ATTRIBUTE_SORT_BY = "sortBy";
    private static final String ATTRIBUTE_SORT_ORDER = "order";
    private static final String ATTRIBUTE_PAGES_NUMBERS = "pages";
    private static final String ATTRIBUTE_FIRST_PAGE = "firstPage";
    private static final String ATTRIBUTE_PREVIOUS_PAGE_NUMBER = "previousPage";
    private static final String ATTRIBUTE_CURRENT_PAGE_NUMBER = "currentPage";
    private static final String ATTRIBUTE_NEXT_PAGE_NUMBER = "nextPage";
    private static final String ATTRIBUTE_LAST_PAGE = "lastPage";
    private static final String HTML_CONTENT_TYPE = "text/html; charset=UTF-8";

    private final CheckService checkService;
    private final CheckDtoMapper checkDtoMapper;

    private static final Map<String, String> properties = new HashMap<>();

    public CheckController(CheckService checkService, CheckDtoMapper checkDtoMapper) {
        this.checkService = checkService;
        this.checkDtoMapper = checkDtoMapper;
    }

    @PostConstruct
    private void init() {
        properties.put("id", "id");
        properties.put("username", "userInfo.username");
        properties.put("site", "site.name");
        properties.put("datetime", "checkDateTime");
        properties.put("checked", "checkedGalleriesNumber");
        properties.put("status", "status");
        properties.put("errorMessage", "errorMessage");
    }

    @GetMapping("")
    public String checks(Model model, HttpServletRequest request, HttpServletResponse response) {
        log.debug("{} '{}' {} checks() session:{} ",
                request.getMethod(),
                request.getRequestURI(),
                parametersToString(request),
                request.getSession().getId());

        Integer pageNumber = parseNumberOrDefault(request.getParameter(ATTRIBUTE_PAGE_NUMBER), 0);
        Integer pageSize = parseNumberOrDefault(request.getParameter(ATTRIBUTE_PAGE_SIZE), 10);
        String sortBy = getSortBy(request.getParameter(ATTRIBUTE_SORT_BY));
        boolean ascending = isSortOrderAscending(request.getParameter(ATTRIBUTE_SORT_ORDER));

        Page<Check> page;
        if (sortBy == null) {
            page = checkService.findPage(pageNumber, pageSize, "id", false);
        } else {
            page = checkService.findPage(pageNumber, pageSize, sortBy, ascending);
        }

        Collection<CheckDto> storedGalleryDtoList = page.get().map(checkDtoMapper::toDto).collect(Collectors.toList());

        Integer recordsNumber = storedGalleryDtoList.size();
        if (recordsNumber < pageSize) {
            Collection<CheckDto> dummyFetchDtoList = IntStream.range(0, pageSize - recordsNumber)
                    .boxed().map(integer -> new CheckDto())
                    .collect(Collectors.toList());
            storedGalleryDtoList.addAll(dummyFetchDtoList);
        }

        model.addAttribute(ATTRIBUTE_ALL_CHECKS, storedGalleryDtoList);
        model.addAttribute(ATTRIBUTE_FIRST_PAGE, 0);
        model.addAttribute(ATTRIBUTE_PREVIOUS_PAGE_NUMBER, page.previousOrFirstPageable().getPageNumber());
        model.addAttribute(ATTRIBUTE_CURRENT_PAGE_NUMBER, page.getNumber());
        model.addAttribute(ATTRIBUTE_NEXT_PAGE_NUMBER, page.nextOrLastPageable().getPageNumber());
        model.addAttribute(ATTRIBUTE_LAST_PAGE, getLastPageNumber(page));
        model.addAttribute(ATTRIBUTE_PAGES_NUMBERS, pageNumbers(page));

        if (request.getParameter(ATTRIBUTE_SORT_BY) != null) {
            model.addAttribute(ATTRIBUTE_SORT_BY, request.getParameter(ATTRIBUTE_SORT_BY));
            model.addAttribute(ATTRIBUTE_SORT_ORDER, request.getParameter(ATTRIBUTE_SORT_ORDER));
        }

        response.setContentType(HTML_CONTENT_TYPE);
        String template = "stored/checks/checks";
        log.debug("return template {} {}", template, HTML_CONTENT_TYPE);
        return template;
    }

    private Integer getLastPageNumber(Page<Check> page) {
        if (page.getTotalPages()>0) {
            return page.getTotalPages() - 1;
        }
        return 0;
    }

    private String getSortBy(String parameter) {
        if (parameter != null) {
            return properties.get(parameter.toLowerCase());
        }
        return null;
    }

    private boolean isSortOrderAscending(String attributeValue) {
        if (attributeValue != null && attributeValue.length() >= 3) {
            return attributeValue.trim().substring(0, 3).equalsIgnoreCase("asc");
        }
        return false;
    }

    private List<Integer> pageNumbers(Page<Check> page) {
        int currentPage = page.getNumber();
        int totalPages = page.getTotalPages() - 1;
        return generatePageNumberList(currentPage, totalPages);
    }
}
