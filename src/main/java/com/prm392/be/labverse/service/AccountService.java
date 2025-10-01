package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.account.RegisterAccountRequest;
import com.prm392.be.labverse.entity.Account;

public interface AccountService {

    Account createAccount(RegisterAccountRequest request);
}
