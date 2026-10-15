package org.catrobat.catroid.neoscript;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import org.catrobat.catroid.content.Project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class NeoScriptProjectFiles {

	public static final String MODULE_SUBDIR = "neoscript";

	private NeoScriptProjectFiles() {
	}

	public static String storePickedModule(Context context, Uri uri, Project project) throws IOException {
		if (context == null || uri == null || project == null || project.getDirectory() == null) {
			throw new IOException("Cannot store module");
		}
		String displayName = queryDisplayName(context.getContentResolver(), uri);
		if (displayName == null || displayName.trim().isEmpty()) {
			displayName = "module" + NeoScriptFile.EXTENSION;
		}
		displayName = sanitize(displayName);
		if (!displayName.toLowerCase().endsWith(NeoScriptFile.EXTENSION)) {
			displayName = displayName + NeoScriptFile.EXTENSION;
		}
		File dir = new File(new File(project.getDirectory(), "files"), MODULE_SUBDIR);
		if (!dir.exists() && !dir.mkdirs()) {
			throw new IOException("Cannot create directory");
		}
		File target = unique(new File(dir, displayName));
		ContentResolver resolver = context.getContentResolver();
		try (InputStream in = resolver.openInputStream(uri)) {
			if (in == null) {
				throw new IOException("Cannot open file");
			}
			try (OutputStream out = new FileOutputStream(target)) {
				byte[] chunk = new byte[8192];
				int read;
				while ((read = in.read(chunk)) != -1) {
					out.write(chunk, 0, read);
				}
			}
		}
		return "files/" + MODULE_SUBDIR + "/" + target.getName();
	}

	public static File resolveModuleFile(String path, Project project) {
		if (path == null || path.trim().isEmpty()) {
			return null;
		}
		String trimmed = path.trim();
		File direct = new File(trimmed);
		if (direct.isAbsolute() && direct.isFile()) {
			return direct;
		}
		if (project == null || project.getDirectory() == null) {
			return direct.isFile() ? direct : null;
		}
		File projectDir = project.getDirectory();
		File inside = new File(projectDir, trimmed);
		if (inside.isFile()) {
			return inside;
		}
		String base = new File(trimmed).getName();
		File bundled = new File(new File(projectDir, "files/" + MODULE_SUBDIR), base);
		if (bundled.isFile()) {
			return bundled;
		}
		return direct.isFile() ? direct : null;
	}

	private static String queryDisplayName(ContentResolver resolver, Uri uri) {
		try (Cursor cursor = resolver.query(uri, null, null, null, null)) {
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
				if (index >= 0) {
					return cursor.getString(index);
				}
			}
		} catch (Exception ignored) {
		}
		String last = uri.getLastPathSegment();
		return last != null ? last : null;
	}

	private static String sanitize(String name) {
		String clean = name.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
		if (clean.isEmpty()) {
			return "module" + NeoScriptFile.EXTENSION;
		}
		return clean;
	}

	private static File unique(File file) {
		if (!file.exists()) {
			return file;
		}
		String name = file.getName();
		int dot = name.lastIndexOf('.');
		String base = dot >= 0 ? name.substring(0, dot) : name;
		String ext = dot >= 0 ? name.substring(dot) : "";
		int counter = 1;
		while (true) {
			File candidate = new File(file.getParent(), base + "_" + counter + ext);
			if (!candidate.exists()) {
				return candidate;
			}
			counter++;
		}
	}
}
