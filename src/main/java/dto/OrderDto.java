package dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class OrderDto {
    private String id;
    private String date;
    private String customerId;
    List<OrderDetailsDto> details;
}
