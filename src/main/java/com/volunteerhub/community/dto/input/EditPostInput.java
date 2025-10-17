package com.volunteerhub.community.dto.input;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EditPostInput {
    private String postName;
    private String content;
}
