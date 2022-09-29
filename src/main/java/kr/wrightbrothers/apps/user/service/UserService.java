package kr.wrightbrothers.apps.user.service;

import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.user.dto.UserAuthDto;
import kr.wrightbrothers.apps.user.dto.UserDto;
import kr.wrightbrothers.apps.user.dto.UserInsertDto;
import kr.wrightbrothers.apps.user.dto.UserPwdUpdateDto;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.user.query.User.";

    public UserDto findUserByDynamic(UserDto paramDto) {
        return dao.selectOne(namespace + "findUserByDynamic", paramDto, PartnerKey.WBDataBase.Alias.Default);
    }

    public List<UserAuthDto>  findAuthById(String id) {
        return dao.selectList(namespace + "findAuthById", id, PartnerKey.WBDataBase.Alias.Default);
    }

    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Default)
    public void insertUser(UserInsertDto paramDto) {
        dao.insert(namespace + "insertUser", paramDto, PartnerKey.WBDataBase.Alias.Default);
    }

    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Default)
    public void updateUserPwd(UserPwdUpdateDto paramDto) {
        dao.update(namespace + "updateUserPwd", paramDto, PartnerKey.WBDataBase.Alias.Default);
    }
}
