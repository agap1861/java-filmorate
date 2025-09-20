package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewDbStorage extends BaseDbStorage<Review> implements ReviewStorage {

    private static final String INSERT_QUERY = "INSERT INTO reviews(content, is_positive, user_id, film_id)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE reviews SET content = ?, is_positive = ?, user_id = ?, film_id = ?  WHERE id = ?";

    private static final String FIND_BY_ID_QUERY = "SELECT " +
            "r.id AS review_id, " +
            "r.content, " +
            "r.is_positive, " +
            "r.user_id, " +
            "r.film_id, " +
            "(SELECT COUNT(CASE WHEN is_like = true THEN 1 END) - " +
            "COUNT(CASE WHEN is_like = false THEN 1 END) " +
            "FROM review_ratings rr " +
            "WHERE rr.review_id = r.id) AS useful " +
            "FROM reviews r " +
            "WHERE r.id = ? ";

    private static final String GET_REVIEWS_BY_FILM_QUERY = "SELECT " +
            "r.id AS review_id, " +
            "r.content, " +
            "r.is_positive, " +
            "r.user_id, " +
            "r.film_id, " +
            "COUNT(CASE WHEN rr.is_like = true THEN 1 END) - " +
            "COUNT(CASE WHEN rr.is_like = false THEN 1 END) AS useful " +
            "FROM reviews r " +
            "LEFT JOIN review_ratings rr ON r.id = rr.review_id " +
            "WHERE r.film_id = ? " +
            "GROUP BY r.id, r.content, r.is_positive, r.user_id, r.film_id " +
            "ORDER BY useful DESC " +
            "LIMIT ?";

    private static final String GET_ALL_REVIEWS_QUERY = "SELECT " +
            "r.id AS review_id, " +
            "r.content, " +
            "r.is_positive, " +
            "r.user_id, " +
            "r.film_id, " +
            "COUNT(CASE WHEN rr.is_like = true THEN 1 END) - " +
            "COUNT(CASE WHEN rr.is_like = false THEN 1 END) AS useful " +
            "FROM reviews r " +
            "LEFT JOIN review_ratings rr ON r.id = rr.review_id " +
            "GROUP BY r.id, r.content, r.is_positive, r.user_id, r.film_id " +
            "ORDER BY useful DESC " +
            "LIMIT ?";

    private static final String REMOVE_REVIEW = "DELETE FROM reviews WHERE id = ?";
    private static final String CHECK_EXISTS_QUERY = "SELECT EXISTS(SELECT 1 FROM reviews WHERE id = ?)";
    private static final String INSERT_LIKE_QUERY =
            "MERGE INTO review_ratings(review_id, user_id, is_like) KEY (review_id, user_id) VALUES (?, ?, ?)";
    private static final String DELETE_RATING_QUERY = "DELETE FROM review_ratings WHERE review_id = ? AND user_id = ?";


    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<Review> getReviewById(long reviewId) {
        return getOneById(FIND_BY_ID_QUERY, reviewId);
    }

    @Override
    public List<Review> getReviews(Long filmId, Integer count) {
        if (filmId == null) {
            return getAll(GET_ALL_REVIEWS_QUERY, count);
        } else return getAll(GET_REVIEWS_BY_FILM_QUERY, filmId, count);
    }

    @Override
    public void removeReview(long reviewId) {
        delete(REMOVE_REVIEW, reviewId);
    }

    @Override
    public Review addReview(Review review) {
        long id = post(INSERT_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId());
        review.setReviewId(id);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        reviewUpdate(UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getReviewId());
        return review;
    }

    @Override
    public void addIsLike(long reviewId, long userId, boolean isLike) {
        reviewUpdate(INSERT_LIKE_QUERY, reviewId, userId, isLike);
    }

    @Override
    public void deleteRating(long reviewId, long userId) {
        delete(DELETE_RATING_QUERY, reviewId, userId);
    }

    @Override
    public boolean existsById(long reviewId) {
        return jdbc.queryForObject(CHECK_EXISTS_QUERY, Boolean.class, reviewId);
    }

}
