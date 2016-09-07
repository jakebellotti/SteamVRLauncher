DROP ALL OBJECTS;

CREATE TABLE tblSteamFolder(
	fldID									INTEGER PRIMARY KEY AUTO_INCREMENT,
	fldFolderLocation						VARCHAR(255) NOT NULL UNIQUE
)	ENGINE = INNODB;

CREATE TABLE tblSteamVRManifestFile(
	fldID									INTEGER PRIMARY KEY AUTO_INCREMENT,
	fldFKSteamFolderID						INTEGER NOT NULL UNIQUE,
	fldFileLocation							VARCHAR(255) NOT NULL UNIQUE,
	fldLastModified							BIGINT NOT NULL,
	FOREIGN KEY(fldFKSteamFolderID)			REFERENCES tblSteamFolder(fldID) ON DELETE CASCADE,
)	ENGINE = INNODB;

CREATE TABLE tblImage(
	fldID									INTEGER PRIMARY KEY AUTO_INCREMENT,
	fldImageData							BLOB NOT NULL
)	ENGINE = INNODB;

CREATE TABLE tblSteamApp(
	fldID									INTEGER PRIMARY KEY AUTO_INCREMENT,
	fldFKSteamVRManifestFileID				INTEGER NOT NULL,
	fldFKSelectedImageID					INTEGER DEFAULT NULL,
	fldAppKey								VARCHAR(255) NOT NULL,
	fldLaunchType							VARCHAR(255) NOT NULL,
	fldName									VARCHAR(255) NOT NULL,
	fldImagePath							VARCHAR(255) NOT NULL,
	fldLaunchPath							VARCHAR(255) NOT NULL,
	UNIQUE INDEX(fldFKSteamVRManifestFileID, fldName),
	FOREIGN KEY(fldFKSelectedImageID)		REFERENCES tblImage(fldID),
	FOREIGN KEY(fldFKSteamVRManifestFileID)	REFERENCES tblSteamVRManifestFile(fldID)  ON DELETE CASCADE
) ENGINE = INNODB;

CREATE TABLE tblSteamAPPVRSettings(
	fldFKSteamAppID							INTEGER PRIMARY KEY,
	fldRenderTargetMultiplier				INTEGER DEFAULT 10,
	fldAllowReprojection					BOOLEAN DEFAULT FALSE,
	FOREIGN KEY(fldFKSteamAppID) 			REFERENCES tblSteamApp(fldID)
)	ENGINE = INNODB;

/*TODO add the date of the modification*/
CREATE TABLE tblFileModificationHistory(
	fldID									INTEGER PRIMARY KEY AUTO_INCREMENT,
	fldFileLocation							VARCHAR(255) NOT NULL,
	fldChangeComment						VARCHAR(999999) NOT NULL,
	fldOldContent							VARCHAR(999999) NOT NULL,
	fldNewContent							VARCHAR(999999) NOT NULL
)	ENGINE = INNODB;











