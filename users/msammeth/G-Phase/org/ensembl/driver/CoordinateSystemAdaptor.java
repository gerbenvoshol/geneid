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

package org.ensembl.driver;

import java.util.List;

import javax.sql.DataSource;

import org.ensembl.datamodel.CoordinateSystem;

/**
 * Provides access to coordinate system and meta data
 * in the datasource(s).
 * 
 * By default meta data is only read from the datasource
 * returned by getDataSource(). Meta tables from additional 
 * datasources will also be read if they are added via
 * addDataSource(DataSource).
 */
public interface CoordinateSystemAdaptor extends Adaptor {

  /**
   * Store the CoordinateSystem in the approriate data source and set the internalID.
   * @return internalID of the stored CoordinateSystem.
   */
  long store(CoordinateSystem cs) throws AdaptorException;

  /**
   * Fetch a CoordinateSystem by its internalID.
   * @return A CoordinateSystem matching the internalID, or null if non found.
   */
  CoordinateSystem fetch(long internalID) throws AdaptorException;

  /**
   * Fetch a CoordinateSystem by name and version.
   * @return The coordinate system object represented by name and version, or null.
   */
  CoordinateSystem fetch(String name, String version) throws AdaptorException;

  /**
   * Fetch a CoordinateSystem from the database.
   * @param skeletonCS A CoordinateSystem object created via the constructor, with only name
   * (and optional version) set.
   * @return A CoordinateSystem object representing a row from the database, and hence
   * having name, version and internal ID set. If there is no corresponding database
   * row, null is returned.
   */
  CoordinateSystem fetchComplete(CoordinateSystem skeletonCS)
    throws AdaptorException;

  /**
   * Fetch all the coordinate systems defined for this species.
   * @return An array of CoordinateSystem objects representing all the coordinate
   * systems defined for this species.
   */
  CoordinateSystem[] fetchAll() throws AdaptorException;

  /**
   * Fetch the co-ordinate system that represents the sequence level for this species.
   * If more than one co-ordinate system is defined as being the top level, 
   * this will return the one which is defined as being the default. Only one
   * co-ordinate system should be defined as being default and top level.
   * @return The coordinate system that represents sequence for this species.
   */
  CoordinateSystem fetchSequenceLevel() throws AdaptorException;

  /**
   * Get the mapping path between two co-ordinate systems, as defined in the 
   * meta table. 
   * @param cs1 The first co-ordinate system.
   * @param cs2 The second co-ordinate system.
   * @return Array of CoordinateSystems in order from assembled to smaller component
   * co-ordinate systems.
   */
  CoordinateSystem[] getMappingPath(CoordinateSystem cs1, CoordinateSystem cs2)
    throws AdaptorException;

  /**
   * This function supports old style Locations with Maps. Maps are essentially CoordinateSystems
   * so we retrieve one by mapname
   * @param map a Map identifier 
   * @return the equivalent CoordinateSystem object
   * @throws AdaptorException
   */
  CoordinateSystem fetchByMap(String map) throws AdaptorException;

  /**
   * Name of the default CoordinateSystemAdaptor available from a driver.
   */
  final static String TYPE = "CoordinateSystem";

  /**
   * Retrieve a list of the coordinate systems in which features in a particular table
   * are stored.
   * @param featureTableName The name of the feature table to query.
   * @return An array of the co-ordinate systems used in this feature table.
   * @throws AdaptorException if there is a problem communicating with the database,
   * or if featureTableName does not refer to an existing feature table.
   */
  CoordinateSystem[] fetchAllByFeatureTable(String featureTableName)
    throws AdaptorException;


  /**
   * There is one top level location for each sequence region in the highest ranked 
   * coordinate system.
   * 
   * @return zero or more top level locations.
   * @throws AdaptorException
   */
  List fetchTopLevelLocations() throws AdaptorException;
  
  
  /**
   * Add an additional datasource to retrieve meta information
   * from.
   * @param dataSource source of additional information.
   */
  void addDataSource(DataSource dataSource);
  
  /**
   * Removes additional datasource.
   * @param dataSource source to be removed.
   * @return true of datasource removed, otherwise false.
   */
  boolean removeDataSource(DataSource dataSource);
  
  /**
   * All datasources in addition to the default one
   * available via getDataSource().
   * @return zero or more datasources.
   * @throws AdaptorException
   */
  List getAllDataSources() throws AdaptorException;

}
