/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.data.database.beans;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kitodo.api.imagemanagement.ImageManagementInterface;
import org.kitodo.forms.FolderGenerator;

/**
 * Stores configuration settings regarding a type of sub-folder in the process
 * directories of processes in a project.
 *
 * <p>
 * Typically, a folder has a corresponding sub-directory in the process
 * directory of each process and a {@code <fileGrp>} structure in the produced
 * METS file. The assumption is, that each folder contains the same number of
 * files with the same names, except for the file extension, which can vary.
 * This structure is used to represent different versions of the same object (a
 * small low quality JPEG thumbnail, a high quality JPEG, an OCR-processed PDF,
 * etc.) Each version is represented by one {@code Folder} object.
 *
 * <p>
 * The sub-directory can be located by appending the value of {@link #path} to
 * the path to the process directory. The {@code <fileGrp>} structure has the
 * {@code USE} attribute set to the value of {@link #fileGroup}. It contains
 * links to the files contained in the directory. The links are formed by
 * concatenating the {@link #urlStructure} with the simple name of the file.
 *
 * <p>
 * However, a {@code Folder} can also only exist on the drive without being
 * exported to METS. Or, it can exist only virtually without correspondence on a
 * drive, just to produce the METS {@code <fileGrp>} structure.
 */
@Entity
@Table(name = "folder")
public class Folder extends BaseBean {
    /**
     * Default {@code fileGrp}s supported by the DFG viewer. The list is used to
     * populate a combo box in the edit dialog.
     *
     * @see "https://www.zvdd.de/fileadmin/AGSDD-Redaktion/METS_Anwendungsprofil_2.0.pdf#page=12"
     */
    private static final List<String> DFG_VIEWER_FILEGRPS = Arrays.asList("DEFAULT", "MIN", "MAX", "THUMBS",
        "DOWNLOAD");
    private static final long serialVersionUID = -627255829641460322L;

    /**
     * Whether the folder is copied to the hotfolder during DMS import.
     */
    @Column(name = "copyFolder")
    private boolean copyFolder = true;

    /**
     * Whether the folder is created empty when a new process is created.
     */
    @Column(name = "createFolder")
    private boolean createFolder = true;

    /**
     * If not null, images in this folder can be generated by the function
     * {@link ImageManagementInterface#createDerivative(java.net.URI, double,
     * java.net.URI, org.kitodo.api.imagemanagement.ImageFileFormat)}.
     * The value is the factor of scaling for the derivative, a value of 1.0
     * indicates the original size.
     */
    @Column(name = "derivative")
    private Double derivative = null;

    /**
     * If not null, images in this folder can be generated by the function
     * {@link ImageManagementInterface#changeDpi(java.net.URI, int)}. The value
     * is the new DPI for the images.
     */
    @Column(name = "dpi")
    private Integer dpi = null;

    /**
     * {@code USE} identifier keyword for the METS {@code <fileGrp>} in which
     * contents of this folder will be linked.
     */
    @Column(name = "fileGroup")
    private String fileGroup = "";

    /**
     * An encapsulation of the content generator properties of the folder in a
     * way suitable to the JSF design.
     */
    @Transient
    private FolderGenerator generator = new FolderGenerator(this);

    /**
     * If not null, images in this folder can be generated by the function
     * {@link ImageManagementInterface#getScaledWebImage(java.net.URI, double)}.
     * The value is the factor of scaling for the derivative, a value of 1.0
     * indicates the original size.
     */
    @Column(name = "imageScale")
    private Double imageScale = null;

    /**
     * If not null, images in this folder can be generated by the function
     * {@link ImageManagementInterface#getSizedWebImage(java.net.URI, int)}. The
     * value is the new the new width in pixels.
     */
    @Column(name = "imageSize")
    private Integer imageSize = null;

    /**
     * Indicates whether a METS {@code <fileGrp>} section is created, and how it
     * is populated.
     */
    @Column(name = "linkingMode")
    @Enumerated(EnumType.STRING)
    private LinkingMode linkingMode = LinkingMode.ALL;

    /**
     * The Internet MIME type of the files contained in this folder. The MIME
     * type is used to derive depending settings, such as the file extension, or
     * which content processors can be employed.
     *
     * @see org.kitodo.config.xml.fileformats.FileFormatsConfig
     */
    @Column(name = "mimeType")
    private String mimeType = "image/jpeg";

    /**
     * The path to the folder in the process directory of each processes.
     */
    @Column(name = "path")
    private String path = "";

