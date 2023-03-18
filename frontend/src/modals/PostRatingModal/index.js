import { useState } from 'react';
import { Rating } from 'react-simple-star-rating';
import { toast, ToastContainer } from "react-toastify";
import { EMPTY, TOAST_CONFIG } from '../../constants/default';
import { postVote } from '../../services/me.service';

const PostRatingModal = ({ bookingId, toogleShowModal, handleSubmitPostSuccess }) => {

    const [score, setScore] = useState(0);
    const [comment, setComment] = useState(EMPTY);
    const [isSubmiting, setIsSubmiting] = useState(false)

    const handleOnChangeScoreRating = (rate) => {
        setScore(rate);
    };

    const handleOnChangeComment = (e) => {
        setComment(e.target.value)
    }

    const handlePostRating = async () => {
        try {
            setIsSubmiting(true);
            const data = JSON.stringify({
                bookingId,
                score,
                comment: comment.trim().length > 0 ? comment.trim() : null
            });

            await postVote(data);
            toast.success("Rating yard success!", TOAST_CONFIG);
            toogleShowModal();
            handleSubmitPostSuccess();
        } catch (error) {
            toast.error("Rating yard failed. Try again!", TOAST_CONFIG);
        } finally {
            setIsSubmiting(false)
        }
    }

    return (
        <>
            <div className="custom-confirm">
                <h4 className="w-min-content d-inline-block me-5">Rating</h4>
                <Rating
                    onClick={handleOnChangeScoreRating}
                    ratingValue={score}
                    allowHalfIcon={true}
                />
                <p className="my-3">How do you feel about this yard?</p>
                <textarea
                    className="w-100 mb-3"
                    name='comment'
                    value={comment}
                    onChange={handleOnChangeComment}
                    style={{ height: "100px", borderRadius: "5px", resize: "none" }}
                />
                <button
                    className="btn btn-primary me-3"
                    onClick={handlePostRating}
                    disabled={isSubmiting}
                >
                    Confirm
                </button>
                <button className="btn btn-light" onClick={toogleShowModal}>
                    Cancel
                </button>
            </div>
            <ToastContainer />
        </>

    )
}

export default PostRatingModal
