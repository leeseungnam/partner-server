package kr.wrightbrothers.apps.partner.service;

import kr.wrightbrothers.apps.common.constants.Partner;
import kr.wrightbrothers.apps.common.constants.User;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.partner.dto.*;
import kr.wrightbrothers.apps.user.dto.UserAuthInsertDto;
import kr.wrightbrothers.apps.user.service.UserService;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartnerService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.partner.query.Partner.";
    private final UserService userService;

    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Default)
    public void updatePartnerAll(PartnerInsertDto paramDto) {

        //  update updatePartner
        dao.update(namespace+"updatePartner", paramDto.getPartner());

        //  update updatePartnerContractOperator
        dao.update(namespace+"updatePartnerContract", PartnerUpdateDto.Param.Contract.builder()
                        .partnerCode(paramDto.getPartner().getPartnerCode())
                        .contractNo(paramDto.getPartnerContract().getContractNo())
                        .contractManagerPhone(paramDto.getPartnerContract().getContractManagerPhone())
                        .contractManagerName(paramDto.getPartnerContract().getContractManagerName())
                        .accountNo(paramDto.getPartnerContract().getAccountNo())
                        .accountHolder(paramDto.getPartnerContract().getAccountHolder())
                        .bankCode(paramDto.getPartnerContract().getBankCode())
                        .taxBillEmail(paramDto.getPartnerContract().getTaxBillEmail())
                        .build()
        );
    }

    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Default)
    public void updatePartner(PartnerUpdateDto.ReqBody paramDto) {

        //  update updatePartnerContractOperator
        dao.update(namespace+"updatePartnerContractOperator", PartnerUpdateDto.Param.Contract.builder()
                        .partnerCode(paramDto.getPartnerCode())
                        .contractNo(paramDto.getContractNo())
                        .contractManagerName(paramDto.getContractManagerName())
                        .contractManagerPhone(paramDto.getContractManagerPhone())
                        .userId(paramDto.getUserId())
                        .build());

        // update partnerNotification
        //  -> delete nofification by partnerCode
        dao.delete("deletePartnerNotification", paramDto.getPartnerCode());
        //  -> insert noficicationPhone list
        if(!ObjectUtils.isEmpty(paramDto.getNotificationPhoneList())) {
            dao.insert("insertPartnerNotification", PartnerUpdateDto.Param.Notification.builder()
                    .partnerCode(paramDto.getPartnerCode())
                    .notificationPhoneList(paramDto.getNotificationPhoneList())
                    .userId(paramDto.getUserId())
                    .build());
        }
    }

    public PartnerViewDto.ResBody findPartnerByPartnerCode(PartnerViewDto.Param paramDto) {
        PartnerViewDto.ResBody result = PartnerViewDto.ResBody
                .builder()
                .partner(Optional.of((PartnerDto.ResBody) dao.selectOne(namespace + "findPartnerByPartnerCode", paramDto.getPartnerCode())).orElse(new PartnerDto.ResBody()))
                .partnerContract(Optional.of((PartnerContractDto.ResBody) dao.selectOne(namespace + "findPartnerContractByPartnerCode", paramDto.getPartnerCode())).orElse(new PartnerContractDto.ResBody()))
                .partnerOperator(userService.findUserByPartnerCodeAndAuthCode(paramDto))
                .partnerNotification(dao.selectList(namespace + "findPartnerNotificationByPartnerCode", paramDto.getPartnerCode()))
                .partnerReject(dao.selectList(namespace + "findPartnerRejectByPartnerCode", paramDto.getPartnerCode()))
                .build();

        // set code name
        result.getPartner().changePartnerStatusName(Partner.Status.valueOfCode(result.getPartner().getPartnerStatus()).getName());
        result.getPartner().changeBusinessClassificationCodeName(Partner.Classification.valueOfCode(result.getPartner().getBusinessClassificationCode()).getName());

        result.getPartnerContract().changeContractStatusName(Partner.Contract.Status.valueOfCode(result.getPartnerContract().getContractStatus()).getName());
        result.getPartnerContract().changeBankCodeName(Partner.Contract.Bank.valueOfCode(result.getPartnerContract().getBankCode()).getName());

        return result;
    }

    public List<PartnerDto.ResBody> findPartnerListByBusinessNo(PartnerFindDto.Param paramDto) {
        return dao.selectList(namespace + "findPartnerListByBusinessNo", paramDto);
    }

    public List<PartnerAndAuthFindDto.ResBody> findUserAuthAndPartnerListByUserId(PartnerAndAuthFindDto.Param paramDto) {
        return dao.selectList(namespace + "findUserAuthAndPartnerListByUserId", paramDto);
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