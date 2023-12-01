package com.codebook;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentChange;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.annotations.Nullable;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.codec.Base64;
import com.mashape.unirest.http.exceptions.UnirestException;

import animatefx.animation.FadeInUp;
import animatefx.animation.SlideOutLeft;
import eu.iamgio.animated.transition.AnimationPair;
import eu.iamgio.animated.transition.container.AnimatedVBox;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SceneController extends ChangeToMessenger implements Initializable {
	@FXML
	private Button SearchIcon;

	@FXML
	private TextField SearchText;
	@FXML
	private Circle TopCir;

	@FXML
	private Label TopUsername;
	@FXML
	public VBox post;
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private StackPane stackPane;
	@FXML
	private StackPane stackPane2;
	@FXML
	private StackPane loading;
	@FXML
	private AnchorPane contribution;
	@FXML
	private Circle developer1;

	@FXML
	private Circle developer11;

	@FXML
	private Circle developer12;

	AnimatedVBox vBox = new AnimatedVBox(new AnimationPair(new FadeInUp(), new SlideOutLeft()).setSpeed(3, 3));
	AnimatedVBox vBox1 = new AnimatedVBox(new AnimationPair(new FadeInUp(), new SlideOutLeft()).setSpeed(3, 3));
	AnimatedVBox vBox2 = new AnimatedVBox(new AnimationPair(new FadeInUp(), new SlideOutLeft()).setSpeed(3, 3));
	Firestore db = FirestoreClient.getFirestore();
	CollectionReference codeRef = db.collection("code");
	Query query = codeRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(20);
	DocumentSnapshot lastDoc;

	public void initialize(URL location, ResourceBundle resources) {
		vBox.setSpacing(10);
		vBox1.setSpacing(10);
		contribution.getChildren().add(vBox1);
		FXMLLoader postForm = new FXMLLoader(getClass().getResource("CreatePost.fxml"));
		AnchorPane scenePostForm = new AnchorPane();
		try {
			scenePostForm = postForm.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		post.getChildren().add(vBox);
		vBox.getChildren().add(scenePostForm);
		try {
			PageVariable.contests = Methods.getCurrentContest().contests;
		} catch (JsonProcessingException | UnirestException e) {
			e.printStackTrace();
		}
		DataInit();
		TopCir.setFill(new ImagePattern(new Image(MainUser.photoUrl)));
		developer1.setFill(new ImagePattern(
				new Image(
						"https://media.discordapp.net/attachments/1067001126697840693/1178593643188199507/arik.jpg")));
		developer11.setFill(new ImagePattern(
				new Image(
						"https://cdn.discordapp.com/attachments/1067001126697840693/1178748794901364736/raisul-removebg-preview.png")));
		developer12.setFill(new ImagePattern(new Image(
				"https://cdn.discordapp.com/attachments/1067001126697840693/1178593699609976832/moon.jpg")));
		TopUsername.setText(MainUser.username);
		Button button = new Button("Load More");
		button.setOnAction(event -> {
			try {
				loadMore();
			} catch (InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
			}
		});
		button.getStyleClass().add("Button");
		post.getChildren().add(button);
		System.out.println("Initialized " + PageVariable.posts.size());
	}

	public void loadMore() throws InterruptedException, ExecutionException {
		Query query = codeRef.orderBy("timestamp").startAfter(lastDoc).limit(20);
		for (QueryDocumentSnapshot d : query.get().get().getDocuments()) {
			PostCodeStruct p = d.toObject(PostCodeStruct.class);
			PageVariable.posts.add(p);
			System.out.println(p.id);
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {

					ProfileStruct profile = Methods.getProfile(p.userEmail);
					p.displayName = p.displayName;
					p.userImage = p.userImage;
					addSinglePost(p);
					return null;
				}
			};
			Thread thread = new Thread(task);
			thread.start();
		}

	}

	public void changeToMsg(ActionEvent e) {
		vBox.getChildren().clear();
		if (!post.getChildren().isEmpty() && post.getChildren().get(post.getChildren().size() - 1) instanceof Button) {
			post.getChildren().remove(post.getChildren().size() - 1);
		}
		changeToMsg(vBox, post);
	}

	public void changeToHome(ActionEvent e) throws IOException {
		vBox.getChildren().clear();
		if (!post.getChildren().isEmpty() && post.getChildren().get(post.getChildren().size() - 1) instanceof Button) {
			post.getChildren().remove(post.getChildren().size() - 1);
		}
		FXMLLoader postForm1 = new FXMLLoader(getClass().getResource("CreatePost.fxml"));
		AnchorPane scenePostForm1 = postForm1.load();
		vBox.getChildren().add(scenePostForm1);
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				for (PostCodeStruct p : PageVariable.posts) {
					// UserDetails user = Methods.getUserDetails(p.id_token);
					ProfileStruct profile = Methods.getProfile(p.userEmail);
					p.displayName = profile.getDisplayName();
					p.userImage = profile.userImage;

					addSinglePost(p);
				}
				return null;
			}
		};
		task.setOnSucceeded(evv -> {
			Button button = new Button("Load More");
			post.getChildren().add(button);

			button.setOnAction(event -> {
				try {
					loadMore();
				} catch (InterruptedException | ExecutionException e1) {
					e1.printStackTrace();
				}
			});
			button.getStyleClass().add("Button");
		});
		Thread thread = new Thread(task);
		thread.start();

	}

	public void changeToSearch(ActionEvent event) throws IOException {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("Search.fxml"));
		AnchorPane pane1;
		String search = "";
		Button searchBT = null;
		pane1 = loader.load();
		SearchController controller = loader.getController();
		javafx.scene.control.TextField searchField = controller.getSearch();
		search = controller.getSearch().getText();
		searchBT = controller.getSearchButton();
		vBox.getChildren().clear();
		if (!post.getChildren().isEmpty() && post.getChildren().get(post.getChildren().size() - 1) instanceof Button) {
			post.getChildren().remove(post.getChildren().size() - 1);
		}
		vBox.getChildren().add(pane1);
		Label label1 = new Label("Please Search");
		label1.setStyle("-fx-text-fill: white;");
		label1.setStyle("-fx-font-size: 18px;");
		if (search.equals("")) {
			vBox.getChildren().add(label1);
		}

		javafx.scene.control.TextField finalSearchField = searchField;
		searchBT.setOnMouseClicked(e -> {
			Label label = new Label("Loading...");
			label.setStyle("-fx-text-fill: white;");
			label.setStyle("-fx-font-size: 18px;");
			stackPane2.getChildren().add(label);
			String localSearch = searchField.getText();
			Task<AllPosts> SearchResult = new Task<AllPosts>() {

				@Override
				protected AllPosts call() throws Exception {
					try {
						AllPosts searchResult = Methods.search(MainUser.idToken, localSearch);
						return searchResult;
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
			};
			SearchResult.setOnSucceeded(evv -> {
				AllPosts searchResult = SearchResult.getValue();
				Platform.runLater(() -> {
					stackPane2.getChildren().remove(label);
					if (searchResult == null) {
						Label label2 = new Label("No Result Found");
						label2.setStyle("-fx-text-fill: white;");
						label2.setStyle("-fx-font-size: 18px;");
						stackPane2.getChildren().add(label2);
						return;
					}
					for (PostCodeStruct p : searchResult.posts) {
						try {
							ProfileStruct profile = Methods.getProfile(p.userEmail);
							p.displayName = profile.getDisplayName();
							p.userImage = profile.getUserImage();
							addSinglePost(p);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}

				});
			});
			Thread thread = new Thread(SearchResult);
			thread.start();
			label.visibleProperty().bind(SearchResult.runningProperty());
			finalSearchField.setText("");
		});

	}

	public void enableSearch(ActionEvent event) {
		SearchText.setDisable(true);
		SearchText.setVisible(true);
	}

	public void goToSearch(ActionEvent event) throws IOException, UnirestException {

		if (SearchText.getText().equals("")) {
			return;
		}
		vBox.getChildren().clear();
		if (!post.getChildren().isEmpty() &&
				post.getChildren().get(post.getChildren().size() - 1) instanceof Button) {
			post.getChildren().remove(post.getChildren().size() - 1);
		}
		AllPosts all = Methods.search(MainUser.idToken, SearchText.getText());
		if (all == null || all.posts.size() == 0) {
			Label label = new Label("No Result Found");
			label.setStyle("-fx-text-fill: #ffffff;");
			label.setStyle("-fx-font-size: 18px;");
			vBox.getChildren().add(label);
			return;
		}
		for (PostCodeStruct p : all.posts) {
			try {
				ProfileStruct profile = Methods.getProfile(p.userEmail);
				p.displayName = profile.getDisplayName();
				p.userImage = profile.getUserImage();
				addSinglePost(p);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		SearchText.setText("");

	}

	public void changeToProfile() throws IOException {
		vBox.getChildren().clear();
		if (!post.getChildren().isEmpty() && post.getChildren().get(post.getChildren().size() - 1) instanceof Button) {
			post.getChildren().remove(post.getChildren().size() - 1);
		}
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Profile.fxml"));
		AnchorPane pane = loader.load();
		ProfileController pr = loader.getController();
		pr.displayName.setText(MainUser.displayName);
		pr.username.setText(MainUser.username);
		pr.displayUrl.setImage(new Image(MainUser.photoUrl));
		pr.getConNumber().setText(Methods.getContribution(MainUser.username));
		editProfile(pr);
		ExtraDetails extra = Methods.getExtraDetails(MainUser.username);
		pr.CcIcon.setOnMouseClicked(e -> {
			try {
				java.awt.Desktop.getDesktop()
						.browse(java.net.URI.create("https://www.codechef.com/users/" + extra.cchef));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		pr.EmailIcon.setOnMouseClicked(e -> {
			try {
				java.awt.Desktop.getDesktop().browse(java.net.URI.create("mailto:" + MainUser.userEmail));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		pr.LnIcon.setOnMouseClicked(e -> {
			try {
				java.awt.Desktop.getDesktop()
						.browse(java.net.URI.create("https://www.linkedin.com/in/" + extra.linkedin));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		pr.cFicon.setOnMouseClicked(e -> {
			try {
				java.awt.Desktop.getDesktop()
						.browse(java.net.URI.create("https://codeforces.com/profile/" + extra.getCf()));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		pr.ghIcon.setOnMouseClicked(e -> {
			try {
				java.awt.Desktop.getDesktop()
						.browse(java.net.URI.create("https://github.com/" + extra.getGithub()));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		pr.getAddFrnd().setText("Friends");
		pr.getAddFrnd().setOnMouseClicked(e -> {
			changeToFriendList(MainUser.username);
		});
		pr.getFrndReq().setText("Requests");
		pr.getFrndReq().setOnMouseClicked(e -> {
			changeToFriendRequest(MainUser.username);
		});

		ProfileStruct user = Methods.getProfile(MainUser.username);
		if (user.isAdmin == false) {
			pr.AddAdmin.setVisible(false);
		} else {
			pr.AddAdmin.setOnAction(e -> {
				FXMLLoader loader1 = new FXMLLoader(getClass().getResource("AddAdmin.fxml"));
				AnchorPane pane1 = null;
				try {
					pane1 = loader1.load();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				AddAdminController controller = loader1.getController();

				Parent root = (Parent) pane1;
				Stage stage = new Stage();
				stage.setScene(new Scene(root));
				stage.show();

			});
		}

		vBox.getChildren().add(pane);
		// System.out.println(id);
		for (PostCodeStruct p : PageVariable.personal) {
			System.out.println(p.id);
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					ProfileStruct user = Methods.getProfile(p.userEmail);
					p.displayName = user.getDisplayName();
					p.userImage = user.getUserImage();
					addSinglePost(p);
					return null;
				}
			};
			Thread thread = new Thread(task);
			thread.start();
		}
	}

	private void changeToFriendRequest(String username) {
		List<ProfileStruct> friends = Methods.allFriendRequests(username);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("FriendList.fxml"));
		AnchorPane pane = null;
		try {
			pane = loader.load();
			FriendList controllerMain = loader.getController();
			for (ProfileStruct p : friends) {
				FXMLLoader loader1 = new FXMLLoader(getClass().getResource("FriendRequest.fxml"));
				AnchorPane pane1 = loader1.load();
				FriendReq controller = loader1.getController();
				ProfileStruct profile = Methods.getProfile(p.username);
				controller.getIdToken().setText(profile.username);
				controller.getUserImage().setImage(new Image(profile.userImage));
				controller.getIdToken().setOnMouseClicked(ev -> {
					try {
						changeToProfile(profile.username, profile.displayName, profile.userImage, "");
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				controller.getAccept().setOnAction(e -> {
					Methods.AcceptFriend(MainUser.username, profile.username);
					controllerMain.FrndList.getChildren().remove(pane1);
				});
				controller.getReject().setOnAction(e -> {
					Methods.RejectFriendRequest(MainUser.username, profile.username);
					controllerMain.FrndList.getChildren().remove(pane1);
				});
				controllerMain.FrndList.getChildren().add(pane1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Parent root = (Parent) pane;
		Stage stage = new Stage();
		stage.setScene(new Scene(root));
		stage.show();
	}

	private void changeToFriendList(String username) {
		List<ProfileStruct> friends = Methods.allFriends(username);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("FriendList.fxml"));
		AnchorPane pane = null;
		try {
			pane = loader.load();
			FriendList controllerMain = loader.getController();
			for (ProfileStruct p : friends) {
				FXMLLoader loader1 = new FXMLLoader(getClass().getResource("single_mesage.fxml"));
				AnchorPane pane1 = loader1.load();
				SingleMessageController controller = loader1.getController();
				ProfileStruct profile = Methods.getProfile(p.username);

				controller.getIdToken().setText(p.username);
				controller.getIdToken().setOnMouseClicked(ev -> {
					try {
						changeToProfile(p.username, profile.displayName, profile.userImage, "");
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				controller.getUserImage().setFill(new ImagePattern(new Image(profile.getUserImage())));
				controllerMain.FrndList.getChildren().add(pane1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Parent root = (Parent) pane;
		Stage stage = new Stage();
		stage.setScene(new Scene(root));
		stage.show();
	}

	public void editProfile(ProfileController pr) {
		if (pr.username.getText().equals(MainUser.username) == false) {
			pr.getEditProfile().setVisible(false);
			return;
		}
		pr.displayUrl.setOnMouseClicked(e -> {
			File file = new FileChooser().showOpenDialog(null);
			if (file != null) {
				Methods.updateImage(file, MainUser.idToken);
				pr.displayUrl.setImage(new Image(file.toURI().toString()));
				TopCir.setFill(new ImagePattern(new Image(file.toURI().toString())));
			}
		});
	}

	public void changeToProfile(String userName, String displayName, String imgUrl, String id) throws IOException {
		if (userName.equals(MainUser.username)) {
			changeToProfile();
			return;
		}

		vBox.getChildren().clear();
		if (!post.getChildren().isEmpty() && post.getChildren().get(post.getChildren().size() - 1) instanceof Button) {
			post.getChildren().remove(post.getChildren().size() - 1);
		}
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Profile.fxml"));
		AnchorPane pane = loader.load();
		ProfileController pr = loader.getController();
		pr.displayName.setText(displayName);
		pr.username.setText(userName);
		pr.displayUrl.setImage(new Image(imgUrl));
		pr.getConNumber().setText(Methods.getContribution(userName));
		editProfile(pr);
		ExtraDetails extra = Methods.getExtraDetails(userName);
		System.out.println(extra.cf);
		pr.CcIcon.setOnMouseClicked(e -> {
			try {
				java.awt.Desktop.getDesktop()
						.browse(java.net.URI.create("https://www.codechef.com/users/" + extra.cchef));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		pr.EmailIcon.setOnMouseClicked(e -> {
			try {
				java.awt.Desktop.getDesktop().browse(java.net.URI.create("mailto:" + MainUser.userEmail));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		pr.LnIcon.setOnMouseClicked(e -> {
			try {
				java.awt.Desktop.getDesktop()
						.browse(java.net.URI.create("https://www.linkedin.com/in/" + extra.linkedin));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		pr.cFicon.setOnMouseClicked(e -> {
			try {
				java.awt.Desktop.getDesktop()
						.browse(java.net.URI.create("https://codeforces.com/profile/" + extra.getCf()));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		pr.ghIcon.setOnMouseClicked(e -> {
			try {
				java.awt.Desktop.getDesktop()
						.browse(java.net.URI.create("https://github.com/" + extra.getGithub()));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		pr.getFrndReq().setText("Friend List");
		pr.getFrndReq().setOnMouseClicked(e -> {
			changeToFriendList(userName);
		});

		if (Methods.isFriend(MainUser.username, userName) == 3) {
			pr.getAddFrnd().setText("Unfriend");
			pr.getAddFrnd().setOnMouseClicked(e -> {
				Methods.UnFriend(MainUser.username, userName);
				try {
					changeToProfile(userName, displayName, imgUrl, id);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});
		} else if (Methods.isFriend(MainUser.username, userName) == 2) {
			pr.getAddFrnd().setText("Accept");
			pr.getAddFrnd().setOnMouseClicked(e -> {
				Methods.AcceptFriend(MainUser.username, userName);
				try {
					changeToProfile(userName, displayName, imgUrl, id);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});
		} else if (Methods.isFriend(MainUser.username, userName) == 1) {
			pr.getAddFrnd().setText("Cancel");
			pr.getAddFrnd().setOnMouseClicked(e -> {
				Methods.CancelFrndRequest(MainUser.username, userName);
				try {
					changeToProfile(userName, displayName, imgUrl, id);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});
		} else {
			pr.getAddFrnd().setText("Add Friend");
			pr.getAddFrnd().setOnMouseClicked(e -> {
				try {
					Methods.FriendRequest(MainUser.username, userName);
				} catch (InterruptedException | ExecutionException e1) {
					e1.printStackTrace();
				}
				try {
					changeToProfile(userName, displayName, imgUrl, id);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});
		}
		pr.AddAdmin.setVisible(false);
		vBox.getChildren().add(pane);
		// System.out.println(id);
		List<PostCodeStruct> personal = Methods.allPostOfUser(userName).posts;
		for (PostCodeStruct p : personal) {
			System.out.println(p.id);
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					ProfileStruct profile = Methods.getProfile(p.userEmail);
					p.displayName = profile.getDisplayName();
					p.userImage = profile.getUserImage();
					addSinglePost(p);
					return null;
				}
			};
			Thread thread = new Thread(task);
			thread.start();
		}

	}

	public void changeToMonalisa() throws IOException {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Chat.fxml"));
			AnchorPane pane = loader.load();
			vBox.getChildren().clear();
			if (!post.getChildren().isEmpty()
					&& post.getChildren().get(post.getChildren().size() - 1) instanceof Button) {
				post.getChildren().remove(post.getChildren().size() - 1);
			}
			vBox.getChildren().add(pane);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void changeToContestReminder(ActionEvent event) {
		Label label = new Label("Loading...");
		label.setStyle("-fx-text-fill: white;");
		label.setStyle("-fx-font-size: 18px;");
		stackPane2.getChildren().add(label);

		Task<List<SingleContest>> goToContest = new Task<List<SingleContest>>() {

			@Override
			protected List<SingleContest> call() throws Exception {
				try {
					RequestHandler requestHandler = new RequestHandler();
					List<SingleContest> contests = PageVariable.contests;
					return contests;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

		};

		goToContest.setOnSucceeded((e) -> {
			vBox.getChildren().clear();
			if (!post.getChildren().isEmpty()
					&& post.getChildren().get(post.getChildren().size() - 1) instanceof Button) {
				post.getChildren().remove(post.getChildren().size() - 1);
			}
			List<SingleContest> contests = goToContest.getValue();
			Platform.runLater(() -> {
				try {

					for (SingleContest i : contests) {
						FXMLLoader loader = new FXMLLoader(getClass().getResource("contestReminder.fxml"));
						AnchorPane pane = loader.load();
						System.out.println(i.name + " " + i.site + " " + i.start_time + " " +
								i.duration);
						SingleContestPost s = loader.getController();
						if (i.site != null && i.site != null && i.start_time != null && i.duration != null) {
							s.setPostDate(i.site, i.name, i.start_time, i.duration, i.url);
						}
						vBox.getChildren().add(pane);

					}

				} catch (Exception evv) {
					evv.printStackTrace();
				}
			});

		});
		Thread thread = new Thread(goToContest);
		thread.start();
		label.visibleProperty().bind(goToContest.runningProperty());

	}

	public void changeToResource() {
		vBox.getChildren().clear();
		if (!post.getChildren().isEmpty() && post.getChildren().get(post.getChildren().size() - 1) instanceof Button) {
			post.getChildren().remove(post.getChildren().size() - 1);
		}
		FXMLLoader postForm1 = new FXMLLoader(getClass().getResource("CreateResource.fxml"));
		AnchorPane scenePostForm1 = new AnchorPane();
		try {
			scenePostForm1 = postForm1.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		vBox.getChildren().add(scenePostForm1);
		for (ResourceStruct r : PageVariable.resources) {
			System.out.println("Displaying " + r.id);
			addSingleResource(r);
		}

	}

	public void addSingleResource(ResourceStruct r) {
		Platform.runLater(() -> {
			ProfileStruct user = Methods.getProfile(MainUser.userEmail);
			FXMLLoader singleResourceLoader = new FXMLLoader(getClass().getResource("Resource.fxml"));
			AnchorPane resourceScene = new AnchorPane();
			try {
				resourceScene = singleResourceLoader.load();
				SingleDetailedResourceController singleResource = singleResourceLoader.getController();
				singleResource.setResource(r.title, r.now.toDate().toString(), r.email, r.mainPost, "", r.id);
				System.out.println("Adding " + r.id);
				if (!singleResource.getEmail().getText().equals(MainUser.userEmail) && user.isAdmin == false) {
					singleResource.getDltBtn().setVisible(false);
					singleResource.getEditBtn().setVisible(false);
				}

				singleResource.ResourceTitle.setOnMouseClicked(e -> {
					try {
						vBox.getChildren().clear();
						if (!post.getChildren().isEmpty()
								&& post.getChildren().get(post.getChildren().size() - 1) instanceof Button) {
							post.getChildren().remove(post.getChildren().size() - 1);
						}
						FXMLLoader loader = new FXMLLoader(getClass().getResource("DetailedResource.fxml"));
						AnchorPane pane = loader.load();
						DetailedResourceController controller = loader.getController();

						controller.setId(r.id);
						System.out.println("Setting " + r.mainPost);
						controller.getMainResource().setText(r.mainPost);
						controller.setTitle(r.title);
						controller.getAll();
						controller.getBackButton().setOnAction(ev -> {
							try {
								changeToResource();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						});
						vBox.getChildren().add(pane);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				});

				singleResource.getEditBtn().setOnMouseClicked(e -> {
					try {
						FXMLLoader loader2 = new FXMLLoader(getClass().getResource("CreateResource.fxml"));
						Parent root = (Parent) loader2.load();
						AddResourceController controller1 = loader2.getController();

						UpdateResource obj = new UpdateResource();
						obj.updateFXML(controller1);
						loader2.setController(obj);
						UpdateResource up = loader2.getController();
						up.setEditResource(r.title, r.mainPost, r.id);
						Stage stage = new Stage();
						stage.setScene(new Scene(root));
						stage.show();

					} catch (IOException e1) {
						e1.printStackTrace();
					}
				});
				AnchorPane pn = resourceScene;
				singleResource.getDltBtn().setOnMouseClicked(e -> {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Do you want to delete");
					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK) {
						ProfileStruct user1 = Methods.getProfile(MainUser.userEmail);
						BasicResponse res = new BasicResponse();
						try {
							System.out.println("Deleting " + r.id);
							res = user1.DeleteResource(r.id);
						} catch (ExceptionHandler e1) {
							e1.printStackTrace();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ExecutionException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						Integer status_code = 401;
						status_code = res.getStatus_code();
						if (status_code == 200) {
							Alert alert1 = new Alert(AlertType.INFORMATION);
							alert1.setTitle("Success");
							alert1.setHeaderText(null);
							alert1.setContentText("Resource deleted successfully!");
							alert1.showAndWait();
							vBox.getChildren().remove(pn);
						} else {
							Alert alert1 = new Alert(AlertType.ERROR);
							alert1.setTitle("Error");
							alert1.setHeaderText(null);
							alert1.setContentText("Resource not deleted!");
							alert1.showAndWait();
						}
					}
				});
				vBox.getChildren().add(resourceScene);
			} catch (IOException e) {
				e.printStackTrace();
			}

		});
	}

	public void changeToCart() {
		vBox.getChildren().clear();
		if (!post.getChildren().isEmpty() && post.getChildren().get(post.getChildren().size() - 1) instanceof Button) {
			post.getChildren().remove(post.getChildren().size() - 1);
		}
		Button download = new Button("Download");
		download.getStyleClass().add("Button");
		download.setOnAction(e -> {
			try {
				downloadCart();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		vBox.getChildren().add(download);
		for (PostCodeStruct p : PageVariable.carts) {
			try {
				ProfileStruct user = Methods.getProfile(p.userEmail);
				p.displayName = user.getDisplayName();
				p.userImage = user.getUserImage();
				addSinglePost(p);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static String decodeBase64(String base64String) {
		byte[] decodedBytes = Base64.decode(base64String);
		return new String(decodedBytes);
	}

	public static String DecodeBase64(String base64String) {
		byte[] decodedBytes = java.util.Base64.getUrlDecoder().decode(base64String);
		return new String(decodedBytes);
	}

	private void downloadCart() throws IOException {
		com.itextpdf.text.Document document = new com.itextpdf.text.Document();
		try {
			com.itextpdf.text.pdf.PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(document,
					new java.io.FileOutputStream("Codebook.pdf"));
			document.open();
			com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(1);
			// table.addCell("Code");
			for (PostCodeStruct p : PageVariable.carts) {
				increaseDownload(p.username, p.getId());
				System.out.println("Downloading " + DecodeBase64(p.code));
				Paragraph para1 = new Paragraph();
				Chunk chunk1 = new Chunk("Title: " + p.getTitle() + "\n");
				chunk1.setFont(FontFactory.getFont(FontFactory.HELVETICA_BOLD));
				para1.add(chunk1);

				Chunk chunk2 = new Chunk("Code: " + DecodeBase64(p.code) + "\n\n\n\n");
				chunk2.setFont(FontFactory.getFont(FontFactory.HELVETICA));
				para1.add(chunk2);
				para1.setAlignment(Element.ALIGN_UNDEFINED);

				PdfPCell cell = new PdfPCell(para1);
				cell.setBorder(0);
				cell.setNoWrap(false);
				cell.setHorizontalAlignment(Element.ALIGN_UNDEFINED);
				table.addCell(cell);
				System.out.println("Downloaded " + p.getId());

			}
			document.add(table);
			document.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		PageVariable.carts.clear();
		Methods.clearCart(MainUser.username);
		changeToCart();
	}

	public void increaseDownload(String username, String document_id) throws InterruptedException, ExecutionException {

		Methods.increaseDownload(username, document_id);
	}

	public void addSinglePost(PostCodeStruct p) throws IOException {
		Platform.runLater(() -> {
			ProfileStruct user = Methods.getProfile(MainUser.userEmail);
			ProfileStruct user1 = Methods.getProfile(p.userEmail);
			p.displayName = user1.getDisplayName();
			p.userImage = user1.getUserImage();
			System.out.println("Added " + p.id);
			FXMLLoader singlePostLoader = new FXMLLoader(getClass().getResource("Post.fxml"));
			AnchorPane postScene = new AnchorPane();
			try {
				postScene = singlePostLoader.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
			SinglePostController singlePost = singlePostLoader.getController();
			singlePost.setData(p.title, p.code, p.userImage, p.stdin, p.output, p.time,
					p.memory, p.aiResponse);
			singlePost.getLikeNumber().setText(String.valueOf(p.likes));
			singlePost.getDownloadNumbers().setText(String.valueOf(p.download));
			if (p.getUserEmail().equals(MainUser.userEmail) == false && user.isAdmin() == false) {
				singlePost.getDeleteBtn().setVisible(false);
				singlePost.getEditBtn().setVisible(false);
			}
			Button likeBtn = singlePost.getLikeNumber();
			likeBtn.setOnAction(e -> {
				Boolean isUserLikes = Methods.isUserLikes(MainUser.username, p.getId());
				System.out.println(isUserLikes + p.id);
				likeHandler(p.id, isUserLikes, likeBtn, MainUser.username, p.username);
			});
			Label postCodeSec = singlePost.getPostCode();
			postCodeSec.setOnMouseClicked(e -> {
				try {
					vBox.getChildren().clear();
					if (!post.getChildren().isEmpty()
							&& post.getChildren().get(post.getChildren().size() - 1) instanceof Button) {
						post.getChildren().remove(post.getChildren().size() - 1);
					}
					FXMLLoader loader = new FXMLLoader(getClass().getResource("DetailedPost.fxml"));
					BorderPane pane = loader.load();
					DetailedPageController controller = loader.getController();

					controller.setData(p.title, p.getCode(), p.getStdin(), p.getOutput(), p.getTime(), p.getMemory(),
							p.getAiResponse());
					controller.getBack().setOnAction(ev -> {
						try {
							changeToHome(null);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					});
					vBox.getChildren().add(pane);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});

			singlePost.getPostPic().setOnMouseClicked(e -> {
				try {
					changeToProfile(p.username, p.displayName, p.userImage, Methods.getIdToken(p.userEmail).idToken);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});

			Button addToCartBtn = singlePost.getAdd();
			addToCartBtn.setOnAction(e -> {
				addCart(p);
				alert("Added", 0);
			});
			Button dlButton = singlePost.getDeleteBtn();
			AnchorPane pn = postScene;
			dlButton.setOnAction(e -> {
				try {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Delete");
					alert.setHeaderText("Are you sure you want to delete this post?");
					alert.setContentText("Press OK to delete");
					alert.showAndWait();
					if (alert.getResult() == ButtonType.CANCEL)
						return;
					user.DeletePost(p.getId());

					vBox.getChildren().remove(pn);
				} catch (UnirestException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExceptionHandler e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				alert("Deleted", 0);
			});

			Button edit = singlePost.getEditBtn();
			edit.setOnAction(ev -> {
				// editBtn.setOnAction((EVENT) -> {
				try {
					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CreatePost.fxml"));
					Parent root = (Parent) fxmlLoader.load();
					CreatePost controller1 = fxmlLoader.getController();
					UpdatePost obj = new UpdatePost();
					obj.updateFXML(controller1);
					fxmlLoader.setController(obj);
					UpdatePost up = fxmlLoader.getController();
					up.setEditData(p.id, p.title,
							p.code, singlePost.getInput(), singlePost.getOutput(),
							singlePost.getTime(), singlePost.getMemory(), singlePost.getAiResponse());
					up.like = String.valueOf(p.likes);
					up.download = String.valueOf(p.download);
					Stage stage = new Stage();
					stage.setScene(new Scene(root));
					stage.show();
				} catch (IOException e) {
					e.printStackTrace();
				}

			});

			// Animated animated = new Animated(postScene,
			// AnimationProperty.of(postScene.opacityProperty()));
			// if (post.getChildren().size() == 1)
			// post.getChildren().add(0, animated);
			// else
			// post.getChildren().add(2, animated);
			vBox.getChildren().add(postScene);
		});

	}

	public void likeHandler(String doc_id, Boolean liked, Button likeBtn, String username, String postUsername) {
		if (liked) {
			likeBtn.setText(String.valueOf(Integer.parseInt(likeBtn.getText()) - 1));
			Methods.decreaseLike(postUsername, doc_id);
		} else {
			likeBtn.setText(String.valueOf(Integer.parseInt(likeBtn.getText()) + 1));
			Methods.increaseLike(postUsername, doc_id);
		}
	}

	public void addCart(PostCodeStruct p) {
		ProfileStruct user = Methods.getProfile(p.userEmail);
		p.displayName = user.getDisplayName();
		p.userImage = user.getUserImage();
		Methods.AddCart(p, MainUser.username);
	}

	// 0 means success 1 means not successful
	public void alert(String text, int type) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		if (type == 1) {
			alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error!");
		} else if (type == 0) {
			alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Success");
		}
		alert.setHeaderText(null);
		alert.setContentText(text);
		alert.showAndWait();
	}

	private void DataInit() {

		query.addSnapshotListener(new EventListener<QuerySnapshot>() {

			@Override
			public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirestoreException e) {
				if (e != null) {
					System.err.println("Listen failed: " + e);
					return;
				}
				System.out.println("Current data: ");

				List<DocumentChange> documentChanges = snapshots.getDocumentChanges();

				for (DocumentChange d : documentChanges) {
					if (d.getType() == DocumentChange.Type.ADDED) {
						PostCodeStruct p = d.getDocument().toObject(PostCodeStruct.class);
						ProfileStruct user = Methods.getProfile(p.userEmail);
						p.displayName = user.getDisplayName();
						p.userImage = user.getUserImage();
						PageVariable.posts.add(p);
						System.out.println(p.id);
						Task<Void> task = new Task<Void>() {
							@Override
							protected Void call() throws Exception {
								addSinglePost(p);
								return null;
							}
						};
						Thread thread = new Thread(task);
						thread.start();
					}
					if (d.getType() == DocumentChange.Type.MODIFIED) {
						PostCodeStruct p = d.getDocument().toObject(PostCodeStruct.class);
						for (PostCodeStruct postCodeStruct : PageVariable.posts) {
							if (postCodeStruct.id.equals(p.id)) {
								ProfileStruct user = Methods.getProfile(p.userEmail);
								p.displayName = user.getDisplayName();
								p.userImage = user.getUserImage();
								postCodeStruct.likes = p.likes;
								postCodeStruct.liked = p.liked;
								postCodeStruct.download = p.download;
								postCodeStruct.aiResponse = p.aiResponse;
								postCodeStruct.output = p.output;
								postCodeStruct.time = p.time;
								postCodeStruct.memory = p.memory;
								postCodeStruct.stdin = p.stdin;
								postCodeStruct.code = p.code;
								postCodeStruct.title = p.title;
								postCodeStruct.userImage = p.userImage;
								postCodeStruct.username = p.username;

								break;
							}
						}
					}
					if (d.getType() == DocumentChange.Type.REMOVED) {
						PostCodeStruct p = d.getDocument().toObject(PostCodeStruct.class);
						for (PostCodeStruct postCodeStruct : PageVariable.posts) {
							if (postCodeStruct.id.equals(p.id)) {
								PageVariable.posts.remove(postCodeStruct);
								break;
							}
						}
					}

				}
				lastDoc = snapshots.getDocuments().get(snapshots.size() - 1);
			}

		});

		db.collection("UsersPost").document(MainUser.username).collection("Posts")
				.addSnapshotListener(new EventListener<QuerySnapshot>() {

					@Override
					public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirestoreException e) {
						if (e != null) {
							System.err.println("Listen failed: " + e);
							return;
						}
						System.out.println("Current data: ");

						List<DocumentChange> documentChanges = snapshots.getDocumentChanges();

						for (DocumentChange d : documentChanges) {
							if (d.getType() == DocumentChange.Type.ADDED) {
								PostCodeStruct p = d.getDocument().toObject(PostCodeStruct.class);
								ProfileStruct user = Methods.getProfile(p.userEmail);
								p.displayName = user.getDisplayName();
								p.userImage = user.getUserImage();
								PageVariable.personal.add(p);

							}
							if (d.getType() == DocumentChange.Type.MODIFIED) {
								PostCodeStruct p = d.getDocument().toObject(PostCodeStruct.class);
								for (PostCodeStruct postCodeStruct : PageVariable.personal) {
									if (postCodeStruct.id.equals(p.id)) {
										ProfileStruct user = Methods.getProfile(p.userEmail);
										p.displayName = user.getDisplayName();
										p.userImage = user.getUserImage();
										postCodeStruct.likes = p.likes;
										postCodeStruct.liked = p.liked;
										postCodeStruct.download = p.download;
										postCodeStruct.aiResponse = p.aiResponse;
										postCodeStruct.output = p.output;
										postCodeStruct.time = p.time;
										postCodeStruct.memory = p.memory;
										postCodeStruct.stdin = p.stdin;
										postCodeStruct.code = p.code;
										postCodeStruct.title = p.title;
										postCodeStruct.userImage = p.userImage;
										postCodeStruct.username = p.username;

										break;
									}
								}
							}
							if (d.getType() == DocumentChange.Type.REMOVED) {
								PostCodeStruct p = d.getDocument().toObject(PostCodeStruct.class);
								for (PostCodeStruct postCodeStruct : PageVariable.personal) {
									if (postCodeStruct.id.equals(p.id)) {
										PageVariable.personal.remove(postCodeStruct);
										break;
									}
								}
							}

						}
					}

				});

		db.collection("UsersCart").document(MainUser.username).collection("Carts")
				.addSnapshotListener(new EventListener<QuerySnapshot>() {

					@Override
					public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirestoreException e) {
						if (e != null) {
							System.err.println("Listen failed: " + e);
							return;
						}
						System.out.println("Current data: ");

						List<DocumentChange> documentChanges = snapshots.getDocumentChanges();

						for (DocumentChange d : documentChanges) {
							if (d.getType() == DocumentChange.Type.ADDED) {
								PostCodeStruct p = d.getDocument().toObject(PostCodeStruct.class);
								ProfileStruct user = Methods.getProfile(p.userEmail);
								p.displayName = user.getDisplayName();
								p.userImage = user.getUserImage();
								PageVariable.carts.add(p);

							}
							if (d.getType() == DocumentChange.Type.MODIFIED) {
								PostCodeStruct p = d.getDocument().toObject(PostCodeStruct.class);
								for (PostCodeStruct postCodeStruct : PageVariable.carts) {
									if (postCodeStruct.id.equals(p.id)) {
										ProfileStruct user = Methods.getProfile(p.userEmail);
										p.displayName = user.getDisplayName();
										p.userImage = user.getUserImage();
										postCodeStruct.likes = p.likes;
										postCodeStruct.liked = p.liked;
										postCodeStruct.download = p.download;
										postCodeStruct.aiResponse = p.aiResponse;
										postCodeStruct.output = p.output;
										postCodeStruct.time = p.time;
										postCodeStruct.memory = p.memory;
										postCodeStruct.stdin = p.stdin;
										postCodeStruct.code = p.code;
										postCodeStruct.title = p.title;
										postCodeStruct.userImage = p.userImage;
										postCodeStruct.username = p.username;
										break;
									}
								}
							}
							if (d.getType() == DocumentChange.Type.REMOVED) {
								PostCodeStruct p = d.getDocument().toObject(PostCodeStruct.class);
								for (PostCodeStruct postCodeStruct : PageVariable.carts) {
									if (postCodeStruct.id.equals(p.id)) {
										PageVariable.carts.remove(postCodeStruct);
										break;
									}
								}
							}

						}
					}
				});

		FXMLLoader loader = new FXMLLoader(getClass().getResource("ContributionList.fxml"));
		AnchorPane pane = null;
		ContribtuionController controller = null;
		VBox CntLst = null;
		try {
			pane = loader.load();
			contribution.getChildren().add(pane);
			controller = loader.getController();
			CntLst = controller.getContributions();
			CntLst.getChildren().add(vBox2);
			CntLst.setSpacing(10);
		} catch (IOException e1) {
			System.out.println("Error");
			e1.printStackTrace();
		}
		// contribution.getChildren().add(pane);
		vBox1.getChildren().add(pane);

		db.collection("Contributions").orderBy("contribution", Query.Direction.DESCENDING)
				.addSnapshotListener(new EventListener<QuerySnapshot>() {

					@Override
					public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirestoreException e) {
						if (e != null) {
							System.err.println("Listen failed: " + e);
							return;
						}
						System.out.println("Current data: ");

						List<DocumentChange> documentChanges = snapshots.getDocumentChanges();
						Platform.runLater(() -> {
							for (DocumentChange d : documentChanges) {

								System.out.println(d.getType() + " " + d.getDocument().getId());
								if (d.getType() == DocumentChange.Type.ADDED) {
									System.out.println("Contribution " + d.getDocument().getId());
									ContributionStruct p = d.getDocument().toObject(ContributionStruct.class);
									// System.out.println(p.id_token);
									// System.out.println(p.username);
									ProfileStruct user = Methods.getProfile(p.username);
									System.out.println(user.getDisplayName() + " " + user.getUserImage());
									p.userImage = user.getUserImage();
									PageVariable.contributions.add(p);

									FXMLLoader loader = new FXMLLoader(
											getClass().getResource("SingleContribution.fxml"));
									AnchorPane pane;
									try {
										pane = loader.load();
										ContributionSingleController controller = loader.getController();
										controller.setImage(p.userImage);
										controller.getContributionUser().setText(p.username);
										pane.setOnMouseClicked(evv -> {
											try {
												ProfileStruct profile = Methods.getProfile(p.username);
												p.userImage = profile.getUserImage();
												String displayName = user.getDisplayName();
												String email = profile.username;
												String idToken = Methods.getIdToken(email).idToken;
												changeToProfile(p.username, displayName, p.userImage, idToken);
											} catch (IOException e1) {
												e1.printStackTrace();
											}
										});
										vBox2.getChildren().add(pane);
										vBox2.setSpacing(10);
									} catch (IOException e1) {
										System.out.println("Error");
										e1.printStackTrace();
									}

								}
								if ((d.getType() == DocumentChange.Type.MODIFIED)) {
									System.out.println("Modified " + d.getDocument().getId());
									ContributionStruct p = d.getDocument().toObject(ContributionStruct.class);
									ProfileStruct user = Methods.getProfile(p.username);
									p.userImage = user.getUserImage();
									for (ContributionStruct contributionStruct : PageVariable.contributions) {
										if (contributionStruct.username.equals(p.username)) {
											contributionStruct.contribution = p.contribution;
											contributionStruct.userImage = p.userImage;
											contributionStruct.username = p.username;
											break;
										}
									}

									vBox2.getChildren().clear();
									for (ContributionStruct contributionStruct : PageVariable.contributions) {
										FXMLLoader loader = new FXMLLoader(
												getClass().getResource("SingleContribution.fxml"));
										AnchorPane pane;
										try {
											pane = loader.load();
											ContributionSingleController controller = loader.getController();
											controller.setImage(contributionStruct.userImage);
											controller.getContributionUser().setText(contributionStruct.username);
											pane.setOnMouseClicked(evv -> {
												try {
													ProfileStruct profile = Methods
															.getProfile(contributionStruct.username);
													contributionStruct.userImage = profile.getUserImage();
													String displayName = user.getDisplayName();
													String email = profile.username;
													String idToken = Methods.getIdToken(email).idToken;
													changeToProfile(contributionStruct.username, displayName,
															contributionStruct.userImage, idToken);
												} catch (IOException e1) {
													e1.printStackTrace();
												}
											});
											vBox2.getChildren().add(pane);
											vBox2.setSpacing(10);
										} catch (IOException e1) {
											System.out.println("Error");
											e1.printStackTrace();
										}
									}

								}
								if (d.getType() == DocumentChange.Type.REMOVED) {
									ContributionStruct p = d.getDocument().toObject(ContributionStruct.class);
									ProfileStruct user = Methods.getProfile(p.username);
									p.userImage = user.getUserImage();
									for (ContributionStruct contributionStruct : PageVariable.contributions) {
										if (contributionStruct.username.equals(p.username)) {
											PageVariable.contributions.remove(contributionStruct);
											for (int i = 0; i < vBox2.getChildren().size(); i++) {
												AnchorPane pane = (AnchorPane) vBox2.getChildren().get(i);
												ContributionSingleController controller = (ContributionSingleController) pane
														.getProperties().get("controller");
												if (controller != null && controller.getContributionUser().getText()
														.equals(p.username)) {
													vBox2.getChildren().remove(i);
													break;
												}
											}
											break;
										}
									}
								}

							}
						});
					}
				});
		// add resource later
		db.collection("Resources").orderBy("now", Query.Direction.DESCENDING)
				.addSnapshotListener(new EventListener<QuerySnapshot>() {

					@Override
					public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirestoreException e) {
						if (e != null) {
							System.err.println("Listen failed: " + e);
							return;
						}
						System.out.println("Current data: ");

						List<DocumentChange> documentChanges = snapshots.getDocumentChanges();

						for (DocumentChange d : documentChanges) {
							if (d.getType() == DocumentChange.Type.ADDED) {
								ResourceStruct p = d.getDocument().toObject(ResourceStruct.class);
								PageVariable.resources.add(p);
								// addSingleResource(p);
							}
							if (d.getType() == DocumentChange.Type.MODIFIED) {
								ResourceStruct p = d.getDocument().toObject(ResourceStruct.class);
								for (ResourceStruct resourceStruct : PageVariable.resources) {
									if (resourceStruct.id.equals(p.id)) {
										resourceStruct.title = p.title;
										resourceStruct.now = p.now;
										resourceStruct.email = p.email;
										resourceStruct.mainPost = p.mainPost;
										resourceStruct.id = p.id;
										break;
									}
								}
							}
							if (d.getType() == DocumentChange.Type.REMOVED) {
								ResourceStruct p = d.getDocument().toObject(ResourceStruct.class);
								for (ResourceStruct resourceStruct : PageVariable.resources) {
									if (resourceStruct.id.equals(p.id)) {
										PageVariable.resources.remove(resourceStruct);
										break;
									}
								}
							}

						}
					}

				});
		db.collection("UserResource").document(MainUser.userEmail).collection("Resources")
				.orderBy("now", Query.Direction.DESCENDING)
				.addSnapshotListener(new EventListener<QuerySnapshot>() {

					@Override
					public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirestoreException e) {
						if (e != null) {
							System.err.println("Listen failed: " + e);
							return;
						}
						System.out.println("Current data: ");

						List<DocumentChange> documentChanges = snapshots.getDocumentChanges();

						for (DocumentChange d : documentChanges) {
							if (d.getType() == DocumentChange.Type.ADDED) {
								ResourceStruct p = d.getDocument().toObject(ResourceStruct.class);
								PageVariable.personalResource.add(p);
							}
							if (d.getType() == DocumentChange.Type.MODIFIED) {
								ResourceStruct p = d.getDocument().toObject(ResourceStruct.class);
								for (ResourceStruct resourceStruct : PageVariable.personalResource) {
									if (resourceStruct.id.equals(p.id)) {
										resourceStruct.title = p.title;
										resourceStruct.now = p.now;
										resourceStruct.email = p.email;
										resourceStruct.mainPost = p.mainPost;
										resourceStruct.id = p.id;
										break;
									}
								}
							}
							if (d.getType() == DocumentChange.Type.REMOVED) {
								ResourceStruct p = d.getDocument().toObject(ResourceStruct.class);
								for (ResourceStruct resourceStruct : PageVariable.personalResource) {
									if (resourceStruct.id.equals(p.id)) {
										PageVariable.personalResource.remove(resourceStruct);
										break;
									}
								}
							}

						}
					}

				});
	}

	public void signOut() throws IOException {
		LoginValidator.fileDelete();
		Main.setRoot("login");
	}
}