/*************************************************************************
*                                                                        *
*   Module: SwitchPositions                                              *
*                                                                        *
*   Exchanging left and right signals in reverse sense exons             *
*                                                                        *
*   This file is part of the geneid 1.2 distribution                     *
*                                                                        *
*     Copyright (C) 2003 - Enrique BLANCO GARCIA                         *
*                          Roderic GUIGO SERRA                           * 
*                                                                        *
*  This program is free software; you can redistribute it and/or modify  *
*  it under the terms of the GNU General Public License as published by  *
*  the Free Software Foundation; either version 2 of the License, or     *
*  (at your option) any later version.                                   *
*                                                                        *
*  This program is distributed in the hope that it will be useful,       *
*  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
*  GNU General Public License for more details.                          *
*                                                                        *
*  You should have received a copy of the GNU General Public License     *
*  along with this program; if not, write to the Free Software           * 
*  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.             *
*************************************************************************/

/*  $Id: SwitchPositions.c,v 1.3 2003-11-05 15:11:00 eblanco Exp $  */

#include "geneid.h"

/* Move left and right signals to preserve the ordering: left < right */
/* Exons are then pieces with special properties to be assembled */
void SwitchPositions(packExons* allExons)
{
  long i;
  site *c;
 
  for (i=0;i<allExons->nInitialExons;i++) 
    {
      c = (allExons->InitialExons+i)->Acceptor; 
      (allExons->InitialExons+i)->Acceptor = 
	(allExons->InitialExons+i)->Donor;
      (allExons->InitialExons+i)->Donor = c;
    }

  for (i=0;i<allExons->nInternalExons;i++) 
    {
      c = (allExons->InternalExons+i)->Acceptor; 
      (allExons->InternalExons+i)->Acceptor = 
	(allExons->InternalExons+i)->Donor;
      (allExons->InternalExons+i)->Donor = c;
    }

  for (i=0;i<allExons->nTerminalExons;i++) 
    {
      c = (allExons->TerminalExons+i)->Acceptor; 
      (allExons->TerminalExons+i)->Acceptor = 
	(allExons->TerminalExons+i)->Donor;
      (allExons->TerminalExons+i)->Donor = c;
    }

  for (i=0;i<allExons->nSingles;i++) 
    {
      c = (allExons->Singles+i)->Acceptor; 
      (allExons->Singles+i)->Acceptor = 
	(allExons->Singles+i)->Donor;
      (allExons->Singles+i)->Donor = c;
    }

  for (i=0;i<allExons->nORFs;i++) 
    {
      c = (allExons->ORFs+i)->Acceptor; 
      (allExons->ORFs+i)->Acceptor = 
	(allExons->ORFs+i)->Donor;
      (allExons->ORFs+i)->Donor = c;
    }
}

   