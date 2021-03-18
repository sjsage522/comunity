package com.example.comunity.dto.comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDto {

    @JsonProperty("created_at")
    private LocalDateTime createdDate;
    @JsonProperty("last_modified_at")
    private LocalDateTime lastModifiedDate;
}
