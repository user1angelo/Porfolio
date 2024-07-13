const mongoose = require('mongoose');

const establishmentSchema = new mongoose.Schema({
    name: { type: String, required: true },
    description: { type: String },
    location: { type: String },
    owner: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true }, 

    reviews: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Review' }]
});

const Establishment = mongoose.model('Establishment', establishmentSchema);

module.exports = Establishment;
