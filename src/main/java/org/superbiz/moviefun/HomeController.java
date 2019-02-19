package org.superbiz.moviefun;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    //@Qualifier("albumsTransactionManager")
    PlatformTransactionManager albumsTransactionManager;

    @Autowired
   //@Qualifier("albumsTransactionManager")
    PlatformTransactionManager moviesTransactionManager;

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;

    public HomeController(MoviesBean moviesBean, AlbumsBean albumsBean, MovieFixtures movieFixtures, AlbumFixtures albumFixtures) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {
        TransactionDefinition moviedef = new DefaultTransactionDefinition();
        TransactionStatus moviestatus = moviesTransactionManager.getTransaction(moviedef);
        try {
            for (Movie movie : movieFixtures.load()) {
                moviesBean.addMovie(movie);
            }
            moviesTransactionManager.commit(moviestatus);
        }
        catch (Exception e) {
            System.out.println("Movies Error in creating record, rolling back");
            moviesTransactionManager.rollback(moviestatus);
            throw e;
        }

        TransactionDefinition albumsdef = new DefaultTransactionDefinition();
        TransactionStatus albumstatus = albumsTransactionManager.getTransaction(albumsdef);
        try {
            for (Album album : albumFixtures.load()) {
                albumsBean.addAlbum(album);
            }
            albumsTransactionManager.commit(albumstatus);
        }
        catch (Exception e) {
            System.out.println("Albums Error in creating record, rolling back");
            albumsTransactionManager.rollback(albumstatus);
            throw e;
        }

        model.put("movies", moviesBean.getMovies());
        model.put("albums", albumsBean.getAlbums());

        return "setup";
    }
}
