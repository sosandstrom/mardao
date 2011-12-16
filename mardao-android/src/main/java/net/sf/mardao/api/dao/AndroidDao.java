/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mardao.api.dao;

import net.sf.mardao.api.domain.AndroidLongEntity;

/**
 *
 * @author os
 */
public interface AndroidDao<T extends AndroidLongEntity> {
    CursorIterable<T> queryAll();
}
