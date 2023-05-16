package chat.teco.tecochat.common;

import static org.mockito.Mockito.spy;

import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class FakeTransactionTemplate extends TransactionTemplate {

    public static FakeTransactionTemplate spied() {
        return spy(new FakeTransactionTemplate());
    }

    @Override
    public <T> T execute(TransactionCallback<T> action) throws TransactionException {
        return action.doInTransaction(new SimpleTransactionStatus());
    }
}
