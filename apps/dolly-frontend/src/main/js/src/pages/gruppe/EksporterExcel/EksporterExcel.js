import React from 'react'
import { format } from 'date-fns'
import Button from '~/components/ui/button/Button'
import Logger from '~/logger'
import Loading from '~/components/ui/loading/Loading'
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

export const EksporterExcel = ({ gruppeId, action, loading }) => {
	if (loading) return <Loading label="henter fil..." />

	const download = async () => {
		Logger.log({ event: 'Eksporterer til excel' })
		const data = await action(gruppeId)
		downloadExcelString(gruppeId, data?.value)
	}

	return (
		<Button
			className="flexbox--align-center csv-eksport-btn"
			kind="file-new-table"
			onClick={download}
		>
			EKSPORTER TIL EXCEL
		</Button>
	)
}
