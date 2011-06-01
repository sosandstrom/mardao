package net.sf.mardao.api.web;

public abstract class GenericController<E, ID, PDAO, EDAO> {
    protected PDAO parentDao;

    protected EDAO dao;

    public final void setParentDao(PDAO parentDao) {
        this.parentDao = parentDao;
    }

    public final void setDao(EDAO dao) {
        this.dao = dao;
    }
}
