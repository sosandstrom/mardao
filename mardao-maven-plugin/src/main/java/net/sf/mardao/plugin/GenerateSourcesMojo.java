package net.sf.mardao.plugin;

import net.sf.mardao.domain.MergeTemplate;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * This Mojo merges the generic (non-entity) templates into their sources, 
 * to be ready for the maven <code>compile</code> phase.
 * @goal generate-sources
 * @author f94os
 *
 */
public class GenerateSourcesMojo extends AbstractMardaoMojo {
	
	private void mergeGeneric() throws ResourceNotFoundException, ParseErrorException, Exception {
		for (MergeTemplate mt : mergeScheme.getTemplates()) {
			if (false == mt.isEntity() && false == mt.isListingEntities()) {
				mergeTemplate(mt, null);
			}
		}
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		try {
			mergeGeneric();
		} catch (Exception e) {
			throw new MojoExecutionException(getClass().getSimpleName(), e);
		}
	}

}
