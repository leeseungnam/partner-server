package kr.wrightbrothers.apps.token.service;

import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.token.dto.BlackListDto;
import kr.wrightbrothers.apps.token.dto.RefreshTokenDto;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlackListService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.token.query.BlackList.";

    public BlackListDto findById(String id) {
        return dao.selectOne(namespace + "findById", id, PartnerKey.WBDataBase.Alias.Default);
    }

    public void insert(BlackListDto paramDto) {
        dao.insert(namespace + "insertBlackList", paramDto, PartnerKey.WBDataBase.Alias.Default);
    }
}
