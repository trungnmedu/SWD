import axios from "axios";
import { SERVICE_URL } from "../constants/default";
import { decrypt, encryptKey } from "../helpers/crypto.helper";

export const loginRequest = async (values) => {
  const response = await axios.post(`${SERVICE_URL}/v1/login`, values, {
    headers: {
      "Content-Type": "application/json",
    },
  });
  return response ? response.data : null;
};

export const sendForgotPassword = async (values) => {
  const response = await axios.post(
    `${SERVICE_URL}/v1/forgot/send-mail`,
    values,
    {
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
  return response ? response.data : null;
};

export const verifyForgotPassword = async (values) => {
  const response = await axios.post(
    `${SERVICE_URL}/v1/forgot/confirm-otp`,
    values,
    {
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
  return response ? response.data : null;
};

export const registerUser = async (values) => {
  const response = await axios.post(`${SERVICE_URL}/v1/register`, values, {
    headers: {
      "Content-Type": "application/json",
    },
  });
  return response ? response.data : null;
};

export const logout = async () => {
  const credential = localStorage.getItem(encryptKey("credential"));
  const response = await axios.get(`${SERVICE_URL}/v1/logout`, {
    headers: {
      Authorization: `Bearer ${decrypt(credential).token}`,
    },
  });
  return response ? response.data : null;
};

export const updatePassword = async (data, token) => {
  const response = await axios.post(
    `${SERVICE_URL}/v1/forgot/new-password`,
    data,
    {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response ? response.data : null;
};

export const verifyAccount = async (token, code) => {
  const response = await axios.post(
    `${SERVICE_URL}/v1/verify-account`,
    {
      otpCode: code,
    },
    {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    }
  );

  return response ? response.data : null;
};

export const receiveVerifyCode = async (token) => {
  const response = await axios.get(`${SERVICE_URL}/v1/verify-account`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response ? response.data : null;
};
