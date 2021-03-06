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

package org.kitodo.selenium;

import java.io.File;

import org.apache.commons.lang.SystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kitodo.config.KitodoConfig;
import org.kitodo.config.enums.ParameterCore;
import org.kitodo.data.database.beans.Task;
import org.kitodo.data.database.helper.enums.TaskStatus;
import org.kitodo.selenium.testframework.BaseTestSelenium;
import org.kitodo.selenium.testframework.Browser;
import org.kitodo.selenium.testframework.Pages;
import org.kitodo.selenium.testframework.pages.CurrentTasksEditPage;
import org.kitodo.selenium.testframework.pages.ProcessesPage;
import org.kitodo.selenium.testframework.pages.TasksPage;
import org.kitodo.services.ServiceManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class WorkingST extends BaseTestSelenium {

    private ServiceManager serviceManager = new ServiceManager();

    private static CurrentTasksEditPage currentTasksEditPage;
    private static ProcessesPage processesPage;
    private static TasksPage tasksPage;

    @BeforeClass
    public static void setup() throws Exception {
        currentTasksEditPage = Pages.getCurrentTasksEditPage();
        processesPage = Pages.getProcessesPage();
        tasksPage = Pages.getTasksPage();
    }

    @Before
    public void login() throws Exception {
        Pages.getLoginPage().goTo().performLoginAsAdmin();
    }

    @After
    public void logout() throws Exception {
        Pages.getTopNavigation().logout();
        if (Browser.isAlertPresent()) {
            Browser.getDriver().switchTo().alert().accept();
        }
    }

    @Test
    public void takeOpenTaskAndGiveItBackTest() throws Exception {
        Task task = serviceManager.getTaskService().getById(2);
        assertEquals("Task can not be taken by user!", TaskStatus.OPEN, task.getProcessingStatusEnum());

        tasksPage.goTo().takeOpenTask();
        assertTrue("Redirection after click take task was not successful", currentTasksEditPage.isAt());

        task = serviceManager.getTaskService().getById(2);
        assertEquals("Task was not taken by user!", TaskStatus.INWORK, task.getProcessingStatusEnum());

        currentTasksEditPage.releaseTask();
        assertTrue("Redirection after click release task was not successful", tasksPage.isAt());

        task = serviceManager.getTaskService().getById(2);
        assertEquals("Task was not released by user!", TaskStatus.OPEN, task.getProcessingStatusEnum());
    }

    @Test
    public void editOwnedTaskTest() throws Exception {
        tasksPage.goTo().editOwnedTask();
        assertTrue("Redirection after click edit own task was not successful", currentTasksEditPage.isAt());

        currentTasksEditPage.closeTask();
        assertTrue("Redirection after click close task was not successful", tasksPage.isAt());

        Task task = serviceManager.getTaskService().getById(8);
        assertEquals("Task was not closed!", TaskStatus.DONE, task.getProcessingStatusEnum());
    }

    @Test
    public void downloadDocketTest() throws Exception {
        processesPage.goTo().downloadDocket();
        assertTrue("Docket file was not downloaded", new File(Browser.DOWNLOAD_DIR + "Second__process.pdf").exists());
    }

    @Test
    public void downloadLogTest() throws Exception {
        assumeTrue(!SystemUtils.IS_OS_WINDOWS && !SystemUtils.IS_OS_MAC);

        processesPage.goTo().downloadLog();
        assertTrue("Log file was not downloaded",
            new File(KitodoConfig.getParameter(ParameterCore.DIR_USERS) + "kowal/Second__process_log.xml").exists());
    }

    @Test
    public void editMetadataTest() throws Exception {
        processesPage.goTo().editMetadata();
        assertTrue("Redirection after click edit metadata was not successful", Pages.getMetadataEditorPage().isAt());
    }

    @Test
    public void downloadSearchResultAsExcelTest() throws Exception {
        processesPage.goTo().downloadSearchResultAsExcel();
        assertTrue("Search result excel file was not downloaded",
            new File(Browser.DOWNLOAD_DIR + "search.xls").exists());
    }

    @Test
    public void downloadSearchResultAsPdfTest() throws Exception {
        processesPage.goTo().downloadSearchResultAsPdf();
        assertTrue("Search result pdf file was not downloaded", new File(Browser.DOWNLOAD_DIR + "search.pdf").exists());
    }
}
