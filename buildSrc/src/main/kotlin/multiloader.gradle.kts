plugins {
    id("me.modmuss50.mod-publish-plugin")
    id("dev.kikugie.fletching-table")
    id("com.bizcub.multiloader")
}

multiloader {
    sc.replacements {
        string(scp >= "26.2") {
            replace("item.is(ItemTags.LANTERNS)", "item.is(BlockItemTags.LANTERNS.item())")
        }
        string(scp >= "1.21.11") {
            replace("ResourceLocation", "Identifier")
        }
        string(scp >= "1.21.9") {
            replace("item.is(Items.LANTERN) || item.is(Items.SOUL_LANTERN)", "item.is(ItemTags.LANTERNS)")
        }
    }

    setMREnvironment(mrEnvs.clientOnly)
    setCFEnvironment(cfEnvs.client)

    addDependency(
        dependency = "com.zigythebird.playeranim:PlayerAnimationLib${if (isFabric) "Fabric" else "Neo"}:${getDep("player-animation-library")}+mc.${if (scp eq "1.21.8") mod.mc else getMinCompatVersion(mod.mc)}",
        repository = "repo.redlance.org/public",
        isPublishDepEnabled = true,
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
