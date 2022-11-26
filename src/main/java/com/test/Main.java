package com.test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import com.google.gson.Gson;

public class Main {

	public static void main(String[] args) {
		try {
			Transcript transcript = new Transcript();
			transcript.setAudio_url("https://github.com/beiminator/api-rest-call/blob/main/src/main/resources/media/Thirsty.mp4");
			Gson gson = new Gson();
			String gsonRequest = gson.toJson(transcript);
			
			System.out.println(gsonRequest);
			
			HttpRequest postRequest = HttpRequest.newBuilder()
				.uri(new URI("https://api.assemblyai.com/v2/transcript"))
				.header("Authorization", Constants.API_KEY)
				.POST(BodyPublishers.ofString(gsonRequest))
				.build();
			
			HttpClient httpClient = HttpClient.newHttpClient();
			HttpResponse<String> httpResponse = httpClient.send(postRequest, BodyHandlers.ofString());
			
			System.out.println(httpResponse.body());
			
			transcript = gson.fromJson(httpResponse.body(), Transcript.class);
			System.out.println(transcript.getId());
			
			HttpRequest getRequest = HttpRequest.newBuilder()
				.uri(new URI("https://api.assemblyai.com/v2/transcript/"+transcript.getId()))
				.header("Authorization", Constants.API_KEY)
				.GET()
				.build();
			while (true) {
				httpResponse = httpClient.send(getRequest, BodyHandlers.ofString());
				transcript = gson.fromJson(httpResponse.body(), Transcript.class);
				System.out.println(transcript.getStatus());
				if ("completed".equals(transcript.getStatus()) ||
						"error".equals(transcript.getStatus())) {
					break;
				}
				Thread.sleep(1000);
			}
			System.out.println("Transcription completed with status "+transcript.getStatus());
			if ("completed".equals(transcript.getStatus())) {
				System.out.println("Output text is ["+transcript.getText()+"]");				
			} else {
				System.out.println("Output error is ["+transcript.getError()+"]");
			}
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
