multiloader {
    setMREnvironment(mrEnvs.clientOnly)
    setCFEnvironment(cfEnvs.client)

    configureLoaders {
        val animationLoader = when {
            isCommon -> "Common"
            isFabric -> "Fabric"
            else -> "Neo"
        }

        addDependency(
            dependency = "com.zigythebird.playeranim:PlayerAnimationLib$animationLoader:${getDep("player-animation-library")}+mc.${mod.mc}",
            repository = "repo.redlance.org/public",
            isPublishDepEnabled = true,
            isPublishDepRequired = true,
            excludedModules = listOf("com.google.code.gson", "org.slf4j", "io.netty", "it.unimi.dsi", "org.joml"),
            publishProjectId = "player-animation-library"
        )

        if (isFabric) {
            addDependency(
                dependency = "net.fabricmc:fabric-loader:${getDep("fabric")}"
            )
            addDependency(
                dependency = "net.fabricmc.fabric-api:fabric-api:${getDep("fabric-api")}",
                isPublishDepEnabled = true,
                isPublishDepRequired = true
            )
        }
    }
}
