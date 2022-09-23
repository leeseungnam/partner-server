package kr.wrightbrothers.apps.address.service;

import kr.wrightbrothers.apps.address.dto.*;
import kr.wrightbrothers.apps.common.util.ErrorCode;
import kr.wrightbrothers.framework.lang.WBBusinessException;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final WBCommonDao dao;
    private final String namespace = "kr.wrightbrothers.apps.address.query.Address.";

    /**
     * <pre>
     * 대표 설정 여부에 따라 등록 되어있는 주소록 데이터에 해당 대표 설정 여부의 값을
     * N 값으로 일괄 처리 하도록 한다.
     *
     * 해당 처리 프로세스 이유는 출고지, 반품/교환지 대표는 스토어 한개의 주소록에 설정 할 수 있기에
     * 대표 설정 값이 넘어올 경우 이전 데이터는 N 으로 변경 함.
     * </pre>
     *
     * @param repUnstoringFlag 대표 출고지 지정 여부
     * @param repReturnFlag 대표 반품/교환지 지정 여부
     */
    public void removeRepresentative(String partnerCode,
                                     String repUnstoringFlag,
                                     String repReturnFlag) {
        if ("Y".equals(repUnstoringFlag))
            // 출고지 대표 초기화 처리
            dao.update(namespace  + "removeReqUnstoringFlag", partnerCode);

        if ("Y".equals(repReturnFlag))
            // 반품 대표 초기화 처리
            dao.update(namespace + "removeReqReturnFlag", partnerCode);
    }

    public List<AddressListDto.Response> findAddressList(AddressListDto.Param paramDto) {
        return dao.selectList(namespace + "findAddressList", paramDto, paramDto.getRowBounds());
    }

    public void insertAddress(AddressInsertDto paramDto) {
        // 대표 출고지, 반품 지정여부 사전 처리
        removeRepresentative(
                paramDto.getPartnerCode(),
                paramDto.getRepUnstoringFlag(),
                paramDto.getRepReturnFlag());

        dao.insert(namespace + "insertAddress", paramDto);
    }

    public AddressFindDto.Response findAddress(AddressFindDto.Param paramDto) {
        return dao.selectOne(namespace + "findAddress", paramDto);
    }

    public void updateAddress(AddressUpdateDto paramDto) {
        // 대표 출고지, 반품 지정여부 사전 처리
        removeRepresentative(
                paramDto.getPartnerCode(),
                paramDto.getRepUnstoringFlag(),
                paramDto.getRepReturnFlag());

        dao.update(namespace + "updateAddress", paramDto);
    }

    public void deleteAddress(AddressDeleteDto paramDto) {
        dao.delete(namespace + "deleteAddress", paramDto);
    }
}
