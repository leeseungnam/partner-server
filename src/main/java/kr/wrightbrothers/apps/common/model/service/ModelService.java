package kr.wrightbrothers.apps.common.model.service;

import kr.wrightbrothers.apps.common.model.dto.ModelListDto;
import kr.wrightbrothers.apps.util.PartnerKey;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.common.model.query.Model.";

    public List<ModelListDto> findModelList(String brandNo) {
        return dao.selectList(namespace + "findModelList", brandNo, PartnerKey.WBDataBase.Alias.Admin);
    }
}
