const { MongoClient, ObjectId } = require('mongodb');
const fs = require('fs');
const path = require('path');

const uri = "mongodb+srv://itaybendaniel:YQ4wLqMHZ7anvp7C@cluster0.61qv0ya.mongodb.net/test?retryWrites=true&w=majority&appName=Cluster0";
const client = new MongoClient(uri);

async function importCollection(db, collectionName) {
    try {
        const collection = db.collection(collectionName);
        const filePath = path.join(__dirname, `${collectionName}.json`);
        const fileContent = fs.readFileSync(filePath, 'utf8');
        console.log(`Importing ${collectionName} from ${filePath}`);

        // Parse and transform data
        const data = JSON.parse(fileContent).map(item => {
            if (item._id && item._id.$oid) {
                item._id = new ObjectId(item._id.$oid);
            }
            if (item.user_id && item.user_id.$oid) {
                item.user_id = new ObjectId(item.user_id.$oid);
            }
            if (item.userDetails && item.userDetails.$oid) {
                item.userDetails = new ObjectId(item.userDetails.$oid);
            }
            if (item.video_id && item.video_id.$oid) {
                item.video_id = new ObjectId(item.video_id.$oid);
            }
            if (item.likes) {
                item.likes = item.likes.map(like => new ObjectId(like.$oid));
            }
            if (item.dislikes) {
                item.dislikes = item.dislikes.map(dislike => new ObjectId(dislike.$oid));
            }
            if (item.createdAt && item.createdAt.$date) {
                item.createdAt = new Date(item.createdAt.$date);
            }
            if (item.updatedAt && item.updatedAt.$date) {
                item.updatedAt = new Date(item.updatedAt.$date);
            }
            return item;
        });

        // Drop the collection if it exists to avoid duplicate key errors
        await collection.drop().catch(err => {
            if (err.code !== 26) {
                throw err;
            }
        });

        await collection.insertMany(data);
        console.log(`${collectionName} data imported successfully`);
    } catch (error) {
        console.error(`Error importing ${collectionName} data: `, error);
    }
}

async function importData() {
    try {
        await client.connect();
        console.log("Connected to MongoDB");

        const db = client.db('test');

        await importCollection(db, 'users');
        await importCollection(db, 'videos');
        await importCollection(db, 'comments');

    } catch (error) {
        console.error("Error importing data: ", error);
    } finally {
        await client.close();
    }
}

importData();
