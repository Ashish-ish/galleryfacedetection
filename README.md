The GalleryFaceDetection App detects human faces in gallery images, displays the images with bounding boxes around detected faces, and allows users to tag and save face metadata in a local Room database. The app is built using Kotlin and adheres to Clean Architecture principles. It utilizes Google MediaPipe SDK for face detection and incorporates modern Android development tools such as Hilt for Dependency Injection, Jetpack Compose for UI, and Room Database for data persistence.

Key Features
	1.	Image Permission Management: The app requests permission to access the gallery to fetch images.
	2.	Face Detection:
	•	Converts images to ARGB_8888 format.
	•	Feeds images in batches to the MediaPipe SDK for face detection using parallel processing.
	3.	Bounding Box Display: Displays bounding boxes around detected faces on the images.
	4.	Face Tagging: Allows users to tag faces for identification.
	5.	Data Persistence: Stores tagged face information in a Room Database.

 Architecture

The app uses Clean Architecture to separate concerns:
	•	ui Layer: Handles UI components using Jetpack Compose.
	•	Domain Layer: Contains business logic (use cases for face detection and tagging).
	•	Data Layer: Manages data sources (Room Database).
 	•	Repository Layer: Manages key operations for app.
  
  Tech Stack
	•	Language: Kotlin
	•	UI: Jetpack Compose
	•	Dependency Injection: Hilt
	•	Face Detection: MediaPipe SDK
	•	Database: Room
	•	Image Processing: Bitmap (ARGB_8888 format)
	•	Parallel Processing: Kotlin Coroutines for batching images

 Workflow

1. Permission Handling
	•	Component: PermissionHandler
	•	Flow:
	•	Request gallery access permission using PermissionHandler.
	•	Handle denied/accepted permissions with feedback to the user.

2. Image Loading and Preprocessing
	•	Component: ImageLoader
	•	Steps:
	1.	Fetch images from the camera gallery.
	2.	Convert images to ARGB_8888 format using Bitmap.createBitmap().

3. Face Detection
	•	Component: GalleryRepository
	•	Steps:
	1.	Batch images for processing using Dispatchers.IO.
	2.	Feed batches to MediaPipe’s face detection model.
	3.	Capture bounding box coordinates for detected faces.

4. Bounding Box Display
	•	Component: ImageWithBoundingBoxes
	•	Steps:
	•	Render images with bounding boxes using Compose Canvas.

5. Face Tagging
	•	Component: FaceDetectionRepository
	•	Steps:
	1.	Capture user input for tagging.
	2.	Persist tagged data (face ID, image URI, tag) to the Room database.

6. Data Persistence
	•	Component: TaggingRepository (Room)

Key Concepts

Clean Architecture
	•	Enforces a modular and maintainable codebase.
	•	Domain layer drive all business logic.

Dependency Injection with Hilt
	•	Provides dependencies (e.g., MediaPipe, Room) across layers efficiently.

MediaPipe SDK
	•	Detects faces in images.
	•	Provides bounding box coordinates for detected faces.

Compose for UI
	•	Builds declarative, responsive UI components.
	•	Uses Canvas for drawing bounding boxes on images.

Room Database
	•	Stores metadata (tags) persistently.
	•	Provides DAO for querying tagged faces.

Parallel Processing
	•	Utilizes Kotlin Coroutines to batch-process images for faster detection.

 Data Flow Diagram
    A[Request Gallery Permission] --> B[Load Images]
    B --> C[Convert to ARGB_8888 Format]
    C --> D[Batch Images for Processing]
    D --> E[Feed to MediaPipe SDK]
    E --> F[Face Detection with Bounding Boxes]
    F --> G[Display Images with Bounding Boxes]
    G --> H[Tag Face]
    H --> I[Persist Tagged Data in Room Database]

Working Demo
 https://drive.google.com/file/d/11fAs2lO-Qp-3px848M7xfqM2Vw9hYb4I/view?usp=drivesdk


 
