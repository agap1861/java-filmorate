package ru.yandex.practicum.filmorate.service.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public ReviewService(@Qualifier("reviewDbStorage") ReviewStorage reviewStorage,
                         @Qualifier("userDbStorage") UserStorage userStorage,
                         @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Review getReviewById(long id) {
        return reviewStorage.getReviewById(id).orElseThrow(() -> new NotFoundException("Отзыв с ID " + id + " не найден"));
    }

    public List<Review> getReviews(Long filmId, Integer count) {
        return reviewStorage.getReviews(filmId, count);
    }

    public Review createReview(Review review) {
        isExistUser(review.getUserId());
        isExistFilm(review.getFilmId());
        reviewStorage.addReview(review);
        return review;
    }

    public void removeReview(long reviewId) {
        reviewStorage.getReviewById(reviewId)
                .orElseThrow(() -> new NotFoundException("Not found"));
        reviewStorage.removeReview(reviewId);
    }

    public Review updateReview(Review newReview) {
        if (newReview.getReviewId() < 1) {
            throw new ValidationException("Должен быть указан корректный id");
        }
        isExistReview(newReview.getReviewId());
        if (newReview.getUserId() != null) {
            isExistUser(newReview.getUserId());
        }
        if (newReview.getFilmId() != null) {
            isExistFilm(newReview.getFilmId());
        }
        return reviewStorage.updateReview(newReview);
    }

    public void addLike(long reviewId, long userId) {
        isExistReview(reviewId);
        isExistUser(userId);
        reviewStorage.addIsLike(reviewId, userId, true);
    }

    public void addDislike(long reviewId, long userId) {
        isExistReview(reviewId);
        isExistUser(userId);
        reviewStorage.addIsLike(reviewId, userId, false);
    }

    public void deleteRating(long reviewId, long userId) {
        isExistReview(reviewId);
        isExistUser(userId);
        reviewStorage.deleteRating(reviewId, userId);
    }

    private void isExistUser(long userId) {
        if (!userStorage.exists(userId)) {
            throw new NotFoundException("Пользователя с id =" + userId + " не найдено");
        }
    }

    private void isExistFilm(long filmId) {
        if (!filmStorage.isExistFilmById(filmId)) {
            throw new NotFoundException("Фильма с id =" + filmId + " не найдено");
        }
    }

    private void isExistReview(long reviewId) {
        if (!reviewStorage.existsById(reviewId)) {
            throw new NotFoundException("Отзыва с id =" + reviewId + " не найдено");
        }
    }
}
