package com.iamsmkr

import com.typesafe.config.ConfigFactory

package object primecommon {

  case class ServerConfig private(interface: String, port: Int)

  object ServerConfig {
    def apply(configStr: String): ServerConfig = {
      val proxyConfig = ConfigFactory.load().getConfig(configStr)
      val serverInterface = proxyConfig.getString("interface")
      val serverPort = proxyConfig.getInt("port")

      new ServerConfig(serverInterface, serverPort)
    }
  }

  case class InvalidNumberArg private(msg: String)

  def validateNumberArg(number: Long): Option[InvalidNumberArg] =
    if (number < 2) Some(InvalidNumberArg("Please provide a number greater than 1")) else None
}
