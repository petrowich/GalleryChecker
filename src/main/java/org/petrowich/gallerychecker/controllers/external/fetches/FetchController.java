package org.petrowich.gallerychecker.controllers.external.fetches;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.dto.external.FetchDto;
import org.petrowich.gallerychecker.dto.external.FetchInvocationDto;
import org.petrowich.gallerychecker.mappers.external.FetchDtoMapper;
import org.petrowich.gallerychecker.mappers.external.FetchInvocationDtoMapper;
import org.petrowich.gallerychecker.models.fetches.Fetch;
import org.petrowich.gallerychecker.services.fetches.FetchService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
@RequestMapping("/external/fetches")
public class FetchController {
    private static final String ATTRIBUTE_ALL_FETCHES = "allFetches";
    private static final String ATTRIBUTE_FETCH_INVOCATIONS = "fetchInvocations";
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

    private final FetchService fetchService;
    private final FetchDtoMapper fetchDtoMapper;
    private final FetchInvocationDtoMapper fetchInvocationDtoMapper;

    public FetchController(FetchService fetchService,
                           FetchDtoMapper fetchDtoMapper,
                           FetchInvocationDtoMapper fetchInvocationDtoMapper) {
        this.fetchService = fetchService;
        this.fetchDtoMapper = fetchDtoMapper;
        this.fetchInvocationDtoMapper = fetchInvocationDtoMapper;
    }

    private static final Map<String, String> properties = new HashMap<>();

    @PostConstruct
    private void init() {
        properties.put("id", "id");
        properties.put("username", "userInfo.username");
        properties.put("host", "tube.host");
        properties.put("datetime", "fetchDateTime");
        properties.put("fetched", "fetchedGalleriesNumber");
        properties.put("aim", "fetchAim");
        properties.put("status", "status");
        properties.put("errorMessage", "errorMessage");
    }

    @GetMapping("")
    public String fetches(Model model, HttpServletRequest request, HttpServletResponse response) {
        log.debug("{} '{}' {} fetches() session:{} ",
                request.getMethod(),
                request.getRequestURI(),
                parametersToString(request),
                request.getSession().getId());

        Integer pageNumber = parseNumberOrDefault(request.getParameter(ATTRIBUTE_PAGE_NUMBER), 0);
        Integer pageSize = parseNumberOrDefault(request.getParameter(ATTRIBUTE_PAGE_SIZE), 10);
        String sortBy = getSortBy(request.getParameter(ATTRIBUTE_SORT_BY));
        boolean ascending = isSortOrderAscending(request.getParameter(ATTRIBUTE_SORT_ORDER));

        Page<Fetch> page;
        if (sortBy == null) {
            page = fetchService.findPage(pageNumber, pageSize, "id", false);
        } else {
            page = fetchService.findPage(pageNumber, pageSize, sortBy, ascending);
        }

        Collection<FetchDto> storedGalleryDtoList = page.get().map(fetchDtoMapper::toDto).collect(Collectors.toList());

        Integer recordsNumber = storedGalleryDtoList.size();
        if (recordsNumber < pageSize) {
            Collection<FetchDto> dummyFetchDtoList = IntStream.range(0, pageSize - recordsNumber)
                    .boxed().map(integer -> new FetchDto())
                    .collect(Collectors.toList());
            storedGalleryDtoList.addAll(dummyFetchDtoList);
        }

        model.addAttribute(ATTRIBUTE_ALL_FETCHES, storedGalleryDtoList);
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
        String template = "external/fetches/fetches";
        log.debug("return template {} {}", template, HTML_CONTENT_TYPE);
        return template;
    }

    @GetMapping("/invocations")
    public String fetchInvocationsModal(@RequestParam("fetchId") Integer fetchId,
                                        Model model,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        log.debug("{} '{}' fetchId={} fetchInvocationsModal() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                fetchId,
                request.getSession().getId());

        Fetch fetch = fetchService.findById(fetchId);
        if (fetch != null && fetch.getFetchInvocations() != null) {
            Collection<FetchInvocationDto> storedGalleryDtoList = fetch.getFetchInvocations().stream()
                    .map(fetchInvocationDtoMapper::toDto)
                    .collect(Collectors.toList());
            model.addAttribute(ATTRIBUTE_FETCH_INVOCATIONS, storedGalleryDtoList);
        }

        String template = "external/fetches/fetch-invocations :: fetch-invocations";
        response.setContentType(HTML_CONTENT_TYPE);
        log.debug("return template {} {}", template, HTML_CONTENT_TYPE);
        return template;
    }

    private Integer getLastPageNumber(Page<Fetch> page) {
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

    private List<Integer> pageNumbers(Page<Fetch> page) {
        int currentPage = page.getNumber();
        int totalPages = page.getTotalPages() - 1;
        return generatePageNumberList(currentPage, totalPages);
    }
}
