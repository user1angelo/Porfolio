const express = require('express');
const mongoose = require('mongoose');
const fs = require('fs').promises;
const path = require('path');
const session = require('express-session');
const bcrypt = require('bcrypt'); 
const app = express();
const port = 3000;
const router = express.Router();
const multer = require('multer');
const User = require('./Model/user');
const Review = require('./Model/review');
const Comment = require('./Model/comment');
const Establishment = require('./Model/establishment');

// Set the views directory for EJS files
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

// Serve the styles.css file directly
app.use(express.static(path.join(__dirname, 'public')));

mongoose.connect('mongodb+srv://julliantalino:Suicidalneko1@nekokami.hcwrogz.mongodb.net/?retryWrites=true&w=majority', { useNewUrlParser: true, useUnifiedTopology: true })
  .then(() => {
    console.log('MongoDB connected');
  })
  .catch((err) => {
    console.error('MongoDB connection error', err);
  });

// Middleware for parsing JSON and handling URL-encoded form data
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Add this middleware for session
app.use(session({
  secret: 'your-secret-key',
  resave: true,
  saveUninitialized: true
}));

// Routes
app.use('/', router);

// Middleware to check if the user is authenticated
const isAuthenticated = (req, res, next) => {
  if (req.session.userId) {
    res.locals.authenticated = true; // Set authenticated as a local variable
    next();
  } else {
    res.redirect('/login'); // Redirect to the login page if not authenticated
  }
};

// Homepage route
app.get('/', async (req, res) => {
  try {
    // Fetch reviews from the database or any source
    const reviews = await Review.find().populate('reviewer');

    // Render the 'homepage.ejs' template and pass the 'authenticated' status and 'reviews'
    res.render('homepage.ejs', { 
      authenticated: false,
      reviews: reviews // Pass the fetched reviews to the template
    });
  } catch (error) {
    console.error(error);
    res.status(500).send('Error fetching reviews');
  }
});



// Home route
router.get('/homepage.ejs', isAuthenticated, async (req, res) => {
  try {
    // Fetch reviews from the database or any source
    const reviews = await Review.find().populate('reviewer');

    // Render the 'homepage.ejs' template and pass the 'authenticated' status and 'reviews'
    res.render('homepage', { 
      authenticated: res.locals.authenticated, // Use the authenticated status from middleware
      reviews: reviews // Pass the fetched reviews to the template
    });
  } catch (error) {
    console.error(error);
    res.status(500).send('Error fetching reviews');
  }
});


// Login route
router.get('/login', (req, res) => {
  res.render('login', { authenticated: false });
});

// Login route (POST method)
router.post('/login', async (req, res) => {
  try {
    const { username, password } = req.body;

    // Find user by username
    const user = await User.findOne({ username });

    if (!user) {
      console.log('User not found for username:', username);
      return res.status(401).send('Invalid username or password');
    }

    // Compare the provided password with the password from the database
    if (password !== user.password) {
      console.log('Invalid password for username:', username);
      return res.status(401).send('Invalid username or password');
    }

    // Set the user as authenticated in the session
    req.session.userId = user._id;
    res.locals.authenticated = true;

    // Redirect to the homepage route with authentication status
    res.redirect('/homepage.ejs');
  } catch (error) {
    console.error('Error during login:', error);
    res.status(500).send('Error during login');
  }
});

// Logout route
router.get('/logout.ejs', (req, res) => {
  // Clear the session variables
  req.session.destroy(err => {
    if (err) {
      console.error(err);
      return res.status(500).send('Error during logout');
    }
    res.redirect('/');
  });
});

// Register route
router.get('/register.ejs', isAuthenticated, (req, res) => {
  res.render('register', { authenticated: res.locals.authenticated });
});

// Register route (POST method)
router.post('/register', async (req, res) => {
  try {
    const { username, password, email } = req.body;

    // Check if username, password, and email are provided
    if (!username || !password || !email) {
      return res.status(400).send('Username, password, and email are required');
    }

    // Check if the username is already taken
    const existingUsername = await User.findOne({ username });
    if (existingUsername) {
      console.log('Username is already taken:', username);
      return res.status(400).send('Username is already taken');
    }

    // Check if the email is already taken
    const existingEmail = await User.findOne({ email });
    if (existingEmail) {
      console.log('Email is already taken:', email);
      return res.status(400).send('Email is already taken');
    }

    // Hash the password
    const hashedPassword = await bcrypt.hash(password, 10);

    const newUser = new User({
      username,
      password: hashedPassword,
      email,
    });

    console.log('Saving new user:', newUser);

    // Save the new user to the database
    await newUser.save();

    // Set the new user as authenticated in the session
    req.session.userId = newUser._id;

    // Render the homepage with the authentication status
    res.redirect('/homepage.ejs');

  } catch (error) {
    console.error(error);
    res.status(500).send('Error during registration');
  }
});


