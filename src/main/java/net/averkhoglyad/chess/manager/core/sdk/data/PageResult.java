package net.averkhoglyad.chess.manager.core.sdk.data;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private int currentPage;
    private int previousPage;
    private int nextPage;
    private int maxPerPage;
    private int nbPages;
    private int nbResults;
    private List<T> currentPageResults;

}
