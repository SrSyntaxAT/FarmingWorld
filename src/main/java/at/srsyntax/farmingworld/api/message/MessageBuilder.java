package at.srsyntax.farmingworld.api.message;

import java.util.LinkedList;

/*
 * CONFIDENTIAL
 *  Unpublished Copyright (c) 2022 Sytonix, All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of Sytonix. The intellectual and
 * technical concepts contained herein are proprietary to Sytonix and may be covered by U.S. and Foreign
 * Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of this
 * information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from Sytonix.  Access to the source code contained herein is hereby forbidden to anyone without written
 * permission Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure  of  this source code,
 * which includes information that is confidential and/or proprietary, and is a trade secret, of Sytonix.
 * ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE, OR PUBLIC DISPLAY OF OR THROUGH USE  OF THIS
 * SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF Sytonix IS STRICTLY PROHIBITED, AND IN VIOLATION OF
 * APPLICABLE LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED
 * INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO
 * MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.
 */
public class MessageBuilder {
  
  private final LinkedList<String> lines = new LinkedList<>();
  private final Message message = new Message();
  
  public MessageBuilder addLine(String line) {
    this.lines.add(line);
    return this;
  }
  
  public MessageBuilder addReplace(String key, String value) {
    this.message.add(key, value);
    return this;
  }
  
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    for (String line : lines)
      builder.append(line).append("\n");
    return new Message(builder.toString()).replace();
  }
}
