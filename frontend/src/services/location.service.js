import axios from "axios";
import { SERVICE_URL } from "../constants/default";

export const getAllProvinces = async () => {
  const response = await axios.get(`${SERVICE_URL}/v1/provinces`);
  return response ? response.data : null;
};

export const getDistrictsByProvinceId = async (id) => {
  const response = await axios.get(
    `${SERVICE_URL}/v1/provinces/${id}/districts`
  );
  return response ? response.data : null;
};
