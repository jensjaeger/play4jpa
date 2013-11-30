package customplay.db;

import play.mvc.*;
import play.mvc.Http.*;

/**
 * Wraps an action in a JPA no commit transaction.
 *
 * @author Jens (mail@jensjaeger.com)
 */
public class TxAction extends Action<Tx>{

    @Override
    public Result call(final Context ctx) throws Throwable {
        return Db.withTx(
                configuration.value(),
                configuration.readOnly(),
                new play.libs.F.Function0<Result>() {
                    @Override
                    public Result apply() throws Throwable {
                        return delegate.call(ctx);
                    }
                }
        );
    }
}
