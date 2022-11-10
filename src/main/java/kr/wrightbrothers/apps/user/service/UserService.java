package kr.wrightbrothers.apps.user.service;

import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.email.dto.SingleEmailDto;
import kr.wrightbrothers.apps.email.service.EmailService;
import kr.wrightbrothers.apps.partner.dto.PartnerOperatorDto;
import kr.wrightbrothers.apps.partner.dto.PartnerViewDto;
import kr.wrightbrothers.apps.user.dto.*;
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
    private final EmailService emailService;

    public boolean checkAuth(UserAuthDto paramDto) {
        return dao.selectOne(namespace + "checkAuth", paramDto, PartnerKey.WBDataBase.Alias.Default);
    }
    public List<PartnerOperatorDto.ResBody>  findUserByPartnerCodeAndAuthCode(PartnerViewDto.Param paramDto) {
        return dao.selectList(namespace + "findUserByPartnerCodeAndAuthCode", paramDto, PartnerKey.WBDataBase.Alias.Default);
    }

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

    public void _insertUsersPartner(UserAuthInsertDto.ReqBody paramDto) {
        dao.insert(namespace + "insertUsersPartner", paramDto, PartnerKey.WBDataBase.Alias.Default);
    }


    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Default)
    public void updateUserPwd(UserPwdUpdateDto paramDto) {
        dao.update(namespace + "updateUserPwd", paramDto, PartnerKey.WBDataBase.Alias.Default);
    }

    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Default)
    public SingleEmailDto.ResBody findUserPwd(UserDto userParamDto, SingleEmailDto.ReqBody mailParamDto) throws Exception{

        //  update userPwd
        this.updateUserPwd(UserPwdUpdateDto.builder()
                .userId(userParamDto.getUserId())
                .userPwd(userParamDto.getUserPwd())
                .changePwdFlag(true)
                .build());

        //  send email
        SingleEmailDto.ResBody resBody = emailService.singleSendEmail(mailParamDto);

        return resBody;
    }
}
