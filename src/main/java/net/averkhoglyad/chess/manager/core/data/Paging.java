package net.averkhoglyad.chess.manager.core.data;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Paging {

    private final int pageSize;
    private final int page;

}
