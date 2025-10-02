package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.account.AccountSimpleResponse;
import com.prm392.be.labverse.dto.account.RegisterAccountRequest;
import com.prm392.be.labverse.entity.Account;

public interface AccountService {

    AccountSimpleResponse createAccount(RegisterAccountRequest request);

    Account findAccountByEmail(String email);
}
