import com.bizcub.multiloader.MultiLoader
import dev.kikugie.stonecutter.build.StonecutterBuildExtension

apply(plugin = "dev.kikugie.fletching-table")

val stonecutter = project.extensions.getByType(StonecutterBuildExtension::class.java)

project.extensions.configure<MultiLoader>("multiloader") {
    access()

    project.afterEvaluate {
        stonecutter.let { sc ->
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
        }
    }

    addDependency("repo.redlance.org/public", "implementation", "com.zigythebird.playeranim:PlayerAnimationLib${if (isFabric) "Fabric" else "Neo"}:${getDep("player-animation-library")}+mc.${if (scp eq "1.21.8") mod.mc else getMinCompatVersion(mod.mc)}")

    if (isFabric) {
        addDependency("implementation", "net.fabricmc:fabric-loader:${getDep("fabric")}")
        addDependency("implementation", "net.fabricmc.fabric-api:fabric-api:${getDep("fabric-api")}")
    }

    if (isFabric) addPublishDep("requires", "fabric-api")
    addPublishDep("requires", "player-animation-library")
}
