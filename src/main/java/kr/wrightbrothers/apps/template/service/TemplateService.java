package kr.wrightbrothers.apps.template.service;

import kr.wrightbrothers.apps.template.dto.TemplateListDto;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.template.";

    public List<TemplateListDto.Response> findTemplateList(TemplateListDto.Param paramDto) {
        return dao.selectList(namespace + "findTemplateList", paramDto, paramDto.getRowBounds());
    }
}
