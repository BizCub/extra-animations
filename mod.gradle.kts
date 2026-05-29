import com.bizcub.multiloader.MultiLoader
import dev.kikugie.stonecutter.build.StonecutterBuildExtension

apply(plugin = "dev.kikugie.fletching-table")

val stonecutter = project.extensions.getByType(StonecutterBuildExtension::class.java)

project.extensions.configure<MultiLoader>("multiloader") {
    access()

    project.afterEvaluate {
        stonecutter.let { sc ->
        }
    }

    addDependency("repo.redlance.org/public", "implementation", "com.zigythebird.playeranim:PlayerAnimationLib${if (isFabric) "Fabric" else "Neo"}:${getDep("player-animation-library")}+mc.${getMinCompatVersion(mod.mc)}")

    if (isFabric) {
        addDependency("implementation", "net.fabricmc:fabric-loader:${getDep("fabric")}")
        addDependency("implementation", "net.fabricmc.fabric-api:fabric-api:${getDep("fabric-api")}")
    }

    if (isFabric) addPublishDep("requires", "fabric-api")
    addPublishDep("requires", "player-animation-library")
}
