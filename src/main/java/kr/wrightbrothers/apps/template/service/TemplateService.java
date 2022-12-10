package kr.wrightbrothers.apps.template.service;

import kr.wrightbrothers.apps.common.constants.TemplateConst;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.template.dto.*;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.template.query.Template.";

    public List<TemplateListDto.Response> findTemplateList(TemplateListDto.Param paramDto) {
        return dao.selectList(namespace + "findTemplateList", paramDto, paramDto.getRowBounds());
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Default)
    public void insertTemplate(TemplateInsertDto paramDto) {
        // 템플릿 등록
        dao.insert(namespace + "insertTemplate", paramDto);

        // 템플릿 배송정보 등록
        if (TemplateConst.Type.DELIVERY.getType().equals(paramDto.getTemplateType())) {
            dao.insert(namespace + "insertTemplateDelivery", paramDto);
            return;
        }

        // 템플릿 안내정보 등록
        dao.insert(namespace + "insertTemplateGuide", paramDto);
    }

    public TemplateFindDto.Response findTemplate(TemplateFindDto.Param paramDto) {
        // 템플릿 정보 조회
        return dao.selectOne(namespace + "findTemplate", paramDto.getTemplateNo());
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Default)
    public void updateTemplate(TemplateUpdateDto paramDto) {
        // 템플릿 수정
        dao.update(namespace + "updateTemplate", paramDto);

        // 템플릿 배송정보 수정
        if (TemplateConst.Type.DELIVERY.getType().equals(paramDto.getTemplateType())) {
            dao.update(namespace + "updateTemplateDelivery", paramDto);
            return;
        }

        // 템플릿 안내정보 수정
        dao.update(namespace + "updateTemplateGuide", paramDto);
    }

    @Transactional(transactionManager = PartnerKey.WBDataBase.TransactionManager.Default)
    public void deleteTemplate(TemplateDeleteDto paramDto) {
        Arrays.stream(paramDto.getTemplateNoList()).forEach(
                templateNo -> {
                    // 입점몰 등록 템플릿 여부 확인
                    if (dao.selectOne(namespace + "isTemplateAuth",
                            TemplateAuthDto.builder()
                                    .partnerCode(paramDto.getPartnerCode())
                                    .templateNo(templateNo)
                                    .build()))
                        throw new WBBusinessException(ErrorCode.FORBIDDEN.getErrCode());

                    // 템플릿 삭제
                    dao.delete(namespace + "deleteTemplateDelivery", templateNo);
                    dao.delete(namespace + "deleteTemplateGuide", templateNo);
                    dao.delete(namespace + "deleteTemplate", templateNo);
                }
        );
    }
}
