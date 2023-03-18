import React, { useState } from 'react';
import ReactDOM from 'react-dom';
import "./style.scss";

export const useModal = () => {
    const [isShowing, setIsShowing] = useState(false);

    const toggle = () => {
        setIsShowing(!isShowing);
    }

    return [
        isShowing,
        toggle,
    ]
};

const Modal = ({ isShowing, hide, children }) => isShowing ? ReactDOM.createPortal(
    <React.Fragment>
        <div className="modal-overlay" />
        <div className="modal-wrapper" aria-modal aria-hidden tabIndex={-1} role="dialog" onClick={() => hide()}>
            <div className='modal-inner' onClick={(e) => e.stopPropagation()}>
                {children}
            </div>
        </div>
    </React.Fragment>, document.body
) : null;

export default Modal;
