package kr.wrightbrothers.apps.partner.service;

import kr.wrightbrothers.apps.common.constants.Partner;
import kr.wrightbrothers.apps.common.constants.User;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.RandomUtil;
import kr.wrightbrothers.apps.file.dto.*;
import kr.wrightbrothers.apps.file.service.FileService;
import kr.wrightbrothers.apps.partner.dto.*;
import kr.wrightbrothers.apps.user.dto.UserAuthInsertDto;
import kr.wrightbrothers.apps.user.service.UserService;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartnerService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.partner.query.Partner.";
    private final UserService userService;
    private final FileService fileService;

    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Global)
    public FileUploadDto savePartnerThumbnail(String userId, String partnerCode, MultipartFile multipartFile) {

        String fileNo = RandomUtil.generateNo();

        // 기존 thumbnail 존재하는 경우 삭제
        deletePartnerThumbnail(userId, partnerCode, fileNo);

        return fileService.uploadProfileThumbnail(multipartFile, fileNo);
    }

    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Global)
    public void deletePartnerThumbnail(String userId, String partnerCode, String fileNo) {

        PartnerDto.ResBody partnerDto = dao.selectOne(namespace + "findPartnerByPartnerCode", partnerCode);

        // 기존 thumbnail 존재하는 경우 삭제
        if(!ObjectUtils.isEmpty(partnerDto.getThumbnail())) {
            List<FileListDto> fileListDto = fileService.findFileList(partnerDto.getThumbnail());

            List<FileUpdateDto> fileList = fileListDto.stream().map(obj -> FileUpdateDto.builder()
                    .fileNo(obj.getFileNo())
                    .fileSeq(obj.getFileSeq())
                    .fileStatus(WBKey.TransactionType.Delete)
                    .fileSource(obj.getFileSource())
                    .userId(userId)
                    .build()).collect(Collectors.toList());

            fileService.s3FileUpload(fileList, null, false);

        }
        dao.update(namespace+"updatePartnerThumbnail", PartnerDto.ReqBody.builder()
                .partnerCode(partnerCode)
                .thumbnail(fileNo)
                .build());
    }
    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Default)
    public void deletePartnerOperator(PartnerInviteDto.Param paramDto) {
        dao.delete(namespace+"deletePartnerOperator", paramDto);
    }
    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Default)
    public void acceptInvite(PartnerInviteDto.Param paramDto) {
        log.info("[acceptInvite]::authCode={}",paramDto.getAuthCode());
        // update inviteStatus
        dao.update(namespace+"updateInviteStatus", paramDto);

        // insert usersPartner
        userService._insertUsersPartner(UserAuthInsertDto.ReqBody.builder()
                        .partnerCode(paramDto.getPartnerCode())
                        .authCode(paramDto.getAuthCode())
                        .userId(paramDto.getInviteReceiver())
                        .build());
    }

    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Default)
    public void updatePartnerAll(PartnerInsertDto paramDto) {

        //  update updatePartner
        dao.update(namespace+"updatePartner", paramDto.getPartner());

        //  update updatePartnerContractOperator
        dao.update(namespace+"updatePartnerContract", PartnerUpdateDto.Param.Contract.builder()
                        .partnerCode(paramDto.getPartner().getPartnerCode())
                        .contractCode(paramDto.getPartnerContract().getContractCode())
                        .contractManagerPhone(paramDto.getPartnerContract().getContractManagerPhone())
                        .contractManagerName(paramDto.getPartnerContract().getContractManagerName())
                        .accountNo(paramDto.getPartnerContract().getAccountNo())
                        .accountHolder(paramDto.getPartnerContract().getAccountHolder())
                        .bankCode(paramDto.getPartnerContract().getBankCode())
                        .taxBillEmail(paramDto.getPartnerContract().getTaxBillEmail())
                        .contractStatus(paramDto.getPartnerContract().getContractStatus())
                        .contractFileNo(paramDto.getPartnerContract().getContractFileNo())
                        .build()
        );

        if(Partner.Contract.Status.REJECT.getCode().equals(paramDto.getPartnerContract().getContractStatus())) {
            log.info("[insertPartnerReject]");
            dao.insert(namespace+"insertPartnerReject", paramDto.getPartnerReject());
        }
    }

    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Default)
    public void updatePartner(PartnerUpdateDto.ReqBody paramDto) {

        //  update updatePartnerContractOperator
        dao.update(namespace+"updatePartnerContractOperator", PartnerUpdateDto.Param.Contract.builder()
                        .partnerCode(paramDto.getPartnerCode())
                        .contractCode(paramDto.getContractCode())
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
    public boolean checkPartnerNameCount(String partnerName) {
        return dao.selectOne(namespace + "checkPartnerNameCount", partnerName);
    }
    public boolean checkPartnerOperatorCount(PartnerInviteDto.Param paramDto) {
        return dao.selectOne(namespace + "checkPartnerOperatorCount", paramDto);
    }
    public boolean checkPartnerOperatorAuthCount(PartnerInviteDto.PartnerOperator paramDto) {
        return dao.selectOne(namespace + "checkPartnerOperatorAuthCount", paramDto);
    }
    public boolean checkNotAcceptInviteCount(PartnerInviteDto.ReqBody paramDto) {
        return dao.selectOne(namespace + "checkNotAcceptInviteCount", paramDto);
    }

    public PartnerContractDto.ResBody findPartnerContract(String partnerCode, String contractCode) {
        return dao.selectOne(namespace + "findPartnerContractByPartnerCode", PartnerViewDto.Param.builder()
                .partnerCode(partnerCode)
                .contractCode(contractCode)
        );
    }

    public PartnerViewDto.ResBody findPartnerByPartnerCode(PartnerViewDto.Param paramDto) {

        List<PartnerOperatorDto.ResBody> operatorList = userService.findUserByPartnerCodeAndAuthCode(paramDto);
        operatorList.forEach(entity -> {
            entity.changeAuthCodeName(User.Auth.valueOfCode(entity.getAuthCode()).getName());
        });

        PartnerViewDto.ResBody result = PartnerViewDto.ResBody
                .builder()
                .partner(Optional.of((PartnerDto.ResBody) dao.selectOne(namespace + "findPartnerByPartnerCode", paramDto.getPartnerCode())).orElse(new PartnerDto.ResBody()))
                .partnerContract(Optional.of((PartnerContractDto.ResBody) dao.selectOne(namespace + "findPartnerContractByPartnerCode", paramDto)).orElse(new PartnerContractDto.ResBody()))
                .partnerOperator(operatorList)
                .partnerNotification(dao.selectList(namespace + "findPartnerNotificationByPartnerCode", paramDto.getPartnerCode()))
                .partnerReject(dao.selectList(namespace + "findPartnerRejectByPartnerCode", paramDto))
                .build();

        // set code name
        result.getPartner().changePartnerStatusName(Partner.Status.valueOfCode(result.getPartner().getPartnerStatus()).getName());
        result.getPartner().changeBusinessClassificationCodeName(Partner.Classification.valueOfCode(result.getPartner().getBusinessClassificationCode()).getName());

        if(!ObjectUtils.isEmpty(result.getPartner().getThumbnail())) {
            FileDto file = fileService.findFile(FileParamDto.builder().fileNo(result.getPartner().getThumbnail()).fileSeq(Long.valueOf(1)).build());
            if(!ObjectUtils.isEmpty(file)) result.getPartner().changeThumbmailUrl(file.getFileSource());
        }

        result.getPartnerContract().changeContractStatusName(Partner.Contract.Status.valueOfCode(result.getPartnerContract().getContractStatus()).getName());
        result.getPartnerContract().changeBankCodeName(Partner.Contract.Bank.valueOfCode(result.getPartnerContract().getBankCode()).getName());

        return result;
    }

    public PartnerContractSNSDto findPartnerContractByPartnerCode(String partnerCode) {
        return dao.selectOne(namespace + "findPartnerContractSNS", partnerCode);
    }
    public PartnerInviteDto.ResBody findOperatorInvite(PartnerInviteDto.Param paramDto) {
        return dao.selectOne(namespace + "findOperatorInvite", paramDto);
    }

    public List<PartnerDto.ResBody> findPartnerListByBusinessNo(PartnerFindDto.Param paramDto) {
        return dao.selectList(namespace + "findPartnerListByBusinessNo", paramDto);
    }

    public List<PartnerAndAuthFindDto.ResBody> findUserAuthAndPartnerListByUserId(PartnerAndAuthFindDto.Param paramDto) {
        return dao.selectList(namespace + "findUserAuthAndPartnerListByUserId", paramDto);
    }
    public void insetPartnerOperator(PartnerInviteInsertDto paramDto) {
        dao.insert(namespace + "insertPartnerOperator", paramDto.getPartnerOperator());
    }
    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Default)
    public void insertPartner(PartnerInsertDto paramDto) {

        // create partnerCode
        String partnerCode = RandomStringUtils.randomAlphanumeric(10).toUpperCase();
        paramDto.getPartner().changePartnerCode(partnerCode);
        paramDto.getPartner().changePartnerStatus(Partner.Status.STOP.getCode());

        // insert partner
        dao.insert(namespace + "insertPartner", paramDto.getPartner());

        // create contractCode
        String contractCode = RandomStringUtils.randomAlphanumeric(10).toUpperCase();
        paramDto.getPartnerContract().changeContractCode(contractCode);
        paramDto.getPartnerContract().changePartnerCode(partnerCode);
        paramDto.getPartnerContract().changeContractStatus(Partner.Contract.Status.REQUEST.getCode());

        // insert contract
        dao.insert(namespace + "insertPartnerContract", paramDto.getPartnerContract());

        // insert usersPartner
        userService._insertUsersPartner(UserAuthInsertDto.ReqBody.builder()
                        .authCode(User.Auth.ADMIN.getType())
                        .partnerCode(partnerCode)
                        .userId(paramDto.getPartner().getUserId())
                        .partnerKind(paramDto.getPartner().getPartnerKind())
                        .build());
    }

    // 계약 갱신
    public void updateContractDay(PartnerContractDto.ReqBody paramDto){
        dao.update(namespace+"updatePartnerContractDay", paramDto);
    }

    // 파트너 상태 변경
    public void updateStatus(){

    }
}