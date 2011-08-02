package de.sub.goobi.Forms;
/**
 * This file is part of the Goobi Application - a Workflow tool for the support of mass digitization.
 * 
 * Visit the websites for more information. 
 * 			- http://digiverso.com 
 * 			- http://www.intranda.com
 * 
 * Copyright 2011, intranda GmbH, Göttingen
 * 
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Linking this library statically or dynamically with other modules is making a combined work based on this library. Thus, the terms and conditions
 * of the GNU General Public License cover the whole combination. As a special exception, the copyright holders of this library give you permission to
 * link this library with independent modules to produce an executable, regardless of the license terms of these independent modules, and to copy and
 * distribute the resulting executable under terms of your choice, provided that you also meet, for each linked independent module, the terms and
 * conditions of the license of that module. An independent module is a module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but you are not obliged to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 */
import java.util.List;

import javax.faces.model.SelectItem;

public class AdditionalField {
	private String titel;
	private String wert = "";
	private boolean required = false;
	private String from = "prozess";
	private List<SelectItem> selectList;
	private boolean ughbinding = false;
	private String docstruct;
	private String metadata;
	private String isdoctype = "";
	private String isnotdoctype = "";
	private String initStart = ""; // defined in projects.xml
	private String initEnd = "";
	private boolean autogenerated = false;
	private ProzesskopieForm pkf;

	public AdditionalField(ProzesskopieForm inPkf) {
		this.pkf = inPkf;
	}

	public String getInitStart() {
		return this.initStart;
	}

	public void setInitStart(String newValue) {
		this.initStart = newValue;
		if (this.initStart == null) {
			this.initStart = "";
		}
		this.wert = this.initStart + this.wert;
	}

	public String getInitEnd() {
		return this.initEnd;
	}

	public void setInitEnd(String newValue) {
		this.initEnd = newValue;
		if (this.initEnd == null) {
			this.initEnd = "";
		}
		this.wert = this.wert + this.initEnd;
	}

	public String getTitel() {
		return this.titel;
	}

	public void setTitel(String titel) {
		this.titel = titel;
	}

	public String getWert() {
		return this.wert;
	}

	public void setWert(String newValue) {
		if (newValue == null || newValue.equals(this.initStart)) {
			newValue = "";
		}
		if (newValue.startsWith(this.initStart)) {
			this.wert = newValue + this.initEnd;
		} else {
			this.wert = this.initStart + newValue + this.initEnd;
		}
	}

	public String getFrom() {
		return this.from;
	}

	public void setFrom(String infrom) {
		if (infrom != null && infrom.length() != 0) {
			this.from = infrom;
		}
	}

	public List<SelectItem> getSelectList() {
		return this.selectList;
	}

	public void setSelectList(List<SelectItem> selectList) {
		this.selectList = selectList;
	}

	public boolean isRequired() {
		return this.required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isUghbinding() {
		return this.ughbinding;
	}

	public void setUghbinding(boolean ughbinding) {
		this.ughbinding = ughbinding;
	}

	public String getDocstruct() {
		return this.docstruct;
	}

	public void setDocstruct(String docstruct) {
		this.docstruct = docstruct;
		if (this.docstruct == null) {
			this.docstruct = "topstruct";
		}
	}

	public String getMetadata() {
		return this.metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public String getIsdoctype() {
		return this.isdoctype;
	}

	public void setIsdoctype(String isdoctype) {
		this.isdoctype = isdoctype;
		if (this.isdoctype == null) {
			this.isdoctype = "";
		}
	}

	public String getIsnotdoctype() {
		return this.isnotdoctype;
	}

	public void setIsnotdoctype(String isnotdoctype) {
		this.isnotdoctype = isnotdoctype;
		if (this.isnotdoctype == null) {
			this.isnotdoctype = "";
		}
	}

	public boolean getShowDependingOnDoctype() {

		/* wenn nix angegeben wurde, dann anzeigen */
		if (this.isdoctype.equals("") && this.isnotdoctype.equals("")) {
			return true;
		}

		/* wenn pflicht angegeben wurde */
		if (!this.isdoctype.equals("") && !this.isdoctype.contains(this.pkf.getDocType())) {
			return false;
		}

		/* wenn nur "darf nicht" angegeben wurde */
		if (!this.isnotdoctype.equals("") && this.isnotdoctype.contains(this.pkf.getDocType())) {
			return false;
		}

		return true;
	}

	/**
	 * @param autogenerated the autogenerated to set
	 */
	public void setAutogenerated(boolean autogenerated) {
		this.autogenerated = autogenerated;
	}

	/**
	 * @return the autogenerated
	 */
	public boolean getAutogenerated() {
		return this.autogenerated;
	}
}

/* =============================================================== */
