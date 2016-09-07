package jakebellotti.steamvrlauncher.model;

import java.io.File;
/**
 * 
 * @author Jake Bellotti
 *
 */
public class FileModificationHistory {

	private final int id;
	private final String fileLocation;
	private final String changeComment;
	private final String oldContent;
	private final String newContent;

	public FileModificationHistory(int id, String fileLocation, String changeComment, String oldContent, String newContent) {
		super();
		this.id = id;
		this.fileLocation = fileLocation;
		this.changeComment = changeComment;
		this.oldContent = oldContent;
		this.newContent = newContent;
	}
	
	public File asFile() {
		return new File(fileLocation);
	}
	
	@Override
	public String toString() {
		//TODO complete this
		return "'" + asFile().getName() + "' - " + changeComment;
	}

	public int getId() {
		return id;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public String getChangeComment() {
		return changeComment;
	}

	public String getOldContent() {
		return oldContent;
	}

	public String getNewContent() {
		return newContent;
	}

}
