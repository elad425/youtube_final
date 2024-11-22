#include <iostream>
#include <cstring>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <thread>
#include <vector>
#include <map>
#include <set>
#include <algorithm>
#include <random>
#include <sstream>
#include <mutex>
#include <fstream>

// Data structures to store viewing history and video popularity
std::map<std::string, std::set<std::string>> userViews; // userId -> set of videoIds
std::vector<std::pair<std::string, int>> videosWithViews;
std::map<std::string, std::map<std::string, int>> videoSimilarity;
std::mutex dataMutex;

// Function to load initial video data

void init_videos(std::string message)
{
    std::string videoList = message.substr(15); // Remove "INIT_VIDEO_DATA " prefix

    std::stringstream ss(videoList);
    std::string videoEntry;

    while (std::getline(ss, videoEntry, ','))
    {
        videoEntry.erase(0, videoEntry.find_first_not_of(" ")); // Trim leading whitespace
        size_t delimiterPos = videoEntry.find(':');
        if (delimiterPos != std::string::npos)
        {
            std::string videoName = videoEntry.substr(0, delimiterPos);
            int views = std::stoi(videoEntry.substr(delimiterPos + 1));
            videosWithViews.push_back(std::make_pair(videoName, views));

            // Initialize videoSimilarity for each video with an empty map
            for (const auto &video : videosWithViews)
            {
                if (video.first != videoName)
                {
                    videoSimilarity[videoName][video.first] = 0;
                    videoSimilarity[video.first][videoName] = 0;
                }
            }
        }
    }

    // Print video names and views to check them
    std::cout << "Received Video Names and Views:" << std::endl;
    for (const auto &video : videosWithViews)
    {
        std::cout << "Video: " << video.first << ", Views: " << video.second << std::endl;
    }
}
void deleteVideo(const std::string &videoId)
{
    std::lock_guard<std::mutex> lock(dataMutex);

    // Remove video from videosWithViews
    auto it = std::remove_if(videosWithViews.begin(), videosWithViews.end(),
                             [&videoId](const std::pair<std::string, int> &video)
                             {
                                 return video.first == videoId;
                             });
    videosWithViews.erase(it, videosWithViews.end());

    // Remove video from userViews
    for (auto &userView : userViews)
    {
        userView.second.erase(videoId);
    }

    // Remove video from videoSimilarity
    videoSimilarity.erase(videoId);
    for (auto &similarityPair : videoSimilarity)
    {
        similarityPair.second.erase(videoId);
    }

    std::cout << "Deleted video with ID: " << videoId << std::endl;
}
void init_users(std::string message)
{
    std::string userList = message.substr(15); // Remove "INIT_USER_DATA " prefix
    std::stringstream ss(userList);
    std::string username;

    // Clear existing user data before adding new ones
    userViews.clear();

    // Split the usernames by the delimiter (comma)
    while (std::getline(ss, username, ','))
    {
        username.erase(0, username.find_first_not_of(" ")); // Trim leading whitespace
        userViews[username] = std::set<std::string>();      // Initialize with an empty set
    }

    // Optional: Print the userViews to verify they've been initialized
    std::cout << "Users initialized with empty views:" << std::endl;
    for (const auto &user : userViews)
    {
        std::cout << "User: " << user.first << " with " << user.second.size() << " views." << std::endl;
    }
}
void printVideoSimilarity()
{
    std::lock_guard<std::mutex> lock(dataMutex); // Ensure thread safety if this function is called in a multi-threaded context

    std::cout << "Video Similarity Matrix:" << std::endl;

    for (const auto &videoPair : videoSimilarity)
    {
        const std::string &videoId = videoPair.first;
        const auto &similarityMap = videoPair.second;

        std::cout << "Video " << videoId << " similarities:" << std::endl;
        for (const auto &similarityPair : similarityMap)
        {
            const std::string &otherVideoId = similarityPair.first;
            int similarityScore = similarityPair.second;

            std::cout << "  - Similar to " << otherVideoId << " with score " << similarityScore << std::endl;
        }
    }
}
void updateUserViews(const std::string &userId, const std::string &videoId)
{
    // Check if the video is new for this user
    if (userViews[userId].find(videoId) == userViews[userId].end())
    {
        // If the video is new, insert it into the user's viewed videos
        userViews[userId].insert(videoId);

        // Update the similarity matrix
        for (const auto &otherVideoId : userViews[userId])
        {
            if (videoId != otherVideoId)
            {
                videoSimilarity[videoId][otherVideoId]++;
                videoSimilarity[otherVideoId][videoId]++;
            }
        }
    }
    printVideoSimilarity();
}

