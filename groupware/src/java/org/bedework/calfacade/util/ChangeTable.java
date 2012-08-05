/* **********************************************************************
    Copyright 2007 Rensselaer Polytechnic Institute. All worldwide rights reserved.

    Redistribution and use of this distribution in source and binary forms,
    with or without modification, are permitted provided that:
       The above copyright notice and this permission notice appear in all
        copies and supporting documentation;

        The name, identifiers, and trademarks of Rensselaer Polytechnic
        Institute are not used in advertising or publicity without the
        express prior written permission of Rensselaer Polytechnic Institute;

    DISCLAIMER: The software is distributed" AS IS" without any express or
    implied warranty, including but not limited to, any implied warranties
    of merchantability or fitness for a particular purpose or any warrant)'
    of non-infringement of any current or pending patent rights. The authors
    of the software make no representations about the suitability of this
    software for any particular purpose. The entire risk as to the quality
    and performance of the software is with the user. Should the software
    prove defective, the user assumes the cost of all necessary servicing,
    repair or correction. In particular, neither Rensselaer Polytechnic
    Institute, nor the authors of the software are liable for any indirect,
    special, consequential, or incidental damages related to the software,
    to the maximum extent the law permits.
*/
package org.bedework.calfacade.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.bedework.calfacade.BwEvent;
import org.bedework.calfacade.BwVenue;
import org.bedework.calfacade.exc.CalFacadeException;
import org.bedework.calfacade.util.PropertyIndex.PropertyInfoIndex;

/** Class to track changes to calendar entities. CalDAV (and file uploads)
 * present a new copy of the event. From this we have to figure out what the
 * changes were and apply only those changes.
 *
 * <p>This is particularly important for multivalued fields where replacement of
 * the entire property can lead to a large number of deletions and reinsertions.
 *
 * @author Mike Douglass
 */
public class ChangeTable implements Serializable {
  private HashMap<String, ChangeTableEntry> map = new HashMap<String, ChangeTableEntry>();

  /**
   *
   */
  public ChangeTable() {
  }

  /** Set the changed and present flag on the named entry.
   *
   * @param name
   * @return boolean false if entry not found
   */
  public boolean changed(String name) {
    ChangeTableEntry ent = getEntry(name);

    if (ent != null) {
      ent.present = true;
      ent.changed = true;
      return true;
    }

    return false;
  }

  /** Set the present flag on the named entry.
   *
   * @param name
   * @return boolean false if entry not found
   */
  public boolean present(String name) {
    ChangeTableEntry ent = getEntry(name);

    if (ent != null) {
      ent.present = true;
      return true;
    }

    return false;
  }

  /** Return true if from is not the same as to and set the entry changed flag.
   *
   * @param name
   * @param from
   * @param to
   * @return boolean true if changed
   */
  public boolean changed(String name, Object from, Object to) {
    ChangeTableEntry ent = getEntry(name);

    boolean ch = changed(from, to);
    if (ent == null) {
      return ch;
    }

    ent.present = true;
    ent.changed = ch;
    if (to == null) {
      ent.deleted = true;
    }

    return ent.changed;
  }

  /**
   * @param name
   * @param val
   */
  public void addValue(String name, Object val) {
    ChangeTableEntry ent = getEntry(name);

    if (ent == null) {
      throw new RuntimeException("org.bedework.icalendar.notmultivalued");
    }

    ent.present = true;
    ent.addValue(val);
  }

  /**
   * @param name
   * @param val
   */
  public void addValues(String name, Collection val) {
    ChangeTableEntry ent = getEntry(name);

    if (ent == null) {
      throw new RuntimeException("org.bedework.icalendar.notmultivalued");
    }

    ent.present = true;
    ent.addValues(val);
  }

  /**
   * @param name
   * @return Collection of values or null
   */
  public Collection getValues(String name) {
    ChangeTableEntry ent = getEntry(name);

    if (ent == null) {
      throw new RuntimeException("org.bedework.icalendar.notmultivalued");
    }

    return ent.values;
  }

