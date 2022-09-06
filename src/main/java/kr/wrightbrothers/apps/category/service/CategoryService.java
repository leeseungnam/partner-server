package kr.wrightbrothers.apps.category.service;

import kr.wrightbrothers.apps.category.dto.CategoryListDto;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.category.query.Category.";

    public List<CategoryListDto> findCategoryList(String categoryGroup) {
        return dao.selectList(namespace + "findCategoryList", categoryGroup, PartnerKey.WBDataBase.Alias.Admin);
    }
}
