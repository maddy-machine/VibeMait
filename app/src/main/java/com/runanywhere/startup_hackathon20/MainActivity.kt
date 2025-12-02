package com.runanywhere.startup_hackathon20

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.runanywhere.startup_hackathon20.ui.theme.EventPlannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventPlannerTheme {
                val eventViewModel: EventViewModel = viewModel()
                val chatViewModel: ChatViewModel = viewModel()
                EventPlannerApp(eventViewModel, chatViewModel)
            }
        }
    }
}

@Composable
fun EventPlannerApp(
    eventViewModel: EventViewModel,
    chatViewModel: ChatViewModel
) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "event_list") {
        composable("event_list") {
            EventListScreen(
                events = eventViewModel.events,
                onEventClick = { eventId ->
                    navController.navigate("event_detail/$eventId")
                },
                onAddEvent = { navController.navigate("add_event") }
            )
        }

        composable("add_event") {
            AddEventScreen(
                onEventCreated = { event ->
                    eventViewModel.addEvent(event)
                    navController.popBackStack()
                }
            )
        }

        composable("event_detail/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            EventDetailScreen(
                event = eventViewModel.events.find { it.id == eventId },
                tasks = eventViewModel.getTasksForEvent(eventId),
                onToggleTask = { taskId -> eventViewModel.toggleTask(eventId, taskId) },
                chatViewModel = chatViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    events: List<Event>,
    onEventClick: (String) -> Unit,
    onAddEvent: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("My Events") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEvent) {
                Icon(Icons.Default.Add, "Add Event")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(events) { event ->
                EventCard(event = event, onClick = { onEventClick(event.id) })
            }
        }
    }
}

@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(event.name, style = MaterialTheme.typography.headlineSmall)
            Text("${event.type} • ${event.date}", style = MaterialTheme.typography.bodyMedium)
            if (event.budget.isNotEmpty()) {
                Text("Budget: ₹${event.budget}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(onEventCreated: (Event) -> Unit) {
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(EventType.BIRTHDAY) }
    var budget by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())) {
        TextField(value = name, onValueChange = { name = it }, label = { Text("Event Name") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = date, onValueChange = { date = it }, label = { Text("Date (e.g., Dec 15, 2025)") })
        Spacer(modifier = Modifier.height(8.dp))

        Text("Event Type:")
        EventType.entries.forEach { type ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = selectedType == type, onClick = { selectedType = type })
                Text(type.name)
            }
        }

        TextField(value = budget, onValueChange = { budget = it }, label = { Text("Budget (₹)") })
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (name.isNotEmpty() && date.isNotEmpty()) {
                    onEventCreated(Event(name = name, date = date, type = selectedType, budget = budget))
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Event")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    event: Event?,
    tasks: List<Task>,
    onToggleTask: (String) -> Unit,
    chatViewModel: ChatViewModel,
    onBack: () -> Unit
) {
    if (event == null) return

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(event.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Tasks") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("AI Assistant") })
            }

            when (selectedTab) {
                0 -> TaskListTab(tasks = tasks, onToggleTask = onToggleTask)
                1 -> ChatTab(event = event, chatViewModel = chatViewModel)
            }
        }
    }
}

@Composable
fun TaskListTab(tasks: List<Task>, onToggleTask: (String) -> Unit) {
    LazyColumn {
        items(tasks) { task ->
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = task.isCompleted, onCheckedChange = { onToggleTask(task.id) })
                Column {
                    Text(task.title, style = if (task.isCompleted) MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.LineThrough) else MaterialTheme.typography.bodyMedium)
                    Text(task.category, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTab(event: Event, chatViewModel: ChatViewModel) {
    val messages by chatViewModel.messages.collectAsState()
    val isLoading by chatViewModel.isLoading.collectAsState()
    var userInput by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { message ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = if (message.isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 1.dp
                    ) {
                        Text(
                            text = message.text,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }



        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier.weight(1f),
                label = { Text("Ask for suggestions...") },
                enabled = !isLoading,
                maxLines = 3
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (userInput.isNotBlank()) {
                        chatViewModel.sendMessageWithContext(userInput, event)
                        userInput = ""
                    }
                },
                enabled = !isLoading
            ) {
                Text("Send")
            }
        }
    }
}
