# Online Wardrobe Manager

A modern, animated Java Swing application to help users manage their wardrobe, get outfit suggestions, and visualize clothing combinations with interactive UI elements.

## Features

- **User Authentication:** Secure login and signup with password hashing.
- **Add Clothing Items:** Add clothes with type, color, pattern, occasion, and image.
- **Animated Image Previews:** See animated, color-matched clothing illustrations if no image is provided.
- **Wardrobe View:** Browse and filter your wardrobe by type and color.
- **Outfit Suggestions:** Get AI-powered outfit suggestions based on occasion and color compatibility.
- **Persistent Storage:** User-specific wardrobe data is saved locally.
- **Logout & Account Management:** Easily switch users or exit the application.


## Getting Started

### Prerequisites

- Java 11 or higher
- Windows (batch script provided; for Linux/Mac, adapt the script accordingly)

### Build & Run

1. **Clone the repository:**
   ```sh
   git clone https://github.com/aakashbhogolu/Online-wardrobe.git
   cd Online-wardrobe
   ```

2. **Compile and run:**
   ```sh
   ./compile-and-run.bat
   ```
   _or on Windows:_
   ```bat
   .\compile-and-run.bat
   ```

3. **Login or Sign Up:**  
   On first launch, create a new account or log in with your credentials.

4. **Add Items & Get Suggestions:**  
   Use the "Add Item" tab to add clothes, then visit "Outfit Suggestions" to get recommendations.

## Project Structure

```
src/
  main/
    java/
      com/wardrobemanager/
        components/      # Custom Swing components and image generators
        model/           # Data models (ClothingItem, User, etc.)
        WardrobeManagerGUI.java  # Main application window
        LoginDialog.java         # Login/signup dialog
compile-and-run.bat     # Windows batch script to build and run
```

## Customization

- **Add More Clothing Types/Colors:**  
  Edit `ClothingType.java` and `ColorMatcher.java` in `src/main/java/com/wardrobemanager/model/`.

- **Change Image Generation:**  
  Update `ClothingImageGenerator.java` in `src/main/java/com/wardrobemanager/components/`.

## Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

## License

This project is open source and available under the [MIT License](LICENSE). 
