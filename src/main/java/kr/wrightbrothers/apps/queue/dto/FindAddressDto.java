package kr.wrightbrothers.apps.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FindAddressDto {
    private String partnerName;
    private List<String> addressList;
}
