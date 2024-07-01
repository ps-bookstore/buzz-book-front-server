package store.buzzbook.front.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import store.buzzbook.front.common.util.PageRequestInfo;

@Setter
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadOrdersRequest extends PageRequestInfo {
	private String loginId;
}
