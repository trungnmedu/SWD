import axios from "axios";
import { SERVICE_URL } from "../constants/default";
import { decrypt, encryptKey } from "../helpers/crypto.helper";

export const addOwner = async (data) => {
  const credential = localStorage.getItem(encryptKey("credential"));
  if (!credential) {
    throw new Error("No token provided!");
  }
  const response = await axios.post(
    `${SERVICE_URL}/v1/admin/owner-register`,
    data,
    {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${decrypt(credential).token}`,
      },
    }
  );
  return response ? response.data : null;
};

export const searchAllUsers = async (payload) => {
  const credential = localStorage.getItem(encryptKey("credential"));
  if (!credential) {
    throw new Error("No token provided!");
  }
  const response = await axios.post(
    `${SERVICE_URL}/v1/admin/all-accounts`,
    payload,
    {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${decrypt(credential).token}`,
      },
    }
  );
  return response ? response.data : null;
};

export const updateUser = async (userId, payload) => {
  const credential = localStorage.getItem(encryptKey("credential"));
  if (!credential) {
    throw new Error("No token provided!");
  }

  const response = await axios.put(
    `${SERVICE_URL}/v1/admin/accounts/${userId}`,
    payload,
    {
      headers: {
        Authorization: `Bearer ${decrypt(credential).token}`,
      },
    }
  );
  return response ? response.data : null;
};
