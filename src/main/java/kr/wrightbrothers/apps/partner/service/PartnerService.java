package kr.wrightbrothers.apps.partner.service;

import kr.wrightbrothers.apps.common.constants.Partner;
import kr.wrightbrothers.apps.common.constants.User;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.common.util.RandomUtil;
import kr.wrightbrothers.apps.file.dto.FileListDto;
import kr.wrightbrothers.apps.file.dto.FileUpdateDto;
import kr.wrightbrothers.apps.file.dto.FileUploadDto;
import kr.wrightbrothers.apps.file.service.FileService;
import kr.wrightbrothers.apps.file.service.S3Service;
import kr.wrightbrothers.apps.partner.dto.*;
import kr.wrightbrothers.apps.user.dto.UserAuthInsertDto;
import kr.wrightbrothers.apps.user.service.UserService;
import kr.wrightbrothers.framework.lang.WBException;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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

        // 기존 thumbnail 존재하는 경우 삭제
        deletePartnerThumbnail(userId, partnerCode);

        String fileNo = RandomUtil.generateNo();
        dao.update(namespace+"updatePartnerThumbnail", PartnerDto.ReqBody.builder()
                        .partnerCode(partnerCode)
                        .thumbnail(fileNo)
                        .build());

        return fileService.uploadProfileThumbnail(multipartFile, fileNo);
    }

    @Transactional(value = PartnerKey.WBDataBase.TransactionManager.Global)
    public void deletePartnerThumbnail(String userId, String partnerCode) {

        PartnerDto.ResBody partnerDto = dao.selectOne(namespace + "findPartnerByPartnerCode", partnerCode);

        // 기존 thumbnail 존재하는 경우 삭제
        if(!ObjectUtils.isEmpty(partnerDto.getThumbnail())) {
            List<FileListDto> fileListDto = fileService.findFileList(partnerDto.getThumbnail());

            List<FileUpdateDto> fileList = new ArrayList<>();

            fileListDto.forEach(fileDto -> {
                fileList.add(FileUpdateDto.builder()
                        .fileNo(fileDto.getFileNo())
                        .fileSeq(fileDto.getFileSeq())
                        .fileStatus(WBKey.TransactionType.Delete)
                        .userId(userId)
                        .build());

                fileService.s3FileUpload(fileList, null, false);
            });
        }
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
                        .userId(paramDto.getUserId())
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
                        .build()
        );
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

    public boolean checkPartnerOperatorCount(PartnerInviteDto.Param paramDto) {
        return dao.selectOne(namespace + "checkPartnerOperatorCount", paramDto);
    }
    public boolean checkPartnerOperatorAuthCount(PartnerInviteDto.PartnerOperator paramDto) {
        return dao.selectOne(namespace + "checkPartnerOperatorAuthCount", paramDto);
    }

    public PartnerViewDto.ResBody findPartnerByPartnerCode(PartnerViewDto.Param paramDto) {
        PartnerViewDto.ResBody result = PartnerViewDto.ResBody
                .builder()
                .partner(Optional.of((PartnerDto.ResBody) dao.selectOne(namespace + "findPartnerByPartnerCode", paramDto.getPartnerCode())).orElse(new PartnerDto.ResBody()))
                .partnerContract(Optional.of((PartnerContractDto.ResBody) dao.selectOne(namespace + "findPartnerContractByPartnerCode", paramDto)).orElse(new PartnerContractDto.ResBody()))
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

    // 계약 생신
    public void updateContract(){

    }

    // 파트너 상태 변경
    public void updateStatus(){

    }
}