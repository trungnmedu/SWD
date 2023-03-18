import React, { useEffect, useState } from 'react';
import { toast } from 'react-toastify';

import { EMPTY, TOAST_CONFIG } from '../../constants/default';
import { INTERNAL_SERVER_ERROR } from '../../constants/error-message';
import { VOUCHER_STATUS, VOUCHER_TYPE } from '../../constants/voucher';
import { createVoucher, saveVoucherChanges } from '../../services/voucher.service';

const VoucherDetailsModal = ({ voucher, toggleModal, voucherTypeCreate, fetchVouchers }) => {
	const [currentVoucher, setCurrentVoucher] = useState({})
	useEffect(() => {
		const initVoucherValue = {
			"id": voucher ? voucher.id : "",
			"reference": voucher ? voucher.reference : "",
			"voucherCode": voucher ? voucher.voucherCode : "",
			"createdAt": voucher ? voucher.createdAt : "",
			"usages": voucher ? voucher.usages : 0,
			"description": voucher ? voucher.description : "",
			"type": voucher ? voucher.type : voucherTypeCreate,
			"title": voucher ? voucher.title : EMPTY,
			"discount": voucher ? voucher.discount : 1,
			"maxQuantity": voucher ? voucher.maxQuantity : 1,
			"status": voucher ? voucher.status : "",
			"startDate": voucher ? new Date(voucher?.startDate?.split("/").reverse().join("/")).toISOString().substring(0, 10) : null,
			"endDate": voucher ? new Date(voucher?.endDate?.split("/").reverse().join("/")).toISOString().substring(0, 10) : null,
			"isActive": true,
			"createdByAccountId": voucher ? voucher.createdByAccountId : null,
		}
		setCurrentVoucher(initVoucherValue)
	}, [voucher, voucherTypeCreate])

	const handleVoucherOnChange = (event) => {
		const { name, value } = event.target;
		setCurrentVoucher(previousVoucher => ({
			...previousVoucher,
			[name]: value
		}))
	}

	const handleSubmitVoucher = async () => {
		const startDate = convertFormatDate(currentVoucher.startDate);
		const endDate = convertFormatDate(currentVoucher.endDate);
		const voucherValues = {
			...currentVoucher,
			startDate,
			endDate
		}
		if (!voucherValues.startDate || !voucherValues.endDate) {
			toast.warn("Start and end date did not empty!", TOAST_CONFIG);
			return;
		}

		try {
			if (voucher) {
				const response = await saveVoucherChanges(voucherValues);
				toast.success(response.message, TOAST_CONFIG);
			} else {
				const response = await createVoucher(voucherValues);
				toast.success(response.message, TOAST_CONFIG);
			}
			fetchVouchers()
			toggleModal()
		} catch (error) {
			toast.error(INTERNAL_SERVER_ERROR, TOAST_CONFIG)
		}
	}

	const convertFormatDate = (date) => String(date).split("-").reverse().join("/");

	return (
		<div className="custom-confirm" style={{ width: "600px" }}>
			<h4>{voucher ? "Voucher Details" : "Create Voucher"}</h4>
			<div>
				<form className="my-3" onSubmit={(e) => { e.preventDefault(); handleSubmitVoucher() }}>
					<div className="row p-2">
						<span
							className="col-1 lh-44 signup__icon-wrapper"
						>
							<i className="fas fa-heading"></i>
						</span>
						<input
							className="col-11 outline-none p-2 signup__input-border"
							type="text"
							placeholder="Title voucher ..."
							name='title'
							value={currentVoucher.title || ''}
							onChange={(e) => handleVoucherOnChange(e)}
						/>
					</div>

					<div className="row p-2">
						<span
							className="col-1 lh-44 signup__icon-wrapper"
							title="Code"
						>
							{(currentVoucher.type === VOUCHER_TYPE.PERCENT) && <b>%</b>}
							{(currentVoucher.type === VOUCHER_TYPE.CASH) && <i className="fas fa-money-bill-wave"></i>}
						</span>
						<input
							className="col-11 outline-none p-2 signup__input-border"
							type="number"
							placeholder={(voucherTypeCreate === VOUCHER_TYPE.PERCENT) ? "Discount percent (%) :" : "Amount discount ...VND"}
							name='discount'
							value={currentVoucher.discount || ''}
							onChange={(e) => handleVoucherOnChange(e)}
							min="1"
							max={(voucherTypeCreate === VOUCHER_TYPE.PERCENT) ? 100 : Number.MAX_SAFE_INTEGER}
						/>
					</div>

					<div className="row p-2">
						<span
							className="col-1 lh-44 signup__icon-wrapper"
							title="Code"
						>
							<i className="fas fa-tags"></i>
						</span>
						<input
							className="col-11 outline-none p-2 signup__input-border"
							type="number"
							placeholder="Quantity"
							name='maxQuantity'
							value={currentVoucher.maxQuantity || ''}
							onChange={(e) => handleVoucherOnChange(e)}
							min={currentVoucher.usages}
						/>
					</div>

					<div className="row p-2">
						<span
							className="col-1 lh-44 signup__icon-wrapper"
							title="Start Date"
						>
							<i className="far fa-clock"></i>
						</span>
						<input
							className="col-11 outline-none p-2 signup__input-border"
							type="date"
							placeholder="Start date ..."
							value={currentVoucher.startDate || ''}

							name='startDate'
							onChange={(e) => handleVoucherOnChange(e)}
						/>
					</div>

					<div className="row p-2">
						<span
							className="col-1 lh-44 signup__icon-wrapper"
							title="End Date"
						>
							<i className="far fa-clock"></i>
						</span>
						<input
							className="col-11 outline-none p-2 signup__input-border"
							type="date"
							min={currentVoucher.startDate}
							placeholder="End date ..."
							value={currentVoucher.endDate || ''}
							required
							name='endDate'
							onChange={(e) => handleVoucherOnChange(e)}
						/>
					</div>

					<div className="py-2">
						{voucher?.status !== VOUCHER_STATUS.INACTIVE && (
							<button
								className="btn btn-primary me-3 px-4"
							>
								{voucher ? "Save" : "Create"}
							</button>)
						}
						<button className="btn btn-secondary" onClick={(e) => { e.preventDefault(); toggleModal() }}>
							Cancel
						</button>
					</div>
				</form>
			</div>
		</div>
	)
}

export default VoucherDetailsModal