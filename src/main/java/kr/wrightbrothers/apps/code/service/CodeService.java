package kr.wrightbrothers.apps.code.service;

import kr.wrightbrothers.apps.code.dto.CodeListDto;
import kr.wrightbrothers.apps.util.PartnerKey;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CodeService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.code.query.Code.";

    public List<CodeListDto> findCodeList(String codeGroup) {
        return dao.selectList(namespace + "findCodeList", codeGroup, PartnerKey.WBDataBase.Alias.Admin);
    }
}
