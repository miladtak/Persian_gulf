package com.example.engine.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.engine.localization.EngineLanguage
import com.example.engine.model.EngineConstants
import com.example.engine.persistence.ProjectEntity
import com.example.engine.persistence.ProjectTemplates
import com.example.engine.persistence.TemplateProjectData
import com.example.engine.state.EngineUiState
import com.example.engine.state.GameEngineViewModel
import com.example.ui.theme.*

@Composable
fun ProjectSaveLoadDialog(
    uiState: EngineUiState,
    viewModel: GameEngineViewModel,
    savedProjects: List<ProjectEntity>,
    onDismiss: () -> Unit
) {
    val lang = uiState.language
    val isRtl = lang == EngineLanguage.PERSIAN
    var selectedTab by remember { mutableStateOf(0) } // 0: Templates, 1: Saved Projects, 2: Save Current, 3: Export/Import JSON

    var projectNameInput by remember { mutableStateOf("My Persian Gulf Game") }
    var persianNameInput by remember { mutableStateOf("بازی خلیج فارس من") }
    var importJsonText by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    val clipboardManager = LocalClipboardManager.current

    val templates = remember { ProjectTemplates.getStarterTemplates() }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .testTag("project_saveload_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = GulfNavyDark,
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = Brush.linearGradient(listOf(GulfGold, GulfCyan)),
                width = 1.5.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderSpecial,
                            contentDescription = "Projects",
                            tint = GulfGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = if (isRtl) "مدیریت و ذخیره‌سازی پروژه‌ها" else "Project Manager & Persistence",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("close_project_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = GulfNavyDeep,
                    contentColor = GulfGold
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text(if (isRtl) "قالب‌های آماده" else "Templates", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text(if (isRtl) "پروژه‌های ذخیره شده" else "Saved Projects", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text(if (isRtl) "ذخیره پروژه جاری" else "Save Project", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text(if (isRtl) "ورودی/خروجی JSON" else "JSON Export", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (statusMessage != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = GulfNavyBorder
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = GulfCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = statusMessage!!, color = TextPrimary, fontSize = 12.sp)
                        }
                    }
                }

                // Tab Content
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> TemplatesTab(
                            templates = templates,
                            isRtl = isRtl,
                            onLoadTemplate = { template ->
                                viewModel.loadProjectFromTemplate(template.id)
                                statusMessage = if (isRtl) "قالب ${template.persianTitle} با موفقیت بارگذاری شد!" else "Loaded ${template.title}!"
                            }
                        )
                        1 -> SavedProjectsTab(
                            projects = savedProjects,
                            isRtl = isRtl,
                            onLoad = { proj ->
                                viewModel.loadSavedProject(proj)
                                statusMessage = if (isRtl) "پروژه ${proj.persianTitle} بارگذاری شد!" else "Loaded ${proj.title}!"
                            },
                            onDelete = { proj ->
                                viewModel.deleteSavedProject(proj.projectId)
                                statusMessage = if (isRtl) "پروژه حذف شد." else "Project deleted."
                            }
                        )
                        2 -> SaveCurrentTab(
                            projectName = projectNameInput,
                            onProjectNameChange = { projectNameInput = it },
                            persianName = persianNameInput,
                            onPersianNameChange = { persianNameInput = it },
                            isRtl = isRtl,
                            onSave = {
                                viewModel.saveCurrentProject(projectNameInput, persianNameInput)
                                statusMessage = if (isRtl) "پروژه در دیتابیس بومی Room ذخیره شد!" else "Project saved to Room database!"
                                selectedTab = 1
                            }
                        )
                        3 -> JsonExportImportTab(
                            uiState = uiState,
                            viewModel = viewModel,
                            importText = importJsonText,
                            onImportTextChange = { importJsonText = it },
                            isRtl = isRtl,
                            onExportToClipboard = {
                                val json = viewModel.exportProjectToJson()
                                clipboardManager.setText(AnnotatedString(json))
                                statusMessage = if (isRtl) "کد JSON پروژه به کلیپ‌بورد کپی شد!" else "Project JSON copied to clipboard!"
                            },
                            onImportJson = {
                                val success = viewModel.importProjectFromJson(importJsonText)
                                statusMessage = if (success) {
                                    if (isRtl) "پروژه از JSON با موفقیت وارد شد!" else "Project successfully imported!"
                                } else {
                                    if (isRtl) "خطا در پردازش فایل JSON!" else "Error parsing JSON!"
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TemplatesTab(
    templates: List<TemplateProjectData>,
    isRtl: Boolean,
    onLoadTemplate: (TemplateProjectData) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(templates) { item ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, GulfNavyBorder, RoundedCornerShape(10.dp)),
                color = GulfNavyDeep
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isRtl) item.persianTitle else item.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GulfGold
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = GulfCyan.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = item.genre,
                                    color = GulfCyan,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isRtl) item.persianDescription else item.description,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = { onLoadTemplate(item) },
                        colors = ButtonDefaults.buttonColors(containerColor = GulfNavyLight),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(listOf(GulfGold, GulfCyan))
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = GulfGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isRtl) "بارگذاری" else "Load", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedProjectsTab(
    projects: List<ProjectEntity>,
    isRtl: Boolean,
    onLoad: (ProjectEntity) -> Unit,
    onDelete: (ProjectEntity) -> Unit
) {
    if (projects.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.FolderOpen, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isRtl) "هنوز پروژه‌ای ذخیره نشده است. به تب 'ذخیره پروژه' بروید." else "No saved projects yet. Go to 'Save Project' tab.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(projects) { proj ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, GulfNavyBorder, RoundedCornerShape(10.dp)),
                    color = GulfNavyDeep
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isRtl) proj.persianTitle else proj.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Author: ${proj.author} • v${proj.version}",
                                style = MaterialTheme.typography.bodySmall.copy(color = GulfCyan),
                                fontSize = 10.sp
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onLoad(proj) },
                                colors = ButtonDefaults.buttonColors(containerColor = GulfNavyLight),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(if (isRtl) "باز کردن" else "Open", fontSize = 11.sp)
                            }

                            IconButton(
                                onClick = { onDelete(proj) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SaveCurrentTab(
    projectName: String,
    onProjectNameChange: (String) -> Unit,
    persianName: String,
    onPersianNameChange: (String) -> Unit,
    isRtl: Boolean,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedTextField(
            value = projectName,
            onValueChange = onProjectNameChange,
            label = { Text("Project Title (English)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GulfGold,
                unfocusedBorderColor = GulfNavyBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        OutlinedTextField(
            value = persianName,
            onValueChange = onPersianNameChange,
            label = { Text("عنوان فارسی پروژه") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GulfGold,
                unfocusedBorderColor = GulfNavyBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(46.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GulfNavyLight),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = Brush.linearGradient(listOf(GulfGold, GulfCyan))
            )
        ) {
            Icon(Icons.Default.Save, contentDescription = null, tint = GulfGold)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isRtl) "ذخیره در دیتابیس پایدار Room" else "Save Project to Room Database",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun JsonExportImportTab(
    uiState: EngineUiState,
    viewModel: GameEngineViewModel,
    importText: String,
    onImportTextChange: (String) -> Unit,
    isRtl: Boolean,
    onExportToClipboard: () -> Unit,
    onImportJson: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = if (isRtl) "خروجی یا ورودی کامل ساختار پروژه به صورت فایل/کد JSON (.gulfproj)"
            else "Export or Import the full project structure as JSON (.gulfproj)",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Button(
            onClick = onExportToClipboard,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = GulfNavyDeep),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GulfGold, GulfCyan)))
        ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = GulfGold, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (isRtl) "کپی کد JSON پروژه به کلیپ‌بورد" else "Copy Project JSON to Clipboard")
        }

        OutlinedTextField(
            value = importText,
            onValueChange = onImportTextChange,
            label = { Text(if (isRtl) "کد JSON را اینجا جای‌گذاری کنید" else "Paste Project JSON here") },
            modifier = Modifier.fillMaxWidth().weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GulfCyan,
                unfocusedBorderColor = GulfNavyBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Button(
            onClick = onImportJson,
            modifier = Modifier.fillMaxWidth(),
            enabled = importText.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = GulfNavyLight)
        ) {
            Icon(Icons.Default.FileUpload, contentDescription = null, tint = GulfCyan)
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (isRtl) "بارگذاری پروژه از JSON" else "Import Project from JSON")
        }
    }
}
