import * as _ from "lodash";
import * as crypto from "crypto-js";
import { EMPTY } from "../constants/default";

const privateKey = "swp_booking_basketball_yard";

export const encryptKey = (key, prefix = EMPTY) => {
  return prefix + crypto.MD5(privateKey + key);
};

export const encrypt = (data) => {
  if (data) {
    if (!_.isString(data)) {
      data = JSON.stringify(data);
    }

    data = crypto["AES"].encrypt(data, privateKey);
    data = data.toString();
  }
  return data;
};

export const decrypt = (data) => {
  if (data) {
    const bytes = crypto["AES"].decrypt(data, privateKey),
      byteString = bytes.toString(crypto["enc"].Utf8);

    if (byteString !== EMPTY) {
      data = JSON.parse(byteString);
    }
  }

  return data;
};
