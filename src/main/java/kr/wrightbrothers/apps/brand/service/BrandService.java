package kr.wrightbrothers.apps.brand.service;

import kr.wrightbrothers.apps.brand.dto.BrandListDto;
import kr.wrightbrothers.apps.util.PartnerKey;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.brand.query.Brand.";

    public List<BrandListDto> findBrandList() {
        return dao.selectList(namespace + "findBrandList", null, PartnerKey.WBDataBase.Alias.Admin);
    }
}
