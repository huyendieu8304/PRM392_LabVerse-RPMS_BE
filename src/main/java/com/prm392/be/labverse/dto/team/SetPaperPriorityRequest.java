package com.prm392.be.labverse.dto.team;

import com.prm392.be.labverse.constant.EPriority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetPaperPriorityRequest {
    private EPriority priority;
}