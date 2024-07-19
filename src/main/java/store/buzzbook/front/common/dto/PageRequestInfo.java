package store.buzzbook.front.common.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PageRequestInfo {
    private Integer page;
    private Integer size;
}
