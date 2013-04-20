package net.sf.mardao.test;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.mardao.core.Filter;
import net.sf.mardao.core.dao.TypeDaoImpl;
import net.sf.mardao.core.domain.AndroidLongEntity;
import android.test.InstrumentationTestCase;
import net.sf.mardao.core.dao.TypeDaoImpl;

public class ExpressionTest extends InstrumentationTestCase {

	private TypeDaoImpl<AndroidLongEntity, Long>	daoImpl;

	// public void testIn() throws Exception {
	// Expression inFilter = daoImpl.createInFilter("test", 12);
	// String expressionString = new
	// StringBuilder().append(inFilter.getColumn()).append(inFilter.getOperation()).toString();
	// assertTrue(Pattern.matches("^\\s?\\w+\\sIN\\s?\\([\\w\\?]?\\)\\s?$",
	// expressionString));
	// }

	public void testEquals() throws Exception {
		final Filter equalsFilter = daoImpl.createEqualsFilter("test", 12);
		final String expressionString = new StringBuilder()
				.append(equalsFilter.getColumn())
				.append(equalsFilter.getOperation()).toString();
		assertTrue(Pattern.matches("^\\s?\\w+\\s?=\\s?[\\w\\?]?\\s?$",
				expressionString));
	}

	@Override
	protected void setUp() throws Exception {
		daoImpl = new TypeDaoImpl<AndroidLongEntity, Long>(
				AndroidLongEntity.class, Long.class) {

			public List<String> getColumnNames() {
				return null;
			}

			@Override
			public String getTableName() {
				return null;
			}

			public String getPrimaryKeyColumnName() {
				return null;
			}

			@Override
			protected void setDomainStringProperty(
					final AndroidLongEntity domain, final String name,
					final Map<String, String> properties) {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			public Class getColumnClass(final String columnName) {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			public Long getSimpleKey(final AndroidLongEntity domain) {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			public void setSimpleKey(final AndroidLongEntity domain,
					final Long simpleKey) {
				throw new UnsupportedOperationException("Not supported yet.");
			}
		};
	}
}
