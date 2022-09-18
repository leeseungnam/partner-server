package kr.wrightbrothers.apps.template.service;

import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.template.dto.TemplateFindDto;
import kr.wrightbrothers.apps.template.dto.TemplateInsertDto;
import kr.wrightbrothers.apps.template.dto.TemplateListDto;
import kr.wrightbrothers.apps.template.dto.TemplateUpdateDto;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if ("T01".equals(paramDto.getTemplateType())) {
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
        if ("T01".equals(paramDto.getTemplateType())) {
            dao.update(namespace + "updateTemplateDelivery", paramDto);
            return;
        }

        // 템플릿 안내정보 수정
        dao.update(namespace + "updateTemplateGuide", paramDto);
    }
}
