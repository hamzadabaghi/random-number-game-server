package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class ServeurJeu extends Thread {

	private boolean isActive = true;
	private int nombreClients = 0;
	private int nombreSecret;
	private String gagnant;
	private boolean fin = false;

	public static void main(String[] args) throws IOException {

		System.out.println("demarrage du serveur");
		new ServeurJeu().start();
		System.out.println("suite de l\'application , les interfaces graphiques par exemple");
	}

	@Override
	public void run() {

		/* Creation de l'objet server socket avec numero du port : 1234 */

		try {

			ServerSocket serverSocket = new ServerSocket(1234);
			nombreSecret = new Random().nextInt(1000);
			System.out.println("le nombre secret est : " + nombreSecret);
			while (isActive) {

				Socket socket = serverSocket.accept();
				++nombreClients;
				new Conversation(socket, nombreClients).start();

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	 class Conversation extends Thread {

		private Socket socketClient;
		private int clientCode;

		public Conversation(Socket socket, int code) {

			this.socketClient = socket;
			this.clientCode = code;
		}

		@Override
		public void run() {

			try {

				// input
				InputStream is = socketClient.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);

				// output

				OutputStream os = socketClient.getOutputStream();
				PrintWriter pw = new PrintWriter(os, true);
				String IpAdress = socketClient.getRemoteSocketAddress().toString();
				pw.println("Bienvenue , vous etes le client numero " + clientCode);
				System.out.println("connexion du client : " + IpAdress);
				while (true) {

					String requete = br.readLine();
					int nombre = Integer.parseInt(requete);
					System.out.println("le client "+ IpAdress + " a envoye une tentative avec nombre : "+nombre);

					if(fin==false)
					{
						if(nombre>nombreSecret)
						{
							pw.println("votre nombre est superieure au nombre secret");
							
						}
						else if(nombre<nombreSecret)
						{
							pw.println("votre nombre est inferieure au nombre secret");
							
						}
						
						else 
						{
							pw.println("bravo , vous avez gange");
							gagnant = IpAdress;
							System.out.println("le gangant est : " + gagnant);
							fin = true;
						}
					}
					else
					{
						pw.println("jeu termine , le gagnant est : "+gagnant);
						
					}
					

				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			
		

		}

	}

}
