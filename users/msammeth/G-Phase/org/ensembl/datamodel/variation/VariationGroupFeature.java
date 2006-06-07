/*
	Copyright (C) 2003 EBI, GRL

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.ensembl.datamodel.variation;

import org.ensembl.datamodel.Feature;

/**
 * The point of this class is....
 *
 * @author <a href="mailto:craig@ebi.ac.uk">Craig Melsopp</a>
 */
public interface VariationGroupFeature extends Feature {
	
	/**
	 * Get the variation group associated with the feature.
	 * @return variation group associated with the feature.
	 */
	VariationGroup getVariationGroup();
	
	/**
	 * Get the name of the variation group associated with the feature.
	 * @return name of the variation group associated with the feature.
	 */
	String getVariationGroupName();
	
	
}