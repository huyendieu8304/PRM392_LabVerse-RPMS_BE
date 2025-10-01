package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.account.AccountSimpleResponse;
import com.prm392.be.labverse.dto.account.RegisterAccountRequest;

public interface AccountService {

    AccountSimpleResponse createAccount(RegisterAccountRequest request);
}
