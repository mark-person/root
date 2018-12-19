package com.ppx.cloud.auth.console.grant;

import java.util.Set;

public interface GrantService {
    
    Set<Integer> getGrantResIds(Integer accountId);
    
}
