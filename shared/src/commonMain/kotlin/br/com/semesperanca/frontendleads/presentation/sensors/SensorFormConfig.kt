package br.com.semesperanca.frontendleads.presentation.sensors

import br.com.semesperanca.frontendleads.presentation.ui.form.FormField
import br.com.semesperanca.frontendleads.presentation.ui.form.FormSection

val sensorFormSections: List<FormSection> = listOf(
    FormSection(
        fields = listOf(
            FormField(
                key = "propertyId",
                label = "Imóvel (ID)",
                required = true,
                placeholder = "UUID do imóvel",
            ),
            FormField(
                key = "deviceId",
                label = "Dispositivo",
                required = true,
                placeholder = "ex.: SENSOR-01",
                maxLength = 64,
            ),
        ),
    ),
)

val sensorFormFields: List<FormField> = sensorFormSections.flatMap { it.fields }

fun sensorFormDefaults(): Map<String, String> = emptyMap()
