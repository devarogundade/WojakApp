package dev.arogundade.wojak

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.arogundade.wojak.databinding.ActivityMainBinding
import dev.arogundade.wojak.storage.WojakDatastore
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var datastore: WojakDatastore
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navInflater = navHostFragment.navController.navInflater
        val graph = navInflater.inflate(R.navigation.nav_graph)

        lifecycleScope.launch {
            datastore.observe().distinctUntilChanged().collect {
                if (it.firstTime == null) {
                    graph.setStartDestination(R.id.onBoardFragment)
                } else {
                    graph.setStartDestination(R.id.homeFragment)
                }
                navHostFragment.navController.graph = graph
            }
        }

    }

}