// User Profile route
router.get('/userprofile.ejs', isAuthenticated, async (req, res) => {
  try {
    const userId = req.session.userId;

    // Fetch the user profile based on the provided userId
    const userProfile = await User.findById(userId);

    // Render your user profile page (userprofile.ejs) with the retrieved userProfile data
    res.render('userprofile.ejs', { user: userProfile });
  } catch (error) {
    console.error(error);
    res.status(500).send('Error fetching user profile');
  }
});


// route for handling GET requests to /createestablishment
router.get('/createestablishment.ejs', isAuthenticated, async (req, res) => {
  res.render('createestablishment', { 
    authenticated: res.locals.authenticated
  });

});

// POST for create establishment
router.post('/createestablishment.ejs', async (req, res) => {
  try {
    // Access the data from the POST request body
    const { name, description, location } = req.body;

    if (!name || !description || !location) {
      return res.status(400).send('All fields are required');
    }

    if (!req.session.userId) {
      return res.status(401).send('User not authenticated');
    }

    // Assuming you want to associate the establishment with the logged-in user
    const ownerId = req.session.userId;

    const newEstablishment = new Establishment({
      name,
      description,
      location,
      owner: ownerId, // Assuming you have a field named 'owner' in your Establishment model
    });

    await newEstablishment.save();

    // Redirect to a success page or do something else based on the result
    return res.redirect('/success.ejs'); // Change '/success' to the desired success page URL
  } catch (err) {
    console.error(err);
    return res.status(500).send('Error creating establishment');
  }
});


// Createreview route
router.get('/createreview.ejs', isAuthenticated ,async (req, res) => {
  try {
    // Fetch reviews from the database
    const reviews = await Review.find().populate('reviewer');
    res.render('createreview', { authenticated: true, reviews });
  } catch (error) {
    console.error(error);
    res.status(500).send('Error fetching reviews');
  }
});

// Create review (POST method)
router.post('/createreview.ejs', async (req, res) => {
  const { establishment, reviewTitle, reviewBody, rating, media } = req.body;

  try {
    if (!establishment || !reviewTitle || !reviewBody || !rating) {
      return res.status(400).send('All fields are required');
    }

    if (!req.session.userId) {
      return res.status(401).send('User not authenticated');
    }

    const reviewerId = req.session.userId;

    const newReview = new Review({
      establishment,
      title: reviewTitle,
      content: reviewBody,
      rating,
      reviewer: reviewerId,
    });

    await newReview.save();

    // After saving the review, fetch all reviews again to include the newly created one
    const reviews = await Review.find().populate('reviewer');
    
    // Pass the reviews variable when rendering the template
    res.render('homepage.ejs', { authenticated: true, reviews });
  } catch (err) {
    console.error(err);
    res.status(500).send('Error creating review');
  }
});


router.get('/createestablishment.ejs', isAuthenticated, (req, res) => {
  res.render('createestablishment'); 
});


// create establishment route
app.post('/createestablishment', async (req, res) => {
  try {
    const { name, description, location } = req.body;

    // Check if all required fields are provided
    if (!name || !description || !location) {
      return res.status(400).send('All fields are required');
    }

    // Check if the user is authenticated
    if (!req.session.userId) {
      return res.status(401).send('User not authenticated');
    }

    // Assuming ownerId is retrieved from req.session.userId
    const ownerId = req.session.userId;

    // Create a new establishment instance
    const newEstablishment = new Establishment({
      name,
      description,
      location,
      owner: ownerId,
    });

    // Save the new establishment to the database
    await newEstablishment.save();

    return res.redirect('/success.ejs'); // Redirect to success page after creation
  } catch (error) {
    console.error(error); // Log detailed error for debugging
    return res.status(500).send('Error creating establishment');
  }
});


// About Us route
router.get('/aboutus.ejs', (req, res) => {
  res.render('aboutus', { authenticated: false }); // Set authenticated as needed
});

// Establishments route
app.get('/establishments.ejs', (req, res) => {
  res.render('establishments', { 
    authenticated: res.locals.authenticated, // Use the authenticated status from middleware
  });
});

app.get('/establishmentreviews.ejs', (req, res) => {
  res.render('establishmentreviews', { authenticated: res.locals.authenticated });
});


const editReview = async (reviewId, editedContent) => {
  try {
    const review = await Review.findById(reviewId);

    if (!review) {
      return null; // or throw an error
    }

    review.content = editedContent;
    await review.save();

    return review;
  } catch (err) {
    console.error(err);
    throw err;
  }
};

