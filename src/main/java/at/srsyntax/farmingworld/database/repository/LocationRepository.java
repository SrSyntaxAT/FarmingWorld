package at.srsyntax.farmingworld.database.repository;

import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.api.farmworld.LocationCache;
import org.bukkit.Location;

import java.util.Map;

/*
 * CONFIDENTIAL
 *  Unpublished Copyright (c) 2022 Marcel Haberl, All Rights Reserved.
 *
 * NOTICE:
 * All information contained herein is, and remains the property of Marcel Haberl. The intellectual and
 * technical concepts contained herein are proprietary to Marcel Haberl and may be covered by U.S. and Foreign
 * Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of this
 * information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from Marcel Haberl.  Access to the source code contained herein is hereby forbidden to anyone without written
 * permission Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure  of  this source code,
 * which includes information that is confidential and/or proprietary, and is a trade secret, of Marcel Haberl.
 * ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE, OR PUBLIC DISPLAY OF OR THROUGH USE  OF THIS
 * SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF Marcel Haberl IS STRICTLY PROHIBITED, AND IN VIOLATION OF
 * APPLICABLE LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED
 * INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO
 * MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.
 */
public interface LocationRepository {

    void save(FarmWorld farmWorld, String id, Location location);
    void delete(String id);
    void delete(FarmWorld farmWorld);
    Map<String, LocationCache> getLocations(FarmWorld farmWorld);
    Map<String, Map<String, LocationCache>> getLocations();
}
