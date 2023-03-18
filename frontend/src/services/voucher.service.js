import axios from "axios";
import { SERVICE_URL } from "../constants/default";
import { decrypt, encryptKey } from "../helpers/crypto.helper";

export const searchVouchers = async (params, ownerId) => {
  const response = await axios.post(
    `${SERVICE_URL}/v1/owners/${ownerId}/vouchers`,
    params
  );
  return response ? response.data : null;
};

export const calculateBookingList = async (voucherCode, bookingList) => {
  const response = await axios.post(
    `${SERVICE_URL}/v1/vouchers/${voucherCode}/calculate`,
    { bookingList }
  );
  return response ? response.data : null;
};

export const getAllVouchers = async (params) => {
  const credential = localStorage.getItem(encryptKey("credential"));
  const response = await axios.post(
    `${SERVICE_URL}/v1/owners/me/vouchers`,
    params,
    {
      headers: { Authorization: `Bearer ${decrypt(credential).token}` },
    }
  )
  return response ? response.data : null;
} 

export const createVoucher = async(params) => {
  const credential = localStorage.getItem(encryptKey("credential"));
  const response = await axios.post(
    `${SERVICE_URL}/v1/owners/me/vouchers/create`,
    params,
    {
      headers: { Authorization: `Bearer ${decrypt(credential).token}` },
    }
  )
  return response ? response.data : null;
} 

export const saveVoucherChanges = async(params) => {
  const credential = localStorage.getItem(encryptKey("credential"));
  const response = await axios.put(
    `${SERVICE_URL}/v1/owners/me/vouchers/update`,
    params,
    {
      headers: { Authorization: `Bearer ${decrypt(credential).token}` },
    }
  )
  return response ? response.data : null;
} 

