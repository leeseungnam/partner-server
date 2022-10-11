package kr.wrightbrothers.apps.partner.service;

import kr.wrightbrothers.apps.common.constants.Partner;
import kr.wrightbrothers.apps.common.constants.User;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.partner.dto.PartnerDto;
import kr.wrightbrothers.apps.partner.dto.PartnerInsertDto;
import kr.wrightbrothers.apps.user.dto.UserAuthDto;
import kr.wrightbrothers.apps.user.dto.UserAuthInsertDto;
import kr.wrightbrothers.apps.user.service.UserService;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartnerService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.partner.query.Partner.";
    private final UserService userService;

    public PartnerDto.ResBody findPartnerByBusinessNo(String businessNo) {
        return dao.selectOne(namespace + "findPartnerByBusinessNo", businessNo);
    }
    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Default)
    public void insertPartner(PartnerInsertDto paramDto) {

        // create partnerCode
        String partnerCode = RandomStringUtils.randomAlphanumeric(10).toUpperCase();
        paramDto.getPartner().changePartnerCode(partnerCode);
        paramDto.getPartner().changePartnerStatus(Partner.Status.REQUEST.getCode());

        // insert partner
        dao.insert(namespace + "insertPartner", paramDto.getPartner());

        // create contractNo
        String contractNo = RandomStringUtils.randomAlphanumeric(10).toUpperCase();
        paramDto.getPartnerContract().changeContractNo(contractNo);
        paramDto.getPartnerContract().changePartnerCode(partnerCode);
        paramDto.getPartnerContract().changeContractStatus(Partner.Contract.Status.EMPTY.getCode());

        // insert contract
        dao.insert(namespace + "insertPartnerContract", paramDto.getPartnerContract());

        // insert usersPartner
        userService._insertUser(UserAuthInsertDto.ReqBody.builder()
                        .authCode(User.Auth.ADMIN.getType())
                        .partnerCode(partnerCode)
                        .userId(paramDto.getPartner().getUserId())
                        .partnerKind(paramDto.getPartner().getPartnerKind())
                        .build());
    }

    // 계약 생신
    public void updateContract(){

    }

    // 파트너 상태 변경
    public void updateStatus(){

    }
}