  /** Get the named entry
   *
   * @param name
   * @return Entry null if not found
   */
  public ChangeTableEntry getEntry(String name) {
    ChangeTableEntry ent = map.get(name);
    if (ent != null) {
      return ent;
    }

    ent = ChangeTableEntry.newEntry(name);
    if (ent != null) {
      map.put(name, ent);
      return ent;
    }

    // Presumably an unknown property - assume multi?

    ent = new ChangeTableEntry(true, name, true, true);
    map.put(name, ent);
    return ent;
  }

  /** Get the indexed entry
   *
   * @param index
   * @return Entry null if not found
   */
  public ChangeTableEntry getEntry(PropertyInfoIndex index) {
    return getEntry(PropertyIndex.propertyName(index));
  }

  /** Go through the change table entries removing fields that were not present
   * in the incoming data
   *
   * @param ev
   * @param update
   * @throws CalFacadeException
   */
  @SuppressWarnings("unchecked")
  public void processChanges(BwEvent ev,
                             boolean update) throws CalFacadeException {
    HashMap<String, ChangeTableEntry> fullmap =
      new HashMap<String, ChangeTableEntry>(map);

    for (PropertyInfoIndex pii: PropertyInfoIndex.values()) {
      String name = PropertyIndex.propertyName(pii);

      ChangeTableEntry ent = fullmap.get(name);
      if (ent == null) {
        ent = ChangeTableEntry.newEntry(name);
      }

      if (ent == null) {
//        warn("No entry for index " + pii + " name " +  name);
      } else {
        fullmap.put(name, ent);
      }
    }

    /* Single valued first */
    for (ChangeTableEntry ent: fullmap.values()) {
      if (ent.present || !ent.eventProperty) {
        continue;
      }

      switch (ent.index) {
      case CLASS:
        break;

      case COMPLETED:
        ev.setCompleted(null);
        break;

      case CREATED:
        break;

      case DESCRIPTION:
        if (ev.getDescription() != null) {
          ent.deleted = true;
          if (update) {
            ev.setDescription(null);
          }
        }
        break;

      case DTSTAMP:
        break;

      case DTSTART:
        break;

      case DURATION:
        break;

      case GEO:
        break;

      case LAST_MODIFIED:
        break;

      case LOCATION:
        if (ev.getLocation() != null) {
          ent.deleted = true;
          if (update) {
            ev.setLocation(null);
          }
        }
        break;

      case ORGANIZER:
        if (ev.getOrganizer() != null) {
          ent.deleted = true;
          if (update) {
            ev.setOrganizer(null);
          }
        }
        break;

      case PERCENT_COMPLETE:
        ev.setPercentComplete(null);
        break;

      case PRIORITY:
        ev.setPriority(null);
        break;

      case RECURRENCE_ID:
        break;

      case RELATED_TO:
        ev.setRelatedTo(null);
        break;

      case SEQUENCE:
        break;

      case STATUS:
        break;

      case SUMMARY:
        if (ev.getSummary() != null) {
          ent.deleted = true;
          if (update) {
            ev.setSummary(null);
          }
        }
        break;

      case UID:
        break;

      case URL:
        ev.setLink(null);
        break;

      case DTEND:
        break;

      case TRANSP:
        if (ev.getTransparency() != null) {
          ent.deleted = true;
          if (update) {
            ev.setTransparency(null);
          }
        }
        break;
      }
    }

    /* ---------------------------- Multi valued --------------- */

    for (ChangeTableEntry ent: fullmap.values()) {
      /* These can be present but we still need to delete members. */
      if (!ent.eventProperty) {
        continue;
      }

      Collection originalVals;

      switch (ent.index) {
      case ATTACH:
        originalVals = ev.getAttachments();
        if (checkMulti(ent, originalVals, update)) {
          ev.setAttachments(ent.addedValues);
        }
        break;

      case ATTENDEE:
        originalVals = ev.getAttendees();
        if (checkMulti(ent, originalVals, update)) {
          ev.setAttendees(ent.addedValues);
        }
        break;

      case CATEGORIES:
        originalVals = ev.getCategories();
        if (checkMulti(ent, originalVals, update)) {
          ev.setCategories(ent.addedValues);
        }
        break;

      case COMMENT:
        originalVals = ev.getComments();
        if (checkMulti(ent, originalVals, update)) {
          ev.setComments(ent.addedValues);
        }
        break;

      case CONTACT:
        originalVals = ev.getContacts();
        if (checkMulti(ent, originalVals, update)) {
          ev.setContacts(ent.addedValues);
        }
        break;

      case REQUEST_STATUS:
        originalVals = ev.getRequestStatuses();
        if (checkMulti(ent, originalVals, update)) {
          ev.setRequestStatuses(ent.addedValues);
        }
        break;

      case RELATED_TO:
        break;

      case RESOURCES:
        originalVals = ev.getResources();
        if (checkMulti(ent, originalVals, update)) {
          ev.setResources(ent.addedValues);
        }
        break;

      case VALARM:
        originalVals = ev.getAlarms();
        if (checkMulti(ent, originalVals, update)) {
          ev.setAlarms(ent.addedValues);
        }
        break;

      case XPROP:
        originalVals = ev.getXproperties();
        if (checkMulti(ent, originalVals, update)) {
          ev.setXproperties(ent.addedValues);
        }
        break;

      /* ---------------------------- Recurrence --------------- */

      case EXDATE:
        originalVals = ev.getExdates();
        if (checkMulti(ent, originalVals, update)) {
          ev.setExdates(ent.addedValues);
        }
        break;

      case EXRULE:
        originalVals = ev.getExrules();
        if (checkMulti(ent, originalVals, update)) {
          ev.setExrules(ent.addedValues);
        }
        break;

      case RDATE:
        originalVals = ev.getRdates();
        if (checkMulti(ent, originalVals, update)) {
          ev.setRdates(ent.addedValues);
        }
        break;

      case RRULE:
        originalVals = ev.getRrules();
        if (checkMulti(ent, originalVals, update)) {
          ev.setRrules(ent.addedValues);
        }
        break;
      }
    }
  }

