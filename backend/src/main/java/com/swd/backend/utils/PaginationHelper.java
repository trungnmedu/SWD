package com.swd.backend.utils;

public class PaginationHelper {
    private int itemsPerPages;
    private int maxItems;

    public PaginationHelper(int itemsPerPages, int maxItems) {
        this.itemsPerPages = itemsPerPages;
        this.maxItems = maxItems;
    }

    public int getStartIndex(int page) {
        return (page - 1) * itemsPerPages;
    }

    public int getEndIndex(int page) {
        return getStartIndex(page) + itemsPerPages - 1 < maxItems ? getStartIndex(page) + itemsPerPages - 1 : maxItems - 1;
    }
}
