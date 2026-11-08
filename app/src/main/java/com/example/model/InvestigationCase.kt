package com.example.model

data class InvestigationCase(
    val id: String,
    val caseNumber: String,
    val title: String,
    val leadInvestigator: String,
    val jurisdiction: Jurisdiction,
    val status: String, // Active, Escalated, Triage, Closed
    val progressPercentage: Int,
    val suspectIds: List<String>,
    val evidenceCount: Int,
    val synopsis: String,
    val openedDate: String,
    val crossAgencyCooperation: List<String>
)