  /** Go through the change table entries removing fields that were not present
   * in the incoming data
   *
   * @param val
   * @param update
   * @throws CalFacadeException
   */
  @SuppressWarnings("unchecked")
  public void processChanges(BwVenue val,
                             boolean update) throws CalFacadeException {
    /* Single valued first */
    for (ChangeTableEntry ent: map.values()) {
      if (ent.present || !ent.eventProperty) {
        continue;
      }

      switch (ent.index) {
      case COUNTRY:
        break;

      case CREATED:
        break;

      case DESCRIPTION:
        if (val.getDescription() != null) {
          ent.deleted = true;
          if (update) {
            val.setDescription(null);
          }
        }
        break;

      case DTSTAMP:
        break;

      case EXTENDEDADDRESS:
        break;

      case GEO:
        break;

      case LAST_MODIFIED:
        break;

      case LOCALITY:
        break;

      case NAME:
        break;

      case POSTALCODE:
        break;

      case REGION:
        break;

      case STREETADDRESS:
        break;

      case TEL:
        break;

      case UID:
        break;
      }
    }

    /* ---------------------------- Multi valued --------------- */

    for (ChangeTableEntry ent: map.values()) {
      /* These can be present but we still need to delete members. */
      if (!ent.eventProperty) {
        continue;
      }

      Collection originalVals;

      switch (ent.index) {
      case CATEGORIES:
        originalVals = val.getCategories();
        if (checkMulti(ent, originalVals, update)) {
          val.setCategories(ent.addedValues);
        }
        break;

      case LOCATIONTYPES:
        originalVals = val.getLocationTypes();
        if (checkMulti(ent, originalVals, update)) {
          val.setLocationTypes(ent.addedValues);
        }
        break;

      case TYPEDURLS:
        originalVals = val.getTypedUrls();
        if (checkMulti(ent, originalVals, update)) {
          val.setTypedUrls(ent.addedValues);
        }
        break;
      }
    }
  }

