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
        User user = new User();
        user.setName(name);
        user.setMobile(mobile);
        users.add(user);

        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist();
        artist.setName(name);
        artist.setLikes(0);

        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist artist1 = null;

        for(Artist artist:artists){
            if(artist.getName()==artistName){
                artist1=artist;
                break;
            }
        }
        if(artist1==null){
            artist1 = createArtist(artistName);

            Album album = new Album();

            album.setTitle(title);
            album.setReleaseDate(new Date());

            albums.add(album);

            List<Album> l = new ArrayList<>();
            l.add(album);
            artistAlbumMap.put(artist1,l);

            return album;
        }else {
            Album album = new Album();

            album.setTitle(title);
            album.setReleaseDate(new Date());

            albums.add(album);

            List<Album> l = artistAlbumMap.get(artist1);
            if(l == null){
                l = new ArrayList<>();
            }
            l.add(album);
            artistAlbumMap.put(artist1,l);

            return album;
        }
    }

    public Song createSong(String title, String albumName, int length) throws Exception{

        Album album = null;
        for(Album album1:albums){
            if(album1.getTitle()==albumName){
                album=album1;
                break;
            }
        }
        if(album==null)
            throw new Exception("Album does not exist");
        else {
            Song song = new Song();
            song.setTitle(title);
            song.setLength(length);
            song.setLikes(0);

            songs.add(song);

//            List<Song> l = albumSongMap.get(album);
//            l.add(song);
//            albumSongMap.put(album,l);

            if(albumSongMap.containsKey(album)){
                List<Song> l = albumSongMap.get(album);
                l.add(song);
                albumSongMap.put(album,l);
            }else{
                List<Song> songList = new ArrayList<>();
                songList.add(song);
                albumSongMap.put(album,songList);
            }

            return song;
        }
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {

        //Create a playlist with given title and add all songs having the given length in the database to that playlist
        //The creater of the playlist will be the given user and will also be the only listener at the time of playlist creation
        //If the user does not exist, throw "User does not exist" exception

        User user = null;
        for(User user1:users){
            if(user1.getMobile()==mobile){
                user=user1;
                break;
            }
        }
        if(user==null)
            throw new Exception("User does not exist");
        else {
            Playlist playlist = new Playlist();
            playlist.setTitle(title);
            playlists.add(playlist);

            List<Song> l = new ArrayList<>();
            for(Song song:songs){
                if(song.getLength()==length){
                    l.add(song);
                }
            }
            playlistSongMap.put(playlist,l);

            List<User> list = new ArrayList<>();
            list.add(user);
            playlistListenerMap.put(playlist,list);

            creatorPlaylistMap.put(user,playlist);

//            List<Playlist> userPlayList = userPlaylistMap.get(user);  //error possibility
//            userPlayList.add(playlist);
//            userPlaylistMap.put(user,userPlayList);

            if(userPlaylistMap.containsKey(user)){
                List<Playlist> userPlayList = userPlaylistMap.get(user);
                userPlayList.add(playlist);
                userPlaylistMap.put(user,userPlayList);
            }else{
                List<Playlist> plays = new ArrayList<>();
                plays.add(playlist);
                userPlaylistMap.put(user,plays);
            }

            return playlist;
        }
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {

        //Create a playlist with given title and add all songs having the given titles in the database to that playlist
        //The creater of the playlist will be the given user and will also be the only listener at the time of playlist creation
        //If the user does not exist, throw "User does not exist" exception

        User user = null;
        for(User user1:users){
            if(user1.getMobile()==mobile){
                user=user1;
                break;
            }
        }
        if(user==null)
            throw new Exception("User does not exist");
        else {
            Playlist playlist = new Playlist();
            playlist.setTitle(title);
            playlists.add(playlist);

            List<Song> l = new ArrayList<>();
            for(Song song:songs){
                if(songTitles.contains(song.getTitle())){
                    l.add(song);
                }
            }
            playlistSongMap.put(playlist,l);

            List<User> list = new ArrayList<>();
            list.add(user);
            playlistListenerMap.put(playlist,list);

            creatorPlaylistMap.put(user,playlist);

            if(userPlaylistMap.containsKey(user)){
                List<Playlist> userPlayList = userPlaylistMap.get(user);
                userPlayList.add(playlist);
                userPlaylistMap.put(user,userPlayList);
            }else{
                List<Playlist> plays = new ArrayList<>();
                plays.add(playlist);
                userPlaylistMap.put(user,plays);
            }

            return playlist;
        }
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {

        //Find the playlist with given title and add user as listener of that playlist and update user accordingly
        //If the user is creater or already a listener, do nothing
        //If the user does not exist, throw "User does not exist" exception
        //If the playlist does not exists, throw "Playlist does not exist" exception
        // Return the playlist after updating

        User user = null;
        for(User user1:users){
            if(user1.getMobile()==mobile){
                user=user1;
                break;
            }
        }
        if(user==null)
            throw new Exception("User does not exist");

        Playlist playlist = null;
        for(Playlist playlist1:playlists){
            if(playlist1.getTitle()==playlistTitle){
                playlist=playlist1;
                break;
            }
        }
        if(playlist==null)
            throw new Exception("Playlist does not exist");

        if(creatorPlaylistMap.containsKey(user))
            return playlist;

        List<User> listener = playlistListenerMap.get(playlist);
        for(User user1:listener){
            if(user1==user)
                return playlist;
        }

        listener.add(user);
        playlistListenerMap.put(playlist,listener);

        List<Playlist> playlists1 = userPlaylistMap.get(user);
        if(playlists1 == null){
            playlists1 = new ArrayList<>();
        }
        playlists1.add(playlist);
        userPlaylistMap.put(user,playlists1);

        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {

        //The user likes the given song. The corresponding artist of the song gets auto-liked
        //A song can be liked by a user only once. If a user tried to like a song multiple times, do nothing
        //However, an artist can indirectly have multiple likes from a user, if the user has liked multiple songs of that artist.
        //If the user does not exist, throw "User does not exist" exception
        //If the song does not exist, throw "Song does not exist" exception
        //Return the song after updating

        User user = null;
        for(User user1:users){
            if(user1.getMobile()==mobile){
                user=user1;
                break;
            }
        }
        if(user==null)
            throw new Exception("User does not exist");

        Song song = null;
        for(Song song1:songs){
            if(song1.getTitle()==songTitle){
                song=song1;
                break;
            }
        }
        if (song==null)
            throw new Exception("Song does not exist");

        if(songLikeMap.containsKey(song)){
            List<User> list = songLikeMap.get(song);
            if(list.contains(user)){
                return song;
            }else {
                int likes = song.getLikes() + 1;
                song.setLikes(likes);
                list.add(user);
                songLikeMap.put(song,list);

                Album album=null;
                for(Album album1:albumSongMap.keySet()){
                    List<Song> songList = albumSongMap.get(album1);
                    if(songList.contains(song)){
                        album = album1;
                        break;
                    }
                }
                Artist artist = null;
                for(Artist artist1:artistAlbumMap.keySet()){
                    List<Album> albumList = artistAlbumMap.get(artist1);
                    if (albumList.contains(album)){
                        artist = artist1;
                        break;
                    }
                }
                int likes1 = artist.getLikes() +1;
                artist.setLikes(likes1);
                artists.add(artist);
                return song;
            }
        }else {
            int likes = song.getLikes() + 1;
            song.setLikes(likes);
            List<User> list = new ArrayList<>();
            list.add(user);
            songLikeMap.put(song,list);

            Album album=null;
            for(Album album1:albumSongMap.keySet()){
                List<Song> songList = albumSongMap.get(album1);
                if(songList.contains(song)){
                    album = album1;
                    break;
                }
            }
            Artist artist = null;
            for(Artist artist1:artistAlbumMap.keySet()){
                List<Album> albumList = artistAlbumMap.get(artist1);
                if (albumList.contains(album)){
                    artist = artist1;
                    break;
                }
            }
            int likes1 = artist.getLikes() +1;
            artist.setLikes(likes1);
            artists.add(artist);

            return song;
        }
    }

    public String mostPopularArtist() {
        //
        int max = 0;
        Artist artist1=null;

        for(Artist artist:artists){
            if(artist.getLikes()>=max){
                artist1=artist;
                max = artist.getLikes();
            }
        }
        if(artist1==null)
            return null;
        else
            return artist1.getName();
    }

    public String mostPopularSong() {
        int max=0;
        Song song = null;

        for(Song song1:songLikeMap.keySet()){
            if(song1.getLikes()>=max){
                song=song1;
                max = song1.getLikes();
            }
        }
        if(song==null)
            return null;
        else
            return song.getTitle();
    }
}
//    public HashMap<Artist, List<Album>> artistAlbumMap;
//    public HashMap<Album, List<Song>> albumSongMap;
//    public HashMap<Playlist, List<Song>> playlistSongMap;
//    public HashMap<Playlist, List<User>> playlistListenerMap;
//    public HashMap<User, Playlist> creatorPlaylistMap;
//    public HashMap<User, List<Playlist>> userPlaylistMap;
//    public HashMap<Song, List<User>> songLikeMap;
//
//    public List<User> users;
//    public List<Song> songs;
//    public List<Playlist> playlists;
//    public List<Album> albums;
//    public List<Artist> artists;
//
//    public SpotifyRepository(){
//        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
//        artistAlbumMap = new HashMap<>();
//        albumSongMap = new HashMap<>();
//        playlistSongMap = new HashMap<>();
//        playlistListenerMap = new HashMap<>();
//        creatorPlaylistMap = new HashMap<>();
//        userPlaylistMap = new HashMap<>();
//        songLikeMap = new HashMap<>();
//
//        users = new ArrayList<>();
//        songs = new ArrayList<>();
//        playlists = new ArrayList<>();
//        albums = new ArrayList<>();
//        artists = new ArrayList<>();
//    }
//
//    public User createUser(String name, String mobile) {
//        User user = new User(name, mobile);
//        users.add(user);
//        return user;
//    }
//
//    public Artist createArtist(String name) {
//        Artist artist = new Artist(name);
//        artists.add(artist);
//        artistAlbumMap.put(artist, new ArrayList<>());
//        return artist;
//    }
//
//    public Album createAlbum(String title, String artistName) {
//        Artist artist = artists.stream().filter(a -> a.getName().equals(artistName)).findFirst().orElseGet(() -> createArtist(artistName));
//        Album album = new Album(title);
//        albums.add(album);
//        artistAlbumMap.get(artist).add(album);
//        albumSongMap.put(album, new ArrayList<>());
//        return album;
//    }
//
//    public Song createSong(String title, String albumName, int length) throws Exception{
//        Album album = albums.stream().filter(a -> a.getTitle().equals(albumName)).findFirst().orElseThrow(() -> new Exception("Album does not exist"));
//        Song song = new Song(title, length);
//        songs.add(song);
//        albumSongMap.get(album).add(song);
//        songLikeMap.put(song, new ArrayList<>());
//        return song;
//    }
//
//    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
//        User user = users.stream().filter(u -> u.getMobile().equals(mobile)).findFirst().orElseThrow(() -> new Exception("User does not exist"));
//        Playlist playlist = new Playlist(title);
//        playlists.add(playlist);
//        creatorPlaylistMap.put(user, playlist);
//        playlistListenerMap.put(playlist, new ArrayList<>(Collections.singletonList(user)));
//        List<Song> songsOfLength = new ArrayList<>();
//        for (Song song : songs) {
//            if (song.getLength() == length) {
//                songsOfLength.add(song);
//            }
//        }
//        playlistSongMap.put(playlist, songsOfLength);
//        userPlaylistMap.putIfAbsent(user, new ArrayList<>());
//        userPlaylistMap.get(user).add(playlist);
//        return playlist;
//    }
//
//    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
//        User user = users.stream().filter(u -> u.getMobile().equals(mobile)).findFirst().orElseThrow(() -> new Exception("User does not exist"));
//        Playlist playlist = new Playlist(title);
//        playlists.add(playlist);
//        creatorPlaylistMap.put(user, playlist);
//        playlistListenerMap.put(playlist, new ArrayList<>(Collections.singletonList(user)));
//        List<Song> songsByName = new ArrayList<>();
//        for (String songTitle : songTitles) {
//            Song song = songs.stream().filter(s -> s.getTitle().equals(songTitle)).findFirst().orElse(null);
//            if (song != null) {
//                songsByName.add(song);
//            }
//        }
//        playlistSongMap.put(playlist, songsByName);
//        userPlaylistMap.putIfAbsent(user, new ArrayList<>());
//        userPlaylistMap.get(user).add(playlist);
//        return playlist;
//    }
//
//    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
//        User user = users.stream().filter(u -> u.getMobile().equals(mobile)).findFirst().orElseThrow(() -> new Exception("User does not exist"));
//        Playlist playlist = playlists.stream().filter(p -> p.getTitle().equals(playlistTitle)).findFirst().orElseThrow(() -> new Exception("Playlist does not exist"));
//        List<User> listeners = playlistListenerMap.get(playlist);
//        if (!listeners.contains(user) && !creatorPlaylistMap.get(user).equals(playlist)) {
//            listeners.add(user);
//        }
//        return playlist;
//    }
//
//    public Song likeSong(String mobile, String songTitle) throws Exception {
//        User user = users.stream().filter(u -> u.getMobile().equals(mobile)).findFirst().orElseThrow(() -> new Exception("User does not exist"));
//        Song song = songs.stream().filter(s -> s.getTitle().equals(songTitle)).findFirst().orElseThrow(() -> new Exception("Song does not exist"));
//        List<User> likers = songLikeMap.get(song);
//        if (!likers.contains(user)) {
//            likers.add(user);
//            song.setLikes(song.getLikes() + 1);
//            Album album = albumSongMap.keySet().stream().filter(a -> albumSongMap.get(a).contains(song)).findFirst().orElse(null);
//            if (album != null) {
//                Artist artist = artistAlbumMap.keySet().stream().filter(a -> artistAlbumMap.get(a).contains(album)).findFirst().orElse(null);
//                if (artist != null) {
//                    artist.setLikes(artist.getLikes() + 1);
//                }
//            }
//        }
//        return song;
//    }
//
//    public String mostPopularArtist() {
//        return artists.stream().max(Comparator.comparingInt(Artist::getLikes)).map(Artist::getName).orElse(null);
//    }
//
//    public String mostPopularSong() {
//        return songs.stream().max(Comparator.comparingInt(Song::getLikes)).map(Song::getTitle).orElse(null);
//    }
//}
