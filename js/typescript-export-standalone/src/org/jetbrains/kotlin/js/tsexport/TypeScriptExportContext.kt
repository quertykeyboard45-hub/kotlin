/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.tsexport

import org.jetbrains.kotlin.KtSourceFile
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.jetbrains.kotlin.diagnostics.DiagnosticContext
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnostic
import org.jetbrains.kotlin.diagnostics.KtSourcelessDiagnosticFactory
import org.jetbrains.kotlin.diagnostics.report

internal class TypeScriptExportContext(
    val config: TypeScriptExportConfig,
    val diagnosticReporter: DiagnosticReporter,
) : DiagnosticContext {
    private val reportedDiagnostics = hashSetOf<Triple<KtSourcelessDiagnosticFactory, String, CompilerMessageSourceLocation?>>()

    // Those two properties are not used inside the TypeScript standalone exporter
    override val containingFile: KtSourceFile? get() = null
    override val languageVersionSettings: LanguageVersionSettings get() = LanguageVersionSettingsImpl.DEFAULT

    override fun isDiagnosticSuppressed(diagnostic: KtDiagnostic): Boolean = false

    /**
     * KLIBs don't contain the exact source positions of the declarations, so the best location we can provide is the source file
     * of the top-level declaration that is currently being exported.
     */
    private var currentLocation: CompilerMessageSourceLocation? = null

    fun <T> withLocation(location: CompilerMessageSourceLocation?, block: () -> T): T {
        val previousLocation = currentLocation
        currentLocation = location
        try {
            return block()
        } finally {
            currentLocation = previousLocation
        }
    }

    /**
     * The same failure type may be met many times while exporting a single file. Because, we don't have info about lines and columns,
     * to not spam with identical diagnostics on a file the identical diagnostics are reported only once.
     */
    fun report(factory: KtSourcelessDiagnosticFactory, message: String = "") {
        val location = currentLocation
        if (reportedDiagnostics.add(Triple(factory, message, location))) {
            diagnosticReporter.report(factory, message, location)
        }
    }
}