    /**
     * The project this folder is configured in.
     */
    @ManyToOne
    @JoinColumn(name = "project_id", foreignKey = @ForeignKey(name = "FK_folder_project_id"))
    private Project project = null;

    /**
     * URL path where the contents of the linked METS {@code <fileGrp>} will be
     * published on a web server. The path may contain variables that must be
     * replaced before concatenation.
     */
    @Column(name = "urlStructure")
    private String urlStructure;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Folder that = (Folder) o;
        return Objects.equals(fileGroup, that.fileGroup) && Objects.equals(urlStructure, that.urlStructure)
                && Objects.equals(mimeType, that.mimeType) && Objects.equals(path, that.path)
                && Objects.equals(project, that.project) && copyFolder == that.copyFolder
                && createFolder == that.createFolder && Objects.equals(derivative, that.derivative)
                && Objects.equals(dpi, that.dpi) && Objects.equals(imageScale, that.imageScale)
                && Objects.equals(imageSize, that.imageSize) && Objects.equals(linkingMode, that.linkingMode);
    }

    /**
     * Returns the scale factor to create the contents of the folder as
     * derivative form the content of another folder, if any. If absent, the
     * function is disabled.
     *
     * @return the scale factor. A value of 1.0 refers to the original size.
     */
    public Optional<Double> getDerivative() {
        return Optional.ofNullable(derivative);
    }

    /**
     * Returns the number of DPI to change the resolution of the contents of the
     * folder form the content of another folder, if any. If absent, the
     * function is disabled.
     *
     * @return the resolution
     */
    public Optional<Integer> getDpi() {
        return Optional.ofNullable(dpi);
    }

    /**
     * Returns the file group of the folder.
     *
     * @return the file group
     */
    public String getFileGroup() {
        return this.fileGroup;
    }

    /**
     * Returns the pre-defined entries for the combo box to select the METS use
     * in {@code projectEditMetsPopup.xhtml}.
     *
     * @return the pre-defined entries for the combo box
     */
    public Collection<String> getFileGroups() {
        Collection<String> result = new TreeSet<>(DFG_VIEWER_FILEGRPS);
        result.add(this.fileGroup);
        return result;
    }

    /**
     * Returns an encapsulation to access the generator properties of the folder
     * in a JSF-friendly way.
     *
     * @return the generator controller
     */
    public FolderGenerator getGenerator() {
        return generator;
    }

    /**
     * Returns the scale factor to get the contents of the folder as scaled web
     * images form the content of another folder, if any. If absent, the
     * function is disabled.
     *
     * @return the scale factor. A value of 1.0 refers to the original size.
     */
    public Optional<Double> getImageScale() {
        return Optional.ofNullable(imageScale);
    }

    /**
     * Returns the pixel width to get the contents of the folder as sized web
     * images form the content of another folder, if any. If absent, the
     * function is disabled.
     *
     * @return the pixel width
     */
    public Optional<Integer> getImageSize() {
        return Optional.ofNullable(imageSize);
    }

    /**
     * Returns the linking mode of the folder.
     *
     * @return the linking mode
     */
    public LinkingMode getLinkingMode() {
        return linkingMode;
    }

    /**
     * Returns the MIME type of the folder.
     *
     * @return the MIME type
     */
    public String getMimeType() {
        return this.mimeType;
    }

    /**
     * Returns the path pattern, containing the path to the folder relative to
     * the process directory, and maybe an extra file name pattern.
     *
     * @deprecated This getter is here to be used by Hibernate and JSF to access the
     *             field value, but should not be used for other purpose, unless you
     *             know what you are doing. Use {@link #getRelativePath()}.
     *
     * @return path with optional filename pattern
     */
    @Deprecated
    public String getPath() {
        return this.path;
    }

    /**
     * Returns the project of the folder.
     *
     * @return the project
     */
    public Project getProject() {
        return this.project;
    }

    /**
     * Returns the path to the folder.
     *
     * @return the path
     */
    @Transient
    public String getRelativePath() {
        int lastDelimiter = path.lastIndexOf(File.separatorChar);
        return path.substring(lastDelimiter + 1).indexOf('*') > -1
                ? lastDelimiter >= 0 ? path.substring(0, lastDelimiter) : ""
                : path;
    }

    /**
     * Returns the filename suffix with file extension for the UGH library.
     *
     * @deprecated This is a temporary solution and should no longer be used after
     *             that the UGH is removed.
     * @param extensionWithoutDot
     *            filename extension without dot, to be read from the configuration.
     *            The extension can be retrieved from the configuration based on the
     *            mimeType, but reading the configuration is part of the core
     *            module, so it cannot be done here and must be returned here from
     *            the collar.
     * @return the filename suffix with file extension
     */
    @Deprecated
    @Transient
    public String getUGHTail(String extensionWithoutDot) {
        String lastSegment = path.substring(path.lastIndexOf(File.separatorChar) + 1);
        if (lastSegment.indexOf('*') > -1) {
            if (lastSegment.startsWith("*.")) {
                String tail = lastSegment.substring(2);
                if (tail.endsWith(".*")) {
                    tail = tail.substring(0, tail.length() - 1).concat(extensionWithoutDot);
                }
                return tail;
            } else {
                throw new UnsupportedOperationException("The UGH does not support file name prefixes");
            }
        } else {
            return extensionWithoutDot;
        }
    }

    /**
     * Returns the URL structure of the folder.
     *
     * @return the URL structure
     */
    public String getUrlStructure() {
        return this.urlStructure;
    }

    /**
     * Returns a hash code value for the object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(fileGroup, urlStructure, mimeType, path, copyFolder, createFolder, derivative, dpi,
            imageScale, imageSize, linkingMode);
    }

    /**
     * Returns whether the folder is copy folder.
     *
     * @return whether the folder is copy folder
     */
    public boolean isCopyFolder() {
        return copyFolder;
    }

    /**
     * Returns whether the folder is created on process creation.
     *
     * @return whether the folder is created on process creation
     */
    public boolean isCreateFolder() {
        return createFolder;
    }

    /**
     * Sets whether the folder is copied on DMS import.
     *
     * @param copyFolder
     *            whether the folder is copied on DMS import
     */
    public void setCopyFolder(boolean copyFolder) {
        this.copyFolder = copyFolder;
    }

    /**
     * Sets whether the folder is created on process creation.
     *
     * @param createFolder
     *            whether the folder is created on process creation
     */
    public void setCreateFolder(boolean createFolder) {
        this.createFolder = createFolder;
    }

    /**
     * Sets the scale factor to create the contents of the folder as derivative
     * form the content of another folder. Can be set to {@code null} to disable
     * the function.
     *
     * @param derivative
     *            the scale factor. A value of 1.0 refers to the original size.
     */
    public void setDerivative(Double derivative) {
        this.derivative = derivative;
    }

    /**
     * Sets the number of DPI to change the resolution of the contents of the
     * folder form the content of another folder. Can be set to {@code null} to
     * disable the function.
     *
     * @param dpi
     *            resolution to set
     */
    public void setDpi(Integer dpi) {
        this.dpi = dpi;
    }

    /**
     * Sets the file group of the folder.
     *
     * @param fileGroup
     *            file group to set
     */
    public void setFileGroup(String fileGroup) {
        this.fileGroup = fileGroup;
    }

    /**
     * Sets the scale factor to get the contents of the folder as scaled web
     * images form the content of another folder. Can be set to {@code null} to
     * disable the function.
     *
     * @param imageScale
     *            the scale factor. A value of 1.0 refers to the original size.
     */
    public void setImageScale(Double imageScale) {
        this.imageScale = imageScale;
    }

    /**
     * Returns the pixel width to get the contents of the folder as sized web
     * images form the content of another folder, if any. If absent, the
     * function is disabled.
     *
     * @param imageSize
     *            the pixel width
     */
    public void setImageSize(Integer imageSize) {
        this.imageSize = imageSize;
    }

    /**
     * Sets the linking mode of the folder.
     *
     * @param linkingMode
     *            linking mode to set
     */
    public void setLinkingMode(LinkingMode linkingMode) {
        this.linkingMode = linkingMode;
    }

    /**
     * Sets the MIME type of the folder.
     *
     * @param mimeType
     *            MIME type to set
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Sets the path pattern, containing the path to the folder relative to the
     * process directory, and maybe an extra file name pattern. This getter is
     * here to be used by Hibernate and JSF to access the field value, but
     * should not be used for other purpose, unless you know what you are doing.
     *
     * @param path
     *            pat to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Sets the project of the folder.
     *
     * @param project
     *            project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Sets the URL structure of the folder.
     *
     * @param urlStructure
     *            URL structure to set
     */
    public void setUrlStructure(String urlStructure) {
        this.urlStructure = urlStructure;
    }

    @Override
    public String toString() {
        return path + (path.isEmpty() || fileGroup.isEmpty() ? "" : ", ") + fileGroup;
    }
}
