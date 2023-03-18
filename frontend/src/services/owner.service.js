import axios from "axios";
import moment from "moment";
import { SERVICE_URL } from "../constants/default";
import { decrypt, encryptKey } from "../helpers/crypto.helper";

export const getOwnerSlots = async (subYardId, date) => {
  const credential = localStorage.getItem(encryptKey("credential"));
  const response = await axios.post(
    `${SERVICE_URL}/v1/owners/me/sub-yards/${subYardId}/slots`,
    { date: moment(date).format("DD/MM/yyyy") },
    {
      headers: {
        Authorization: `Bearer ${decrypt(credential).token}`,
      },
    }
  );

  return response ? response.data : null;
};

export const getSimpleYardDetails = async () => {
  const credential = localStorage.getItem(encryptKey("credential"));
  const response = await axios.get(`${SERVICE_URL}/v1/owners/me/all-yards`, {
    headers: {
      Authorization: `Bearer ${decrypt(credential).token}`,
    },
  });

  return response ? response.data : null;
};

export const getSlotDetails = async (subYardId, slotId, date) => {
  const credential = localStorage.getItem(encryptKey("credential"));
  const response = await axios.post(
    `${SERVICE_URL}/v1/owners/me/sub-yards/${subYardId}/slots/${slotId}`,
    { date: moment(date).format("DD/MM/yyyy") },
    {
      headers: {
        Authorization: `Bearer ${decrypt(credential).token}`,
      },
    }
  );

  return response ? response.data : null;
};
