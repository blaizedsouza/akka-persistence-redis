package com.hootsuite.akka.persistence.redis.snapshot

import akka.persistence.snapshot.SnapshotStoreSpec
import com.hootsuite.akka.persistence.redis.SentinelUtils
import com.typesafe.config.ConfigFactory
import redis.RedisClient

/**
 * Akka Persistence Snapshot TCK tests provided by Akka
 */
class RedisSnapshotSpec extends SnapshotStoreSpec(
  config = ConfigFactory.parseString(
    """
      |akka.persistence.snapshot-store.plugin = "akka-persistence-redis.snapshot"
      |akka-persistence-redis.snapshot.class = "com.hootsuite.akka.persistence.redis.snapshot.RedisSnapshotStore"
    """.stripMargin)
) {

  override def beforeAll() = {
    super.beforeAll()
    val config = ConfigFactory.load()
    val sentinel = config.getBoolean("redis.sentinel")

    lazy val redis = if(sentinel){
      SentinelUtils.getSentinelBasedClient(config)
    } else {
      val host = config.getString("redis.host")
      val port = config.getInt("redis.port")
      new RedisClient(host, port)
    }

    // Clean up database to prevent data from previous tests interfering with current run
    redis.flushall()
  }
}

