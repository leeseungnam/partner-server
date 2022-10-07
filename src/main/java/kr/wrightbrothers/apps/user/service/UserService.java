package kr.wrightbrothers.apps.user.service;

import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.email.dto.SingleEmailDto;
import kr.wrightbrothers.apps.email.service.EmailService;
import kr.wrightbrothers.apps.user.dto.UserAuthDto;
import kr.wrightbrothers.apps.user.dto.UserDto;
import kr.wrightbrothers.apps.user.dto.UserInsertDto;
import kr.wrightbrothers.apps.user.dto.UserPwdUpdateDto;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.user.query.User.";
    private final EmailService emailService;

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

    public void _insertUser(UserAuthDto paramDto) {
        dao.insert(namespace + "insertUsersPartner", paramDto, PartnerKey.WBDataBase.Alias.Default);
    }


    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Default)
    public void updateUserPwd(UserPwdUpdateDto paramDto) {
        dao.update(namespace + "updateUserPwd", paramDto, PartnerKey.WBDataBase.Alias.Default);
    }

    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Default)
    public SingleEmailDto.ResBody findUserPwd(String authCode, UserDto userParamDto, SingleEmailDto.ReqBody mailParamDto) throws Exception{

        //  update userPwd
        this.updateUserPwd(UserPwdUpdateDto.builder()
                .userId(userParamDto.getUserId())
                .userPwd(userParamDto.getUserPwd())
                .build());

        //  send email
        SingleEmailDto.ResBody resBody = emailService.singleSendEmail(mailParamDto);

        return resBody;
    }
}
