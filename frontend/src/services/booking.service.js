import axios from "axios";
import { EMPTY, SERVICE_URL } from "../constants/default";
import { OWNER } from "../constants/roles";
import { decrypt, encryptKey } from "../helpers/crypto.helper";

export const cancelBooking = async (bookingId, reason) => {
  const credential = localStorage.getItem(encryptKey("credential"));
  const role = decrypt(credential).role;
  let url = EMPTY;
  if(role === OWNER){
    url = `${SERVICE_URL}/v1/owners/me/bookings/${bookingId}`
  } else {
    url = `${SERVICE_URL}/v1/me/bookings/${bookingId}`
  }
  const response = await axios.delete(
    url,
    {
      headers: {
        Authorization: `Bearer ${decrypt(credential).token}`,
      },
      data: { reason },
    }
  );

  return response ? response.data : null;
};
