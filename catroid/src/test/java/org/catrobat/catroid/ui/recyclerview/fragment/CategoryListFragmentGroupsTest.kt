package org.catrobat.catroid.ui.recyclerview.fragment

import android.content.Context

import org.catrobat.catroid.ui.recyclerview.fragment.CategoryListFragment.CategoryGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CategoryListFragmentGroupsTest {

    private fun groupsFor(tag: String): List<CategoryGroup> {
        val context: Context = RuntimeEnvironment.getApplication()
        return CategoryListFragment.getCategoryGroups(context, tag)
    }

    private fun pairsMatch(groups: List<CategoryGroup>, tag: String) {
        for (group in groups) {
            val params = group.paramResIds
            if (params == null) {
                continue
            }
            assertEquals(
                "'${group.header}' ($tag): functions=${group.nameResIds.size} params=${params.size}",
                group.nameResIds.size,
                params.size
            )
        }
    }

    @Test
    fun function_groups_have_matching_param_counts() {
        val groups = groupsFor(CategoryListFragment.FUNCTION_TAG)
        assertTrue(groups.isNotEmpty())
        pairsMatch(groups, CategoryListFragment.FUNCTION_TAG)
    }

    @Test
    fun object_groups_have_matching_param_counts() {
        val groups = groupsFor(CategoryListFragment.OBJECT_TAG)
        assertTrue(groups.isNotEmpty())
        pairsMatch(groups, CategoryListFragment.OBJECT_TAG)
    }

    @Test
    fun sensor_groups_have_matching_param_counts() {
        val groups = groupsFor(CategoryListFragment.SENSOR_TAG)
        assertTrue(groups.isNotEmpty())
        pairsMatch(groups, CategoryListFragment.SENSOR_TAG)
    }

    @Test
    fun no_group_lists_the_same_function_twice() {
        for (tag in listOf(
            CategoryListFragment.FUNCTION_TAG,
            CategoryListFragment.OBJECT_TAG,
            CategoryListFragment.SENSOR_TAG
        )) {
            for (group in groupsFor(tag)) {
                val duplicates = group.nameResIds.groupingBy { it }.eachCount()
                    .filterValues { it > 1 }.keys
                assertTrue(
                    "'${group.header}' ($tag) lists duplicates: $duplicates",
                    duplicates.isEmpty()
                )
            }
        }
    }
}
