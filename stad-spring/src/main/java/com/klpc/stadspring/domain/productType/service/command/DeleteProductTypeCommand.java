package com.klpc.stadspring.domain.productType.service.command;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteProductTypeCommand {
    Long productId;
    Long id;
}
