package main;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import exceptions.InvalidServerException;

public class DasChatControl {

	private static Socket connection;
	private static Scanner scanner = new Scanner(System.in);
	private static String command;

	public static void main(String[] args) {
		System.out.println("Geben sie den Server an mit dem sie sich verbinden wollen (IP:Port).");
		connectToServer();

		listenToCommandLine();
	}

	private static void listenToCommandLine() {
		System.out.println("Geben sie Befehle ein, für eine Übersicht der Befehle geben sie help ein.");
		while (true) {
			command = scanner.nextLine();
			System.out.println(command);
		}
	}

	private static void connectToServer() {
		try {
			command = scanner.next();
			String ip = command.split("[:]")[0];
			int port;
			try {
				port = Integer.parseInt(command.split("[:]")[1]);
			} catch (IndexOutOfBoundsException e) {
				throw new InvalidServerException("Bitte geben sie einen Port an.");
			} catch (NumberFormatException e) {
				throw new InvalidServerException(
						"Bitte geben sie einen gültigen Port an, ihre Angabe war: " + command.split("[:]")[1]);
			}
			try {
				connection = new Socket(ip, port);
			} catch (IOException e) {
				// TODO(msc) Handle
			}
		} catch (InvalidServerException e) {
			System.out.println(e.getMessage());
			connectToServer();
		}
	}
}
