package kr.wrightbrothers.apps.user.service;

import kr.wrightbrothers.apps.code.dto.CodeListDto;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.user.dto.UserDto;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.user.query.User.";

    public UserDto findById(String id) {
        return dao.selectOne(namespace + "findById", id, PartnerKey.WBDataBase.Alias.Default);
    }
}
