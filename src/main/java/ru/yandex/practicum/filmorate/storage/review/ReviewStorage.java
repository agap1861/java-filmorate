package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Optional<Review> getReviewById(long reviewId);

    void removeReview(long reviewId);

    Review addReview(Review review);

    boolean existsById(long reviewId);

    Review updateReview(Review review);

    void addIsLike(long reviewId, long userId, boolean isLike);

    void deleteRating(long reviewId, long userId);

    List<Review> getReviews(Long filmId, Integer count);
}
