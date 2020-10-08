package org.jellyfin.mobile.ui.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import kotlin.math.max
import kotlin.math.min

interface GridScope {
    fun Modifier.fillItemMaxWidth(): Modifier
}

@Composable
fun <T> GridListFor(
    items: List<T>,
    modifier: Modifier = Modifier,
    numberOfColumns: Int = 2,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    itemContent: @Composable GridScope.(T) -> Unit
) {
    WithConstraints {
        val maxItemWidth = with(DensityAmbient.current) {
            constraints.maxWidth.toDp() / numberOfColumns
        }
        val gridScope = GridScopeImpl(maxItemWidth)

        // TODO: we really want an actual grid composable here
        LazyColumnFor(
            items = GridList(items, numberOfColumns),
            modifier = modifier,
            contentPadding = contentPadding,
            horizontalAlignment = horizontalAlignment,
        ) { row ->
            Row(Modifier.fillParentMaxWidth()) {
                row.fastForEach { info ->
                    gridScope.itemContent(info)
                }
            }
        }
    }
}

private data class GridScopeImpl(val maxWidth: Dp) : GridScope {
    override fun Modifier.fillItemMaxWidth() = width(maxWidth)
}

@Stable
internal class GridList<T>(private val wrappedList: List<T>, private val numberOfColumns: Int) : List<List<T>> {
    override val size: Int
        get() {
            val size = wrappedList.size / numberOfColumns
            return if (wrappedList.size % numberOfColumns == 0) size else size + 1
        }

    override fun get(index: Int): List<T> {
        val startIndex = index * numberOfColumns
        return wrappedList.subList(startIndex, min(startIndex + 3, max(0, wrappedList.size - 1)))
    }

    override fun isEmpty(): Boolean = wrappedList.isEmpty()

    override fun iterator(): Iterator<List<T>> = object : Iterator<List<T>> {
        override fun hasNext(): Boolean = false
        override fun next(): List<T> = throw UnsupportedOperationException()
    }

    override fun contains(element: List<T>) = false
    override fun containsAll(elements: Collection<List<T>>): Boolean = false
    override fun indexOf(element: List<T>): Int = -1
    override fun lastIndexOf(element: List<T>): Int = -1
    override fun listIterator(): ListIterator<List<T>> = throw UnsupportedOperationException()
    override fun listIterator(index: Int): ListIterator<List<T>> = throw UnsupportedOperationException()
    override fun subList(fromIndex: Int, toIndex: Int): List<List<T>> = throw UnsupportedOperationException()
}