  /** True if any recurrence property changed.
   *
   * @return boolean true if changed
   */
  public boolean recurrenceChanged() {
    return getEntry(PropertyInfoIndex.EXDATE).changed ||
           getEntry(PropertyInfoIndex.EXRULE).changed ||
           getEntry(PropertyInfoIndex.RDATE).changed ||
           getEntry(PropertyInfoIndex.RRULE).changed;
  }

  /** True if any recurrence rules property changed.
   *
   * @return boolean true if changed
   */
  public boolean recurrenceRulesChanged() {
    return getEntry(PropertyInfoIndex.EXRULE).changed ||
    getEntry(PropertyInfoIndex.RRULE).changed;
  }

  /* ====================================================================
                      Private methods
     ==================================================================== */

  /* Return true if Collection needs to be set in the entity. adds and removes
   * are done here.
   */
  private boolean checkMulti(ChangeTableEntry ent, Collection originalVals,
                             boolean update) {
    diff(ent, originalVals);

    if (!ent.changed || !update) {
      return false;
    }

    if (originalVals == null) {
      return ent.addedValues != null;
    }

    if (ent.removedValues != null) {
      for (Object o: ent.removedValues) {
        originalVals.remove(o);
      }
    }

    if (ent.addedValues != null) {
      originalVals.addAll(ent.addedValues);
    }

    return false;
  }

  private void diff(ChangeTableEntry ent, Collection originalVals) {
    Collection newVals = ent.values;

    if (originalVals != null) {
      if (newVals == null) {
        ent.removedValues = new TreeSet();
        ent.removedValues.addAll(originalVals);
        ent.changed = true;
      } else {
        for (Object o: originalVals) {
          if (!newVals.contains(o)) {
            if (ent.removedValues == null) {
              ent.removedValues = new TreeSet();
            }

            ent.removedValues.add(o);
            ent.changed = true;
          }
        }
      }
    }

    if (newVals != null) {
      if (originalVals == null) {
        ent.addedValues = new TreeSet();
        ent.addedValues.addAll(newVals);
        ent.changed = true;
      } else {
        for (Object o: newVals) {
          if (!originalVals.contains(o)) {
            if (ent.addedValues == null) {
              ent.addedValues = new TreeSet();
            }

            ent.addedValues.add(o);
            ent.changed = true;
          }
        }
      }
    }
  }

  private boolean changed(Object from, Object to) {
    if (from == null) {
      if (to instanceof Collection) {
        if (to == null) {
          return false;
        }

        return !((Collection)to).isEmpty();
      }
      return to != null;
    }

    if (to == null) {
      if (from instanceof Collection) {
        return !((Collection)from).isEmpty();
      }
      return true;
    }

    return !from.equals(to);
  }

  /**
   * @return Logger
   */
  public Logger getLog() {
    return Logger.getLogger(this.getClass());
  }

  /**
   * @param msg
   */
  public void warn(String msg) {
    getLog().warn(msg);
  }

  /* ====================================================================
                      Object methods
     ==================================================================== */

  public String toString() {
    StringBuilder sb = new StringBuilder("ChangeTable{");

    for (ChangeTableEntry ent: map.values()) {
      if (ent.present) {
        sb.append("\n");
        sb.append(ent.name);
        if (ent.changed) {
          sb.append(": changed");
        }
        if (ent.deleted) {
          sb.append(": deleted");
        }
        if (ent.added) {
          sb.append(": added");
        }
      }
    }

    sb.append("}");
    return sb.toString();
  }
}

