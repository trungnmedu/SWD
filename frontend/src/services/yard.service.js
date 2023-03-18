import axios from "axios";
import * as moment from "moment";
import { SERVICE_URL } from "../constants/default";
import { decrypt, encryptKey } from "../helpers/crypto.helper";

export const searchYard = async (params) => {
  const response = await axios.post(`${SERVICE_URL}/v1/yards/search`, params, {
    headers: {
      "Content-Type": "application/json",
    },
  });
  return response ? response.data : null;
};

export const getYardById = async (id) => {
  const response = await axios.get(`${SERVICE_URL}/v1/yards/${id}`);
  return response ? response.data : null;
};

export const getSlots = async (subYardId, date) => {
  const response = await axios.post(
    `${SERVICE_URL}/v1/sub-yards/${subYardId}/slots`,
    { date: moment(date).format("DD/MM/yyyy") },
    {
      headers: {
        "Content-Type": "application/json",
      },
    }
  );

  return response ? response.data : null;
};

export const bookingYard = async (yardId, payload, token) => {
  const response = await axios.post(
    `${SERVICE_URL}/v1/yards/${yardId}/booking`,
    payload,
    {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    }
  );

  return response ? response.data : null;
};

export const addNewYard = async (payload) => {
  const form = new FormData();
  for (const image of payload.images) {
    form.append("images", image);
  }
  form.append("yard", JSON.stringify(payload.yard));

  const credential = localStorage.getItem(encryptKey("credential"));
  const response = await axios.post(`${SERVICE_URL}/v1/owners/me/yards`, form, {
    headers: {
      "Content-Type": "multipart/form-data",
      Authorization: `Bearer ${decrypt(credential).token}`,
    },
  });

  return response ? response.data : null;
};

export const searchOwnerYard = async (payload) => {
  const credential = localStorage.getItem(encryptKey("credential"));
  const response = await axios.post(
    `${SERVICE_URL}/v1/owners/me/yards/search`,
    payload,
    {
      headers: {
        Authorization: `Bearer ${decrypt(credential).token}`,
      },
    }
  );

  return response ? response.data : null;
};

export const getAllRatingOfYard = async (yardId, payload) => {
  const response = await axios.post(`${SERVICE_URL}/v1/vote/yards/${yardId}`, payload)
  return response ?  response.data : [];
}
export const getYardDetailById = async (yardId) => {
  const credential = localStorage.getItem(encryptKey("credential"));
  const response = await axios.get(
    `${SERVICE_URL}/v1/owners/me/yards/search/${yardId}`,
    {
      headers: {
        Authorization: `Bearer ${decrypt(credential).token}`,
      },
    }
  );

  return response ? response.data : null;
};

export const deactivateYard = async (yardId) => {
  const credential = localStorage.getItem(encryptKey("credential"));
  const response = await axios.put(
    `${SERVICE_URL}/v1/owners/me/yards/${yardId}/deactivate`,
    null,
    { headers: { Authorization: `Bearer ${decrypt(credential).token}` } }
  );

  return response ? response.data : null;
};

export const activateYard = async (yardId) => {
  const credential = localStorage.getItem(encryptKey("credential"));
  const response = await axios.put(
    `${SERVICE_URL}/v1/owners/me/yards/${yardId}/activate`,
    null,
    { headers: { Authorization: `Bearer ${decrypt(credential).token}` } }
  );

  return response ? response.data : null;
};

export const deleteYard = async (yardId) => {
  const credential = localStorage.getItem(encryptKey("credential"));
  const response = await axios.delete(
    `${SERVICE_URL}/v1/owners/me/yards/${yardId}`,
    { headers: { Authorization: `Bearer ${decrypt(credential).token}` } }
  );

  return response ? response.data : null;
};

export const updateYard = async (payload, yardId) => {
  const credential = localStorage.getItem(encryptKey("credential"));
  const form = new FormData();
  for (const image of payload.newImages) {
    form.append("newImages", image);
  }
  form.append("yard", JSON.stringify(payload.yard));
  form.append("images", JSON.stringify(payload.images));

  const response = await axios.put(
    `${SERVICE_URL}/v1/owners/me/yards/${yardId}`,
    form,
    {
      headers: {
        Authorization: `Bearer ${decrypt(credential).token}`,
      },
    }
  );

  return response ? response.data : null;
};

export const deactivateSubYard = async (yardId) => {
  const credential = localStorage.getItem(encryptKey("credential"));
  const response = await axios.put(
    `${SERVICE_URL}/v1/owners/me/sub-yards/${yardId}/deactivate`,
    null,
    { headers: { Authorization: `Bearer ${decrypt(credential).token}` } }
  );

  return response ? response.data : null;
};

export const activateSubYard = async (yardId) => {
  const credential = localStorage.getItem(encryptKey("credential"));
  const response = await axios.put(
    `${SERVICE_URL}/v1/owners/me/sub-yards/${yardId}/activate`,
    null,
    { headers: { Authorization: `Bearer ${decrypt(credential).token}` } }
  );

  return response ? response.data : null;
};

export const deleteSubYard = async (yardId) => {
  const credential = localStorage.getItem(encryptKey("credential"));
  const response = await axios.delete(
    `${SERVICE_URL}/v1/owners/me/sub-yards/${yardId}`,
    { headers: { Authorization: `Bearer ${decrypt(credential).token}` } }
  );

  return response ? response.data : null;
};

export const reportYard = async (yardId, reason) => {
  const credential = localStorage.getItem(encryptKey("credential"));
  const response = await axios.post(
    `${SERVICE_URL}/v1/me/report/yards/${yardId}`,
    { "reason": reason},
    {
      headers: {
        Authorization: `Bearer ${decrypt(credential).token}`,
      }
    }
  );

  return response ? response.data : null;
};
