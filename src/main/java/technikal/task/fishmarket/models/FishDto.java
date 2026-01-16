package technikal.task.fishmarket.models;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FishDto {

	@NotEmpty(message = "{fish.name.empty}")
	private String name;
	@Min(0)
	private double price;
	@Size(max = 10, message = "{fish.images.max_size}")//обмеження на кількість файлів, обмеження на типи файлів
	private List<MultipartFile> imageFiles;
}
