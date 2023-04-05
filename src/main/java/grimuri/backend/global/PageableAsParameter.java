package grimuri.backend.global;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
        in = ParameterIn.QUERY,
        description = "0부터 시작하는 페이지 인덱스",
        name = "page",
        schema = @Schema(type = "integer", defaultValue = "0")
)
@Parameter(
        in = ParameterIn.QUERY,
        description = "페이지 하나에 나타낼 항목의 최대 크기",
        name = "size",
        schema = @Schema(type = "integer", defaultValue = "10")
)
@Parameter(
        in = ParameterIn.QUERY,
        description = "정렬 기준과 오름차순/내림차순.\n" +
                "Diary에 있는 항목 중 id를 기준으로 오름차순 정렬을 한다면, \"id,ASC\"와 같이 적는다.\n" +
                "만약 여러 항목을 기준으로 한다면, 아이템을 더 추가해서 순서대로 적는다.",
        name = "sort",
        array = @ArraySchema(schema = @Schema(type = "String", defaultValue = "", example = "id,ASC"))
)
public @interface PageableAsParameter {
}
