//import be.seeseemelk.mockbukkit.MockBukkit
//import be.seeseemelk.mockbukkit.ServerMock
//import com.shampaggon.crackshot.CSDirector
//import kr.sul.crackshotaddition.CrackShotAddition
//import kr.sul.servercore.ServerCore
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//
//class WeaponTest {
//    lateinit var server: ServerMock
//    lateinit var crackShotPlugin: CSDirector
//    lateinit var serverCorePlugin: ServerCore
//    lateinit var plugin: CrackShotAddition
//
//    @Before
//    fun setUp() {
//        server = MockBukkit.mock()
//        crackShotPlugin = MockBukkit.load(CSDirector::class.java) as CSDirector
//        serverCorePlugin = MockBukkit.load(ServerCore::class.java) as ServerCore
//        plugin = MockBukkit.load(CrackShotAddition::class.java) as CrackShotAddition
//    }
//
//    @Test
//    fun itemTest() {
//        val p = server.addPlayer("tester")
////        val item = ItemStack(Material.DIAMOND)
////        p.inventory.addItem(item)
////        println(p.inventory.getItem(0))
//    }
//
//
//    @After
//    fun tearDown() {
//        MockBukkit.unload()
//    }
//}