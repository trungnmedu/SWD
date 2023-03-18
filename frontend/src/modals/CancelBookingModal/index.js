import { useFormik } from "formik";
import { toast, ToastContainer } from "react-toastify";
import * as yup from "yup";
import { EMPTY, TOAST_CONFIG } from "../../constants/default";
import { INTERNAL_SERVER_ERROR } from "../../constants/error-message";
import { cancelBooking } from "../../services/booking.service";

const validation = yup.object({
  reason: yup
    .string("Enter reason to cancel booking")
    .required("The reason is required"),
});

const CancelBookingModal = ({ toggleModal, bookingId, onSave }) => {
  const formik = useFormik({
    initialValues: {
      reason: EMPTY,
    },
    validateOnMount: true,
    validationSchema: validation,
    onSubmit: async (values, { resetForm }) => cancel(values, resetForm),
  });

  const cancel = (values, resetForm) => {
    cancelBooking(bookingId, values.reason)
      .then((res) => {
        toast.success("Cancel booking successfully", TOAST_CONFIG);
        toggleModal();
        onSave();
      })
      .catch((error) => {
        toast.error(
          error.response.data.message || INTERNAL_SERVER_ERROR,
          TOAST_CONFIG
        );
      });
  };

  return (
    <div className="custom-confirm" style={{ width: 550 }}>
      <h4>Cancel Booking</h4>
      <p className="mb-3">Give us your reason why you cancel this booking</p>
      <textarea
        id="cancel-reason"
        name="reason"
        className="w-100 mb-3"
        style={{ height: "100px", borderRadius: "5px", resize: "none" }}
        value={formik.values.reason}
        onChange={formik.handleChange}
        onBlur={formik.handleBlur}
      />
      <button
        className="btn btn-primary me-3"
        disabled={
          formik.isSubmitting ||
          !formik.isValid ||
          formik.values.reason === EMPTY
        }
        onClick={formik.handleSubmit}
      >
        Confirm
      </button>
      <button onClick={toggleModal} className="btn btn-light">
        Cancel
      </button>
      <ToastContainer />
    </div>
  );
};

export default CancelBookingModal;
