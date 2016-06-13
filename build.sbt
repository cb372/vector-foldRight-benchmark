scalaVersion := "2.11.8"

lazy val root = (project in file(".")).enablePlugins(JmhPlugin)

javaOptions in Jmh ++= Seq("-server", "-Xms4G", "-Xmx4G", "-XX:+UseG1GC", "-XX:-UseBiasedLocking")
