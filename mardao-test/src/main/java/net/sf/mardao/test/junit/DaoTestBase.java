package net.sf.mardao.test.junit;

/*
 * #%L
 * mardao-core
 * %%
 * Copyright (C) 2010 - 2014 Wadpam
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import net.sf.mardao.core.CursorPage;
import net.sf.mardao.dao.*;
import net.sf.mardao.test.dao.DBasicDaoBean;
import net.sf.mardao.test.dao.DBasicMapper;
import net.sf.mardao.test.domain.DBasic;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Tests for AbstractDao.
 *
 * @author osandstrom Date: 2014-09-12 Time: 20:17
 */
public class DaoTestBase {

    public static final String PRINCIPAL_FIXTURE = "fixture";
    public static final String PRINCIPAL_SET_UP = "setUp";
    protected Supplier supplier;

    /**
     * Override to test specific supplier
     */
    protected Supplier createSupplier() {
        return new InMemorySupplier();
    }

    /** Invoke from subclass method */
    public void setUp() {
        supplier = createSupplier();
        AbstractDao.setPrincipalName(PRINCIPAL_SET_UP);
    }

}
