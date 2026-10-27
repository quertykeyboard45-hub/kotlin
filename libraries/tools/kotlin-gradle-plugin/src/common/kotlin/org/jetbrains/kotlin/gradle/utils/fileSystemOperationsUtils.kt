/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.utils

import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.FileTree
import org.gradle.api.file.RelativePath
import java.io.File

/**
 * Copies the contents of [directoryContent] into [destination], dropping the top-level directory segment.
 *
 * [directoryContent] is expected to be rooted at a single top-level directory.
 */
internal fun FileSystemOperations.copyDirectoryContentWithoutTopLevelSegment(directoryContent: FileTree, destination: File) {
    copy { copy ->
        copy.from(directoryContent) { wit ->
            wit.includeEmptyDirs = false
            wit.eachFile { file ->
                file.relativePath = RelativePath(
                    true,
                    *file.relativePath.segments.drop(1).toTypedArray()
                )
            }
        }
        copy.into(destination)
    }
}
