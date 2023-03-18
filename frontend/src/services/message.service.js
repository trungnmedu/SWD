import { map, Subject } from "rxjs";

const subject = new Subject();

export const messageService = {
  sendMessage: (key, data) => subject.next({ key, data }),
  clearMessages: () => subject.next(),
  getMessage: (key) =>
    subject.asObservable().pipe(
      map((data) => {
        if (data.key === key) {
          return data.data;
        }
      })
    ),
};
