package com.example.smriti.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smriti.data.SmritiRepository
import com.example.smriti.ui.theme.EmeraldGreen
import com.example.smriti.ui.theme.ForestGreen
import com.example.smriti.ui.theme.MintPastel
import com.example.smriti.ui.theme.PineGreen

@Composable
fun LoginScreen(
    onLoginSuccess: (role: String) -> Unit
) {
    var selectedRole by remember { mutableStateOf("patient") }
    var email by remember { mutableStateOf("patient@smriti.care") }
    var password by remember { mutableStateOf("smriti123") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F4))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // App Icon Emblem
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(PineGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = "Smriti Logo",
                tint = Color.White,
                modifier = Modifier.size(62.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Smriti (स्मृति)",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreen
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Cognitive Care & Memory Assistance Platform",
            fontSize = 15.sp,
            color = EmeraldGreen,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Role Selector Tabs (Patient / Caregiver / Admin)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                RoleTabItem(
                    label = "Patient",
                    isSelected = selectedRole == "patient",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        selectedRole = "patient"
                        email = "patient@smriti.care"
                    }
                )
                RoleTabItem(
                    label = "Caregiver",
                    isSelected = selectedRole == "caregiver",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        selectedRole = "caregiver"
                        email = "caregiver@smriti.care"
                    }
                )
                RoleTabItem(
                    label = "Admin",
                    isSelected = selectedRole == "admin",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        selectedRole = "admin"
                        email = "admin@smriti.care"
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = PineGreen) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PineGreen,
                unfocusedBorderColor = Color(0xFFCBD5E1),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("email_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PineGreen) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PineGreen,
                unfocusedBorderColor = Color(0xFFCBD5E1),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("password_input")
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Login Button
        Button(
            onClick = {
                SmritiRepository.setRole(selectedRole)
                onLoginSuccess(selectedRole)
            },
            colors = ButtonDefaults.buttonColors(containerColor = PineGreen),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .testTag("login_button")
        ) {
            Text(
                text = "Enter Smriti (${selectedRole.replaceFirstChar { it.uppercase() }})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Instant Demo Access Button
        TextButton(
            onClick = {
                SmritiRepository.setRole("patient")
                onLoginSuccess("patient")
            },
            modifier = Modifier.testTag("quick_patient_access")
        ) {
            Text(
                text = "Continue Directly as Patient (One-Tap Access)",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = PineGreen
            )
        }
    }
}

@Composable
private fun RoleTabItem(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PineGreen else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.DarkGray
        )
    }
}
