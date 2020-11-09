package io.d2a.schwurbelwatch.tgcrawler.api.response;

import lombok.ToString;

/**
 * <pre>
 *   "affectedRows": 1,
 *   "insertId": 0,
 *   "warningStatus": 0
 * </pre>
 */
@ToString
public class DatabaseResult extends ErrorResult {

  public int affectedRows;
  public int insertId;
  public int warningStatus;

}