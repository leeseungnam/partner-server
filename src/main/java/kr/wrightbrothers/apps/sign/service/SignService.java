package kr.wrightbrothers.apps.sign.service;

import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.sign.dto.UserDetailDto;
import kr.wrightbrothers.apps.token.dto.BlackListDto;
import kr.wrightbrothers.apps.token.dto.RefreshTokenDto;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignService {

    private final WBCommonDao dao;

    private final String namespace = "kr.wrightbrothers.apps.sign.query.Sign.";
    private final String namespaceInBlackList = "kr.wrightbrothers.apps.token.query.BlackList.";
    private final String namespaceInRefreshToken = "kr.wrightbrothers.apps.token.query.RefreshToken.";

    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Default)
    public BlackListDto findById(String accessToken) {
        return dao.selectOne(namespaceInBlackList + "findById", accessToken);
    }
    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Default)
    public UserDetailDto loadUserByUsername(String userId) {
        return dao.selectOne(namespace + "loadUserByUsername", userId);
    }

    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Default)
    public void logout(BlackListDto blackListDto, RefreshTokenDto refreshTokenDto) {
        dao.insert(namespaceInBlackList + "insertBlackList", blackListDto, PartnerKey.WBDataBase.Alias.Default);
        dao.update(namespaceInRefreshToken + "updateRefreshToken", refreshTokenDto, PartnerKey.WBDataBase.Alias.Default);
    }
}
