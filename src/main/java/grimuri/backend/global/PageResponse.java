package grimuri.backend.global;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Schema(description = "페이지 정보를 포함하는 응답")
@Data
public class PageResponse<T> {

    @Schema(description = "페이지 정보")
    private Pageable pageable;

    @Schema(description = "전체 페이지 개수")
    private int totalPages;

    @Schema(description = "전체 데이터 개수")
    private long totalElements;

    @Schema(description = "현재 페이지에 포함된 데이터 리스트")
    private List<T> content;
}
