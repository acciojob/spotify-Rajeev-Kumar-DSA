package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name, mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        artistAlbumMap.put(artist, new ArrayList<>());
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist artist = artists.stream().filter(a -> a.getName().equals(artistName)).findFirst().orElseGet(() -> createArtist(artistName));
        Album album = new Album(title);
        albums.add(album);
        artistAlbumMap.get(artist).add(album);
        albumSongMap.put(album, new ArrayList<>());
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Album album = albums.stream().filter(a -> a.getTitle().equals(albumName)).findFirst().orElseThrow(() -> new Exception("Album does not exist"));
        Song song = new Song(title, length);
        songs.add(song);
        albumSongMap.get(album).add(song);
        songLikeMap.put(song, new ArrayList<>());
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user = users.stream().filter(u -> u.getMobile().equals(mobile)).findFirst().orElseThrow(() -> new Exception("User does not exist"));
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        creatorPlaylistMap.put(user, playlist);
        playlistListenerMap.put(playlist, new ArrayList<>(Collections.singletonList(user)));
        List<Song> songsOfLength = new ArrayList<>();
        for (Song song : songs) {
            if (song.getLength() == length) {
                songsOfLength.add(song);
            }
        }
        playlistSongMap.put(playlist, songsOfLength);
        userPlaylistMap.putIfAbsent(user, new ArrayList<>());
        userPlaylistMap.get(user).add(playlist);
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user = users.stream().filter(u -> u.getMobile().equals(mobile)).findFirst().orElseThrow(() -> new Exception("User does not exist"));
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        creatorPlaylistMap.put(user, playlist);
        playlistListenerMap.put(playlist, new ArrayList<>(Collections.singletonList(user)));
        List<Song> songsByName = new ArrayList<>();
        for (String songTitle : songTitles) {
            Song song = songs.stream().filter(s -> s.getTitle().equals(songTitle)).findFirst().orElse(null);
            if (song != null) {
                songsByName.add(song);
            }
        }
        playlistSongMap.put(playlist, songsByName);
        userPlaylistMap.putIfAbsent(user, new ArrayList<>());
        userPlaylistMap.get(user).add(playlist);
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user = users.stream().filter(u -> u.getMobile().equals(mobile)).findFirst().orElseThrow(() -> new Exception("User does not exist"));
        Playlist playlist = playlists.stream().filter(p -> p.getTitle().equals(playlistTitle)).findFirst().orElseThrow(() -> new Exception("Playlist does not exist"));
        List<User> listeners = playlistListenerMap.get(playlist);
        if (!listeners.contains(user) && !creatorPlaylistMap.get(user).equals(playlist)) {
            listeners.add(user);
        }
        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = users.stream().filter(u -> u.getMobile().equals(mobile)).findFirst().orElseThrow(() -> new Exception("User does not exist"));
        Song song = songs.stream().filter(s -> s.getTitle().equals(songTitle)).findFirst().orElseThrow(() -> new Exception("Song does not exist"));
        List<User> likers = songLikeMap.get(song);
        if (!likers.contains(user)) {
            likers.add(user);
            song.setLikes(song.getLikes() + 1);
            Album album = albumSongMap.keySet().stream().filter(a -> albumSongMap.get(a).contains(song)).findFirst().orElse(null);
            if (album != null) {
                Artist artist = artistAlbumMap.keySet().stream().filter(a -> artistAlbumMap.get(a).contains(album)).findFirst().orElse(null);
                if (artist != null) {
                    artist.setLikes(artist.getLikes() + 1);
                }
            }
        }
        return song;
    }

    public String mostPopularArtist() {
        return artists.stream().max(Comparator.comparingInt(Artist::getLikes)).map(Artist::getName).orElse(null);
    }

    public String mostPopularSong() {
        return songs.stream().max(Comparator.comparingInt(Song::getLikes)).map(Song::getTitle).orElse(null);
    }
}
