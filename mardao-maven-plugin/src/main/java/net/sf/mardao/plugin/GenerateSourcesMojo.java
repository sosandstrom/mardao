package net.sf.mardao.plugin;

/*
 * #%L
 * net.sf.mardao:mardao-maven-plugin
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
