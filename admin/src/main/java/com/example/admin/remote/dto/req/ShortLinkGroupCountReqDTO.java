package com.example.admin.remote.dto.req;

import lombok.Data;

import java.util.List;

@Data
public class ShortLinkGroupCountReqDTO {

    private List<String> gidList;
}
