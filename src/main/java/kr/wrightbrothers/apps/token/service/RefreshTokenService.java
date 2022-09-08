package kr.wrightbrothers.apps.token.service;

import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.token.dto.RefreshTokenDto;
import kr.wrightbrothers.apps.user.dto.UserDto;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.token.query.RefreshToken.";

    public RefreshTokenDto findById(String id) {
        return dao.selectOne(namespace + "findById", id, PartnerKey.WBDataBase.Alias.Default);
    }

    public void insert(RefreshTokenDto paramDto) {
        dao.insert(namespace + "insertRefreshToken", paramDto, PartnerKey.WBDataBase.Alias.Default);
    }

    public void update(RefreshTokenDto paramDto) {
        dao.update(namespace + "updateRefreshToken", paramDto, PartnerKey.WBDataBase.Alias.Default);
    }
}
