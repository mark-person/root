package com.ppx.cloud.auth.console.grant;

import java.util.List;

public interface GrantService {
    
    List<Integer> getGrantResIds(Integer accountId);
    
}
