import { memo, useEffect, useState } from "react";
import { Rating } from "react-simple-star-rating";

import reviewer from "../../assets/images/user-1.png";
import { getAllRatingOfYard } from "../../services/yard.service";
import Pagination from "../Pagination";
import "./style.scss";

function Reviews({ yardId }) {
  const [ratings, setRatings] = useState([]);
  const ITEMS_PER_PAGE = 10;
  const [maxPage, setMaxPage] = useState(1);
  const [loadingRatings, setLoadingRatings] = useState(false);


  const getAllRating = async (yardId, payload) => {
    try {
      setLoadingRatings(true);
      const { votes, maxResult } = await getAllRatingOfYard(yardId, payload);
      setRatings(votes);
      setMaxPage(maxResult % ITEMS_PER_PAGE === 0 && maxResult !== 0
        ? maxResult / ITEMS_PER_PAGE
        : Math.floor(maxResult / ITEMS_PER_PAGE) + 1);

    } finally {
      setLoadingRatings(false)
    }
  }

  useEffect(() => {
    const payload = { itemsPerPage: ITEMS_PER_PAGE, page: 1 };
    getAllRating(yardId, payload)
  }, [yardId]);

  const onChangePage = (page) => {
    const payload = { page: page, itemsPerPage: ITEMS_PER_PAGE }
    getAllRating(yardId, payload)
  }

  return (
    <div className="mt-5">
      {!!ratings.length && <h2 className="mb-3">Reviews</h2>}
      {
        loadingRatings && (
          <div className="w-100 d-flex justify-content-center pt-5 h-300 align-items-center">
            <div className="spinner-border" role="status">
              <span className="sr-only">Loading...</span>
            </div>
          </div>
        )
      }
      {
        !loadingRatings && !!ratings.length && (
          <div className="reviews-container">
            {
              ratings?.map(rating => (
                <div key={rating.voteId} className="review">
                  <div className="reviewer-img">
                    <img alt="avatar" src={rating.accountAvatar || reviewer} />
                  </div>
                  <div className="review-content">
                    <p>
                      {rating.accountFullName}
                      <Rating
                        ratingValue={rating.score}
                        allowHalfIcon={true}
                        readonly={true}
                        style={{ fontSize: "0.5rem" }}
                      />
                    </p>
                    <p className="review-time">{rating.postedAt}</p>
                    <p>{rating.comment}</p>
                  </div>
                </div>
              ))
            }
            <Pagination maxPage={maxPage} onChangePage={onChangePage} />
          </div>
        )
      }
    </div>
  );
}

export default memo(Reviews);