// Edit review route
router.put('/editreview/:reviewId', async (req, res) => {
  const reviewId = req.params.reviewId;
  const { editedContent } = req.body;

  try {
    const updatedReview = await editReview(reviewId, editedContent);

    if (!updatedReview) {
      return res.status(404).send('Review not found');
    }

    res.redirect(`/reviewdetails/${reviewId}`);
  } catch (err) {
    console.error(err);
    res.status(500).send('Error editing review');
  }
});

// Write a comment route
router.post('/establishmentowner', async (req, res) => {
  const reviewId = req.params.reviewId;
  const { text, authorId } = req.body;

  try {
    const review = await Review.findById(reviewId);

    if (!review) {
      return res.status(404).send('Review not found');
    }

    const newComment = new Comment({
      text,
      author: authorId,
      review: reviewId,
    });

    await newComment.save();

    review.comments.push(newComment);
    await review.save();

    res.redirect(`/reviewdetails/${reviewId}`);
  } catch (err) {
    console.error(err);
    res.status(500).send('Error adding comment');
  }
});

// Define storage for the uploaded files
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
      // Set the destination folder for storing uploaded files to the 'views' folder
      cb(null, 'views/'); // You may need to create the 'views' folder in your project directory
  },
  filename: (req, file, cb) => {
      // Set the filename for the uploaded file
      cb(null, Date.now() + '-' + file.originalname);
  },
});

// Create the multer middleware
const upload = multer({ storage: storage });

// Edit profile route
router.get('/editprofile.ejs', isAuthenticated, async (req, res) => {
  try {
      const userId = req.session.userId;
      const user = await User.findById(userId);

      if (!user) {
          console.error('User not found:', userId);
          return res.status(404).send('User not found');
      }

      res.render('editprofile', { user });
  } catch (error) {
      console.error(error);
      res.status(500).send('Error fetching user details');
  }
});

router.post('/editprofile', isAuthenticated, upload.single('profilePicture'), async (req, res) => {
  try {
      const userId = req.session.userId;
      const { shortDescription } = req.body;

      // Handle file upload if needed
      const profilePicture = req.file ? req.file.path : ''; // Example if using multer

      const updatedUser = await User.findByIdAndUpdate(userId, {
          shortDescription,
          profilePicture,
      }, { new: true });

      if (!updatedUser) {
          console.error('User not found:', userId);
          return res.status(404).send('User not found');
      }

      // Handle file upload error
      if (req.fileValidationError) {
        return res.status(400).send(req.fileValidationError);
      }

      res.redirect('/userprofile/' + userId);
  } catch (error) {
      console.error(error);
      res.status(500).send('Error updating profile');
  }
});


// Establishment Reviews route
router.get('/establishmentreviews', async (req, res) => {
  const establishmentId = req.params.establishmentId;

  try {
    const establishment = await Establishment.findById(establishmentId);

    if (!establishment) {
      return res.status(404).send('Establishment not found');
    }

    const reviews = await Review.find({ establishment: establishmentId }).populate('reviewer');

    res.render('establishment-reviews', { establishment, reviews });
  } catch (err) {
    console.error(err);
    res.status(500).send('Error fetching establishment reviews');
  }
});

// Full review route
app.get('/full-review.ejs', (req, res) => {
  res.render('full-review', { authenticated: false });
});

// Render review details including associated comments
router.get('/reviewdetails/:reviewId', async (req, res) => {
  const reviewId = req.params.reviewId;

  try {
    const review = await Review.findById(reviewId).populate('reviewer').populate('comments');

    if (!review) {
      return res.status(404).send('Review not found');
    }

    res.render('review-details', { review });
  } catch (err) {
    console.error(err);
    res.status(500).send('Error fetching review details');
  }
});


// Route to display search results
router.get('/searchresults', async (req, res) => {
  const searchQuery = req.query.q;

  if (!searchQuery || searchQuery.trim() === '') {
    return res.status(400).send('Search query is required');
  }

  try {
    // Fetch establishments based on the search query
    const establishments = await Establishment.find({
      $or: [
        { name: { $regex: searchQuery, $options: 'i' } },
        { description: { $regex: searchQuery, $options: 'i' } }
      ]
    });

    res.render('search-results', { establishments, searchQuery });
  } catch (err) {
    console.error(err);
    res.status(500).send('Error fetching search results');
  }
});

module.exports = router;

// Global error handler
app.use((err, req, res, next) => {
  console.error(err);
  res.status(500).send('Internal Server Error: ' + err.message); // Provide more detailed error message
});


app.get('/success', (req, res) => {
  res.render('success'); // Render the success.ejs template
});

// Listen for server connections
app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});