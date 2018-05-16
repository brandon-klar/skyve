package modules.admin.User.actions;

import modules.admin.domain.User;
import org.skyve.util.DataBuilder;
import org.skyve.util.test.SkyveFixture.FixtureType;
import util.AbstractActionTest;

/**
 * Generated - local changes will be overwritten.
 * Extend {@link AbstractActionTest} to create your own tests for this action.
 */
public class NewTest extends AbstractActionTest<User, New> {

	@Override
	protected New getAction() {
		return new New();
	}

	@Override
	protected User getBean() throws Exception {
		return new DataBuilder()
			.fixture(FixtureType.crud)
			.build(User.MODULE_NAME, User.DOCUMENT_NAME);
	}
}