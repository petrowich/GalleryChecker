package org.petrowich.gallerychecker.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PageNumberListGenerator {

    private PageNumberListGenerator() {
    }

    public static List<Integer> generatePageNumberList(int currentPage, int totalPageNumber) {
        return generatePageNumberList(currentPage, 0, totalPageNumber, 1);
    }
    public static List<Integer> generatePageNumberList(int currentPage, int firstPageNumber, int lastPageNumber, int padding) {
        int previousPage = previousOffset(currentPage, firstPageNumber, lastPageNumber, padding);
        int nextPage = nextOffset(currentPage, firstPageNumber, lastPageNumber, padding);

        return IntStream.range(firstPageNumber, lastPageNumber + 1).boxed()
                .filter(integer -> integer >= previousPage && integer <= nextPage)
                .collect(Collectors.toList());
    }

    private static Integer previousOffset(int current, int leftLimit, int rightLimit, int padding) {
        int left = current - padding - leftLimit;
        int right = rightLimit - padding - current + 1;
        if (left > 0 && right > 0) {
            return current - padding;
        }
        if (left <= 0) {
            return leftLimit;
        }
        return rightLimit - padding * 2;
    }

    private static Integer nextOffset(int current, int leftLimit, int rightLimit, int padding) {
        int left = current - padding - leftLimit;
        int right = rightLimit - padding - current + 1;
        if (left > 0 && right > 0) {
            return current + padding;
        }
        if (right <= 0) {
            return rightLimit;
        }
        return leftLimit + padding * 2;
    }
}
