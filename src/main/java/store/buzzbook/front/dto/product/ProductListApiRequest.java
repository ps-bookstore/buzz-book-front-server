package store.buzzbook.front.dto.product;

import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class ProductListApiRequest {
	int id;
	int stock;
	int price;
	ZonedDateTime forwardDate;
	int score;
	String thumbnailPath;
	int categoryId;
	String productName;
	String description;
	String stockStatus;
}
