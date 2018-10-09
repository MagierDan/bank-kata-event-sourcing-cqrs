package com.marmulasse.bank.account;

import com.marmulasse.bank.account.aggregate.Account;
import com.marmulasse.bank.account.aggregate.AccountFactoryTest;
import com.marmulasse.bank.account.aggregate.AccountId;
import com.marmulasse.bank.account.aggregate.Amount;
import com.marmulasse.bank.account.commands.MakeWithdrawCommand;
import com.marmulasse.bank.account.commands.handlers.WithdrawCommandHandler;
import com.marmulasse.bank.account.port.AccountRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class WithdrawCommandHandlerShould {

    private WithdrawCommandHandler sut;

    private AccountRepository accountRepositoryMock;

    @Before
    public void setUp() throws Exception {
        accountRepositoryMock = mock(AccountRepository.class);
        sut = new WithdrawCommandHandler(accountRepositoryMock);
    }

    @Test
    public void make_a_new_withdraw_on_existing_account() throws Exception {
        Account existingAccount = AccountFactoryTest.accountWithBalance(Amount.of(10.0));
        when(accountRepositoryMock.get(existingAccount.getAccountId())).thenReturn(Optional.of(existingAccount));

        sut.handle(new MakeWithdrawCommand(existingAccount.getAccountId(), Amount.of(1.0)));

        ArgumentCaptor<Account> updatedAccountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepositoryMock).save(updatedAccountCaptor.capture());
    }

    @Test
    public void fail_when_no_account_found_for_specified_account_id() throws Exception {
        AccountId unknownAccountId = AccountId.from("bb5edf47-929e-45b3-98eb-c238c41cf983");
        when(accountRepositoryMock.get(unknownAccountId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> sut.handle(new MakeWithdrawCommand(unknownAccountId, Amount.of(1.0))))
                .hasMessageContaining("Account not found from accountId AccountId{value=bb5edf47-929e-45b3-98eb-c238c41cf983}")
                .isInstanceOf(RuntimeException.class);
    }
}
