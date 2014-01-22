package net.sf.mardao.crud;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.dao.Dao;

/**
 * Created with IntelliJ IDEA.
 *
 * @author osandstrom
 * Date: 1/19/14 Time: 10:54 AM
 */
@Consumes(value = {MediaType.APPLICATION_JSON})
@Produces(MediaType.APPLICATION_JSON)
public class CrudResource<T, ID extends Serializable, D extends Dao<T, ID>> {

  protected static final Logger LOGGER = LoggerFactory.getLogger(CrudResource.class);

  protected final Dao<T, ID> dao;

  public CrudResource(Dao<T, ID> dao) {
    this.dao = dao;
  }

  @GET
  @Path("count")
  public int count() {
    return dao.count();
  }

  @POST
  @Transactional
  public Response create(T entity) throws URISyntaxException {
    final ID id = dao.persist(entity);
    URI uri = new URI(id.toString());
    return Response.created(uri).entity(id).build();
  }

  @DELETE
  @Path("{id}")
  public Response delete(@PathParam("id") ID id) {
    final boolean found = dao.delete(id);

    if (!found) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.noContent().build();
  }

  @GET
  @Path("{id}")
  public Response read(@PathParam("id") ID id) {
    final T entity = dao.findByPrimaryKey(id);

    if (null == entity) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.ok(entity).build();
  }

  @GET
  public Response readPage(@QueryParam("pageSize") @DefaultValue("10") int pageSize,
                           @QueryParam("cursorKey") String cursorKey) {
    final CursorPage<T> page = dao.queryPage(pageSize, cursorKey);
    return Response.ok(page).build();
  }

  @POST
  @Path("{id}")
  public Response update(T entity) throws URISyntaxException {
    final ID id = (ID) dao.getPrimaryKey(entity);
    if (null == id) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
    dao.update(entity);
    URI uri = new URI(id.toString());
    return Response.ok().contentLocation(uri).build();
  }


}
