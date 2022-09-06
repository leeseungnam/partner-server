package kr.wrightbrothers.apps.brand.service;

import kr.wrightbrothers.apps.brand.dto.ModelListDto;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.brand.query.Model.";

    public List<ModelListDto> findModelList(String brandNo) {
        return dao.selectList(namespace + "findModelList", brandNo, PartnerKey.WBDataBase.Alias.Admin);
    }
}
