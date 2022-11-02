package kr.wrightbrothers.apps.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FindAddressDto {
    private String partnerName;
    private List<String> addressList;
}