void printRecommendationScores(const std::map<std::string, int> &recommendationScores)
{
    std::cout << "Recommendation Scores:" << std::endl;
    for (const auto &entry : recommendationScores)
    {
        std::cout << "  Video: " << entry.first << ", Score: " << entry.second << std::endl;
    }
}

void printSortedRecommendations(const std::vector<std::pair<std::string, int>> &sortedRecommendations)
{
    std::cout << "Sorted Recommendations (before adding random videos):" << std::endl;
    for (const auto &entry : sortedRecommendations)
    {
        std::cout << "  Video: " << entry.first << ", Score: " << entry.second << std::endl;
    }
}

void printFinalRecommendations(const std::vector<std::string> &finalRecommendations)
{
    std::cout << "Final Recommendations:" << std::endl;
    for (const auto &videoId : finalRecommendations)
    {
        std::cout << "  Video: " << videoId << std::endl;
    }
}

std::string generateRecommendations(const std::string &userId, int maxRecommendations = 10)
{
    std::lock_guard<std::mutex> lock(dataMutex);
    std::map<std::string, int> recommendationScores;

    // Iterate through the videos the user has watched
    for (const auto &watchedVideo : userViews[userId])
    {
        for (const auto &similarVideoPair : videoSimilarity[watchedVideo])
        {
            const std::string &similarVideo = similarVideoPair.first;
            int similarityScore = similarVideoPair.second;

            // Only recommend videos the user hasn't watched
            if (userViews[userId].find(similarVideo) == userViews[userId].end())
            {
                recommendationScores[similarVideo] += similarityScore;
            }
        }
    }

    // Convert the map to a vector of pairs and sort by score
    std::vector<std::pair<std::string, int>> sortedRecommendations(recommendationScores.begin(), recommendationScores.end());
    std::sort(sortedRecommendations.begin(), sortedRecommendations.end(), [](const auto &a, const auto &b)
              {
                  if (b.second == a.second)
                  {
                      // Tiebreaker by views if scores are equal
                      return videosWithViews[std::distance(videosWithViews.begin(), std::find_if(videosWithViews.begin(), videosWithViews.end(), [&a](const auto &video)
                                                                                                 { return video.first == a.first; }))]
                                 .second < videosWithViews[std::distance(videosWithViews.begin(), std::find_if(videosWithViews.begin(), videosWithViews.end(), [&b](const auto &video)
                                                                                                               { return video.first == b.first; }))]
                                               .second;
                  }
                  return b.second > a.second; // Sort in descending order of score
              });

    // Print sorted recommendations to debug
    printSortedRecommendations(sortedRecommendations);

    // Initialize finalRecommendations with the top-scoring videos
    std::vector<std::string> finalRecommendations;
    for (size_t i = 0; i < sortedRecommendations.size() && i < maxRecommendations; ++i)
    {
        finalRecommendations.push_back(sortedRecommendations[sortedRecommendations.size() - i - 1].first);
    }

    // Fill remaining slots with random videos if needed
    if (finalRecommendations.size() < maxRecommendations)
    {
        std::vector<std::string> remainingVideos;
        for (const auto &video : videosWithViews)
        {
            if (std::find(finalRecommendations.begin(), finalRecommendations.end(), video.first) == finalRecommendations.end())
            {
                remainingVideos.push_back(video.first);
            }
        }

        std::shuffle(remainingVideos.begin(), remainingVideos.end(), std::mt19937{std::random_device{}()});
        while (finalRecommendations.size() < maxRecommendations && !remainingVideos.empty())
        {
            finalRecommendations.push_back(remainingVideos.back());
            remainingVideos.pop_back();
        }
    }

    // Print the final list of recommendations to debug
    printFinalRecommendations(finalRecommendations);

    std::ostringstream oss;
    for (size_t i = 0; i < finalRecommendations.size(); ++i)
    {
        if (i > 0)
            oss << ",";
        oss << finalRecommendations[i];
    }

    return oss.str();
}
void addVideo(const std::string &videoId)
{
    std::lock_guard<std::mutex> lock(dataMutex);
    std::string new_videoId = videoId.substr(10);
    videosWithViews.push_back({new_videoId, 0});

    for (const auto &video : videosWithViews)
    {
        if (video.first != new_videoId)
        {
            videoSimilarity[new_videoId][video.first] = 0;
            videoSimilarity[video.first][new_videoId] = 0;
        }
    }

    std::cout << "Added video with ID: " << videoId << std::endl;
}
std::string getTopPopularVideos(int maxVideos)
{
    std::lock_guard<std::mutex> lock(dataMutex);

    std::vector<std::pair<std::string, int>> sortedVideos = videosWithViews;
    std::sort(sortedVideos.begin(), sortedVideos.end(), [](const auto &a, const auto &b)
              { return b.second < a.second; });

    std::ostringstream oss;
    for (size_t i = 0; i < sortedVideos.size() && i < maxVideos; ++i)
    {
        if (i > 0)
            oss << ",";
        oss << sortedVideos[i].first;
    }

    return oss.str();
}
void handleClient(int clientSocket)
{
    char buffer[4096];
    while (true)
    {
        int readBytes = recv(clientSocket, buffer, sizeof(buffer) - 1, 0);
        if (readBytes <= 0)
        {
            break;
        }

        buffer[readBytes] = '\0';
        std::string message(buffer);
        std::cout << "Received: " << message << std::endl;

        std::istringstream iss(message);
        std::string command;
        iss >> command;
        std::cout << "COMMAND IS" << command << std::endl;

        if (command == "INIT_VIDEO_DATA")
        {
            init_videos(message);
            std::string response = "Initial video data received\n";
            send(clientSocket, response.c_str(), response.length(), 0);
        }
        else if (command == "INIT_USER_DATA")
        {
            init_users(message);
            std::string response = "Initial users data received\n";
            send(clientSocket, response.c_str(), response.length(), 0);
        }
        else if (command == "GET_RECOMMENDATIONS")
        {
            std::string userId, videoId;
            iss >> userId >> videoId;
            std::string recommendations;

            if (userId == "0")
            {
                recommendations = getTopPopularVideos(10);
            }
            else
            {
                updateUserViews(userId, videoId);

                recommendations = generateRecommendations(userId);
            }

            std::string response = recommendations;
            send(clientSocket, response.c_str(), response.length(), 0);
        }
        else if (command == "DELETE_VIDEO")
        {
            deleteVideo(message);
            std::string response = "Video deleted successfuly";
            send(clientSocket, response.c_str(), response.length(), 0);
        }
        else if (command == "ADD_VIDEO")
        {
            addVideo(message);
            std::string response = "Added video successfully";
            send(clientSocket, response.c_str(), response.length(), 0);
        }
        else
        {
            std::string response = "Unknown command\n";
            send(clientSocket, response.c_str(), response.length(), 0);
        }
    }

    std::cout << "Closing connection with client" << std::endl;
    close(clientSocket);
}
int main(int argc, char *argv[])
{
    const int serverPort = 5555;
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0)
    {
        std::cerr << "Error creating socket" << std::endl;
        return 1;
    }

    struct sockaddr_in sin;
    std::memset(&sin, 0, sizeof(sin));
    sin.sin_family = AF_INET;
    sin.sin_addr.s_addr = INADDR_ANY;
    sin.sin_port = htons(serverPort);
    int opt = 1;
    if (setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt)) < 0)
    {
        std::cerr << "Error setting SO_REUSEADDR option" << std::endl;
        close(sock);
        return 1;
    }
    if (bind(sock, (struct sockaddr *)&sin, sizeof(sin)) < 0)
    {
        std::cerr << "Error binding socket" << std::endl;
        close(sock);
        return 1;
    }

    if (listen(sock, 5) < 0)
    {
        std::cerr << "Error listening to a socket" << std::endl;
        close(sock);
        return 1;
    }

    std::cout << "Server is listening on port " << serverPort << std::endl;

    std::vector<std::thread> clientThreads;

    while (true)
    {
        struct sockaddr_in clientSin;
        unsigned int addrLen = sizeof(clientSin);
        int clientSock = accept(sock, (struct sockaddr *)&clientSin, &addrLen);
        if (clientSock < 0)
        {
            std::cerr << "Error accepting client" << std::endl;
            continue;
        }

        std::cout << "New client connected" << std::endl;
        clientThreads.emplace_back(handleClient, clientSock);
    }

    for (auto &thread : clientThreads)
    {
        thread.join();
    }

    close(sock);
    return 0;
}
