package kr.wrightbrothers.apps.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.session.RowBounds;

@Data
@SuperBuilder
@AllArgsConstructor
public class AbstractPageDto {
    private int count;              // 조회 로우
    private int page;               // 현재 페이지
    private int totalItems;         // 전체 등록건 수
    private int rOne;               // 시작 번호
    private int rTwo;               // 종료 번호

    public RowBounds getRowBounds() {
        if (getPage() == 0) return null;

        int offset = (getPage() - 1) * getCount();
        setROne(offset);
        setRTwo(getCount());

        return new RowBounds(offset, getCount());
    }
}
