package views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import javax.xml.parsers.ParserConfigurationException;
import model.Employee;
import model.InrcProblem;
import model.Schedule;
import org.xml.sax.SAXException;
import utils.Chronograph;
import utils.ImportProblemXML;
import utils.ScheduleImporter;
import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.SimpleTextRenderer;
import eval.CostEvaluator;
import eval.ScheduleHelper;

public class TestApp {
	private static EtmMonitor monitor;
	private String inputFileFullPath;
	private InrcProblem problem;
	private Schedule schedule;

	public static void main(String[] args) {
		BasicEtmConfigurator.configure();
		monitor = EtmManager.getEtmMonitor();
		monitor.start();
		TestApp app = new TestApp();
		app.launch();
		monitor.render(new SimpleTextRenderer());
		monitor.stop();
	}

	private void launch() {
		System.out.println("1. Problem Details");
		System.out.println("2. Load Solution (from folder solution)");
		System.out.print("Choice: ");
		Scanner input = new Scanner(System.in);
		int a = input.nextInt();
		if (a == 1)
			scenario1();
		else if (a == 2)
			scenario2();
	}

	private void openProblemFromList() {
		inputFileFullPath = openProblemFile();
		ImportProblemXML importProblem = new ImportProblemXML();
		importProblem.parseFile(inputFileFullPath);
		problem = importProblem.getProblem();
	}

	private String openProblemFile(int a) {
		String folderPath;
		if (a == 1) {
			folderPath = "datasets" + File.separator + "sprint";
		} else if (a == 2) {
			folderPath = "datasets" + File.separator + "medium";
		} else {
			folderPath = "datasets" + File.separator + "long";
		}
		String file_name_full_path = null;
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		List<File> matchingFiles = new ArrayList<File>();
		for (File aFile : listOfFiles) {
			if (aFile.getName().endsWith(String.format("xml"))) {
				matchingFiles.add(aFile);
			}
		}
		if (matchingFiles.isEmpty()) {
			System.out.printf("No input files found");
		} else {
			System.out.println("Matching files");
			int i = 1;
			Collections.sort(matchingFiles);
			for (File aFile : matchingFiles) {
				System.out.printf("%02d. %20s\t", i, aFile.getName());
				if (i % 3 == 0)
					System.out.println();
				i++;
			}
			System.out.printf("\nSelect problem instance (1-%d): ", i - 1);
			Scanner input = new Scanner(System.in);
			input = new Scanner(System.in);
			file_name_full_path = matchingFiles.get(input.nextInt() - 1)
					.getAbsolutePath();
		}
		return file_name_full_path;
	}

	private String openProblemFile() {
		System.out.println("1. Sprint");
		System.out.println("2. Medium");
		System.out.println("3. Long");
		System.out.println("Choice: ");
		Scanner input = new Scanner(System.in);
		int a = input.nextInt();
		return openProblemFile(a);

	}

	private void scenario1() {
		openProblemFromList();
		System.out.println(problem);
	}

	private void scenario2() {
		openProblemFromList();
		String sol_file_name = null;
		File folder = new File("solutions");
		File[] listOfFiles = folder.listFiles();
		List<File> matchingFiles = new ArrayList<File>();
		for (File aFile : listOfFiles) {
			if (aFile.getName().endsWith(String.format("xml"))) {
				matchingFiles.add(aFile);
			}
		}
		if (matchingFiles.isEmpty()) {
			System.out.printf("No solution files found");
		} else {
			System.out.println("Matching files");
			int i = 1;
			Collections.sort(matchingFiles);
			for (File aFile : matchingFiles) {
				System.out.printf("%02d. %s\n", i, aFile.getName());
				i++;
			}
			System.out.printf("Select solution file (1-%d): ", i - 1);
			Scanner input = new Scanner(System.in);
			sol_file_name = matchingFiles.get(input.nextInt() - 1)
					.getAbsolutePath();
			System.out.printf("Loading %s\n", sol_file_name);
			ScheduleImporter scheduleImporter = new ScheduleImporter(problem);
			try {
				scheduleImporter.loadXML(sol_file_name);
				schedule = scheduleImporter.getSchedule();
				System.out.println(schedule.toString());
				ScheduleHelper sh = new ScheduleHelper(problem);
				sh.populateAssignmentsFrom(schedule);
				CostEvaluator ce = new CostEvaluator(sh);
				ce.evaluate();
				System.out.println(ce.costAsString());
				boolean feasible = ce.isFeasible();
				int cost = ce.getCost();
				System.out.printf("The solution is %s with cost %d\n",
						feasible ? "feasible :)" : "infeasible :(", cost);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
