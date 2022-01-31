import React, { useState } from 'react'
import { format } from 'date-fns'
import Button from '~/components/ui/button/Button'
import Logger from '~/logger'
import api from '@/api'

import './EksporterExcel.less'

const downloadExcelString = (gruppeId, fileString) => {
	const dagensDato = format(new Date(), 'yyyy-MM-dd')
	const link = document.createElement('a')

	link.href = fileString
	link.download = gruppeId + '_' + dagensDato + '.xlsx'
	link.target = '_blank'
	document.body.appendChild(link)
	link.click()
	document.body.removeChild(link)
}

export const EksporterExcel = ({ gruppeId }) => {
	const [isLoading, setLoading] = useState(false)

	const download = async () => {
		setLoading(true)
		Logger.log({ event: 'Eksporterer til excel' })
		const data = await hentExcelFil(gruppeId)
		downloadExcelString(gruppeId, data)
		setLoading(false)
	}

	return (
		<Button
			className="flexbox--align-center csv-eksport-btn"
			kind="file-new-table"
			loading={isLoading}
			onClick={download}
		>
			EKSPORTER TIL EXCEL
		</Button>
	)
}

const hentExcelFil = (gruppeId) => {
	return api
		.fetch(`/dolly-backend/api/v1/excel/gruppe/${gruppeId}`, {
			method: 'GET',
			headers: { 'Content-Type': 'application/json' },
		})
		.then((response) => response.blob())
		.then((blob) => URL.createObjectURL(blob))
}
