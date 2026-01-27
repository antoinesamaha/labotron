/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.neofoc.app.modules.labotron;

import com.foc.admin.FocVersion;
import com.foc.desc.FocModule;
import com.foc.menu.FMenuList;
import com.neofoc.springboot.annotations.FocDeclareModule;
import org.springframework.stereotype.Component;

@Component
@FocDeclareModule
public class LabotronModule extends FocModule {

	public static final String MODULE_NAME = "Labotron";
	public static final int VERSION = 1000;

	public void dispose(){
		
	}
	
	@Override
	public void declareFocObjectsOnce() {
		FocVersion.addVersion(MODULE_NAME, "Labotron 1.0", VERSION);
		scanModelPackage("com.neofoc.app.modules.labotron");
	}

	public void addApplicationMenu(FMenuList menuList) {
	}

	public void addConfigurationMenu(FMenuList menuList) {
	}

	public void afterApplicationEntry() {
	}

	public void afterApplicationLogin() {
	}

	public void beforeAdaptDataModel() {
		super.beforeAdaptDataModel();
//		copyMonoReportToSlaveTable = false;
//		FocVersion version = FocVersion.getDBVersionForModule(MODULE_NAME);
//		if (version != null && version.getId() < VERSION_MULTI_REPORT_IN_TRIGGER) {
//			copyMonoReportToSlaveTable = true;
//		}
//
//		if (version == null || version.getId() < VERSION_ID_MIGRATE_TO_CLOB) {
//			if(Globals.getDBManager() != null && Globals.getDBManager().getProvider(null) == DBManager.PROVIDER_ORACLE) {
//				migrateToCLOB = true;
//				StringBuffer buffer = new StringBuffer();
//				buffer.append("ALTER TABLE \"NOTIF_EMAIL\" RENAME COLUMN \"TEXT\" TO \"TEXTOLD\"");
//				Globals.getApp().getDataSource().command_ExecuteRequest(buffer);
//			}
//		} else {
//			migrateToCLOB = false;
//		}
	}

	public void afterAdaptDataModel() {
	}

	private static LabotronModule labotronModule = null;
	public static LabotronModule getInstance(){
		if(labotronModule == null){
			labotronModule = new LabotronModule();
		}
		return labotronModule;
	}
}
